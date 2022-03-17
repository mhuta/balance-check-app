package com.example.balancecheckapp.adapters.rest;

import com.example.balancecheckapp.domain.dto.AccountDto;
import com.example.balancecheckapp.domain.ports.ForObtainingAccount;
import okhttp3.OkHttpClient;

import java.util.List;

public class AccountRestRepository implements ForObtainingAccount {

    private final OkHttpClient okHttpClient;
    private final String accountsApiUrl;

    public AccountRestRepository(OkHttpClient okHttpClient, String accountsApiUrl) {
        this.okHttpClient = okHttpClient;
        this.accountsApiUrl = accountsApiUrl;
    }

    @Override
    public List<AccountDto> fetchOwnerAccounts(String accessToken) {
        AccountRestApiClient accountRestApiClient = new AccountRestApiClient(okHttpClient, accountsApiUrl);
        return accountRestApiClient.fetchOwnerAccounts(accessToken);
    }

}
