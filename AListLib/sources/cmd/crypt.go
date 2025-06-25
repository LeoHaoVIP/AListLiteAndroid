package cmd

import (
	log "github.com/sirupsen/logrus"

	"io"
	"os"
	"path"
	"path/filepath"
	"strings"

	"github.com/spf13/cobra"

	rcCrypt "github.com/rclone/rclone/backend/crypt"
	"github.com/rclone/rclone/fs/config/configmap"
	"github.com/rclone/rclone/fs/config/obscure"
)

// encryption and decryption command format for Crypt driver

type options struct {
	Op  string //decrypt or encrypt
	src string //source dir or file
	dst string //out destination

	pwd                string //de/encrypt password
	salt               string
	filenameEncryption string //reference drivers\crypt\meta.go Addtion
	dirnameEncryption  string
	filenameEncode     string
	suffix             string
}

var opt options

// CryptCmd represents the crypt command
var CryptCmd = &cobra.Command{
	Use:     "crypt",
	Short:   "Encrypt or decrypt local file or dir",
	Example: `openlist crypt  -s ./src/encrypt/ --op=de --pwd=123456 --salt=345678`,
	Run: func(cmd *cobra.Command, args []string) {
		opt.validate()
		opt.cryptFileDir()

	},
}

func init() {
	RootCmd.AddCommand(CryptCmd)
	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// versionCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	CryptCmd.Flags().StringVarP(&opt.src, "src", "s", "", "src file or dir to encrypt/decrypt")
	CryptCmd.Flags().StringVarP(&opt.dst, "dst", "d", "", "dst dir to output,if not set,output to src dir")
	CryptCmd.Flags().StringVar(&opt.Op, "op", "", "de or en which stands for decrypt or encrypt")

	CryptCmd.Flags().StringVar(&opt.pwd, "pwd", "", "password used to encrypt/decrypt,if not contain ___Obfuscated___ prefix,will be obfuscated before used")
	CryptCmd.Flags().StringVar(&opt.salt, "salt", "", "salt used to encrypt/decrypt,if not contain ___Obfuscated___ prefix,will be obfuscated before used")
	CryptCmd.Flags().StringVar(&opt.filenameEncryption, "filename-encrypt", "off", "filename encryption mode: off,standard,obfuscate")
	CryptCmd.Flags().StringVar(&opt.dirnameEncryption, "dirname-encrypt", "false", "is dirname encryption enabled:true,false")
	CryptCmd.Flags().StringVar(&opt.filenameEncode, "filename-encode", "base64", "filename encoding mode: base64,base32,base32768")
	CryptCmd.Flags().StringVar(&opt.suffix, "suffix", ".bin", "suffix for encrypted file,default is .bin")
}

func (o *options) validate() {
	if o.src == "" {
		log.Fatal("src can not be empty")
	}
	if o.Op != "encrypt" && o.Op != "decrypt" && o.Op != "en" && o.Op != "de" {
		log.Fatal("op must be encrypt or decrypt")
	}
	if o.filenameEncryption != "off" && o.filenameEncryption != "standard" && o.filenameEncryption != "obfuscate" {
		log.Fatal("filename_encryption must be off,standard,obfuscate")
	}
	if o.filenameEncode != "base64" && o.filenameEncode != "base32" && o.filenameEncode != "base32768" {
		log.Fatal("filename_encode must be base64,base32,base32768")
	}

}

