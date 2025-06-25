package op_test

import (
	"testing"

	_ "github.com/OpenListTeam/OpenList/drivers"
	"github.com/OpenListTeam/OpenList/internal/op"
)

func TestDriverItemsMap(t *testing.T) {
	itemsMap := op.GetDriverInfoMap()
	if len(itemsMap) != 0 {
		t.Logf("driverInfoMap: %v", itemsMap)
	} else {
		t.Errorf("expected driverInfoMap not empty, but got empty")
	}
}
