package com.jamesdpeters.buildtools.jdk;

import com.jamesdpeters.buildtools.BuildToolsSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdkChecker {

    private static final Pattern pattern = Pattern.compile("(?<=#define JVM_CLASSFILE_MAJOR_VERSION )\\d+");

    public static Integer getJdkVersion(File jdkPath) {
        File classConstants = new File(jdkPath, "/include/classfile_constants.h");
        if (classConstants.exists()) {
            try {
                var data = Files.readString(classConstants.toPath());
                Matcher matcher = pattern.matcher(data);
                if(matcher.find()) {
                    String version = matcher.group(0);
                    return Integer.parseInt(version);
                }
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
