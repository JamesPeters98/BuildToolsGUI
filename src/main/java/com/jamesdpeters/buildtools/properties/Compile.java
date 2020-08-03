package com.jamesdpeters.buildtools.properties;

import java.util.ArrayList;
import java.util.List;

public enum Compile {
    NONE("None"),
    CRAFTBUKKIT("CraftBukkit"),
    SPIGOT("Spigot");

    String value;

    Compile(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static List<String> toList(){
        List<String> list = new ArrayList<>();
        for (Compile value : values()) {
            list.add(value.toString());
        }
        return list;
    }
}
