package me.goldenshadow.wynnapi.v3.routes;


import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.exceptions.APIRequestException;
import me.goldenshadow.wynnapi.v3.endpoints.*;

import javax.annotation.CheckReturnValue;

public class APIV3Player {

    private final WynncraftAPI api;

    public APIV3Player(WynncraftAPI api) {
        this.api = api;
    }

    @CheckReturnValue
    public APIV3PlayerMainStats mainStats(String playerName) {
        if (isInvalidUsername(playerName))
            throw new APIRequestException("The provided username: " + playerName + " is not a valid Minecraft username");
        return new APIV3PlayerMainStats(api.getBaseURL() + "v3/player/" + playerName, api);
    }

    @CheckReturnValue
    public APIV3PlayerMainStats mainStatsUUID(String uuid) {
        if (isInvalidUUID(uuid)) {
            throw new APIRequestException("The provided UUID: " + uuid + " is not a valid UUID");
        }
        return new APIV3PlayerMainStats(api.getConfig().getBaseURL() + "v3/player/" + uuid, api);
    }

    @CheckReturnValue
    public APIV3PlayerFullStats fullStats(String playerName) {
        if (isInvalidUsername(playerName))
            throw new APIRequestException("The provided username: " + playerName + " is not a valid Minecraft username");
        return new APIV3PlayerFullStats(api.getConfig().getBaseURL() + "v3/player/" + playerName + "?fullResult=True", api);
    }

    @CheckReturnValue
    public APIV3PlayerFullStats fullStatsUUID(String uuid) {
        if (isInvalidUUID(uuid)) {
            throw new APIRequestException("The provided UUID: " + uuid + " is not a valid UUID");
        }
        return new APIV3PlayerFullStats(api.getConfig().getBaseURL() + "v3/player/" + uuid + "?fullResult=True", api);
    }

    @CheckReturnValue
    public APIV3CharacterList characterList(String playerName) {
        if (isInvalidUsername(playerName))
            throw new APIRequestException("The provided username: " + playerName + " is not a valid Minecraft username");
        return new APIV3CharacterList(api.getConfig().getBaseURL() + "v3/player/" + playerName + "/characters", api);
    }

    @CheckReturnValue
    public APIV3CharacterList characterListUUID(String uuid) {
        if (isInvalidUUID(uuid)) {
            throw new APIRequestException("The provided UUID: " + uuid + " is not a valid UUID");
        }
        return new APIV3CharacterList(api.getConfig().getBaseURL() + "v3/player/" + uuid + "/characters", api);
    }

    @CheckReturnValue
    public APIV3Character character(String playerName, String characterUUID) {
        if (isInvalidUsername(playerName) || isInvalidUUID(characterUUID))
            throw new APIRequestException("The provided username: " + playerName + " is not a valid Minecraft username");
        return new APIV3Character(api.getConfig().getBaseURL() + "v3/player/" + playerName + "/characters/" + characterUUID, api);
    }

    @CheckReturnValue
    public APIV3Character characterUUID(String uuid, String characterUUID) {
        if (isInvalidUUID(uuid) || isInvalidUUID(characterUUID)) {
            throw new APIRequestException("The provided UUID: " + uuid + " is not a valid UUID");
        }
        return new APIV3Character(api.getConfig().getBaseURL() + "v3/player/" + uuid + "/characters/" + characterUUID, api);
    }

    @CheckReturnValue
    public APIV3AbilityMap abilityMap(String playerName, String characterUUID) {
        if (isInvalidUsername(playerName) || isInvalidUUID(characterUUID))
            throw new APIRequestException("The provided username: " + playerName + " is not a valid Minecraft username");
        return new APIV3AbilityMap(api.getConfig().getBaseURL() + "v3/player/" + playerName + "/characters/" + characterUUID + "/abilities", api);
    }

    @CheckReturnValue
    public APIV3AbilityMap abilityMapUUID(String uuid, String characterUUID) {
        if (isInvalidUUID(uuid) || isInvalidUUID(characterUUID)) {
            throw new APIRequestException("The provided UUID: " + uuid + " is not a valid UUID");
        }
        return new APIV3AbilityMap(api.getConfig().getBaseURL() + "v3/player/" + uuid + "/characters/" + characterUUID + "/abilities", api);
    }

    @CheckReturnValue
    public APIV3OnlinePlayers onlinePlayers() {
        return new APIV3OnlinePlayers(api.getConfig().getBaseURL() + "v3/player", api);
    }


    private static boolean isInvalidUsername(String s) {
        return !s.matches("[a-zA-Z0-9_]{1,16}");
    }

    private static boolean isInvalidUUID(String s) {
        return !s.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    }
}
