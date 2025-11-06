package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WebCrawler {
  private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

  public WebCrawler(String seedUrl) throws URISyntaxException, UrlInvalidException {
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
  }

  private final String seedHost;

  private final Set<URI> visited = new HashSet<>();
  private final Queue<URI> queue = new LinkedList<>();

  private final HTMLFetcher htmlFetcher = new HTMLFetcher();
  private final LinkExtractor linkExtractor = new LinkExtractor();
  private final URLUtils urlUtils = new URLUtils();
  int counter = 0;

  public void crawl() {
    while (!queue.isEmpty()) {
      log.debug("QUEUE LENGTH: {}, DONE {}", queue.size(), counter);
      URI nextUrl = queue.poll();
      counter++;
      crawlUrl(nextUrl);
    }
  }

  private void crawlUrl(URI nextUrl) {
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
