package me.goldenshadow.poseidon.shells;

import org.jetbrains.annotations.Nullable;

public record ShellTransaction(TransactionType type, int amount, @Nullable String reason) {

    @Override
    public String toString() {
        return "**Type: **'" + type.toString() + "' | **Amount: **'" + amount + "' | **Reason: **" + (reason != null ? reason : "N/A");
    }

    public enum TransactionType {
        ADDITION,
        SUBTRACTION
    }
}
