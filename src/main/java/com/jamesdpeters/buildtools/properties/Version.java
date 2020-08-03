package com.jamesdpeters.buildtools.properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

    public static List<String> getVersions() throws IOException {
        List<String> list = new ArrayList<>();
        Document doc = Jsoup.connect("https://hub.spigotmc.org/versions/").get();
        for (Element file : doc.select("a")) {
            String fileName = file.attr("href");
            if(fileName.contains(".json") && !fileName.contains("pre")){
                fileName = fileName.replace(".json","");
                if(fileName.contains(".")){
                    list.add(fileName);
                }
            }
        }
        list.sort(versionComparator);
        return list;
    }

    public static void init(){

    }

    /*
        Choco's UpdateChecker methods.
    */
    private static final Pattern DECIMAL_SCHEME_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)*");

    private static String[] splitVersionInfo(String version) {
        Matcher matcher = DECIMAL_SCHEME_PATTERN.matcher(version);
        if (!matcher.find()) return null;

        return matcher.group().split("\\.");
    }

    private static final Comparator<String> versionComparator = (first, second) -> {
        String[] firstSplit = splitVersionInfo(first), secondSplit = splitVersionInfo(second);
        if (firstSplit == null || secondSplit == null) return 0;

        for (int i = 0; i < Math.min(firstSplit.length, secondSplit.length); i++) {
            Integer firstVal = NumberUtils.toInt(firstSplit[i]), secondVal = NumberUtils.toInt(secondSplit[i]);

            if(!firstVal.equals(secondVal)){
                return -firstVal.compareTo(secondVal);
            }
        }
        if(firstSplit.length > secondSplit.length) return -1;
        else if(firstSplit.length < secondSplit.length) return 1;
        return 0;
    };
}
