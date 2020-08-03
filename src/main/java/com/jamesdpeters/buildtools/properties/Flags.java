package com.jamesdpeters.buildtools.properties;

import java.util.ArrayList;
import java.util.List;

public enum Flags {
    DISABLE_CERTIFICATE_CHECK("disable-certificate-check"),
    DISABLE_JAVA_CHECK("disable-java-check"),
    DONT_UPDATE("dont-update"),
    GENERATE_SOURCE("generate-source"),
    GENERATE_DOCS("generate-docs"),
    DEV("dev"),
    COMPILE_IF_CHANGED("compile-if-changed");

    String flag;

    Flags(String flag){
        this.flag = flag;
    }

    @Override
    public String toString() {
        return flag;
    }

    public static List<String> toList(){
        List<String> list = new ArrayList<>();
        for (Flags value : values()) {
            list.add(value.toString());
        }
        return list;
    }
}
