package com.ilummc.valkyrie.bukkit;

public abstract class Extension {

    public static final String UNDEFINED = "UNDEFINED";

    public void initForge() {

    }

    public void initBukkit() {

    }

    public String getUpdateUrl() {
        return UNDEFINED;
    }

    public String getVersion() {
        return UNDEFINED;
    }

    public String getName() {
        return "UNNAMED";
    }

}
