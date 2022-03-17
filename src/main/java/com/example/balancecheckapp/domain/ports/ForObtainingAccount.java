package com.example.balancecheckapp.domain.ports;

import com.example.balancecheckapp.domain.dto.AccountDto;

import java.util.List;

public interface ForObtainingAccount {

    List<AccountDto> fetchOwnerAccounts(String accessToken);

}
