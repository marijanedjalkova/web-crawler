package com.nedyalkova.crawler.impl;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerTest {

  @Test
  void createCrawler_whenInvalidUrl_thenException() {
    assertThrows(URISyntaxException.class, () -> new WebCrawler("invalid url"));
  }

  @Test
  void createCrawler_whenUnableToDetermineHost_thenException() {
    assertThrows(URISyntaxException.class, () -> new WebCrawler("invalidUrl"));
  }

  @Test
  void createCrawler_whenUrlValid_itIsAddedToTheQueue() throws URISyntaxException {
    WebCrawler webCrawler = new WebCrawler("https://google.com");
    assertEquals(1, webCrawler.getQueue().size());
    assertEquals("https://google.com", webCrawler.getQueue().peek());
  }

  @Test
  void crawl_whenUrlVisitedBefore_itIsSkipped() throws URISyntaxException {
    WebCrawler webCrawler = new WebCrawler("https://google.com");
    webCrawler.setVisited(Set.of("https://google.com"));

    webCrawler.crawl();

  // TODO add assertions
  }


}
