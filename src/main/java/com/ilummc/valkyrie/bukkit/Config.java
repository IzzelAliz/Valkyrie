package com.ilummc.valkyrie.bukkit;

import com.google.common.io.Files;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Config {

    /**
     * Enable the Valkyrie update check
     */
    public static boolean enableValkyrieUpdateCheck = false;

    /**
     * Enable the extension update check
     */
    public static boolean enableExtensionUpdateCheck = false;

    static {
        // 自动保存并加载配置文件
        File configFile = new File(JavaPlugin.getPlugin(ValkyrieBukkit.class).getDataFolder(),
                "/config/Valkyrie.yml");
        if (!configFile.exists())
            JavaPlugin.getPlugin(ValkyrieBukkit.class).saveResource("config/Valkyrie.yml", true);
        try {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.loadFromString(Files.toString(configFile, Charset.forName("utf-8")));
            enableValkyrieUpdateCheck = configuration.getBoolean("enableValkyrieUpdateCheck", false);
            enableExtensionUpdateCheck = configuration.getBoolean("enableExtensionUpdateCheck", false);
            Files.write(configuration.saveToString().getBytes(), configFile);
            ValkyrieBukkit.info("§a配置文件读取完毕 ...");
        } catch (IOException | InvalidConfigurationException e) {
            ValkyrieBukkit.info("§c配置文件读取失败!");
        }
    }

}
