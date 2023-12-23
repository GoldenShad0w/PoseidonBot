package me.goldenshadow.wynnapi.v3.guild;

import me.goldenshadow.wynnapi.v3.guild.data.Banner;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;
import me.goldenshadow.wynnapi.v3.guild.data.SeasonRanks;

import java.util.Date;

public record WynncraftGuild(String name, String prefix, int level, int xpPercent, int territories, int wars, Date created,
                             GuildMembers members, int online, Banner banner, SeasonRanks seasonRanks) {
}
