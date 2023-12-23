package me.goldenshadow.wynnapi.v3.guild.data;

public record Banner(String base, int tier, String structure, BannerLayer[] layers) {

    public record BannerLayer(String colour, String pattern) {}
}
