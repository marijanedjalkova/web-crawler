package com.nedyalkova.crawler.impl;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Responsible for retrieving raw HTML content from a given URL. Uses {@link org.jsoup.Jsoup} for
 * network requests and HTML retrieval.
 */
public class HTMLFetcher {

  private static final Logger log = LoggerFactory.getLogger(HTMLFetcher.class);

  private static final int TIMEOUT_MS = 5000;

  /**
   * Fetches the HTML content of the given URL as a string. Uses Jsoup to connect to the URL,
   * returns HTML content for successful HTTP responses (status code 200). If the URL is blank,
   * invalid, or cannot be fetched due to a network error, this method returns {@code null}.
   *
   * @param url the URL to fetch HTML from
   * @return the HTML content as a String, or null if the URL is invalid, blank, or the HTTP
   *     response status is not 200
   * @throws IOException if an I/O error occurs during the connection (logged internally)
   */
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
