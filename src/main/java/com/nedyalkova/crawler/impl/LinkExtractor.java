package com.nedyalkova.crawler.impl;

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

  public Set<String> extractLinks(String html, String baseUrl) {
    Document doc = Jsoup.parse(html, baseUrl);
    Elements hRefs = doc.select("a[href]");
    if (CollectionUtils.isEmpty(hRefs)) {
      return new HashSet<>();
    }
    Set<String> linksOnThisPage = new HashSet<>();
    for (Element hRef : hRefs) {
      String hrefAbsoluteUrl = hRef.absUrl("href").trim();
      if (StringUtils.isBlank(hrefAbsoluteUrl)){
        continue;
      }
      try {
        URI uri = new URI(hrefAbsoluteUrl);
        URI normalized =
            new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null);
        // this removes sections, like #segment and helps avoid duplication
        linksOnThisPage.add(normalized.toString());
      } catch (URISyntaxException e) {
        log.debug("Invalid link {} will not be returned", hrefAbsoluteUrl);
      }
    }
    return linksOnThisPage;
  }
}
