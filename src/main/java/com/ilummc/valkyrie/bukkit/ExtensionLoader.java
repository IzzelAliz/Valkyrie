package com.ilummc.valkyrie.bukkit;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static com.ilummc.valkyrie.bukkit.ValkyrieBukkit.info;

public class ExtensionLoader {
    // 自动更新检测线程池
    public static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(32);

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
            List<URL> urls = new ArrayList<>();
            Map<File, List<String>> classes = new HashMap<>();
            // 检测所有目录下的 .jar 文件
            for (File extension : Objects.requireNonNull(extensionFolder.listFiles(
                    file -> file.getName().endsWith(".jar") || file.getName().endsWith(".zip")))) {
                try {
                    ZipFile zipFile = new ZipFile(extension);
                    List<String> classNames = new ArrayList<>();
                    // 存储所有 class 名称
                    for (FileHeader fileHeader : ((List<FileHeader>) zipFile.getFileHeaders())) {
                        if (fileHeader.getFileName().endsWith(".class")) {
                            String className = fileHeader.getFileName();
                            className = className.substring(0, className.length() - 6)
                                    .replace('/', '.');
                            classNames.add(className);
                        }
                    }
                    // 判断此 jar 文件中是否含有 .class 文件
                    if (classNames.isEmpty()) {
                        info("§c文件 " + extension.getName() + " 不是一个有效的 Valkyrie 拓展!");
                    } else {
                        // 标记为拓展
                        urls.add(new URL("file://" + URLEncoder.encode(extension.getAbsolutePath(), "utf-8")));
                        classes.put(extension, classNames);
                    }
                } catch (Exception e) {
                    info("§c文件 " + extension.getName() + " 读取失败或不是一个有效的 Valkyrie 拓展!");
                }
            }
            // 类加载器
            classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ValkyrieBukkit.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            // 加载所有拓展
            classes.forEach(ExtensionLoader::loadSingleExtension);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static void loadSingleExtension(File file, List<String> classes) {
        boolean load = false;
        for (String className : classes) {
            try {
                // 加载类并实例化
                Class clazz = classLoader.loadClass(className);
                Class<Extension> extensionClass = clazz.asSubclass(Extension.class);
                Extension extension = extensionClass.newInstance();
                // 初始化为 Bukkit 拓展
                extension.initBukkit();
                info("§a成功加载了 " + extension.getName() + " 拓展 ...");
                // 检测拓展更新
                if (!Config.enableExtensionUpdateCheck)
                    if (Extension.UNDEFINED.equals(extension.getUpdateUrl()))
                        info("§e拓展 " + extension.getName() + " 沒有提供自动更新地址 ...");
                    else futures.add(pool.submit(() -> Util.get(extension.getUpdateUrl()).orElse("[]")));
                ValkyrieBukkit.addExtension(extension);
                load = true;
                break;
            } catch (Exception e) {
                info("§c文件 " + file.getName() + " 读取失败或不是一个有效的 Valkyrie 拓展!");
            }
        }
        if (!load) {
            info("§c文件 " + file.getName() + " 不是一个有效的 Valkyrie 拓展：没有主类!");
        }
    }
}
