package me.goldenshadow.wynnapi.v3.player;

public record WynncraftAbilityMap(int pages, Node[] map) {
    public record Node(String type, Coordinates coordinates, Meta meta, String[] family) {
        public record Coordinates(int x, int y) {}
        public record Meta(String icon, int page, String id) {}
    }
}
