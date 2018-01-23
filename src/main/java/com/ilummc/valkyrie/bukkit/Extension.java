package com.ilummc.valkyrie.bukkit;

public abstract class Extension {

    /**
     * 未提供的默认字符串
     */
    public static final String UNDEFINED = "UNDEFINED";

    /**
     * 初始化为 Forge 平台的拓展
     */
    public void initForge() {

    }

    /**
     * 初始化为 Bukkit 平台的拓展
     */
    public void initBukkit() {

    }

    /**
     * 启用自动更新后，将会对此 URL 进行 GET，返回类型应为 <code>application/js</code>，返回的 js 文本应为如下格式：
     * <p>
     * <code>
     * [{
     * "version": "1.0",
     * "description": ["1. 更新了XXX", "2. 更新了xxx"],
     * "downloadUrl": "http://下载.地址",
     * "releaseDate": 1516528727988
     * ]}
     * </code>
     * <p>
     * 如果没有更新地址请留空。
     * <p>
     * 如果提供了 downloadUrl，且以 .jar 或 .zip 结尾，将会尝试进行 GET 操作，并自动替换原来的 .jar 文件
     *
     * @return 用于请求的 URL
     */
    public String getUpdateUrl() {
        return UNDEFINED;
    }

    /**
     * @return 返回拓展的版本，如果未提供则不进行获取更新操作
     */
    public String getVersion() {
        return UNDEFINED;
    }

    /**
     * @return 返回拓展名称，如果未提供则不加载
     */
    public String getName() {
        return "UNNAMED";
    }

}
