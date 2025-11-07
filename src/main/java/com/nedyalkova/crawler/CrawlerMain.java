package com.nedyalkova.crawler;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import com.nedyalkova.crawler.impl.ConcurrentWebCrawler;
import com.nedyalkova.crawler.impl.WebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class CrawlerMain {
  private static final Logger log = LoggerFactory.getLogger(CrawlerMain.class);

  /**
   * Entry point for the Simple Web Crawler application.
   *
   * <p>This method expects a single command-line argument: the seed URL to start crawling from. It
   * initializes and runs the {@link com.nedyalkova.crawler.impl.ConcurrentWebCrawler} (can be
   * changed to {@link com.nedyalkova.crawler.impl.WebCrawler} for single-threaded approach) using
   * that seed URL and a maximum crawl limit of 1000 pages.
   *
   * <p>Behaviour:
   *
   * <ul>
   *   <li>If no arguments are provided, the program logs an error and exits.
   *   <li>If more than one argument is provided, it logs a warning and uses the first one.
   *   <li>If the provided seed URL is invalid, it logs an error and terminates gracefully.
   * </ul>
   *
   * @param args the command-line arguments; the first element should be a valid seed URL
   */
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
