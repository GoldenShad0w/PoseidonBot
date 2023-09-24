package me.goldenshadow.poseidon.profile;

import me.goldenshadow.poseidon.Constants;

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

    public final String prefix;

    public static String[] getStringValues() {
        List<String> list = new ArrayList<>();
        for (Rank r :values()) {
            list.add(r.toString());
        }
        return list.toArray(String[]::new);
    }

    public String[] getRoles() {
        return switch (this) {
            case STARFISH -> Constants.STARFISH_ROLES;
            case MANATEE -> Constants.MANATEE_ROLES;
            case PIRANHA -> Constants.PIRANHA_ROLES;
            case BARRACUDA -> Constants.BARRACUDA_ROLES;
            case ANGLER -> Constants.ANGLER_ROLES;
            case HAMMERHEAD -> Constants.HAMMERHEAD_ROLES;
            case SAILFISH -> Constants.SAILFISH_ROLES;
            case DOLPHIN -> Constants.DOLPHIN_ROLES;
            case NARWHAL -> Constants.NARWHAL_ROLES;
            case HYDRA, NONE -> new String[]{};
        };
    }
}
