package io.aftersound.weave.zk;

class ZKAdminServerConfigHolder {

    private static final ThreadLocal<ZKServer.AdminServerConfig> TL = new ThreadLocal<>();

    public static void set(ZKServer.AdminServerConfig config) {
        TL.set(config);
    }

    public static ZKServer.AdminServerConfig get() {
        return TL.get();
    }
}
