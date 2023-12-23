package me.goldenshadow.wynnapi.v3.player.data;

public record Professions(Profession fishing, Profession woodcutting, Profession mining, Profession farming,
                          Profession scribing, Profession jeweling, Profession alchemism, Profession cooking,
                          Profession weaponsmithing, Profession tailoring, Profession woodworking, Profession armouring) {

    public record Profession(int level, int xpPercent) {}
}
