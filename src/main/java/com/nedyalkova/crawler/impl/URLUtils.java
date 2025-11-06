package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class URLUtils {
  Logger log = LoggerFactory.getLogger(URLUtils.class);

  public URI normalizeUrl(String rawUrl) throws URISyntaxException {
    URI uri = new URI(rawUrl.trim()).normalize();

    return new URI(
        uri.getScheme() != null ? uri.getScheme().toLowerCase() : "http",
        uri.getAuthority() != null ? uri.getAuthority().toLowerCase() : null,
        (uri.getPath() == null || uri.getPath().isEmpty()) ? "/" : uri.getPath(),
        uri.getQuery(),
        null // remove fragment
        );
  }

  public void validateScheme(String scheme) throws UrlInvalidException {
    if (!(scheme != null && Set.of("http", "https").contains(scheme.toLowerCase()))) {
      throw new UrlInvalidException("Unexpected protocol");
    }
  }
}
