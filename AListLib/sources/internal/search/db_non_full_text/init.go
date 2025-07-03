package db_non_full_text

import (
	"github.com/OpenListTeam/OpenList/v4/internal/search/searcher"
)

var config = searcher.Config{
	Name:       "database_non_full_text",
	AutoUpdate: true,
}

func init() {
	searcher.RegisterSearcher(config, func() (searcher.Searcher, error) {
		return &DB{}, nil
	})
}
