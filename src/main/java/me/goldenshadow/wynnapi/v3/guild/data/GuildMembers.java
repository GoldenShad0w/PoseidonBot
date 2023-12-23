package me.goldenshadow.wynnapi.v3.guild.data;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record GuildMembers(int total, HashMap<UUID, Member> owner, HashMap<UUID, Member> chief, HashMap<UUID,
        Member> strategist, HashMap<UUID, Member> captain, HashMap<UUID, Member> recruiter, HashMap<UUID, Member> recruit) {

    public HashMap<UUID, Member> getAll() {
        HashMap<UUID, Member> map = new HashMap<>();
        map.putAll(owner);
        map.putAll(chief);
        map.putAll(strategist);
        map.putAll(captain);
        map.putAll(recruiter);
        map.putAll(recruit);
        return map;
    }



    public record Member(UUID uuid, boolean online, String server, long contributed, int contributionRank, Date joined, GuildRank rank, String ign) {

    }

    public static class MembersDeserializer implements JsonDeserializer<GuildMembers> {
        @Override
        public GuildMembers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int total = jsonObject.get("total").getAsInt();
            JsonObject owner = jsonObject.get("owner").getAsJsonObject();
            Map<String, JsonElement> map = owner.asMap();
            HashMap<UUID, Member> ownerMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                ownerMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.OWNER, obj.get("username").getAsString()));
            }

            JsonObject chief = jsonObject.get("chief").getAsJsonObject();
            map = chief.asMap();
            HashMap<UUID, Member> chiefMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                chiefMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.CHIEF, obj.get("username").getAsString()));
            }

            JsonObject strategist = jsonObject.get("strategist").getAsJsonObject();
            map = strategist.asMap();
            HashMap<UUID, Member> stratMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                stratMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.STRATEGIST, obj.get("username").getAsString()));
            }

            JsonObject captain = jsonObject.get("captain").getAsJsonObject();
            map = captain.asMap();
            HashMap<UUID, Member> captainMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                captainMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.CAPTAIN, obj.get("username").getAsString()));
            }

            JsonObject recruiter = jsonObject.get("recruiter").getAsJsonObject();
            map = recruiter.asMap();
            HashMap<UUID, Member> recruiterMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                recruiterMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.RECRUITER, obj.get("username").getAsString()));
            }

            JsonObject recruit = jsonObject.get("recruit").getAsJsonObject();
            map = recruit.asMap();
            HashMap<UUID, Member> recruitMap = new HashMap<>();
            for (String s : map.keySet()) {
                JsonObject obj = map.get(s).getAsJsonObject();
                recruitMap.put(UUID.fromString(s), new Member(UUID.fromString(s), obj.get("online").getAsBoolean(), getStringOrElse(obj, "server"), obj.get("contributed").getAsLong(), obj.get("contributionRank").getAsInt(), context.deserialize(obj.get("joined"), Date.class), GuildRank.RECRUIT, obj.get("username").getAsString()));
            }

            return new GuildMembers(total, ownerMap, chiefMap, stratMap, captainMap, recruiterMap, recruitMap);

        }

        private String getStringOrElse(JsonObject object, String s) {
            return object.get(s).isJsonNull() ? null : object.get(s).getAsString();
        }
    }

}
