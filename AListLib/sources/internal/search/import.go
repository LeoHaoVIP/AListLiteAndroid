package search

import (
	_ "github.com/OpenListTeam/OpenList/internal/search/bleve"
	_ "github.com/OpenListTeam/OpenList/internal/search/db"
	_ "github.com/OpenListTeam/OpenList/internal/search/db_non_full_text"
	_ "github.com/OpenListTeam/OpenList/internal/search/meilisearch"
)
