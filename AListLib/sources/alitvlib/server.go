package alitvlib

import (
	"bytes"
	"context"
	"crypto/aes"
	"crypto/cipher"
	"crypto/md5"
	"crypto/sha256"
	_ "embed"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"math/rand"
	"net"
	"net/http"
	"sort"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/go-resty/resty/v2"
)

//go:embed index.html
var indexHtml []byte

var httpServer *http.Server
var listener net.Listener

var client = resty.New()

func StartServer() error {
	if httpServer != nil {
		return fmt.Errorf("alitv api already running")
	}

	gin.SetMode(gin.ReleaseMode)
	router := gin.New()

	// 设置路由（复制原 main 中的逻辑）
	setupRoutes(router)

	// 创建 server
	httpServer = &http.Server{
		Addr:    ":4015",
		Handler: router,
	}

	// 监听端口
	l, err := net.Listen("tcp", ":4015")
	if err != nil {
		httpServer = nil
		return err
	}
	listener = l

	// 异步启动
	go func() {
		if err := httpServer.Serve(l); err != nil && err != http.ErrServerClosed {
			log.Printf("Server failed: %v", err)
		}
	}()

	log.Println("alitv api HTTP Server started on :4015")
	return nil
}

func StopServer() error {
	if httpServer == nil {
		return fmt.Errorf("server not running")
	}

	err := httpServer.Shutdown(context.Background())
	httpServer = nil
	log.Println("alitv api HTTP Server stopped")
	return err
}

func IsRunning() bool {
	return httpServer != nil
}

// setupRoutes 提取原 main 中的路由设置
func setupRoutes(r *gin.Engine) {
	r.GET("/", func(c *gin.Context) {
		c.Writer.Write(indexHtml)
	})
	r.GET("/qr", getQRCode)
	r.GET("/check", checkStatus)
	r.GET("/token", getToken)
	r.POST("/token", postToken)
}

// 生成随机字符串iv向量（16位）
func randomString(length int) string {
	if length <= 0 {
		length = 32 // 默认 32
	}

	const charset = "abcdefghijklmnopqrstuvwxyz0123456789"
	result := make([]byte, length)

	// 用时间作为随机种子
	rand.Seed(time.Now().UnixNano())

	for i := range result {
		result[i] = charset[rand.Intn(len(charset))]
	}
	return string(result)
}

// 计算签名
func getSign(apiPath string, t string) string {
	params := GetParams(t)
	key := GenerateKey(t)

	// 原始数据
	data := fmt.Sprintf("POST-/api%v-%v-%v-%v", apiPath, t, params["d"], key)
	// 计算 SHA256
	hash := sha256.Sum256([]byte(data))

	// 转为十六进制字符串
	hashStr := hex.EncodeToString(hash[:])

	return hashStr
}

func Encrypt(plaintextStr, ivHex, keyStr string) (string, error) {
	// 1. 解析 key
	key := []byte(keyStr)
	if len(key) != 32 {
		return "", errors.New("key 长度必须为 32 字节（AES-256）")
	}

	// 2. 解析 IV
	iv := []byte(ivHex)
	if len(iv) != aes.BlockSize {
		return "", errors.New("IV 长度必须为 16 字节（128 位）")
	}

	// 3. 转为字节并 PKCS7 padding
	plaintext := []byte(plaintextStr)
	plaintext = pkcs7Pad(plaintext, aes.BlockSize)

	// 4. 初始化 AES-256
	block, err := aes.NewCipher(key)
	if err != nil {
		return "", fmt.Errorf("创建 AES cipher 失败: %w", err)
	}

	// 5. CBC 加密
	ciphertext := make([]byte, len(plaintext))
	mode := cipher.NewCBCEncrypter(block, iv)
	mode.CryptBlocks(ciphertext, plaintext)

	// 6. Base64 编码返回
	return base64.StdEncoding.EncodeToString(ciphertext), nil
}

// PKCS7 填充
func pkcs7Pad(data []byte, blockSize int) []byte {
	padding := blockSize - len(data)%blockSize
	padText := bytes.Repeat([]byte{byte(padding)}, padding)
	return append(data, padText...)
}

func Decrypt(ciphertextB64, ivHex, keyStr string) (string, error) {
	// 1. 解析 key（hex 转 []byte）
	key := []byte(keyStr)
	if len(key) != 32 {
		return "", errors.New("key 长度超过 32 字节，不能用于 AES-256")
	}
	// 2. 解析 IV
	iv, err := hex.DecodeString(ivHex)
	if err != nil {
		return "", fmt.Errorf("iv 解码失败: %w", err)
	}
	if len(iv) != aes.BlockSize {
		return "", errors.New("IV 长度必须为 16 字节（128 位）")
	}
	// 3. 解码 base64 密文
	ciphertext, err := base64.StdEncoding.DecodeString(ciphertextB64)
	if err != nil {
		return "", fmt.Errorf("密文 base64 解码失败: %w", err)
	}
	if len(ciphertext)%aes.BlockSize != 0 {
		return "", errors.New("密文长度不是块大小的倍数")
	}

	// 4. 初始化 AES-256
	block, err := aes.NewCipher(key)
	if err != nil {
		return "", fmt.Errorf("创建 AES cipher 失败: %w", err)
	}

	// 5. CBC 解密
	mode := cipher.NewCBCDecrypter(block, iv)
	plaintext := make([]byte, len(ciphertext))
	mode.CryptBlocks(plaintext, ciphertext)

	// 6. 去除 PKCS7 padding
	plaintext, err = pkcs7Unpad(plaintext, aes.BlockSize)
	if err != nil {
		return "", fmt.Errorf("去 padding 失败: %w", err)
	}

	return string(plaintext), nil
}

