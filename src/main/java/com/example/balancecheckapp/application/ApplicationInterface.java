package com.example.balancecheckapp.application;

import com.example.balancecheckapp.domain.AccountBalanceCheckProcess;
import com.example.balancecheckapp.domain.dto.AccountDto;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

class ApplicationInterface {

    private static final String COLUMNS_FMT = "%-25s %-30s %-15s";
    private final AccountBalanceCheckProcess checkProcess;
    private final Reader reader;
    private final Writer writer;
    private final Map<String, String> properties;

    public ApplicationInterface(AccountBalanceCheckProcess checkProcess, Reader reader, Writer writer, Map<String, String> properties) {
        this.checkProcess = checkProcess;
        this.reader = reader;
        this.writer = writer;
        this.properties = properties;
    }

    public void run() {
        printWelcomeMessage();
        String login = readLogin();
        String password = readPassword();
        printInProgressMessage();
        Supplier<String> readSecurityCode = this::readSecurityCode;
        List<AccountDto> accounts = checkProcess.startProcess(login, password, readSecurityCode);
        printAccountList(accounts);
    }

    private void printWelcomeMessage() {
        printMessage("Santander Bank account balance checker\n");
    }

    private String readLogin() {
        return properties.containsKey("login") ? properties.get("login") : read("Login: ");
    }

    private String readPassword() {
        return properties.containsKey("password") ? properties.get("password") : readPwd();
    }

    private String readSecurityCode() {
        return properties.containsKey("sms-code") ? properties.get("sms-code") : read("Sms code: ");
    }

    private void printInProgressMessage() {
        printMessage("Logging in...\n");
    }

    private void printMessage(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String read(String message) {
        char[] array = new char[30];
        try {
            printMessage(message);
            reader.read(array);
            return String.valueOf(array);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readPwd() {
        String message = "Password: ";
        if (System.console() != null) {
            return String.valueOf(System.console().readPassword(message));
        }
        return read(message);
    }

    private void printAccountList(List<AccountDto> accounts) {
        if (accounts.isEmpty()) {
            printMessage("No accounts to show.");
            return;
        }
        printMessage(String.format(COLUMNS_FMT, "Account name", "Account number", "Balance"));
        accounts.forEach(account -> printMessage(
                String.format(COLUMNS_FMT, account.accountName(), account.accountNumber(), account.balance())));
    }

}
