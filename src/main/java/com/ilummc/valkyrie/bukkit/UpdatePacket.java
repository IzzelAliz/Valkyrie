package com.ilummc.valkyrie.bukkit;

import java.util.List;

public class UpdatePacket {

    private List<UpdateObject> list;

    public List<UpdateObject> getList() {
        return list;
    }

    public static class UpdateObject {
        public String[] description;
        public String version, downloadUrl;
        public long releaseDate = 0;
    }

}
