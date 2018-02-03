package com.ilummc.valkyrie.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ValkyrieBukkit extends JavaPlugin {

    // 拓展容器
    public static final List<Extension> extensions = new ArrayList<>();

    public static void main(String[] args) {
        System.out.print(System.currentTimeMillis());
    }

    public static void line() {
        Bukkit.getConsoleSender().sendMessage("§3###########################################################");
    }

    public static void info(String msg) {
        Bukkit.getConsoleSender().sendMessage("§3#§6 " + msg);
    }

    protected static void addExtension(Extension extension) {
        extensions.add(extension);
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        line();
        info("                                                        ");
        info("#      #          #    #                                ");
        info("#      #          #    #                                ");
        info("#      #          #    #                      #         ");
        info("#      #   ###    #    #   #   #    #  # ##        #### ");
        info("#      #  #   #   #    #  #    #    #   #     #   #    #");
        info("#      #  #   #   #    # #     #    #   #     #    #### ");
        info("#      #  #   #   #    #  #    #    #   #     #   #     ");
        info(" # ## #    ### #   #   #   #    ####    #     #    #### ");
        info("                                    #                   ");
        info("     §dby 754503921§6                   #               ");
        info("                                ####                    ");
        info("                                                        ");
        ValkyrieListener.init();
        checkUpdate();
        ExtensionLoader.load();
        info("");
        info("加载完成于 " + (System.currentTimeMillis() - time) + " 毫秒");
        info("");
        line();
    }

    private void checkUpdate() {
        if (!Config.enableValkyrieUpdateCheck)
            info("§cValkyrie 的更新检测被关闭，如果有需要请在 /config/Valkyrie.yml 中启用");
        else {
            try {
                {
                    UpdatePacket[] packets = Util.fromJson(
                            Util.toString(ValkyrieBukkit.class.getResourceAsStream("/version.json"), "utf-8")
                            , UpdatePacket[].class);
                    info("§b正在使用 Valkyrie " + packets[0].version);
                }
                info("§aValkyrie 启用了更新，正在检测 ...");
                String json = ExtensionLoader.pool.submit(() ->
                        Util.get("https://raw.githubusercontent.com/IzzelAliz/Valkyrie/1.3/src/main/resources/version.json")
                                .orElse("[]")).get();
                UpdatePacket[] packet = Util.fromJson(json, UpdatePacket[].class);
                if (packet.length != 0 && !this.getDescription().getVersion().equalsIgnoreCase(packet[0].version)) {
                    info("§bValkyrie 有新的更新 ...");
                    info("§b  版本：" + packet[0].version + " ，发布于 " +
                            new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss")
                                    .format(packet[0].releaseDate));
                    if (packet[0].downloadUrl != null)
                        info("§b  访问 " + packet[0].downloadUrl + " 下载 ...");
                    else info("§c  没有提供下载地址 ...");
                    info("§b  更新内容：" + (packet[0].description.length == 0 ? "暂无" : ""));
                    for (String s : packet[0].description) {
                        info("§b    " + s);
                    }
                } else {
                    info("§bValkyrie 没有更新 ...");
                }
            } catch (Exception e) {
                info("§c获取 Valkyrie 更新失败 ...");
            }
        }
    }

}
