package com.ilummc.valkyrie.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * How to code an extension
 * <p>
 * 1. Make your main class extends this class
 * <p>
 * 2. Create a ext.json in the root path of the jar file with things below:
 * <ol>
 * <li>main is the main class</li>
 * <li>name is the name of the extension</li>
 * <li>version is the version of the extension</li>
 * <li>updateUrl is the update url of the extension</li>
 * </ol>
 * <p>
 * UpdateUrl is an optional value, the format should be like as https://raw.githubusercontent.com/IzzelAliz/Valkyrie/1.3/version.json
 */
public abstract class Extension {

    private ExtensionDescription description;

    /**
     * Initialize as Forge platform extension
     */
    public void initForge() {

    }

    /**
     * Initialize as Bukkit platform extension
     */
    public void initBukkit() {

    }

    /**
     * Get the extension's description file ext.json
     *
     * @return description file
     */
    public final ExtensionDescription getDescription() {
        return description;
    }

    protected final void setDescription(ExtensionDescription description) {
        this.description = description;
    }

    /**
     * Get the default configuration file
     *
     * @return config file
     */
    public final File getDefaultConfig() {
        File file = new File(JavaPlugin.getPlugin(ValkyrieBukkit.class).getDataFolder(),
                "/config/" + getDescription().getName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
