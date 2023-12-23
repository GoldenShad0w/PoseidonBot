package me.goldenshadow.wynnapi.v3.player.data;

public record PlayerGlobal(int wars, int totalLevels, int killedMobs, int chestsFound, Dungeons dungeons,
                           Raids raids, int completedQuests, PVP pvp) {

}
