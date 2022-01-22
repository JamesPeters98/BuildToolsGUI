package com.jamesdpeters.buildtools;

import javax.swing.JFileChooser;
import java.io.File;

public class BuildToolsSettings {

    private static File buildToolsFolder;
    private static File buildToolsJar;
    private static File jdkPath;

    static {
        File defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory();
        buildToolsFolder = new File(defaultFolder, "/BuildTools/");
        //noinspection ResultOfMethodCallIgnored
        buildToolsFolder.mkdirs();
        buildToolsJar = new File(buildToolsFolder, "/BuildTools.jar");

        jdkPath = new File(System.getProperty("java.home"));
    }

    public static void setBuildToolsFolder(File folder){
        BuildToolsSettings.buildToolsFolder = folder;
    }

    public static File getBuildToolsFolder() {
        return buildToolsFolder;
    }

    public static void setBuildToolsJar(File buildToolsJar) {
        BuildToolsSettings.buildToolsJar = buildToolsJar;
    }

    public static File getBuildToolsJar() {
        return buildToolsJar;
    }

    public static File getJdkPath() { return jdkPath; }
    public static void setJdkPath(File file){ jdkPath = file; }
}
