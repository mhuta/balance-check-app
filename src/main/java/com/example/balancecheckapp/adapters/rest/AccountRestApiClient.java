package com.example.balancecheckapp.adapters.rest;

import com.example.balancecheckapp.domain.dto.AccountDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AccountRestApiClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    private final String accountsApiUrl;

    AccountRestApiClient(OkHttpClient httpClient, String accountsApiUrl) {
        this.httpClient = httpClient;
        this.accountsApiUrl = accountsApiUrl;
    }

    public List<AccountDto> fetchOwnerAccounts(String accessToken) {
        return makeRequest(new Request.Builder()
                .headers(Headers.of(Map.of("Authorization", "Bearer " + accessToken)))
                .url(accountsApiUrl)
                .build());
    }

    private List<AccountDto> makeRequest(Request request) {
        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful() || responseBody == null) {
                throw new IllegalStateException("Response from Santander rest api was not successful.");
            }
            return mapResponseBody(responseBody.string());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<AccountDto> mapResponseBody(String responseBody) {
        try {
            return Stream.of(objectMapper.readValue(responseBody, JsonNode[].class))
                    .map(this::mapAccountDto)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse response body.", e);
        }
    }

    private AccountDto mapAccountDto(JsonNode node) {
        final String accountName = node.get("accountName").get("name").asText();
        final String accountNumber = node.get("accountNumber").get("number").asText();
        final String balance = node.get("balance").asText();
        final String currency = node.get("currency").asText();
        return new AccountDto(accountName, accountNumber, balance + " " + currency);
    }

}
