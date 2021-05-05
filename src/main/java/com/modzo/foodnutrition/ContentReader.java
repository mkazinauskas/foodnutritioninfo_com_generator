package com.modzo.foodnutrition;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Lazy
public class ContentReader implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(ContentReader.class);

    private final WebDriver driver;

    public ContentReader() {
        driver = new FirefoxDriver();
    }

    Optional<PageContent> read(String url) {
        Optional<PageContent> pageContent;
        try {
            driver.get(url);
            String pageSource = driver.getPageSource();

            if (pageSource.contains("<title>503 first byte timeout</title>")
                    || pageSource.contains("503 Backend unavailable")) {
                pageContent = Optional.empty();
            } else {
                pageContent = Optional.of(new PageContent(pageSource));
            }
        } catch (Exception ex) {
            LOG.error(String.format("Failed to open `%s`", url), ex);
            return Optional.empty();
        }
        if (pageContent.filter(content -> content.getBody()
                .contains("Sorry, but the URL you tried to acccess on NutritionData.com is temporarily unavailable"))
                .isPresent()) {
            throw new RuntimeException("There are no more pages to crawl. Last was " + url);
        }
        return pageContent;
    }

    @Override
    public void destroy() throws Exception {
        driver.close();
    }
}
