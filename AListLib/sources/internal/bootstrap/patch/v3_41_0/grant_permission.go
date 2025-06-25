package v3_41_0

import (
	"github.com/OpenListTeam/OpenList/internal/op"
	"github.com/OpenListTeam/OpenList/pkg/utils"
)

// GrantAdminPermissions gives admin Permission 0(can see hidden) - 9(webdav manage) and
// 12(can read archives) - 13(can decompress archives)
// This patch is written to help users upgrading from older version better adapt
func GrantAdminPermissions() {
	admin, err := op.GetAdmin()
	if err == nil && (admin.Permission&0x33FF) == 0 {
		admin.Permission |= 0x33FF
		err = op.UpdateUser(admin)
	}
	if err != nil {
		utils.Log.Errorf("Cannot grant permissions to admin: %v", err)
	}
}
