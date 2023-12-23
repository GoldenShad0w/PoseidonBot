package me.goldenshadow.wynnapi.v3.player;


import me.goldenshadow.wynnapi.v3.player.data.*;

import java.util.Date;

public record WynncraftPlayer(String username, boolean online, String server, String uuid, PlayerRank rank,
                              String rankBadge, LegacyRankColour legacyRankColour, SupportRank supportRank,
                              Date firstJoin, Date lastJoin, double playtime, PlayerGuild guild,
                              PlayerGlobal globalData, Integer forumLink, Ranking ranking,
                              boolean publicProfile) {

}
