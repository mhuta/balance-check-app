package com.example.balancecheckapp.application;

import com.example.balancecheckapp.adapters.rest.AccountRestRepository;
import com.example.balancecheckapp.adapters.webscraper.LogInPageAuthenticator;
import com.example.balancecheckapp.domain.AccountBalanceCheckProcess;
import com.example.balancecheckapp.domain.ports.ForAuthentication;
import com.example.balancecheckapp.domain.ports.ForObtainingAccount;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.Console;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static org.apache.commons.lang3.StringUtils.removeStart;

public class ConsoleApplication {

    public static void main(String[] args) {
        Map<String, String> props = parseProperties(args);
        Console console = System.console();
        runApplication(console.reader(), console.writer(), props);
    }

    private static Map<String, String> parseProperties(String[] args) {
        DefaultProperties defaultProperties = new DefaultProperties();
        Stream.of(args)
                .map(option -> removeStart(option, "--"))
                .map(option -> option.split("="))
                .forEach(options -> defaultProperties.put(options[0], options[1]));
        return defaultProperties;
    }

    static void runApplication(Reader reader, Writer writer, Map<String, String> properties) {
        OkHttpClient okHttpClient = buildOkHttpClient(properties);
        ForAuthentication authentication = new LogInPageAuthenticator(okHttpClient, properties.get("login-page-url"));
        ForObtainingAccount obtainingAccount = new AccountRestRepository(okHttpClient, properties.get("accounts-api-url"));
        AccountBalanceCheckProcess checkProcess = new AccountBalanceCheckProcess(authentication, obtainingAccount);
        new ApplicationInterface(checkProcess, reader, writer, properties).run();
    }

    private static OkHttpClient buildOkHttpClient(Map<String, String> properties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (Boolean.parseBoolean(properties.get("http-debug"))) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(BODY);
            builder.addNetworkInterceptor(interceptor);
        }
        return builder.build();
    }

    private static class DefaultProperties extends HashMap<String, String> {
        {
            put("login-page-url", "https://www.centrum24.pl/centrum24-web/");
            put("accounts-api-url", "https://www.centrum24.pl/centrum24-rest/api/v1/accounts");
        }
    }

}
