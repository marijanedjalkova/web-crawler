package com.nedyalkova.crawler.impl;

import com.nedyalkova.crawler.exception.UrlInvalidException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class URLUtilsTest {

  private final URLUtils urlUtils = new URLUtils();

  @Test
  void normalizeUrl_whenBlank_returnsNull() throws URISyntaxException {
    assertNull(urlUtils.normalizeUrl(" "));
    assertNull(urlUtils.normalizeUrl(null));
  }

  @Test
  void normalizeUrl_whenUrlProvided_lowerCasesHostAndRemovesFragment() throws URISyntaxException {
    URI result = urlUtils.normalizeUrl("HTTP://Web-crawler.COM/Path#section");
    assertEquals("http://web-crawler.com/Path", result.toString());
  }

  @Test
  void normalizeUrl_whenUrlProvided_removesTrailingSlash() throws URISyntaxException {
    URI result = urlUtils.normalizeUrl("https://simple-crawler.com/page/");
    assertEquals("https://simple-crawler.com/page", result.toString());

    URI root = urlUtils.normalizeUrl("https://simple-crawler.com/");
    assertEquals("https://simple-crawler.com/", root.toString());
  }

  @Test
  void normalizeUrl_whenUrlProvided_removesDefaultPorts() throws URISyntaxException {
    URI http = urlUtils.normalizeUrl("http://simple-crawler.com:80/path");
    assertEquals("http://simple-crawler.com/path", http.toString());

    URI https = urlUtils.normalizeUrl("https://simple-crawler.com:443/path");
    assertEquals("https://simple-crawler.com/path", https.toString());
  }

  @Test
  void normalizeUrl_whenUrlProvided_keepsNonDefaultPorts() throws URISyntaxException {
    URI result = urlUtils.normalizeUrl("https://simple-crawler.com:8443/path");
    assertEquals("https://simple-crawler.com:8443/path", result.toString());
  }

  @Test
  void normalizeUrl_whenSchemeOrHostMissing_returnsNull() throws URISyntaxException {
    assertNull(urlUtils.normalizeUrl("www.simple-crawler.com"));
    assertNull(urlUtils.normalizeUrl("/relative/path"));
  }

  @Test
  void normalizeUrl_whenUrlWithQuery_keepsTheQuery() throws URISyntaxException {
    URI result = urlUtils.normalizeUrl("https://simple-crawler.com/page?foo=bar");
    assertEquals("https://simple-crawler.com/page?foo=bar", result.toString());
  }

  @Test
  void validateScheme_whenHttpOrHttps_thenValid() {
    assertDoesNotThrow(() -> urlUtils.validateScheme("http"));
    assertDoesNotThrow(() -> urlUtils.validateScheme("https"));
    assertDoesNotThrow(() -> urlUtils.validateScheme("HTTPS"));
  }

  @Test
  void validateScheme_whenNotHttpOrHttps_throwsForOtherProtocols() {
    assertThrows(UrlInvalidException.class, () -> urlUtils.validateScheme("ftp"));
    assertThrows(UrlInvalidException.class, () -> urlUtils.validateScheme("file"));
    assertThrows(UrlInvalidException.class, () -> urlUtils.validateScheme(null));
  }
}
