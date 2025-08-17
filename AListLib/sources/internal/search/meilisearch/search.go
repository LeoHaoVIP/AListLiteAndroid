package meilisearch

import (
	"context"
	"fmt"
	"path"
	"strings"
	"time"

	"github.com/OpenListTeam/OpenList/v4/internal/model"
	"github.com/OpenListTeam/OpenList/v4/internal/search/searcher"
	"github.com/OpenListTeam/OpenList/v4/pkg/utils"
	"github.com/meilisearch/meilisearch-go"
)

type searchDocument struct {
	// Document id, hash of the file path,
	// can be used for filtering a file exactly(case-sensitively).
	ID string `json:"id"`
	// Hash of parent, can be used for filtering direct children.
	ParentHash string `json:"parent_hash"`
	// One-by-one hash of parent paths (path hierarchy).
	// eg: A file's parent is '/home/a/b',
	// its parent paths are '/home/a/b', '/home/a', '/home', '/'.
	// Can be used for filtering all descendants exactly.
	// Storing path hashes instead of plaintext paths benefits disk usage and case-sensitive filter.
	ParentPathHashes []string `json:"parent_path_hashes"`
	model.SearchNode
}

type Meilisearch struct {
	Client               meilisearch.ServiceManager
	IndexUid             string
	FilterableAttributes []string
	SearchableAttributes []string
}

func (m *Meilisearch) Config() searcher.Config {
	return config
}

func (m *Meilisearch) Search(ctx context.Context, req model.SearchReq) ([]model.SearchNode, int64, error) {
	mReq := &meilisearch.SearchRequest{
		AttributesToSearchOn: m.SearchableAttributes,
		Page:                 int64(req.Page),
		HitsPerPage:          int64(req.PerPage),
	}
	var filters []string
	if req.Scope != 0 {
		filters = append(filters, fmt.Sprintf("is_dir = %v", req.Scope == 1))
	}
	if req.Parent != "" && req.Parent != "/" {
		// use parent_path_hashes to filter descendants
		parentHash := hashPath(req.Parent)
		filters = append(filters, fmt.Sprintf("parent_path_hashes = '%s'", parentHash))
	}
	if len(filters) > 0 {
		mReq.Filter = strings.Join(filters, " AND ")
	}

	search, err := m.Client.Index(m.IndexUid).SearchWithContext(ctx, req.Keywords, mReq)
	if err != nil {
		return nil, 0, err
	}
	nodes, err := utils.SliceConvert(search.Hits, func(src any) (model.SearchNode, error) {
		srcMap := src.(map[string]any)
		return model.SearchNode{
			Parent: srcMap["parent"].(string),
			Name:   srcMap["name"].(string),
			IsDir:  srcMap["is_dir"].(bool),
			Size:   int64(srcMap["size"].(float64)),
		}, nil
	})
	if err != nil {
		return nil, 0, err
	}
	return nodes, search.TotalHits, nil
}

func (m *Meilisearch) Index(ctx context.Context, node model.SearchNode) error {
	return m.BatchIndex(ctx, []model.SearchNode{node})
}

func (m *Meilisearch) BatchIndex(ctx context.Context, nodes []model.SearchNode) error {
	documents, _ := utils.SliceConvert(nodes, func(src model.SearchNode) (*searchDocument, error) {
		parentHash := hashPath(src.Parent)
		nodePath := path.Join(src.Parent, src.Name)
		nodePathHash := hashPath(nodePath)
		parentPaths := utils.GetPathHierarchy(src.Parent)
		parentPathHashes, _ := utils.SliceConvert(parentPaths, func(parentPath string) (string, error) {
			return hashPath(parentPath), nil
		})

		return &searchDocument{
			ID:               nodePathHash,
			ParentHash:       parentHash,
			ParentPathHashes: parentPathHashes,
			SearchNode:       src,
		}, nil
	})

	// max up to 10,000 documents per batch to reduce error rate while uploading over the Internet
	_, err := m.Client.Index(m.IndexUid).AddDocumentsInBatchesWithContext(ctx, documents, 10000)
	if err != nil {
		return err
	}

	// documents were uploaded and enqueued for indexing, just return early
	//// Wait for the task to complete and check
	//forTask, err := m.Client.WaitForTask(task.TaskUID, meilisearch.WaitParams{
	//	Context:  ctx,
	//	Interval: time.Millisecond * 50,
	//})
	//if err != nil {
	//	return err
	//}
	//if forTask.Status != meilisearch.TaskStatusSucceeded {
	//	return fmt.Errorf("BatchIndex failed, task status is %s", forTask.Status)
	//}
	return nil
}

