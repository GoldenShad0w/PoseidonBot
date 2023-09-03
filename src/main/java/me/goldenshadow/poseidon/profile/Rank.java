package me.goldenshadow.poseidon.profile;

import java.util.ArrayList;
import java.util.List;

public enum Rank {
    STARFISH("Starfish "),
    MANATEE("Manatee "),
    PIRANHA("Piranha "),
    BARRACUDA("Barracuda "),
    ANGLER("Angler "),
    HAMMERHEAD("Hammerhead "),
    SAILFISH("Sailfish "),
    DOLPHIN("Dolphin "),
    NARWHAL("Narwhal "),
    HYDRA(""),
    NONE("");

    Rank(String prefix) {
        this.prefix = prefix;
    }

    private final String prefix;

    public static String[] getStringValues() {
        List<String> list = new ArrayList<>();
        for (Rank r :values()) {
            list.add(r.toString());
        }
        return list.toArray(String[]::new);
    }
}
