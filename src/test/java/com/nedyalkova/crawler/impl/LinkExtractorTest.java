package com.nedyalkova.crawler.impl;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkExtractorTest {

  private final LinkExtractor linkExtractor = new LinkExtractor();

  @Test
  void extractLinks_whenEmptyHTML_thenEmptyResult() {
    Set<URI> links = linkExtractor.extractLinks("", "baseUrl");
    assertNotNull(links);
    assertTrue(links.isEmpty());
  }

  @Test
  void extractLinks_whenHTMLWithoutLinks_thenEmptyResult() {
    String html = "<html><body><p>No links here</p></body></html>";
    Set<URI> links = linkExtractor.extractLinks(html, "baseUrl");
    assertTrue(links.isEmpty());
  }

  @Test
  void extractLinks_whenAbsoluteLinks_thenTheyAreReturned() {
    String html = "<a href='https://webcrawler.com/page1'>Page1</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(1, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page1")));
  }

  @Test
  void extractLinks_whenARelativeLink_thenItIsConvertedToAbsolute() {
    String html = "<a href='/page2'>Page2</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(1, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page2")));
  }

  @Test
  void extractLinks_whenFragmentsExist_thenTheyShouldBeRemoved() {
    String html = "<a href='https://webcrawler.com/page#section1'>Page</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(1, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page"))); // fragment removed
  }

  @Test
  void extractLinks_whenNonHttpLink_thenItIsSkipped() {
    String html =
        "<a href='mailto:test@webcrawler.com'>Email</a>"
            + "<a href='javascript:void(0)'>JS Link</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(0, links.size());
  }

  @Test
  void extractLinks_whenDuplicateLinks_thenOnly1Returned() {
    String html =
        "<a href='/page1'>Page1</a>" + "<a href='https://webcrawler.com/page1'>Page1 Duplicate</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(1, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page1")));
  }

  @Test
  void extractLinks_whenWhiteSpace_itIsTrimmed() {
    String html = "<a href=' https://webcrawler.com/page3 '>Page3</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(1, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page3")));
  }

  @Test
  void extractLinks_whenMultipleLinks_allAreReturned() {
    String html = "<a href='/page1'>1</a><a href='/page2'>2</a>";
    Set<URI> links = linkExtractor.extractLinks(html, "https://webcrawler.com");
    assertEquals(2, links.size());
    assertTrue(links.contains(URI.create("https://webcrawler.com/page1")));
    assertTrue(links.contains(URI.create("https://webcrawler.com/page2")));
  }
}
