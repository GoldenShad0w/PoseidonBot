package me.goldenshadow.poseidon.profile;

public record PlayData(int wars, long playtime, long xp, int shells) {
    public static PlayData getEmpty() {
        return new PlayData(0,0,0, 0);
    }
}
