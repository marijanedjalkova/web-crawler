package com.nedyalkova.crawler.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HTMLFetcher {

  private static final Logger log = LoggerFactory.getLogger(HTMLFetcher.class);

  private static final int TIMEOUT_MS = 5000;

  public static String fetchHTML(String url) throws IOException {
    log.info("Fetching html from url {}", url);
    Document doc =
        Jsoup.connect(url)
            .userAgent("SimpleWebCrawler/1.0")
            .timeout(TIMEOUT_MS)
            .get();
    return doc.html();
  }
}
