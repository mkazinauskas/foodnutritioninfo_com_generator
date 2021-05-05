package com.modzo.foodnutrition;

import java.util.List;
import java.util.Optional;

public interface Crawlable {
    List<CrawledContent> crawlPages(int start, int end);

    Optional<CrawledContent> crawl(int page);
}
