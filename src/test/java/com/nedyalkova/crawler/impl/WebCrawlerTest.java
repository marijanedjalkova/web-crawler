package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.junit.jupiter.api.Test;

import java.net.URI;
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
  void createCrawler_whenUrlValid_itIsAddedToTheQueue() throws URISyntaxException, UrlInvalidException {
    WebCrawler webCrawler = new WebCrawler("https://google.com");
    assertEquals(1, webCrawler.getQueue().size());
    URI fromTheQueue = webCrawler.getQueue().peek();
    assertNotNull(fromTheQueue);
    assertEquals("https://google.com", fromTheQueue.toString());
  }

  @Test
  void isSameDomainAsSeed_whenDifferentFirstPart_thenFalse() throws UrlInvalidException, URISyntaxException {
    WebCrawler crawler = new WebCrawler("https://google.com");
    assertFalse(crawler.isSameDomainAsSeed(new URI("abc.google.com")));
    assertFalse(crawler.isSameDomainAsSeed(new URI("facebook.com")));
  }


}
