package me.goldenshadow.wynnapi.v3.guild;

import com.google.gson.*;
import me.goldenshadow.wynnapi.v3.player.WynncraftCharacterList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public record WynncraftGuildList(Guild[] guilds) {
    public record Guild(String name, String prefix) {}

    public static class WynncraftGuildListDeserializer implements JsonDeserializer<WynncraftGuildList> {
        @Override
        public WynncraftGuildList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            Guild[] guilds = new Guild[array.size()];
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                guilds[i] = new Guild(obj.get("name").getAsString(), obj.get("prefix").getAsString());
            }
            return new WynncraftGuildList(guilds);
        }
    }
}
