package com.ilummc.valkyrie.bukkit;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ValkyrieBukkit extends JavaPlugin {

    // 自动更新检测线程池
    public static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(32);
    // 拓展容器
    public static final List<Extension> extensions = new ArrayList<>();

    public static void main(String[] args) {
        File file = new File("E:\\commons-codec-1.10.jar");
        try {
            ZipFile zipFile = new ZipFile(file);
            for (Object o : zipFile.getFileHeaders()) {
                FileHeader fileHeader = (FileHeader) o;
                String className = fileHeader.getFileName();
                if (className.endsWith(".class")) {
                    className = className.substring(0, className.length() - 6)
                            .replace('/', '.');
                    System.out.println(className);
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void info(String msg) {
        Bukkit.getConsoleSender().sendMessage("§3#§6 " + msg);
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
        loadExtensions();
        info("加载完成于 " + (System.currentTimeMillis() - time) + " 毫秒");
        info("");
        info("§3#########################################################");
    }

    public void checkUpdate() {
        if (!Config.enableValkyrieUpdateCheck)
            info("§cValkyrie 的更新检测被关闭，如果有需要请在 /config/Valkyrie.yml 中启用");
        else {
            try {
                info(pool.submit(() -> {
                    try {
                        URL url = new URL("");
                    } catch (Exception ignored) {
                    }
                    return "更新检测失败";
                }).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    public void loadExtensions() {
        // 检测是否关闭了自动更新检测
        if (!Config.enableExtensionUpdateCheck)
            info("§c拓展的更新检测被关闭，如果有需要请在 /config/Valkyrie.yml 中启用");
        // 拓展文件夹
        File extensionFolder = new File(this.getDataFolder(), "/extensions");
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
            URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ValkyrieBukkit.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            // 加载所有拓展
            classes.forEach((file, strings) -> {
                for (String className : strings) {
                    try {
                        // 加载类
                        Class clazz = classLoader.loadClass(className);
                        Class<Extension> extensionClass = clazz.asSubclass(Extension.class);
                        Extension extension = extensionClass.newInstance();
                        // 初始化为 Bukkit 拓展
                        extension.initBukkit();
                        info("§a成功加载了 " + extension.getName() + " 拓展 ...");
                    } catch (Exception e) {
                        info("§c文件 " + file.getName() + " 读取失败或不是一个有效的 Valkyrie 拓展!");
                    }
                }
            });
        }
    }

}
