package com.example.balancecheckapp.adapters.webscraper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.nodes.Document;

import java.util.Map;

class LogInPageClient extends PageClient {

    private final String baseUrl;

    LogInPageClient(OkHttpClient okHttpClient, String baseUrl) {
        super(okHttpClient);
        this.baseUrl = baseUrl;
    }

    Document getLoginPage() {
        return getPage("login");
    }

    Document getDashboardPage() {
        return getPage("multi/dashboard");
    }

    Document getPage(String relativeUrl) {
        Request request = new Request.Builder()
                .url(baseUrl + relativeUrl)
                .build();
        return newRequestCall(request);
    }

    Document getPage(String relativeUrl, String contextBaseUrl) {
        Request request = new Request.Builder()
                .url(baseUrl + relativeUrl)
                .headers(buildHeaders(contextBaseUrl))
                .build();
        return newRequestCall(request);
    }

    Document submitForm(String relativeUrl, Map<String, String> formParams, String contextBaseUrl) {
        Request formSubmitRequest = new Request.Builder()
                .url(baseUrl + relativeUrl)
                .method("POST", buildFormBody(formParams))
                .headers(buildHeaders(contextBaseUrl))
                .build();
        return newRequestCall(formSubmitRequest);
    }

}
