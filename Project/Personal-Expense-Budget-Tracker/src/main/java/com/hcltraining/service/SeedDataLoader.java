package com.hcltraining.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hcltraining.model.Account;

/** Validates the complete CSV before mutating the target finance service. */
public final class SeedDataLoader {
    private SeedDataLoader() { }

    public static List<Account> readAccounts(Reader source) throws IOException {
        Objects.requireNonNull(source, "source");
        List<Account> parsed = new ArrayList<>();
        try (BufferedReader reader = source instanceof BufferedReader buffered
                ? buffered : new BufferedReader(source)) {
            String header = reader.readLine();
            if (!"id,name,type,openingBalance".equals(header)) {
                throw new IOException("Expected CSV header: id,name,type,openingBalance");
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(",", -1);
                if (fields.length != 4) throw rowError(lineNumber, "expected 4 columns");
                try {
                    int id = Integer.parseInt(fields[0].trim());
                    Account account = new Account(id, fields[1].trim(), fields[2].trim(),
                            new BigDecimal(fields[3].trim()));
                    if (id <= 0 || parsed.stream().anyMatch(existing -> existing.getId() == id)) {
                        throw rowError(lineNumber, "ID must be positive and unique");
                    }
                    parsed.add(account);
                } catch (RuntimeException failure) {
                    throw new IOException("Invalid account CSV line " + lineNumber + ": "
                            + failure.getMessage(), failure);
                }
            }
        }
        return List.copyOf(parsed);
    }

    private static IOException rowError(int lineNumber, String message) {
        return new IOException("Invalid account CSV line " + lineNumber + ": " + message);
    }
}