func (m *Meilisearch) getDocumentsByParent(ctx context.Context, parent string) ([]*searchDocument, error) {
	var result meilisearch.DocumentsResult
	query := &meilisearch.DocumentsQuery{
		Limit: int64(model.MaxInt),
	}
	if parent != "" && parent != "/" {
		// use parent_hash to filter direct children
		parentHash := hashPath(parent)
		query.Filter = fmt.Sprintf("parent_hash = '%s'", parentHash)
	}
	err := m.Client.Index(m.IndexUid).GetDocumentsWithContext(ctx, query, &result)
	if err != nil {
		return nil, err
	}
	return utils.SliceConvert(result.Results, func(src map[string]any) (*searchDocument, error) {
		return buildSearchDocumentFromResults(src), nil
	})
}

func (m *Meilisearch) Get(ctx context.Context, parent string) ([]model.SearchNode, error) {
	result, err := m.getDocumentsByParent(ctx, parent)
	if err != nil {
		return nil, err
	}
	return utils.SliceConvert(result, func(src *searchDocument) (model.SearchNode, error) {
		return src.SearchNode, nil
	})
}

func (m *Meilisearch) getDocumentInPath(ctx context.Context, parent string, name string) (*searchDocument, error) {
	var result searchDocument
	// join them and calculate the hash to exactly identify the node
	nodePath := path.Join(parent, name)
	nodePathHash := hashPath(nodePath)
	err := m.Client.Index(m.IndexUid).GetDocumentWithContext(ctx, nodePathHash, nil, &result)
	if err != nil {
		// return nil for documents that no exists
		if err.(*meilisearch.Error).StatusCode == 404 {
			return nil, nil
		}
		return nil, err
	}
	return &result, nil
}

func (m *Meilisearch) delDirChild(ctx context.Context, prefix string) error {
	prefix = hashPath(prefix)
	// use parent_path_hashes to filter descendants,
	// so no longer need to walk through the directories to get their IDs,
	// speeding up the deletion process with easy maintained codebase
	filter := fmt.Sprintf("parent_path_hashes = '%s'", prefix)
	_, err := m.Client.Index(m.IndexUid).DeleteDocumentsByFilterWithContext(ctx, filter)
	// task was enqueued (if succeed), no need to wait
	return err
}

func (m *Meilisearch) Del(ctx context.Context, prefix string) error {
	prefix = utils.FixAndCleanPath(prefix)
	dir, name := path.Split(prefix)
	if dir != "/" {
		dir = dir[:len(dir)-1]
	}

	document, err := m.getDocumentInPath(ctx, dir, name)
	if err != nil {
		return err
	}
	if document == nil {
		// Defensive programming. Document may be the folder, try deleting Child
		return m.delDirChild(ctx, prefix)
	}
	if document.IsDir {
		err = m.delDirChild(ctx, prefix)
		if err != nil {
			return err
		}
	}
	_, err = m.Client.Index(m.IndexUid).DeleteDocumentWithContext(ctx, document.ID)
	// task was enqueued (if succeed), no need to wait
	return err
}

func (m *Meilisearch) Release(ctx context.Context) error {
	return nil
}

func (m *Meilisearch) Clear(ctx context.Context) error {
	_, err := m.Client.Index(m.IndexUid).DeleteAllDocumentsWithContext(ctx)
	// task was enqueued (if succeed), no need to wait
	return err
}

func (m *Meilisearch) getTaskStatus(ctx context.Context, taskUID int64) (meilisearch.TaskStatus, error) {
	forTask, err := m.Client.WaitForTaskWithContext(ctx, taskUID, time.Second)
	if err != nil {
		return meilisearch.TaskStatusUnknown, err
	}
	return forTask.Status, nil
}
