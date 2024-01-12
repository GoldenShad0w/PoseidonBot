package me.goldenshadow.poseidon.profile;

public record PlayData(int wars, long playtime, long xp, int shells) {
    public static PlayData getEmpty() {
        return new PlayData(0,0,0, 0);
    }

    public String getFriendlyXP() {
        String xpAsString = String.valueOf(xp());
        if (xpAsString.length() >= 13) { //t
            return String.format("%.1f Tri", xp() / 1_000_000_000_000.0).replace(',', '.');
        } else if (xpAsString.length() >= 10) { //b
            return String.format("%.1f Bil", xp() / 1_000_000_000.0).replace(',', '.');
        } else if (xpAsString.length() >= 7) { //m
            return String.format("%.1f Mil", xp() / 1_000_000.0).replace(',', '.');
        } else if (xpAsString.length() >= 4) { //t
            return String.format("%.1f K", xp() / 1_000.0).replace(',', '.');
        }
        return xpAsString;
    }
}
