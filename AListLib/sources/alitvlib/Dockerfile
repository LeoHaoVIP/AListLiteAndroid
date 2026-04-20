# ----------- Build Stage -------------
FROM golang:1.24 AS builder

# 设置工作目录
WORKDIR /app

# 复制go.mod和go.sum（加快缓存）
COPY go.mod ./
COPY go.sum ./
RUN go mod download

# 复制源代码
COPY . .

# 编译Go程序，静态链接，禁用CGO
RUN CGO_ENABLED=0 go build -o app .

# ----------- Final Image -------------
FROM alpine:latest

# 安装ca-certificates（如果你需要访问HTTPS）
RUN apk --no-cache add ca-certificates

WORKDIR /root/

# 从构建阶段复制二进制文件
COPY --from=builder /app/app .

# 开放端口（如果有）
EXPOSE 8081

# 设置环境变量
ENV GIN_MODE=release

# 启动
CMD ["./app"]
