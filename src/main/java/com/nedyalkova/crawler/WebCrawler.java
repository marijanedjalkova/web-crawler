package com.nedyalkova.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {
    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);


    void crawl(String seedUrl){
        log.info("Crawling with url: {}", seedUrl);
    }
}
