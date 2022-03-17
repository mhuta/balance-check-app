package com.example.balancecheckapp.adapters.webscraper;

import com.example.balancecheckapp.domain.ports.ForAuthentication;
import okhttp3.OkHttpClient;

import java.util.Optional;
import java.util.function.Supplier;

public class LogInPageAuthenticator implements ForAuthentication {

    private final OkHttpClient okHttpClient;
    private final String logInPageUrl;

    public LogInPageAuthenticator(OkHttpClient okHttpClient, String logInPageUrl) {
        this.okHttpClient = okHttpClient;
        this.logInPageUrl = logInPageUrl;
    }

    @Override
    public Optional<String> getAccessToken(String login, String password, Supplier<String> securityCodeSupplier) {
        LogInPageClient logInPageClient = new LogInPageClient(okHttpClient, logInPageUrl);
        LogInPageScraper logInPageScraper = new LogInPageScraper(logInPageClient, new ResponseDocumentExtractor());
        try {
            return Optional.of(logInPageScraper.getAccessTokenViaLogInPageSteps(login, password, securityCodeSupplier));
        } catch (InvalidLoginCredentials exception) {
            return Optional.empty();
        }
    }

}
