package com.jamesdpeters.buildtools.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionUtil {

    public static ObjectMapper objectMapper = new ObjectMapper();

    private static final String url = "https://hub.spigotmc.org/versions/";

    public static Map<String, Version> getVersions() throws IOException {
        List<String> list = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        for (Element file : doc.select("a")) {
            String fileName = file.attr("href");
            if(fileName.contains(".json") && !fileName.contains("pre")){
                fileName = fileName.replace(".json","");
                if(fileName.contains(".")){
                    list.add(fileName);
                }
            }
        }
        list.add("latest");
//        list.sort(versionComparator);

        return list.parallelStream().map( version -> {
            try {
                var v = objectMapper.readValue(new URL(url + version + ".json"), Version.class);
                v.minecraftVersion = version;
                return v;
            } catch (IOException e) {
                e.printStackTrace();
            }
                    return null;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(version -> version.minecraftVersion, version -> version));
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

    public static final Comparator<String> versionComparator = (first, second) -> {
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
