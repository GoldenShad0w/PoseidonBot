package me.goldenshadow.wynnapi.v3.guild.data;

import java.util.HashMap;

public record SeasonRanks(HashMap<Integer, SeasonRank> seasons) {

    public record SeasonRank(int rating, int finalTerritories) {}
}
