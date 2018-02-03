package com.ilummc.valkyrie.bukkit;

import java.io.File;

public class ExtensionDescription {

    private File file;
    private String main, name, version, updateUrl;

    public File getFile() {
        return file;
    }

    protected void setFile(File file) {
        this.file = file;
    }

    public String getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

}
