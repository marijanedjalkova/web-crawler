package com.nedyalkova.crawler;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import com.nedyalkova.crawler.impl.ConcurrentWebCrawler;
import com.nedyalkova.crawler.impl.WebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class CrawlerMain {
  private static final Logger log = LoggerFactory.getLogger(CrawlerMain.class);


  public static void main(String[] args) {

    log.info("Running the web crawler. It expects the seed domain as a command argument.");
    if (args.length == 0) {
      log.error("Expected 1 command argument, nothing to crawl.");
      return;
    }

    if (args.length != 1) {
      log.warn(
          "Expected 1 command argument, but got {}, will pick the first one : {}",
          args.length,
          args[0]);
    }
    String seedUrl = args[0];
    log.debug("Crawling with seed domain: {}", seedUrl);
      try {
          new ConcurrentWebCrawler(seedUrl, 1000).crawl();
      } catch (URISyntaxException | UrlInvalidException e) {
          log.error("Invalid host: {}", seedUrl);
      }
  }
}
