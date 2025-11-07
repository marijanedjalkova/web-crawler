package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebCrawler {
  private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);
  private final String seedHost;
  protected final Queue<URI> queue = new ConcurrentLinkedQueue<>();
  protected final Set<URI> visited = ConcurrentHashMap.newKeySet();
  private final HTMLFetcher htmlFetcher = new HTMLFetcher();
  private final LinkExtractor linkExtractor = new LinkExtractor();
  private final URLUtils urlUtils = new URLUtils();
  private int counter = 0;
  public final static int DEFAULT_MAX_PAGES = 500;
  private final int maxPages;

  public WebCrawler(String seedUrl, int maxPages) throws URISyntaxException, UrlInvalidException {
    log.debug("Creating a WebCrawler with seedUrl: {}", seedUrl);
    URI seedUri = urlUtils.normalizeUrl(seedUrl);
    if (seedUri == null || StringUtils.isBlank(seedUri.getHost())) {
      throw new URISyntaxException(seedUrl, "Unable to determine host", 0);
    }
    this.seedHost = seedUri.getHost();
    log.debug("Setting host to {}", this.seedHost);
    validateUrl(seedUri);
    queue.add(seedUri);
    log.debug("Added the seedUrl to the queue");
    this.maxPages = maxPages;
  }

  public WebCrawler(String seedUrl) throws URISyntaxException, UrlInvalidException {
    this(seedUrl, DEFAULT_MAX_PAGES);
  }

  public void crawl() {
    while (!queue.isEmpty() && counter < maxPages) {
      log.debug("QUEUE LENGTH: {}, DONE {}", queue.size(), counter);
      URI nextUrl = queue.poll();
      counter++;
      crawlUrl(nextUrl);
    }
  }

  void crawlUrl(URI nextUrl) {
    try {
      log.info("CRAWL {}", nextUrl);
      validateUrl(nextUrl);
      String html = htmlFetcher.fetchHTML(nextUrl.toString());
      Set<URI> linksFromPage = linkExtractor.extractLinks(html, nextUrl.toString());
      log.debug("Extracted links: {}", linksFromPage);
      addLinksToQueue(linksFromPage);
      visited.add(nextUrl);
    } catch (UrlInvalidException e) {
      log.error("URL {} is invalid, skipping", nextUrl);
    } catch (IOException e) {
      log.debug("Unable to connect to the url {}, {}", nextUrl, e.getMessage(), e);
    }
  }

  private void addLinksToQueue(Set<URI> linksFromPage) {
    linksFromPage.forEach(
        uri -> {
          try {
            validateUrl(uri);
            queue.add(uri);
          } catch (UrlInvalidException e) {
            log.debug("Not adding {} to the queue", uri);
          }
        });
  }

  private void validateUrl(URI uri) throws UrlInvalidException {
    try {
      urlUtils.validateScheme(uri.getScheme());
      if (!isSameDomainAsSeed(uri)) {
        throw new UrlInvalidException("Different domain");
      }
      if (!hasNotBeenVisitedBefore(uri)) {
        throw new UrlInvalidException("Visited before");
      }
    } catch (URISyntaxException e) {
      throw new UrlInvalidException("Invalid URL");
    }
  }

  boolean isSameDomainAsSeed(URI uri) {
    String host = uri.getHost();
    return StringUtils.isNotBlank(host)
        && (host.equalsIgnoreCase(this.seedHost) || host.equals("www." + this.seedHost));
  }

  private boolean hasNotBeenVisitedBefore(URI uri) throws URISyntaxException {
    return !visited.contains(uri.normalize());
    // normalize() removed ./ and ../ segments
  }

  public Queue<URI> getQueue() {
    return this.queue;
  }
}
