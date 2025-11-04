package com.nedyalkova.crawler.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class LinkExtractor {

    public static Set<String> extractLinks(String html, String baseUrl){
        Set<String> linksOnThisPage = new HashSet<>();
        Document doc = Jsoup.parse(html, baseUrl);
        Elements hRefs = doc.select("a[href]");
        if (CollectionUtils.isEmpty(hRefs)){
            return linksOnThisPage;
        }
        for (Element hRef : hRefs){
            String hrefAbsoluteUrl = hRef.absUrl("href").trim();
            // todo check protocol?
            linksOnThisPage.add(hrefAbsoluteUrl);
        }
        return linksOnThisPage;
    }
}
