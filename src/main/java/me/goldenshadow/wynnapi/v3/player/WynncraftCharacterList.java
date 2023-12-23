package me.goldenshadow.wynnapi.v3.player;

import com.google.gson.*;
import me.goldenshadow.wynnapi.v3.player.data.CharacterType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public record WynncraftCharacterList(HashMap<String, Character> characters) {

    public record Character(CharacterType type, String nickname, int level, int xp, int xpPercent, int totalLevel, String[] gamemode) {}

    public static class WynncraftCharacterListDeserializer implements JsonDeserializer<WynncraftCharacterList> {
        @Override
        public WynncraftCharacterList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Map<String, JsonElement> jsonMap = jsonObject.asMap();
            HashMap<String, Character> characterHashMap = new HashMap<>();
            for (String s : jsonMap.keySet()) {
                Character c = context.deserialize(jsonMap.get(s).getAsJsonObject(), Character.class);
                characterHashMap.put(s, c);
            }
            return new WynncraftCharacterList(characterHashMap);
        }
    }
}
