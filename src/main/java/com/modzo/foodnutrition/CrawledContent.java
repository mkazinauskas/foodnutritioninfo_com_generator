package com.modzo.foodnutrition;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class CrawledContent {

    private static final Logger LOG = LoggerFactory.getLogger(CrawledContent.class);

    public enum Site {
        NUTRITION_DATA("NUTRITION_DATA");

        private final String folder;

        Site(String folder) {
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }
    }

    private final Site site;

    private final int page;

    private final String raw;

    public CrawledContent(Site site, int page, String raw) {
        this.site = site;
        this.page = page;
        this.raw = raw;
    }

    public Site getSite() {
        return site;
    }

    public int getPage() {
        return page;
    }

    public String getRaw() {
        return raw;
    }


    public static boolean exists(Site site, int pageNumber) {
        return new File(rawUrl(site, pageNumber)).exists();
    }

    public void save() {
        LOG.info("Saving to file " + site.name() + " " + page);
        try {
            FileUtils.writeStringToFile(new File(rawUrl(site, page)), raw, Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOG.error("Error when saving", e);
        }
    }

    private static String rawUrl(Site site, int page) {
        return "/home" + File.separator + "modestas" + File.separator + site.getFolder() + File.separator  + page + "-raw.html";
    }

    public static CrawledContent load(Site site, int page) {
        File file = new File(rawUrl(site, page));
        try {
            return new CrawledContent(site, page, new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8")));
        } catch (IOException e) {
            LOG.error("Error when loading file", e);
            throw new RuntimeException(e.getCause());
        }
    }
}
