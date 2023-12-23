package me.goldenshadow.wynnapi.v3.player.data;

public record Ranking(RankType[] rankings) {

    public record RankType(String rankingType, int rank) {
    }

}
