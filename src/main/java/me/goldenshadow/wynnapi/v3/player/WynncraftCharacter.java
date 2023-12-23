package me.goldenshadow.wynnapi.v3.player;


import me.goldenshadow.wynnapi.v3.player.data.*;

import javax.annotation.Nullable;

public record WynncraftCharacter(CharacterType type, String nickname, int level, int xp, int xpPercent, int totalLevel,
                                 int wars, double playtime, int mobsKilled, int chestsFound, int blocksWalked,
                                 int itemsIdentified, int logins, int deaths, int discoveries, boolean preEconomy, PVP pvp,
                                 String[] gamemode, @Nullable SkillPoints skillPoints, Professions professions,
                                 Dungeons dungeons, Raids raids, String[] quests) {

}
