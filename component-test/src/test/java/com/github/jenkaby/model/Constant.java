package com.github.jenkaby.model;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class Constant {

    public static Map<String, UUID> TXN_IDS = Map.of(
            "TXN1", UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "TXN2", UUID.fromString("00000000-0000-0000-0000-000000000001")
    );


    public UUID getTxnId(Map<String, String> table) {
        return getTxnId(table, UUID.randomUUID());
    }

    public UUID getTxnId(Map<String, String> table, UUID defaultValue) {
        return getTxnId(table, "txnId", defaultValue);
    }

    public UUID getTxnId(Map<String, String> table, String columnName, UUID defaultValue) {
        return Optional.ofNullable(table.get(columnName))
                .map(TXN_IDS::get)
                .orElse(defaultValue);
    }
}
