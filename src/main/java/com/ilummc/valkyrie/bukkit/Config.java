package com.ilummc.valkyrie.bukkit;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Map;

public class Config {

    /**
     * 是否启用 Valkyrie 插件本体的自动更新检查
     */
    public static boolean enableValkyrieUpdateCheck = false;

    /**
     * 是否启用拓展的自动更新检查
     */
    public static boolean enableExtensionUpdateCheck = false;

    /**
     * 检测有更新后是否自动下载
     */
    public static boolean autoDownloadUpdates = false;

    static {
        // 自动保存并加载配置文件
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT).create();
        File configFile = new File(JavaPlugin.getPlugin(ValkyrieBukkit.class).getDataFolder(),
                "config/Valkyrie.yml");
        if (!configFile.exists())
            JavaPlugin.getPlugin(ValkyrieBukkit.class).saveResource("config/Valkyrie.yml", true);
        try {
            Map map = (Map) new Yaml().load(Files.toString(configFile, Charset.forName("utf-8")));
            Config config = gson.fromJson(gson.toJson(map), Config.class);
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(new ByteArrayInputStream(
                            gson.toJson(config).getBytes(Charset.forName("utf-8")))));
            configuration.save(configFile);
            ValkyrieBukkit.info("§a配置文件读取完毕 ...");
        } catch (IOException e) {
            ValkyrieBukkit.info("§c配置文件读取失败!");
        }
    }

}
