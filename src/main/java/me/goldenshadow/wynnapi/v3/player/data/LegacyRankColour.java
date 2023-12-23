package me.goldenshadow.wynnapi.v3.player.data;

import com.google.gson.*;
import me.goldenshadow.wynnapi.v3.guild.data.GuildMembers;

import java.lang.reflect.Type;

public record LegacyRankColour(String main, String sub) {

    public static class LegacyRankColourDeserializer implements JsonDeserializer<LegacyRankColour> {
        @Override
        public LegacyRankColour deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonPrimitive()) {
                return new LegacyRankColour(json.getAsString(), json.getAsString());
            } else {
                JsonObject jsonObject = json.getAsJsonObject();
                return new LegacyRankColour(jsonObject.get("main").getAsString(), jsonObject.get("sub").getAsString());
            }
        }
    }
}
