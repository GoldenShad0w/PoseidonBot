package me.goldenshadow.wynnapi.mojang;

import java.util.UUID;

public record MojangPlayerData(String id, String name) {

    public UUID getUUID() {
        return UUID.fromString(
                id().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")
        );
    }
}
