package com.modzo.foodnutrition.data;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class DataPersistence {

    private static final Logger LOG = LoggerFactory.getLogger(DataPersistence.class);

    public static void save(Folder folder, int fileId, String filePostfix, String fileType, String content) {
        save(path(folder, fileId, filePostfix, fileType), content);
    }

    public static void save(String path, String content) {
        LOG.info("Saving to file " + path );
        try {
            FileUtils.writeStringToFile(
                    new File(path), content, Charset.forName("UTF-8")
            );
        } catch (IOException e) {
            LOG.error("Error when saving", e);
        }
    }

    public static boolean exists(Folder folder, int fileId, String filePostfix, String fileType) {
        return new File(path(folder, fileId, filePostfix, fileType)).exists();
    }

    public static String load(Folder folder, int fileId, String filePostfix, String fileType) {
        File file = new File(path(folder, fileId, filePostfix, fileType));
        try {
            return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOG.error("Error when loading file", e);
            throw new RuntimeException(e.getCause());
        }
    }

    private static String path(Folder folder, int fileId, String filePostfix, String fileType) {
        return "/home" + File.separator + "modestas" + File.separator + folder.name() + File.separator + filePostfix + File.separator + fileId + "-" + filePostfix + "." + fileType;
    }

    public enum Folder {
        NUTRITION_DATA("NUTRITION_DATA");

        private final String folder;

        Folder(String folder) {
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }
    }
}
