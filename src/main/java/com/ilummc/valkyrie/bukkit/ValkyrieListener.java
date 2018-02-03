package com.ilummc.valkyrie.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pw.yumc.Yum.events.PluginNetworkEvent;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * XD XD
 *
 * @author 754503921
 */
class ValkyrieListener implements Listener {

    public static void init() {
        if (Bukkit.getPluginManager().isPluginEnabled("Yum"))
            Bukkit.getPluginManager().registerEvents(new ValkyrieListener(), JavaPlugin.getPlugin(ValkyrieBukkit.class));
    }

    /**
     * XD XD
     * <p>
     * <s>Sorry</s>
     *
     * @param event Yum plugin event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onNetworkEvent(PluginNetworkEvent event) {
        if (event.getPlugin() != null && event.getPlugin().getName().equals("Valkyrie")) {
            try {
                event.setUrl(new URI("http://yumc.502647092.citycraft"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
