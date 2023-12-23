package me.goldenshadow.wynnapi.v3.guild;

import com.google.gson.*;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacterList;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public record WynncraftTerritoryList(HashMap<String, Territory> territories) {

    public record Territory(WynncraftGuildList.Guild guild, Date acquired, TerritoryLocation location) {
        public record TerritoryLocation(int[] start, int[] end) {}
    }

    public static class WynncraftTerritoryListDeserializer implements JsonDeserializer<WynncraftTerritoryList> {
        @Override
        public WynncraftTerritoryList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Map<String, JsonElement> jsonMap = jsonObject.asMap();
            HashMap<String, Territory> territoryHashMap = new HashMap<>();
            for (String s : jsonMap.keySet()) {
                Territory t = context.deserialize(jsonMap.get(s).getAsJsonObject(), WynncraftTerritoryList.Territory.class);
                territoryHashMap.put(s, t);
            }
            return new WynncraftTerritoryList(territoryHashMap);
        }
    }
}
