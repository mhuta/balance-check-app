package com.example.balancecheckapp.application;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import static com.example.balancecheckapp.MatchersHelper.contains;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConsoleApplicationITest {

    @BeforeAll
    static void beforeAll() {
        WireMockServer wireMockServer = new WireMockServer(options()
                .usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();
    }

    @Test
    void should_print_user_accounts_numbers_and_balances_when_log_in_with_correct_credentials() throws IOException {
        Writer writer = mock(Writer.class);

        ConsoleApplication.runApplication(null, writer, new TestProperties());

        verify(writer).write(argThat(contains("74109010980000000142799897")));
        verify(writer).write(argThat(contains("-6.0 PLN")));
        verify(writer).write(argThat(contains("83109010980000000149479543")));
        verify(writer).write(argThat(contains("18.71 EUR")));
        verify(writer).write(argThat(contains("66109010980000000148883396")));
        verify(writer).write(argThat(contains("20.0 USD")));
    }

    private static class TestProperties extends HashMap<String, String> {
        {
            put("login", "12312399");
            put("password", "Test1234");
            put("sms-code", "599-106");
            put("login-page-url", "http://localhost:8080/centrum24-web/");
            put("accounts-api-url", "http://localhost:8080/centrum24-rest/api/v1/accounts");
        }
    }
}