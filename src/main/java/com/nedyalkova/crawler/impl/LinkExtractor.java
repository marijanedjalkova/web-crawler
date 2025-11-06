package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class LinkExtractor {
  Logger log = LoggerFactory.getLogger(LinkExtractor.class);

  private final URLUtils urlUtils = new URLUtils();

  public Set<URI> extractLinks(String html, String baseUrl) {
    if (StringUtils.isBlank(html)) {
      return new HashSet<>();
    }
    Document doc = Jsoup.parse(html, baseUrl);
    Elements hRefs = doc.select("a[href]");
    if (CollectionUtils.isEmpty(hRefs)) {
      return new HashSet<>();
    }
    Set<URI> linksOnThisPage = new HashSet<>();
    for (Element hRef : hRefs) {
      try {
        String hrefAbsoluteUrl = hRef.absUrl("href").trim();
        if (StringUtils.isBlank(hrefAbsoluteUrl)) {
          continue;
        }
        URI normalized = urlUtils.normalizeUrl(hrefAbsoluteUrl);
        urlUtils.validateScheme(normalized.getScheme());
        log.debug("Adding to queue: {}", normalized);
        linksOnThisPage.add(normalized);
      } catch (UrlInvalidException | URISyntaxException e) {

      }
    }
    return linksOnThisPage;
  }
}