// PKCS7 Unpadding
func pkcs7Unpad(data []byte, blockSize int) ([]byte, error) {
	if len(data) == 0 || len(data)%blockSize != 0 {
		return nil, errors.New("无效的数据长度")
	}
	padLen := int(data[len(data)-1])
	if padLen == 0 || padLen > blockSize {
		return nil, errors.New("无效的 padding 长度")
	}
	for i := len(data) - padLen; i < len(data); i++ {
		if data[i] != byte(padLen) {
			return nil, errors.New("padding 内容不合法")
		}
	}
	return data[:len(data)-padLen], nil
}

func h(charArray []rune, modifier interface{}) string {
	// 去重
	uniqueMap := make(map[rune]bool)
	var uniqueChars []rune
	for _, c := range charArray {
		if !uniqueMap[c] {
			uniqueMap[c] = true
			uniqueChars = append(uniqueChars, c)
		}
	}

	// 处理 modifier，截取字符串后部分转换成数字
	modStr := fmt.Sprintf("%v", modifier)
	if len(modStr) < 7 {
		panic("modifier 字符串长度不足7")
	}
	numPart := modStr[7:]
	numericModifier, err := strconv.Atoi(numPart)
	if err != nil {
		panic(err)
	}

	var builder strings.Builder
	for _, char := range uniqueChars {
		charCode := int(char)
		newCharCode := charCode - (numericModifier % 127) - 1
		newCharCode = abs(newCharCode)
		if newCharCode < 33 {
			newCharCode += 33
		}
		builder.WriteRune(rune(newCharCode))
	}

	return builder.String()
}

func GetParams(t interface{}) map[string]string {
	return map[string]string{
		"akv":     "2.8.1496",                         // apk_version_name 版本号
		"apv":     "1.4.1",                            // 内部版本号
		"b":       "vivo",                             // 手机品牌
		"d":       "2c7d30cd7ae5e8017384988393f397c6", // 设备id 可随机生成
		"m":       "V2329A",                           // 手机型号
		"n":       "V2329A",                           // 手机型号名称
		"mac":     "",                                 // mac地址
		"wifiMac": "00db00200063",                     // wifiMac地址
		"nonce":   "",                                 // 随机字符串(好像没用)
		"t":       fmt.Sprintf("%v", t),               // 时间戳
	}
}

func GenerateKey(t interface{}) string {
	params := GetParams(t)

	// 按 key 排序
	keys := make([]string, 0, len(params))
	for k := range params {
		keys = append(keys, k)
	}
	sort.Strings(keys)

	// 拼接除 "t" 外所有的值
	var concatenatedParams strings.Builder
	for _, k := range keys {
		if k != "t" {
			concatenatedParams.WriteString(params[k])
		}
	}

	// 调用 h 函数
	keyArray := []rune(concatenatedParams.String())
	hashedKeyString := h(keyArray, t)

	// MD5 加密，输出 hex
	md5Sum := md5.Sum([]byte(hashedKeyString))
	return hex.EncodeToString(md5Sum[:])
}

// 取绝对值
func abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

// 获取时间戳
func getTimestamp() string {
	statusResp, err := client.R().
		Get("https://api.extscreen.com/timestamp")
	if err != nil || statusResp.StatusCode() != 200 {
		return strconv.FormatInt(time.Now().Unix(), 10)
	}
	var statusData map[string]interface{}
	json.Unmarshal(statusResp.Body(), &statusData)
	if statusData["code"].(float64) != 200 {
		return strconv.FormatInt(time.Now().Unix(), 10)
	}
	data := statusData["data"].(map[string]interface{})

	// strconv.FormatInt((int64)data["timestamp"].(float64), 10);
	return strconv.FormatInt(int64(data["timestamp"].(float64)), 10)
}

