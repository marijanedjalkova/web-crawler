package com.nedyalkova.crawler.impl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class HTMLFetcherTest {

  private final HTMLFetcher htmlFetcher = new HTMLFetcher();

  @Test
  void fetchHTML_whenBlankUrl_thenNullResponse() throws IOException {
    assertNull(htmlFetcher.fetchHTML(""));
    assertNull(htmlFetcher.fetchHTML(null));
    assertNull(htmlFetcher.fetchHTML("   "));
  }

  @Test
  void fetchHTML_whenValidPage_documentIsReturned() throws IOException {
    // Mock Jsoup behavior
    Document mockDoc = mock(Document.class);
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockResponse = mock(Connection.Response.class);

    when(mockDoc.connection()).thenReturn(mockConnection);
    when(mockConnection.response()).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(200);
    String htmlString = "<html><body>Test</body></html>";
    when(mockDoc.html()).thenReturn(htmlString);

    // Jsoup static connect mocking
    try (var mockedJsoup = mockStatic(Jsoup.class)) {
      mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
      when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
      when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
      when(mockConnection.get()).thenReturn(mockDoc);

      String html = htmlFetcher.fetchHTML("https://test-web-crawler.com");
      assertEquals(htmlString, html);
    }
  }

  @Test
  void fetchHTML_whenReturnStatusNpt200_returnsNull() throws IOException {
    Document mockDoc = mock(Document.class);
    Connection mockConnection = mock(Connection.class);
    Connection.Response mockResponse = mock(Connection.Response.class);

    when(mockDoc.connection()).thenReturn(mockConnection);
    when(mockConnection.response()).thenReturn(mockResponse);
    when(mockResponse.statusCode()).thenReturn(404);
    when(mockDoc.html()).thenReturn("<html>404</html>");

    try (var mockedJsoup = mockStatic(Jsoup.class)) {
      mockedJsoup.when(() -> Jsoup.connect(anyString()))
              .thenReturn(mockConnection);
      when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
      when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
      when(mockConnection.get()).thenReturn(mockDoc);

      assertNull(htmlFetcher.fetchHTML("https://test-site.com/404"));
    }
  }

  @Test
  void fetchHTML_whenIOException_returnsNull() throws IOException {
    Connection mockConnection = mock(Connection.class);

    try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
      mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
      when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
      when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
      when(mockConnection.get()).thenThrow(new IOException("Network error"));

      assertNull(htmlFetcher.fetchHTML("someBadUrl"));
    }
  }
}
