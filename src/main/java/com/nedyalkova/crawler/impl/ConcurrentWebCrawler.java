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

public class ConcurrentWebCrawler extends WebCrawler {
  private static final Logger log = LoggerFactory.getLogger(ConcurrentWebCrawler.class);

  private final static int MAX_THREADS = 5;
  private final static int TIMEOUT = 15;
  private final AtomicInteger counter = new AtomicInteger(0);
  private final int maxPages;


  private final ExecutorService executor;

  public ConcurrentWebCrawler(String seedUrl, int maxPages) throws URISyntaxException, UrlInvalidException {
    super(seedUrl, maxPages);
    this.maxPages = maxPages;
    this.executor = Executors.newFixedThreadPool(MAX_THREADS);
  }

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
