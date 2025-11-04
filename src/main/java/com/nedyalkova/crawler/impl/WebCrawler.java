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

  public WebCrawler(String seedUrl) throws URISyntaxException {
    log.info("Creating a WebCrawler with seedUrl: {}", seedUrl);
    this.seedUrl = seedUrl;
    this.seedHost = new URI(seedUrl).getHost();
    if (StringUtils.isBlank(seedHost)) {
      throw new URISyntaxException(seedUrl, "Unable to determine host", 0);
    }
    log.info("Setting host to {}", this.seedHost);
    queue.add(seedUrl);
    log.info("Added the seedUrl to the queue");
  }

  private final String seedUrl;
  private final String seedHost;

  private Set<String> visited = new HashSet<>();
  private final Queue<String> queue = new LinkedList<>();

  public void crawl() {
    while (!queue.isEmpty()) {
      log.info("QUEUE LENGTH: {}", queue.size());
      String nextUrl = queue.poll();
      crawlUrl(nextUrl);
    }
  }

  private void crawlUrl(String nextUrl) {
    try {
      log.info("CRAWL {}", nextUrl);
      validateUrl(nextUrl);
      String html = HTMLFetcher.fetchHTML(nextUrl);
      Set<String> linksFromPage = LinkExtractor.extractLinks(html, nextUrl);
      log.info("Extracted links: {}", linksFromPage);
      queue.addAll(linksFromPage);
    } catch (UrlInvalidException e) {
      log.error("URL is invalid, skipping");
    } catch (IOException e) {
      log.error("Unable to connect to the url {}, {}", nextUrl, e.getMessage(), e);
    }
  }

  private void validateUrl(String url) throws UrlInvalidException {
    try {
      URI uri = new URI(url);
      if (!(hasNotBeenVisitedBefore(uri) && isSameDomainAsSeed(uri))) {
        throw new UrlInvalidException();
      }
    } catch (URISyntaxException e) {
      throw new UrlInvalidException();
    }
  }

  private boolean isSameDomainAsSeed(URI uri) {
    String host = uri.getHost();
    return StringUtils.isNotBlank(host) && host.equalsIgnoreCase(this.seedHost);
  }

  private boolean hasNotBeenVisitedBefore(URI uri) throws URISyntaxException {
    return !visited.contains(getNormalisedURI(uri).toString());
  }

  private URI getNormalisedURI(URI uri) {
    return uri.normalize();
  }

  public Queue<String> getQueue() {
    return this.queue;
  }

  public void setVisited(Set<String> visited) {
    this.visited = visited;
  }

  public Set<String> getVisited() {
    return visited;
  }
}
