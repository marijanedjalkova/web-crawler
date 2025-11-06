package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class URLUtils {
  Logger log = LoggerFactory.getLogger(URLUtils.class);

  public URI normalizeUrl(String rawUrl) throws URISyntaxException {
    if (StringUtils.isBlank(rawUrl)) {
      return null;
    }
    URI uri = new URI(rawUrl.trim()).normalize();
    String scheme = uri.getScheme();
    String host = uri.getHost();
    if (scheme == null || host == null) {
      return null;
    }
    host = host.toLowerCase();

    int port = getPort(scheme, uri.getPort());
    String normalisedPath = getPath(uri.getPath());

    return new URI(scheme, uri.getUserInfo(), host, port, normalisedPath, uri.getQuery(), null);
  }

  private String getPath(String path) {
    if (path != null && path.endsWith("/") && path.length() > 1) {
      return path.substring(0, path.length() - 1);
    }
    return path;
  }

  private int getPort(String scheme, int port) {
    // remove default ones only
    if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
      return -1;
    }
    return port;
  }

  public void validateScheme(String scheme) throws UrlInvalidException {
    if (!(scheme != null && Set.of("http", "https").contains(scheme.toLowerCase()))) {
      throw new UrlInvalidException("Unexpected protocol");
    }
  }
}
