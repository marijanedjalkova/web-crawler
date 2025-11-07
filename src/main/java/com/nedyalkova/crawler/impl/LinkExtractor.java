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

/**
 * Responsible for extracting and resolving hyperlinks from an HTML document.
 *
 * <p>The {@code LinkExtractor} is used by the WebCrawler to parse fetched HTML content and identify
 * all valid anchor elements ({@code <a href="...">}). It converts any relative links into absolute
 * URLs based on the provided base URL and normalizes them for consistent processing.
 *
 * <p><b>Responsibilities:</b>
 *
 * <ul>
 *   <li>Parse HTML content using Jsoup.
 *   <li>Extract all {@code href} attributes from anchor tags.
 *   <li>Resolve relative URLs to absolute URLs based on the provided base URL.
 *   <li>Normalize and validate extracted URLs before returning them.
 * </ul>
 */
public class LinkExtractor {
  Logger log = LoggerFactory.getLogger(LinkExtractor.class);

  private final URLUtils urlUtils = new URLUtils();

  /**
   * Extracts and normalizes all valid hyperlinks from the given HTML content.
   *
   * <p>Parses the HTML using Jsoup, resolves relative links against the provided base URL, and
   * returns a set of normalized {@link URI} objects using {@link URLUtils}. Only {@code http} and
   * {@code https} links are included. Invalid, blank, or unsupported URLs are skipped.
   *
   * @param html the HTML content to parse
   * @param baseUrl the base URL used to resolve relative links
   * @return a set of unique, normalized {@link URI} objects extracted from the page
   */
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
        if (normalized == null) {
          log.warn("Skipping {} since it could not be normalized", hrefAbsoluteUrl);
          continue;
        }
        urlUtils.validateScheme(normalized.getScheme());
        log.debug("Adding to list of results: {}", normalized);
        linksOnThisPage.add(normalized);
      } catch (UrlInvalidException | URISyntaxException e) {
        log.warn("Skipping {} since it is invalid", hRef);
      }
    }
    return linksOnThisPage;
  }
}