func (o *options) cryptFileDir() {
	src, _ := filepath.Abs(o.src)
	log.Infof("src abs is %v", src)

	fileInfo, err := os.Stat(src)
	if err != nil {
		log.Fatalf("reading file/dir %v failed,err:%v", src, err)

	}
	pwd := updateObfusParm(o.pwd)
	salt := updateObfusParm(o.salt)

	//create cipher
	config := configmap.Simple{
		"password":                  pwd,
		"password2":                 salt,
		"filename_encryption":       o.filenameEncryption,
		"directory_name_encryption": o.dirnameEncryption,
		"filename_encoding":         o.filenameEncode,
		"suffix":                    o.suffix,
		"pass_bad_blocks":           "",
	}
	log.Infof("config:%v", config)
	cipher, err := rcCrypt.NewCipher(config)
	if err != nil {
		log.Fatalf("create cipher failed,err:%v", err)

	}
	dst := ""
	//check and create dst dir
	if o.dst != "" {
		dst, _ = filepath.Abs(o.dst)
		checkCreateDir(dst)
	}

	// src is file
	if !fileInfo.IsDir() { //file
		if dst == "" {
			dst = filepath.Dir(src)
		}
		o.cryptFile(cipher, src, dst)
		return
	}

	// src is dir
	if dst == "" {
		//if src is dir and not set dst dir ,create ${src}_crypt dir as dst dir
		dst = path.Join("./", fileInfo.Name()+"_crypt")
	}
	log.Infof("dst : %v", dst)
	filepath.Walk(src, func(p string, info os.FileInfo, err error) error {
		if err != nil {
			log.Errorf("get file %v info failed, err:%v", p, err)
			return err
		}
		if info.IsDir() {
			//create output dir
			d := strings.Replace(p, src, dst, 1)
			log.Infof("create output dir %v", d)
			checkCreateDir(d)

			return nil
		}
		d := strings.Replace(filepath.Dir(p), src, dst, 1)
		o.cryptFile(cipher, p, d)
		return nil
	})

}

func (o *options) cryptFile(cipher *rcCrypt.Cipher, src string, dst string) {
	fileInfo, err := os.Stat(src)
	if err != nil {
		log.Fatalf("get file %v  info failed,err:%v", src, err)

	}
	fd, err := os.OpenFile(src, os.O_RDWR, 0666)
	if err != nil {
		log.Fatalf("open file %v failed,err:%v", src, err)

	}
	defer fd.Close()

	var cryptSrcReader io.Reader
	var outFile string
	if o.Op == "encrypt" || o.Op == "en" {
		filename := fileInfo.Name()
		if o.filenameEncryption != "off" {
			filename = cipher.EncryptFileName(fileInfo.Name())
			log.Infof("encrypt file name %v to %v", fileInfo.Name(), filename)
		}
		cryptSrcReader, err = cipher.EncryptData(fd)
		if err != nil {
			log.Fatalf("encrypt file %v failed,err:%v", src, err)

		}
		outFile = path.Join(dst, filename)
	} else {
		filename := fileInfo.Name()
		if o.filenameEncryption != "off" {
			filename, err = cipher.DecryptFileName(filename)
			if err != nil {
				log.Fatalf("decrypt file name %v failed,err:%v", src, err)
			}
			log.Infof("decrypt file name %v to %v, ", fileInfo.Name(), filename)
		}

		cryptSrcReader, err = cipher.DecryptData(fd)
		if err != nil {
			log.Fatalf("decrypt file %v failed,err:%v", src, err)

		}
		outFile = path.Join(dst, filename)
	}
	//write new file
	wr, err := os.OpenFile(outFile, os.O_CREATE|os.O_WRONLY, 0755)
	if err != nil {
		log.Fatalf("create file %v failed,err:%v", outFile, err)

	}
	defer wr.Close()

	_, err = io.Copy(wr, cryptSrcReader)
	if err != nil {
		log.Fatalf("write file %v failed,err:%v", outFile, err)
	}

}

// check dir exist ,if not ,create
func checkCreateDir(dir string) {
	_, err := os.Stat(dir)

	if os.IsNotExist(err) {
		err := os.MkdirAll(dir, 0755)
		if err != nil {
			log.Fatalf("create dir %v failed,err:%v", dir, err)
		}
		return
	}

	log.Fatalf("read dir %v err: %v", dir, err)
}

func updateObfusParm(str string) string {
	obfuscatedPrefix := "___Obfuscated___"
	if !strings.HasPrefix(str, obfuscatedPrefix) {
		str, err := obscure.Obscure(str)
		if err != nil {
			log.Fatalf("update obfuscated parameter failed,err:%v", str)
		}
	} else {
		str, _ = strings.CutPrefix(str, obfuscatedPrefix)
	}
	return str
}
