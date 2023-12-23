package me.goldenshadow.wynnapi.v3.guild.data;

public enum GuildRank {

    RECRUIT("☆☆☆☆☆", '♙'),
    RECRUITER("★☆☆☆☆", '♘'),
    CAPTAIN("★★☆☆☆", '♗'),
    STRATEGIST("★★★☆☆", '♖'),
    CHIEF("★★★★☆", '♕'),
    OWNER("★★★★★", '♔');

    private final String stars;
    private final char symbol;

    GuildRank(String stars, char symbol) {
        this.stars = stars;
        this.symbol = symbol;
    }

    /**
     * Get a string representing the starts that proceed this rank in the guild
     */
    public String getStars() {
        return stars;
    }

    /**
     * Get the character that represents this rank in the guild (as seen at <a href="https://wynncraft.com/stats">...</a>)
     */
    public char getSymbol() {
        return symbol;
    }
}
