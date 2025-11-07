To use the web crawler, run it with the seed URL as command line argument, or
mvn exec:java -Dexec.args="https://monzo.com"

## Summary:

A single-domain web crawler written in Java, capable of fetching pages, extracting links, and optionally running
concurrently with multiple threads.

## Features:

* Crawl pages starting from a seed URL within a single domain.
* Normalize and validate URLs to avoid duplicates.
* Skip invalid or unsupported URLs.
* Configurable maximum pages to crawl.
* Extract all HTTP/HTTPS links from HTML pages.
* Optional concurrent crawling using a fixed thread pool.

## Design Decisions:

The implementation has been split into 3 main classes:

* WebCrawler: responsible for the maintenance of the queue and progressing through the crawl
* HTMLFetcher: responsible for fetching the pages
* LinkExtractor: responsible for extracting the links from the html page.

There is also the ConcurrentWebCrawler available, it extends the WebCrawler and can be easily swapped in the main class.

## URL Handling:

URLs are normalized in a variety of ways:

* Any default ports are removed (they are redundant)
* Any ./ and ../ paths are normalized to an absolute path
* Any text parts are converted to lower case
* Any trailing slashes are removed

Also, the application only supports http and https protocols.
Any invalid URLs (URLs that could not be instantiated into a URI) are also logged and skipped.

## Known Limitations / Next Steps

Here are the next steps that would make the application more resilient and robust:

* Error handling: for now, pages that return HTTP errors are skipped. In future, more robust error handling and retries
  could be added for temporary network failures.
* Persistence: persistence could be added so that the results of the crawl could be analysed. Or, if the app stops, on
  restart the queue and the list of visited links could be populated from the database so that the process did not have
  to repeat from the beginning.
* Data structures: currently, the visited links are saved as a Set of URIs. They could potentially be saved hashed
  instead, it may be more memory-efficient for larger websites.
* Rate limiting: rate limiting could be added to avoid making too many requests at a time.

Additional parameters could be added to the crawler that did not fit the exact specification:
* which crawler to use - single vs multi-threaded
* the maximum page parameter could also be externalised.
