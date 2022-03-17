package com.example.balancecheckapp.adapters.webscraper;

import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.function.Supplier;

class LogInPageScraper {

    private final LogInPageClient logInPageClient;
    private final ResponseDocumentExtractor pageExtractor;

    LogInPageScraper(LogInPageClient logInPageClient, ResponseDocumentExtractor pageExtractor) {
        this.logInPageClient = logInPageClient;
        this.pageExtractor = pageExtractor;
    }

    public String getAccessTokenViaLogInPageSteps(String login, String password, Supplier<String> codeSupplier) {
        logIn(login, password, codeSupplier);
        return findAccessToken();
    }

    private void logIn(String nik, String password, Supplier<String> codeSupplier) {
        String pinPageUrl = findPinPageUrl(nik);
        String codePageUrl = findCodePageUrl(pinPageUrl, password);
        sendCode(codeSupplier, codePageUrl);
    }

    private String findPinPageUrl(String nik) {
        Document loginPage = logInPageClient.getLoginPage();
        String contextBaseUrl = pageExtractor.extractContextBaseUrl(loginPage)
                .orElseThrow(() -> new IllegalStateException("Could not find context baseUrl on login page."));
        String dpsBtnEnableUrl = pageExtractor.extractDpsBtnEnableRequestUrl(loginPage)
                .orElseThrow(() -> new IllegalStateException("Could not find dps button enable url on login page."));
        Document dpsBtnEnableResult = logInPageClient.getPage(dpsBtnEnableUrl, contextBaseUrl);
        String nikFormSubmitUrl = pageExtractor.extractNikFormSubmitRequestQueryToken(dpsBtnEnableResult)
                .orElseThrow(() -> new IllegalStateException("Could not find baseUrl on login page."));
        Document nikFormSubmitResult = logInPageClient.submitForm(nikFormSubmitUrl, Map.of("nik", nik), contextBaseUrl);
        return pageExtractor.extractRedirectUrl(nikFormSubmitResult)
                .orElseThrow(() -> new IllegalStateException("Could not find pin page url in nik form submit response."));
    }

    private String findCodePageUrl(String pinPageUrl, String pin) {
        Document pinPage = logInPageClient.getPage(pinPageUrl);
        String contextBaseUrl = pageExtractor.extractContextBaseUrl(pinPage)
                .orElseThrow(() -> new IllegalStateException("Could not find context baseUrl on pin page."));
        String pinFormSubmitUrl = pageExtractor.extractFormSubmitUrlPath(pinPage, "pinForm")
                .orElseThrow(() -> new IllegalStateException("Could not extract 'pinForm' submit url from pin page."));
        Document pinFormSubmitResult = logInPageClient.submitForm(pinFormSubmitUrl, Map.of("pinFragment:pin", pin), contextBaseUrl);
        return pageExtractor.extractRedirectUrl(pinFormSubmitResult)
                .orElseThrow(() -> new IllegalStateException("Could not find code page url in pin form submit response."));
    }

    private void sendCode(Supplier<String> code, String codePageUrl) {
        Document codePage = logInPageClient.getPage(codePageUrl);
        String contextBaseUrl = pageExtractor.extractContextBaseUrl(codePage)
                .orElseThrow(() -> new IllegalStateException("Could not find context baseUrl on code page."));
        String codeFormSubmitUrl = pageExtractor.extractFormSubmitUrlPath(codePage, "authenticationForm")
                .orElseThrow(() -> new IllegalStateException("Could not extract 'authenticationForm' submit url from pin code page"));
        Document response = logInPageClient.submitForm(codeFormSubmitUrl, Map.of("response", code.get()), contextBaseUrl);
        if (response.outerHtml().contains("/uep")) {
            throw new InvalidLoginCredentials("Provided user credentials are invalid.");
        }
    }

    private String findAccessToken() {
        Document dashboard = logInPageClient.getDashboardPage();
        return pageExtractor.extractAccessToken(dashboard)
                .orElseThrow(() -> new IllegalStateException("Could not find access token on dashboard page"));
    }

}
