package com.ilummc.valkyrie.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Util {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String s, Class<T> clazz) {
        return gson.fromJson(s, clazz);
    }

    public static Optional<String> get(String var1) {
        try {
            URL url = new URL(var1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = connection.getInputStream().read(buf)) != -1)
                stream.write(buf, 0, len);
            connection.getInputStream().close();
            return Optional.ofNullable(stream.toString("utf-8"));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
