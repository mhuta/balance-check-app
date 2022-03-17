package com.example.balancecheckapp.adapters.webscraper;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.stripStart;
import static org.apache.commons.lang3.StringUtils.substringBetween;

class ResponseDocumentExtractor {

    Optional<String> extractAccessToken(Document document) {
        return getContent(document)
                .map(content -> substringBetween(content, "\"accessToken\":\"", "\""));
    }

    Optional<String> extractFormSubmitUrlPath(Document document, String formId) {
        return Optional.ofNullable(document)
                .map(d -> d.getElementById(formId))
                .map(element -> element.attr("action"))
                .map(element -> removeStart(element, "../"));
    }

    Optional<String> extractContextBaseUrl(Document document) {
        return getElementContent(document, "wicket-ajax-base-url")
                .map(content -> substringBetween(content, "Wicket.Ajax.baseUrl=\"", "\";"));
    }

    Optional<String> extractDpsBtnEnableRequestUrl(Document loginPage) {
        return getElementContent(loginPage, "DpsBtnEnable")
                .map(this::extractLoginPageTokenQueryUrl);
    }

    Optional<String> extractNikFormSubmitRequestQueryToken(Document dpsBtnEnableResponse) {
        return Optional.ofNullable(dpsBtnEnableResponse)
                .map(Document::outerHtml)
                .map(content -> substringBetween(content, "<evaluate>", "</evaluate>"))
                .map(this::extractLoginPageTokenQueryUrl)
                .filter(StringUtils::isNotEmpty);
    }

    Optional<String> extractRedirectUrl(Document formResponse) {
        return Optional.ofNullable(formResponse)
                .map(Document::outerHtml)
                .map(content -> substringBetween(content, "<![CDATA[", "]]>"))
                .map(this::stripToSegment);
    }

    Optional<String> getElementContent(Document document, String elementId) {
        return Optional.ofNullable(document)
                .map(d -> d.getElementById(elementId))
                .map(Node::outerHtml);
    }

    private Optional<String> getContent(Document document) {
        return Optional.ofNullable(document).map(Document::outerHtml);
    }

    private String extractLoginPageTokenQueryUrl(String content) {
        return stripToSegment(substringBetween(content, "\"u\":\"", "\""));
    }

    private String stripToSegment(String path) {
        return path.startsWith("/") ? stripStart(path, "/") : stripStart(path, "./");
    }

}
