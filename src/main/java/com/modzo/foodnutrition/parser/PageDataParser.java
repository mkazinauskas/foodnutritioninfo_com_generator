package com.modzo.foodnutrition.parser;

import com.google.common.collect.ImmutableMap;
import com.modzo.foodnutrition.CrawledContent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class PageDataParser {

    private final Map<CrawledContent.Site, Parser> parsers;

    PageDataParser() {
        this.parsers = ImmutableMap.<CrawledContent.Site, Parser>builder()
                .put(CrawledContent.Site.NUTRITION_DATA, new NutritionDataSiteParser())
                .build();
    }

    public Optional<PageData> parse(CrawledContent crawledContent) {
        if (crawledContent.getRaw().contains("obsolete item - formerly called ")) {
            return Optional.empty();
        }
        if (PageData.exists(crawledContent.getPage())) {
            return Optional.of(PageData.load(crawledContent.getPage()));
        }

        Parser parser = parsers.get(crawledContent.getSite());
        PageData result = parser.parse(crawledContent.getPage(), crawledContent.getRaw());
        result.save(crawledContent.getPage());
        return Optional.of(result);
    }
}
