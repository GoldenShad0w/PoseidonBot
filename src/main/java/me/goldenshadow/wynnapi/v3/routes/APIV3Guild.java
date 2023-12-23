package me.goldenshadow.wynnapi.v3.routes;

import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.exceptions.APIRequestException;
import me.goldenshadow.wynnapi.v3.endpoints.APIV3GuildList;
import me.goldenshadow.wynnapi.v3.endpoints.APIV3GuildStats;
import me.goldenshadow.wynnapi.v3.endpoints.APIV3TerritoryList;

import javax.annotation.CheckReturnValue;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class APIV3Guild {

    private final WynncraftAPI api;

    public APIV3Guild(WynncraftAPI api) {
        this.api = api;
    }

    @CheckReturnValue
    public APIV3GuildStats guildStats(String guildName) {
        return new APIV3GuildStats(api.getBaseURL() + "v3/guild/" + guildName.replace(" ", "%20") + "?identifier=uuid", api);
    }

    @CheckReturnValue
    public APIV3GuildStats guildStatsPrefix(String prefix) {
        return new APIV3GuildStats(api.getBaseURL() + "v3/guild/prefix/" + prefix + "?identifier=uuid", api);
    }

    @CheckReturnValue
    public APIV3GuildList guildList() {
        return new APIV3GuildList(api.getBaseURL() + "v3/guild/list/guild", api);
    }

    @CheckReturnValue
    public APIV3TerritoryList guildTerritoryList() {
        return new APIV3TerritoryList(api.getBaseURL() + "v3/guild/list/territory", api);
    }
}
