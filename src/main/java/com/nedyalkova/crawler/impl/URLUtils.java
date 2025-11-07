package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * A utility class that provides helper methods for working with and validating URLs and URIs within
 * the web crawler.
 */
public class URLUtils {
  Logger log = LoggerFactory.getLogger(URLUtils.class);

  /**
   * Normalizes a raw URL string into a canonical representation. This method performs several
   * normalization steps to ensure URLs are treated consistently throughout the crawling process:
   *
   * <ul>
   *   <li>Trims whitespace and normalizes to remove redundant path segments.
   *   <li>Converts the scheme and host to lowercase for uniform comparison.
   *   <li>Removes default ports (80 for HTTP, 443 for HTTPS).
   *   <li>Removes redundant trailing slashes from the path.
   * </ul>
   *
   * If the provided URL is blank, missing a scheme or host, or cannot be parsed into a valid URI,
   * this method returns null.
   *
   * @param rawUrl the raw URL string to normalize
   * @return a normalized {@link URI} object, or {@code null} if the URL is invalid or incomplete
   * @throws URISyntaxException if the URL cannot be parsed into a valid {@link URI}
   */
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
    int port = getPort(scheme, uri.getPort());
    String normalisedPath = getPath(uri.getPath());

    return new URI(
        scheme.toLowerCase(),
        uri.getUserInfo(),
        host.toLowerCase(),
        port,
        normalisedPath,
        uri.getQuery(),
        null);
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

  /**
   * Validates that the scheme is http or https.
   *
   * @param scheme the scheme of the URI
   * @throws UrlInvalidException if the scheme is not http or https
   */
  public void validateScheme(String scheme) throws UrlInvalidException {
    if (!(scheme != null && Set.of("http", "https").contains(scheme.toLowerCase()))) {
      throw new UrlInvalidException("Unexpected protocol");
    }
  }
}
