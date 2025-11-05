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
    log.info("Creating a WebCrawler with seedUrl: {}", seedUrl);
    URI seedUri = new URI(seedUrl);
    this.seedHost = seedUri.getHost();
    if (StringUtils.isBlank(seedHost)) {
      throw new URISyntaxException(seedUrl, "Unable to determine host", 0);
    }
    validateUrl(seedUrl);
    log.info("Setting host to {}", this.seedHost);
    queue.add(seedUri);
    log.info("Added the seedUrl to the queue");
  }

  private final String seedHost;

  private Set<URI> visited = new HashSet<>();
  private final Queue<URI> queue = new LinkedList<>();

  private final HTMLFetcher htmlFetcher = new HTMLFetcher();
  private final LinkExtractor linkExtractor = new LinkExtractor();
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
      Set<String> linksFromPage = linkExtractor.extractLinks(html, nextUrl.toString());
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
            URI uri = normalizeUrl(link);
            queue.add(uri);
          } catch (UrlInvalidException e) {
            log.debug("Not adding {} to the queue", link);
          }
        });
  }

  private void validateUrl(URI uri) throws UrlInvalidException {
    try {
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

  private void validateUrl(String url) throws UrlInvalidException {
    try {
      URI uri = new URI(url);
      validateUrl(uri);
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
    return !visited.contains(getNormalisedURI(uri));
  }

  private URI getNormalisedURI(URI uri) {
    return uri.normalize();
  }

  private URI normalizeUrl(String rawUrl) {
    try {
      URI uri = new URI(rawUrl.trim()).normalize();

        return new URI(
              uri.getScheme() != null ? uri.getScheme().toLowerCase() : "http",
              uri.getAuthority() != null ? uri.getAuthority().toLowerCase() : null,
              (uri.getPath() == null || uri.getPath().isEmpty()) ? "/" : uri.getPath(),
              uri.getQuery(),
              null // remove fragment
      );
    } catch (URISyntaxException e) {
      return null;
    }
  }


  public Queue<URI> getQueue() {
    return this.queue;
  }

  public void setVisited(Set<URI> visited) {
    this.visited = visited;
  }

}
