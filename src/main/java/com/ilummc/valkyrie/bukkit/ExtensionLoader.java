package com.ilummc.valkyrie.bukkit;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static com.ilummc.valkyrie.bukkit.ValkyrieBukkit.info;

class ExtensionLoader {
    // 自动更新检测线程池
    static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(32);

    private static URLClassLoader classLoader;

    private static transient List<Future<String>> futures = new ArrayList<>();

    @SuppressWarnings({"unchecked"})
    public static void load() {
        // 检测是否关闭了自动更新检测
        if (!Config.enableExtensionUpdateCheck)
            info("§c拓展的更新检测被关闭，如果有需要请在 /config/Valkyrie.yml 中启用");
        // 拓展文件夹
        File extensionFolder = new File(JavaPlugin.getPlugin(ValkyrieBukkit.class).getDataFolder(), "/extensions");
        if (!extensionFolder.exists()) extensionFolder.mkdir();
        else {
            ClassLoader prev = Thread.currentThread().getContextClassLoader();
            Map<File, ExtensionDescription> extensions = new HashMap<>();
            List<URL> urls = new ArrayList<>();
            // 检测所有目录下的 .jar 文件
            for (File extension : Objects.requireNonNull(extensionFolder.listFiles(
                    file -> file.getName().endsWith(".jar") || file.getName().endsWith(".zip")))) {
                try {
                    ZipFile zipFile = new ZipFile(extension);
                    // 读取 ext.json
                    FileHeader header = zipFile.getFileHeader("ext.json");
                    ExtensionDescription description = Util.fromJson(Util.toString(zipFile.getInputStream(header),
                            "utf-8"), ExtensionDescription.class);
                    description.setFile(extension);
                    extensions.put(extension, description);
                    // 添加到类加载器的 URL List
                    urls.add(new URL("file://" + URLEncoder.encode(extension.getAbsolutePath(), "utf-8")));
                } catch (Exception e) {
                    info("§c文件 " + extension.getName() + " 读取失败或不是一个有效的 Valkyrie 拓展!");
                }
            }
            // 类加载器
            classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ValkyrieBukkit.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            // 加载所有拓展
            extensions.forEach(ExtensionLoader::loadSingleExtension);
            Thread.currentThread().setContextClassLoader(prev);
            // 输出拓展更新信息
            if (Config.enableExtensionUpdateCheck) {
                info("§a拓展更新检测已启动，正在检测 ...");
                for (int i = 0; i < futures.size(); i++) {
                    try {
                        String json = futures.get(i).get();
                        UpdatePacket[] packet = Util.fromJson(json, UpdatePacket[].class);
                        ExtensionDescription description = ValkyrieBukkit.extensions.get(i).getDescription();
                        if (packet.length != 0 && !description.getVersion().equalsIgnoreCase(packet[0].version)) {
                            info("§b拓展 " + description.getName() + " 有新的更新 ...");
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
                            info("§b拓展 " + description.getName() + " 没有更新 ...");
                        }
                    } catch (Exception e) {
                        info("§c获取拓展 " + ValkyrieBukkit.extensions.get(i).getDescription().getName() + " 更新数据失败：" + e.toString());
                    }
                }
            }
        }
    }


    @SuppressWarnings({"unchecked"})
    private static void loadSingleExtension(File file, ExtensionDescription description) {
        try {
            // 加载类并实例化
            Class clazz = classLoader.loadClass(description.getMain());
            Class<Extension> extensionClass = clazz.asSubclass(Extension.class);
            Extension extension = extensionClass.newInstance();
            // 初始化为 Bukkit 拓展
            extension.initBukkit();
            info("§a成功加载了 " + description.getName() + " 拓展 ...");
            // 检测拓展更新
            if (!Config.enableExtensionUpdateCheck)
                if (description.getUpdateUrl() == null)
                    info("§e拓展 " + description.getName() + " 沒有提供自动更新地址 ...");
                else futures.add(pool.submit(() -> Util.get(description.getUpdateUrl()).orElse("[]")));
            extension.setDescription(description);
            ValkyrieBukkit.addExtension(extension);
        } catch (Exception e) {
            info("§c文件 " + file.getName() + " 读取失败或不是一个有效的 Valkyrie 拓展!");
        }
    }
}
