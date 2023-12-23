package me.goldenshadow.wynnapi.v3.player.data;

import me.goldenshadow.wynnapi.v3.guild.data.GuildRank;

public record PlayerGuild(String name, String prefix, GuildRank rank, String stars) {
    //TODO: add a method to get guild stats?
}