func GenerateRequestInfo(apiPath string, body map[string]interface{}) (map[string]interface{}, error) {
	t := getTimestamp()
	keyStr := GenerateKey(t)
	headers := GetParams(t)
	bodyJsonBytes, err := json.Marshal(body)
	if err != nil {
		fmt.Println("JSON 编码失败:", err)
		return nil, err
	}
	bodyJsonStr := string(bodyJsonBytes)

	iv := randomString(16)

	encrypted, err := Encrypt(bodyJsonStr, iv, keyStr)
	if err != nil {
		fmt.Println("AES 加密失败:", err)
		return nil, err
	}

	encryptedBody := map[string]interface{}{
		"ciphertext": encrypted,
		"iv":         iv,
	}

	headers["Content-Type"] = "application/json"
	headers["sign"] = getSign(apiPath, t)

	return map[string]interface{}{
		"headers": headers,
		"body":    encryptedBody,
		"key":     keyStr,
	}, nil
}

// 获取二维码
func getQRCode(c *gin.Context) {
	body := map[string]interface{}{
		"scopes": "user:base,file:all:read,file:all:write",
		"width":  500,
		"height": 500,
	}
	requestInfo, err := GenerateRequestInfo("/v2/qrcode", body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}
	resp, err := client.R().
		SetHeaders(requestInfo["headers"].(map[string]string)).
		SetBody(requestInfo["body"].(map[string]interface{})).
		Post("https://api.extscreen.com/aliyundrive/v2/qrcode")

	if err != nil || resp.StatusCode() != 200 {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate QR code"})
		return
	}

	var result map[string]interface{}
	json.Unmarshal(resp.Body(), &result)
	data := result["data"].(map[string]interface{})

	respCiphertext := data["ciphertext"].(string)
	respIv := data["iv"].(string)

	plain, err := Decrypt(respCiphertext, respIv, requestInfo["key"].(string))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	var qrcodeInfo map[string]string
	json.Unmarshal([]byte(plain), &qrcodeInfo)
	c.JSON(http.StatusOK, gin.H{
		"qr_link": qrcodeInfo["qrCodeUrl"],
		"sid":     qrcodeInfo["sid"],
	})
}

// 检查扫码登录状态并获取 token
func checkStatus(c *gin.Context) {
	sid := c.Query("sid")
	if sid == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid sid"})
		return
	}

	statusResp, err := client.R().
		Get("https://openapi.alipan.com/oauth/qrcode/" + sid + "/status")
	if err != nil || statusResp.StatusCode() != 200 {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to check status"})
		return
	}

	var statusData map[string]interface{}
	json.Unmarshal(statusResp.Body(), &statusData)

	if statusData["status"] == "LoginSuccess" {
		authCode := statusData["authCode"].(string)
		handleToken(c, map[string]interface{}{"code": authCode})
		return
	}
	c.JSON(http.StatusOK, statusData)
}

// GET /token
func getToken(c *gin.Context) {
	refresh := c.Query("refresh_ui")
	if refresh == "" {
		c.JSON(http.StatusOK, gin.H{
			"refresh_token": "",
			"access_token":  "",
			"text":          "refresh_ui parameter is required",
		})
		return
	}
	handleToken(c, map[string]interface{}{"refresh_token": refresh})
}

// POST /token
func postToken(c *gin.Context) {
	var body struct {
		RefreshToken string `json:"refresh_token"`
	}
	if err := c.ShouldBindJSON(&body); err != nil || body.RefreshToken == "" {
		c.JSON(http.StatusOK, gin.H{
			"refresh_token": "",
			"access_token":  "",
			"text":          "refresh_token parameter is required",
		})
		return
	}
	handleToken(c, map[string]interface{}{"refresh_token": body.RefreshToken})
}

// 获取 token
func handleToken(c *gin.Context, body map[string]interface{}) {
	requestInfo, err := GenerateRequestInfo("/v4/token", body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		return
	}
	resp, err := client.R().
		SetHeaders(requestInfo["headers"].(map[string]string)).
		SetBody(requestInfo["body"].(map[string]interface{})).
		Post("https://api.extscreen.com/aliyundrive/v4/token")

	if err != nil || resp.StatusCode() != 200 {
		c.JSON(http.StatusOK, gin.H{
			"refresh_token": "",
			"access_token":  "",
			"text":          "Failed to refresh token",
		})
		return
	}

	var tokenData map[string]interface{}
	json.Unmarshal(resp.Body(), &tokenData)

	data := tokenData["data"].(map[string]interface{})

	plain, err := Decrypt(data["ciphertext"].(string), data["iv"].(string), requestInfo["key"].(string))
	if err != nil {
		c.JSON(http.StatusOK, gin.H{
			"refresh_token": "",
			"access_token":  "",
			"text":          err.Error(),
		})
		return
	}

	var token map[string]string
	json.Unmarshal([]byte(plain), &token)

	c.JSON(http.StatusOK, gin.H{
		"refresh_token": token["refresh_token"],
		"access_token":  token["access_token"],
		"text":          "",
	})
}

func main() {
	router := gin.Default()

	router.GET("/", func(c *gin.Context) {
		c.Writer.Write(indexHtml)
	})

	router.GET("/qr", getQRCode)
	router.GET("/check", checkStatus)
	router.GET("/token", getToken)
	router.POST("/token", postToken)

	router.Run(":8081")
}
