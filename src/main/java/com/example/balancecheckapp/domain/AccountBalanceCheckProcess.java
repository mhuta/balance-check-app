package com.example.balancecheckapp.domain;

import com.example.balancecheckapp.domain.dto.AccountDto;
import com.example.balancecheckapp.domain.ports.ForAuthentication;
import com.example.balancecheckapp.domain.ports.ForObtainingAccount;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class AccountBalanceCheckProcess {

    private final ForAuthentication authentication;
    private final ForObtainingAccount obtainingAccount;

    public AccountBalanceCheckProcess(ForAuthentication authentication, ForObtainingAccount obtainingAccount) {
        this.authentication = authentication;
        this.obtainingAccount = obtainingAccount;
    }

    public List<AccountDto> startProcess(String login, String password, Supplier<String> readSecurityCode) {
        Optional<String> accessToken = authentication.getAccessToken(login, password, readSecurityCode);
        if (accessToken.isEmpty()) {
            throw new IllegalStateException("Couldn't retrieve access token to fetch user accounts");
        }
        return obtainingAccount.fetchOwnerAccounts(accessToken.get());
    }

}
