package com.nedyalkova.crawler.impl;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HTMLFetcher {

  private static final Logger log = LoggerFactory.getLogger(HTMLFetcher.class);

  private static final int TIMEOUT_MS = 5000;

  public String fetchHTML(String url) throws IOException {
    if (StringUtils.isBlank(url)) {
      return null;
    }
    url = url.trim();
    try {
      Document doc = Jsoup.connect(url).userAgent("SimpleWebCrawler/1.0").timeout(TIMEOUT_MS).get();
      int statusCode = doc.connection().response().statusCode();
      if (statusCode != 200) { // this can be expanded
        log.debug("{} returned status HTTP {}", url, statusCode);
        return null;
      }
      return doc.html();
    } catch (IOException e) {
      log.error("Failed to fetch {}", url, e);
      return null;
    }
  }
}
