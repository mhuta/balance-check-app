package com.example.balancecheckapp.domain.ports;

import java.util.Optional;
import java.util.function.Supplier;

public interface ForAuthentication {

    Optional<String> getAccessToken(String login, String password, Supplier<String> securityCodeSupplier);

}
