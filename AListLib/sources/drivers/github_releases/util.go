package github_releases

import (
	"fmt"
	"regexp"
	"strings"
	"sync"
	"time"

	"github.com/alist-org/alist/v3/drivers/base"
	"github.com/go-resty/resty/v2"
	jsoniter "github.com/json-iterator/go"
	log "github.com/sirupsen/logrus"
)

var (
	cache   = make(map[string]*resty.Response)
	created = make(map[string]time.Time)
	mu      sync.Mutex
	req     *resty.Request
)

// 解析仓库列表
func ParseRepos(text string, allVersion bool) ([]Release, error) {
	lines := strings.Split(text, "\n")
	var repos []Release
	for _, line := range lines {
		line = strings.TrimSpace(line)
		if line == "" {
			continue
		}
		parts := strings.Split(line, ":")
		path, repo := "", ""
		if len(parts) == 1 {
			path = "/"
			repo = parts[0]
		} else if len(parts) == 2 {
			path = fmt.Sprintf("/%s", strings.Trim(parts[0], "/"))
			repo = parts[1]
		} else {
			return nil, fmt.Errorf("invalid format: %s", line)
		}

		if allVersion {
			releases, _ := GetAllVersion(repo, path)
			repos = append(repos, *releases...)
		} else {
			repos = append(repos, Release{
				Path:     path,
				RepoName: repo,
				Version:  "latest",
				ID:       "latest",
			})
		}

	}
	return repos, nil
}

// 获取下一级目录
func GetNextDir(wholePath string, basePath string) string {
	if !strings.HasSuffix(basePath, "/") {
		basePath += "/"
	}
	if !strings.HasPrefix(wholePath, basePath) {
		return ""
	}
	remainingPath := strings.TrimLeft(strings.TrimPrefix(wholePath, basePath), "/")
	if remainingPath != "" {
		parts := strings.Split(remainingPath, "/")
		return parts[0]
	}
	return ""
}

// 发送 GET 请求
func GetRequest(url string, cacheExpiration int) (*resty.Response, error) {
	mu.Lock()
	if res, ok := cache[url]; ok && time.Now().Before(created[url].Add(time.Duration(cacheExpiration)*time.Minute)) {
		mu.Unlock()
		return res, nil
	}
	mu.Unlock()

	res, err := req.Get(url)
	if err != nil {
		return nil, err
	}
	if res.StatusCode() != 200 {
		log.Warn("failed to get request: ", res.StatusCode(), res.String())
	}

	mu.Lock()
	cache[url] = res
	created[url] = time.Now()
	mu.Unlock()

	return res, nil
}

// 获取 README、LICENSE 等文件
func GetGithubOtherFile(repo string, basePath string, cacheExpiration int) (*[]File, error) {
	url := fmt.Sprintf("https://api.github.com/repos/%s/contents/", strings.Trim(repo, "/"))
	res, _ := GetRequest(url, cacheExpiration)
	body := jsoniter.Get(res.Body())
	var files []File
	for i := 0; i < body.Size(); i++ {
		filename := body.Get(i, "name").ToString()

		re := regexp.MustCompile(`(?i)^(.*\.md|LICENSE)$`)

		if !re.MatchString(filename) {
			continue
		}

		files = append(files, File{
			FileName: filename,
			Size:     body.Get(i, "size").ToInt64(),
			CreateAt: time.Time{},
			UpdateAt: time.Now(),
			Url:      body.Get(i, "download_url").ToString(),
			Type:     body.Get(i, "type").ToString(),
			Path:     fmt.Sprintf("%s/%s", basePath, filename),
		})
	}
	return &files, nil
}

// 获取 GitHub Release 详细信息
func GetRepoReleaseInfo(repo string, version string, basePath string, cacheExpiration int) (*ReleasesData, error) {
	url := fmt.Sprintf("https://api.github.com/repos/%s/releases/%s", strings.Trim(repo, "/"), version)
	res, _ := GetRequest(url, cacheExpiration)
	body := res.Body()

	if jsoniter.Get(res.Body(), "status").ToInt64() != 0 {
		return &ReleasesData{}, fmt.Errorf("%s", res.String())
	}

	assets := jsoniter.Get(res.Body(), "assets")
	var files []File

	for i := 0; i < assets.Size(); i++ {
		filename := assets.Get(i, "name").ToString()

		files = append(files, File{
			FileName: filename,
			Size:     assets.Get(i, "size").ToInt64(),
			Url:      assets.Get(i, "browser_download_url").ToString(),
			Type:     assets.Get(i, "content_type").ToString(),
			Path:     fmt.Sprintf("%s/%s", basePath, filename),

			CreateAt: func() time.Time {
				t, _ := time.Parse(time.RFC3339, assets.Get(i, "created_at").ToString())
				return t
			}(),
			UpdateAt: func() time.Time {
				t, _ := time.Parse(time.RFC3339, assets.Get(i, "updated_at").ToString())
				return t
			}(),
		})
	}

	return &ReleasesData{
		Files: files,
		Url:   jsoniter.Get(body, "html_url").ToString(),

		Size: func() int64 {
			size := int64(0)
			for _, file := range files {
				size += file.Size
			}
			return size
		}(),
		UpdateAt: func() time.Time {
			t, _ := time.Parse(time.RFC3339, jsoniter.Get(body, "published_at").ToString())
			return t
		}(),
		CreateAt: func() time.Time {
			t, _ := time.Parse(time.RFC3339, jsoniter.Get(body, "created_at").ToString())
			return t
		}(),
	}, nil
}

// 获取所有的版本号
func GetAllVersion(repo string, path string) (*[]Release, error) {
	url := fmt.Sprintf("https://api.github.com/repos/%s/releases", strings.Trim(repo, "/"))
	res, _ := GetRequest(url, 0)
	body := jsoniter.Get(res.Body())
	releases := make([]Release, 0)
	for i := 0; i < body.Size(); i++ {
		version := body.Get(i, "tag_name").ToString()
		releases = append(releases, Release{
			Path:     fmt.Sprintf("%s/%s", path, version),
			Version:  version,
			RepoName: repo,
			ID:       body.Get(i, "id").ToString(),
		})
	}
	return &releases, nil
}

func ClearCache() {
	mu.Lock()
	cache = make(map[string]*resty.Response)
	created = make(map[string]time.Time)
	mu.Unlock()
}

func SetHeader(token string) {
	req = base.RestyClient.R()
	if token != "" {
		req.SetHeader("Authorization", fmt.Sprintf("Bearer %s", token))
	}
	req.SetHeader("Accept", "application/vnd.github+json")
	req.SetHeader("X-GitHub-Api-Version", "2022-11-28")
}
