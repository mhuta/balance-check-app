package com.example.balancecheckapp.domain;

import com.example.balancecheckapp.domain.dto.AccountDto;
import com.example.balancecheckapp.domain.ports.ForAuthentication;
import com.example.balancecheckapp.domain.ports.ForObtainingAccount;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountBalanceCheckProcessTest {

    private static final String ACCOUNT_LOGIN = "123";
    private static final String ACCOUNT_PASSWORD = "pwd";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String INVALID_ACCOUNT_LOGIN = "666";

    private final ForAuthentication authentication = mock(ForAuthentication.class);
    private final ForObtainingAccount obtainingAccount = mock(ForObtainingAccount.class);
    private final AccountBalanceCheckProcess underTest = new AccountBalanceCheckProcess(authentication, obtainingAccount);

    @Test
    void should_return_user_accounts_when_log_in_with_correct_credentials() {
        when(authentication.getAccessToken(eq(ACCOUNT_LOGIN), eq(ACCOUNT_PASSWORD), any())).thenReturn(Optional.of(ACCESS_TOKEN));
        List<AccountDto> testAccounts = List.of(buildDefaultAccountDto());
        when(obtainingAccount.fetchOwnerAccounts(ACCESS_TOKEN)).thenReturn(testAccounts);
        List<AccountDto> result = underTest.startProcess(ACCOUNT_LOGIN, ACCOUNT_PASSWORD, () -> "");
        assertEquals(testAccounts, result);
    }

    @Test
    void should_return_no_accounts_when_log_in_with_correct_credentials_but_user_has_no_accounts() {
        when(authentication.getAccessToken(eq(ACCOUNT_LOGIN), eq(ACCOUNT_PASSWORD), any())).thenReturn(Optional.of(ACCESS_TOKEN));
        when(obtainingAccount.fetchOwnerAccounts(ACCESS_TOKEN)).thenReturn(List.of());
        List<AccountDto> result = underTest.startProcess(ACCOUNT_LOGIN, ACCOUNT_PASSWORD, () -> "");
        assertTrue(result.isEmpty());
    }

    @Test
    void should_throw_exception_when_log_in_with_incorrect_credentials() {
        when(authentication.getAccessToken(eq(INVALID_ACCOUNT_LOGIN), eq(ACCOUNT_PASSWORD), any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> underTest.startProcess(INVALID_ACCOUNT_LOGIN, ACCOUNT_PASSWORD, () -> ""));
    }

    private AccountDto buildDefaultAccountDto() {
        return new AccountDto("Best account in the world", "74109010980000000142799897", "100 PLN");
    }

}