package com.ilummc.valkyrie.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ValkyrieBukkit extends JavaPlugin {

    // 拓展容器
    public static final List<Extension> extensions = new ArrayList<>();

    public static void main(String[] args) {
        System.out.print(System.currentTimeMillis());
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
        info("§3#########################################################");
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
        info("                                    #       §av 1.3       ");
        info("                                ####                    ");
        info("                                                        ");
        info("§3#########################################################");
        info("");
        checkUpdate();
        ExtensionLoader.load();
        info("加载完成于 " + (System.currentTimeMillis() - time) + " 毫秒");
        info("");
        info("§3#########################################################");
    }

    private void checkUpdate() {
        if (!Config.enableValkyrieUpdateCheck)
            info("§cValkyrie 的更新检测被关闭，如果有需要请在 /config/Valkyrie.yml 中启用");
        else {
            try {
                String json = ExtensionLoader.pool.submit(() -> Util.get("https://raw.githubusercontent.com/IzzelAliz/Valkyrie/1.3/version.json")
                        .orElse("[]")).get();
                UpdatePacket packet = Util.fromJson(json, UpdatePacket.class);
                if (!packet.getList().isEmpty() && !this.getDescription().getVersion().equalsIgnoreCase(packet.getList().get(0).version)) {
                    UpdatePacket.UpdateObject object = packet.getList().get(0);
                    info("§bValkyrie 有新的更新 ...");
                    info("§b版本：" + object.version + " ，发布于 " +
                            new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss").format(new Date()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
