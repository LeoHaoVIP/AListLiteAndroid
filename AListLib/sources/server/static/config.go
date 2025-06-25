package static

import (
	"strings"

	"github.com/OpenListTeam/OpenList/internal/conf"
	"github.com/OpenListTeam/OpenList/pkg/utils"
)

type SiteConfig struct {
	BasePath string
	Cdn      string
}

func getSiteConfig() SiteConfig {
	siteConfig := SiteConfig{
		BasePath: conf.URL.Path,
		Cdn:      strings.ReplaceAll(strings.TrimSuffix(conf.Conf.Cdn, "/"), "$version", strings.TrimPrefix(conf.WebVersion, "v"),),
	}
	if siteConfig.BasePath != "" {
		siteConfig.BasePath = utils.FixAndCleanPath(siteConfig.BasePath)
	}
	if siteConfig.Cdn == "" {
		siteConfig.Cdn = strings.TrimSuffix(siteConfig.BasePath, "/")
	}
	return siteConfig
}
