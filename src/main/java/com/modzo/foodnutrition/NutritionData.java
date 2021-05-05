package com.modzo.foodnutrition;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class NutritionData implements Crawlable {

    private static final Logger LOG = LoggerFactory.getLogger(NutritionData.class);

    private static final String URLTemplate = "https://nutritiondata.self.com/facts/fruits-and-fruit-juices/{page}/0"; //per 100 gram

    private final ContentReader contentReader;

    public NutritionData(ContentReader contentReader) {
        this.contentReader = contentReader;
    }

    @Override
    public List<CrawledContent> crawlPages(int start, int end) {
        return IntStream.rangeClosed(start, end)
                .mapToObj(this::crawl)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CrawledContent> crawl(int page) {
        if (CrawledContent.exists(CrawledContent.Site.NUTRITION_DATA, page)) {
            LOG.info("Content is already saved " + page);
            return Optional.of(CrawledContent.load(CrawledContent.Site.NUTRITION_DATA, page));
        }
        String url = URLTemplate.replace("{page}", String.valueOf(page));
        return contentReader.read(url)
                .map(PageContent::getBody)
                .map(Jsoup::parse)
                .map(data -> new CrawledContent(CrawledContent.Site.NUTRITION_DATA, page, data.toString()))
                .map(this::save);
    }

    private CrawledContent save(CrawledContent crawledContent) {
        crawledContent.save();
        return crawledContent;
    }
}