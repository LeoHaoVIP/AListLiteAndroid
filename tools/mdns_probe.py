"""AListLite mDNS 探针：在同一局域网内验证 App 的 mDNS 广播是否生效。

用法：
    uv run --with zeroconf --python 3.12 python tools/mdns_probe.py

输出的 FOUND 行里应能看到 AListLite-<机型>，端口为 App 里设置的服务端口。
注意：不要在同一台电脑上同时跑两个 mDNS 程序，会抢 UDP 5353 导致互相看不到。
"""
import time

from zeroconf import Zeroconf, ServiceBrowser, ServiceListener

TARGET = "AListLite"
DURATION = 10

hits = []


class Listener(ServiceListener):
    def __init__(self, kind):
        self.kind = kind

    def add_service(self, zc, type_, name):
        if TARGET not in name:
            return
        info = zc.get_service_info(type_, name, timeout=3000)
        if not info:
            print("FOUND [%s] %s (no info)" % (self.kind, name))
            return
        addrs = [".".join(str(b) for b in a) for a in info.addresses]
        hits.append((self.kind, name, addrs, info.port))
        print("FOUND [%s] %s %s port=%s" % (self.kind, name, addrs, info.port))

    def update_service(self, zc, type_, name):
        pass

    def remove_service(self, zc, type_, name):
        pass


def main():
    zc = Zeroconf()
    # 自定义类型是 App 自己搜索用的；_http._tcp 是标准类型，播放器/系统工具看得到
    ServiceBrowser(zc, "_alistlite._tcp.local.", Listener("alistlite"))
    ServiceBrowser(zc, "_http._tcp.local.", Listener("http"))
    time.sleep(DURATION)
    zc.close()
    print("TOTAL %d" % len(hits))
    if not hits:
        print("没找到服务，依次检查：")
        print("  1. 手机端 AList 服务是否在运行（停止后不广播）")
        print("  2. 手机与电脑是否在同一网段（IP 前三段一致）")
        print("  3. 路由器是否开了 AP 隔离 / 访客网络")
        print("  4. 电脑上是否还有其它 mDNS 程序在跑")


if __name__ == "__main__":
    main()
