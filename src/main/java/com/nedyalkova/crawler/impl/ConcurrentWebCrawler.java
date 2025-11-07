package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A concurrent, single-domain web crawler that extends {@link WebCrawler}.
 *
 * <p>This crawler uses multiple worker threads to process URLs in parallel, improving crawling
 * performance for larger sites. It maintains a thread-safe queue of URLs to visit and a set of
 * visited URLs.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Concurrent URL processing using a fixed-size thread pool.
 *   <li>Thread-safe queue and visited set using {@link java.util.concurrent.ConcurrentLinkedQueue}
 *       and {@link java.util.concurrent.ConcurrentHashMap}.
 *   <li>Respects a maximum page limit to prevent infinite crawling.
 *   <li>Gracefully stops when the queue is empty or the maximum page limit is reached.
 * </ul>
 *
 * <p>This class demonstrates how to safely parallelize a web crawler while avoiding race conditions
 * on shared data structures.
 */
public class ConcurrentWebCrawler extends WebCrawler {
  private static final Logger log = LoggerFactory.getLogger(ConcurrentWebCrawler.class);

  private static final int MAX_THREADS = 5;
  private static final int TIMEOUT = 15;
  private final AtomicInteger counter = new AtomicInteger(0);
  private final int maxPages;

  private final ExecutorService executor;

  public ConcurrentWebCrawler(String seedUrl, int maxPages)
      throws URISyntaxException, UrlInvalidException {
    super(seedUrl, maxPages);
    this.maxPages = maxPages;
    this.executor = Executors.newFixedThreadPool(MAX_THREADS);
  }

  /**
   * Starts the concurrent crawling process.
   *
   * <p>Submits multiple worker threads to process the shared URL queue in parallel. Crawling stops
   * when either the queue is empty or the maximum page limit is reached.
   *
   * <p>This method blocks until all worker threads finish or the timeout expires.
   */
  @Override
  public void crawl() {
    for (int i = 0; i < MAX_THREADS; i++) {
      executor.submit(this::processQueue);
    }
    executor.shutdown();
    try {
      executor.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void processQueue() {
    while (!queue.isEmpty()) {
      log.debug("QUEUE LENGTH: {}", queue.size());
      URI nextUrl = queue.poll();
      if (nextUrl == null) continue;
      int currentCount = counter.incrementAndGet();
      if (currentCount > maxPages) {
        break;
      }
      crawlUrl(nextUrl);
    }
  }
}
