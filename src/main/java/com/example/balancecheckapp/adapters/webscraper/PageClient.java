package com.example.balancecheckapp.adapters.webscraper;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import static java.util.Objects.requireNonNull;

abstract class PageClient {

    private static final String NO_BASE_URI = "";
    private final OkHttpClient okHttpClient;

    PageClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient.newBuilder()
                .cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)))
                .build();
    }

    protected Document newRequestCall(Request request) {
        Response response = makeRequestCall(request);
        ResponseBody body = response.body();
        return parseBody(requireNonNull(body));
    }

    private Response makeRequestCall(Request request) {
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Document parseBody(ResponseBody body) {
        String type = requireNonNull(body.contentType()).type();
        try {
            if ("text/xml".equalsIgnoreCase(type)) {
                return Jsoup.parse(body.string(), NO_BASE_URI, Parser.xmlParser());
            }
            return Jsoup.parse(body.string(), NO_BASE_URI, Parser.htmlParser());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected Headers buildHeaders(String contextBaseUrl) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("Wicket-Ajax-Baseurl", contextBaseUrl);
        headersBuilder.add("Wicket-Ajax", "true");
        return headersBuilder.build();
    }

    protected FormBody buildFormBody(Map<String, String> formParams) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formParams.forEach(formBodyBuilder::add);
        return formBodyBuilder.build();
    }

}
