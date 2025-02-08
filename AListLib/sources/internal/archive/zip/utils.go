package zip

import (
	"bytes"
	"github.com/alist-org/alist/v3/internal/errs"
	"github.com/alist-org/alist/v3/internal/model"
	"github.com/alist-org/alist/v3/internal/stream"
	"github.com/saintfish/chardet"
	"github.com/yeka/zip"
	"golang.org/x/text/encoding"
	"golang.org/x/text/encoding/charmap"
	"golang.org/x/text/encoding/japanese"
	"golang.org/x/text/encoding/korean"
	"golang.org/x/text/encoding/simplifiedchinese"
	"golang.org/x/text/encoding/traditionalchinese"
	"golang.org/x/text/encoding/unicode"
	"golang.org/x/text/encoding/unicode/utf32"
	"golang.org/x/text/transform"
	"io"
	"os"
	stdpath "path"
	"strings"
)

func toModelObj(file os.FileInfo) *model.Object {
	return &model.Object{
		Name:     decodeName(file.Name()),
		Size:     file.Size(),
		Modified: file.ModTime(),
		IsFolder: file.IsDir(),
	}
}

func decompress(file *zip.File, filePath, outputPath, password string) error {
	targetPath := outputPath
	dir, base := stdpath.Split(filePath)
	if dir != "" {
		targetPath = stdpath.Join(targetPath, dir)
		err := os.MkdirAll(targetPath, 0700)
		if err != nil {
			return err
		}
	}
	if base != "" {
		err := _decompress(file, targetPath, password, func(_ float64) {})
		if err != nil {
			return err
		}
	}
	return nil
}

func _decompress(file *zip.File, targetPath, password string, up model.UpdateProgress) error {
	if file.IsEncrypted() {
		file.SetPassword(password)
	}
	rc, err := file.Open()
	if err != nil {
		return err
	}
	defer rc.Close()
	f, err := os.OpenFile(stdpath.Join(targetPath, file.FileInfo().Name()), os.O_WRONLY|os.O_CREATE|os.O_EXCL, 0600)
	if err != nil {
		return err
	}
	defer f.Close()
	_, err = io.Copy(f, &stream.ReaderUpdatingProgress{
		Reader: &stream.SimpleReaderWithSize{
			Reader: rc,
			Size:   file.FileInfo().Size(),
		},
		UpdateProgress: up,
	})
	if err != nil {
		return err
	}
	return nil
}

func filterPassword(err error) error {
	if err != nil && strings.Contains(err.Error(), "password") {
		return errs.WrongArchivePassword
	}
	return err
}

func decodeName(name string) string {
	b := []byte(name)
	detector := chardet.NewTextDetector()
	result, err := detector.DetectBest(b)
	if err != nil {
		return name
	}
	enc := getEncoding(result.Charset)
	if enc == nil {
		return name
	}
	i := bytes.NewReader(b)
	decoder := transform.NewReader(i, enc.NewDecoder())
	content, _ := io.ReadAll(decoder)
	return string(content)
}

func getEncoding(name string) (enc encoding.Encoding) {
	switch name {
	case "UTF-16BE":
		enc = unicode.UTF16(unicode.BigEndian, unicode.IgnoreBOM)
	case "UTF-16LE":
		enc = unicode.UTF16(unicode.LittleEndian, unicode.IgnoreBOM)
	case "UTF-32BE":
		enc = utf32.UTF32(utf32.BigEndian, utf32.IgnoreBOM)
	case "UTF-32LE":
		enc = utf32.UTF32(utf32.LittleEndian, utf32.IgnoreBOM)
	case "ISO-8859-1":
		enc = charmap.ISO8859_1
	case "ISO-8859-2":
		enc = charmap.ISO8859_2
	case "ISO-8859-3":
		enc = charmap.ISO8859_3
	case "ISO-8859-4":
		enc = charmap.ISO8859_4
	case "ISO-8859-5":
		enc = charmap.ISO8859_5
	case "ISO-8859-6":
		enc = charmap.ISO8859_6
	case "ISO-8859-7":
		enc = charmap.ISO8859_7
	case "ISO-8859-8":
		enc = charmap.ISO8859_8
	case "ISO-8859-8-I":
		enc = charmap.ISO8859_8I
	case "ISO-8859-9":
		enc = charmap.ISO8859_9
	case "windows-1251":
		enc = charmap.Windows1251
	case "windows-1256":
		enc = charmap.Windows1256
	case "KOI8-R":
		enc = charmap.KOI8R
	case "Shift_JIS":
		enc = japanese.ShiftJIS
	case "GB-18030":
		enc = simplifiedchinese.GB18030
	case "EUC-JP":
		enc = japanese.EUCJP
	case "EUC-KR":
		enc = korean.EUCKR
	case "Big5":
		enc = traditionalchinese.Big5
	case "ISO-2022-JP":
		enc = japanese.ISO2022JP
	default:
		enc = nil
	}
	return
}
