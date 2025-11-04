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
    this.seedHost = new URI(seedUrl).getHost();
    if (StringUtils.isBlank(seedHost)) {
      throw new URISyntaxException(seedUrl, "Unable to determine host", 0);
    }
    log.info("Setting host to {}", this.seedHost);
    queue.add(seedUrl);
    log.info("Added the seedUrl to the queue");
  }

  private final String seedHost;

  private Set<String> visited = new HashSet<>();
  private final Queue<String> queue = new LinkedList<>();

  private final HTMLFetcher htmlFetcher = new HTMLFetcher();
  private final LinkExtractor linkExtractor = new LinkExtractor();
  int counter = 0;

  public void crawl() {
    while (!queue.isEmpty()) {
      log.debug("QUEUE LENGTH: {}, DONE {}", queue.size(), counter);
      String nextUrl = queue.poll();
      counter++;
      crawlUrl(nextUrl);
    }
  }

  private void crawlUrl(String nextUrl) {
    try {
      log.info("CRAWL {}", nextUrl);
      validateUrl(nextUrl);
      String html = htmlFetcher.fetchHTML(nextUrl);
      Set<String> linksFromPage = linkExtractor.extractLinks(html, nextUrl);
      log.debug("Extracted links: {}", linksFromPage);
      addLinksToQueue(linksFromPage);
      visited.add(nextUrl);
    } catch (UrlInvalidException e) {
      log.error("URL {} is invalid, skipping", nextUrl);
    } catch (IOException e) {
      log.debug("Unable to connect to the url {}, {}", nextUrl, e.getMessage(), e);
    }
  }

  private void addLinksToQueue(Set<String> linksFromPage) {
    linksFromPage.forEach(
        link -> {
          try {
            validateUrl(link);
            queue.add(link);
          } catch (UrlInvalidException e) {
            log.debug("Not adding {} to the queue", link);
          }
        });
  }

  private void validateUrl(String url) throws UrlInvalidException {
    try {
      URI uri = new URI(url);
      String scheme = uri.getScheme();

      if (!(scheme != null && Set.of("http", "https").contains(scheme.toLowerCase()))) {
        throw new UrlInvalidException("Unexpected protocol");
      }
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

  private boolean isSameDomainAsSeed(URI uri) {
    String host = uri.getHost();
    return StringUtils.isNotBlank(host)
        && (host.equalsIgnoreCase(this.seedHost) || host.endsWith("." + this.seedHost));
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
