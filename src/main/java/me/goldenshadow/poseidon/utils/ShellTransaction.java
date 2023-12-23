package me.goldenshadow.poseidon.utils;

import org.jetbrains.annotations.Nullable;

public record ShellTransaction(TransactionType type, int amount, @Nullable String reason) {

    @Override
    public String toString() {
        return "Type: " + type.toString() + " | Amount: " + amount + " | Reason: " + (reason != null ? reason : "N/A\n");
    }

    public enum TransactionType {
        ADDITION,
        SUBTRACTION
    }
}
