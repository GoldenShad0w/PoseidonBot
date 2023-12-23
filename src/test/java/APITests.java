import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.exceptions.APIException;
import me.goldenshadow.wynnapi.mojang.MojangPlayerData;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuild;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuildList;
import me.goldenshadow.wynnapi.v3.guild.WynncraftTerritoryList;
import me.goldenshadow.wynnapi.v3.player.*;
import org.junit.jupiter.api.Test;


public class APITests {

    private static WynncraftAPI api = new WynncraftAPI();

    @Test
    void mainPlayerStatsSuccessful() {
        WynncraftPlayer player = api.v3().player().mainStats("_GoldenShadow").run();
        assert player != null;
    }

    @Test
    void fullPlayerStatsSuccessful() {
        WynncraftFullPlayer player = api.v3().player().fullStats("_GoldenShadow").run();
        assert player != null;
    }

    @Test
    void characterSuccessful() {
        WynncraftCharacterList characterList = api.v3().player().characterList("_GoldenShadow").run();
        assert characterList != null;
        WynncraftCharacter character = api.v3().player().character("_GoldenShadow", characterList.characters().keySet().stream().findAny().get()).run();
        assert character != null;
    }

    @Test
    void abilityMapSuccessful() {
        WynncraftCharacterList list = api.v3().player().characterList("unicorn67").run();
        WynncraftAbilityMap abilityMap = api.v3().player().abilityMap("unicorn67", list.characters().keySet().stream().findAny().get()).run();
        assert abilityMap != null;
    }

    @Test
    void onlinePlayersSuccessful() {
        WynncraftOnlinePlayers onlinePlayers = api.v3().player().onlinePlayers().run();
        assert onlinePlayers != null;
        assert onlinePlayers.players() != null;
    }

    @Test
    void guildStatsSuccessful() {
        WynncraftGuild guild = api.v3().guild().guildStats("The Aquarium").run();
        assert guild != null;
    }

    @Test
    void guildListSuccessful() {
        WynncraftGuildList list = api.v3().guild().guildList().run();
        assert list != null;
        assert list.guilds() != null;
    }

    @Test
    void guildTerritoryListSuccessful() {
        WynncraftTerritoryList territoryList = api.v3().guild().guildTerritoryList().run();
        assert territoryList != null;
        assert territoryList.territories() != null;
    }

    @Test
    void uuidTest() {
        MojangPlayerData p = api.getMojangData("_GoldenShadow").run();
        System.out.println(p.name());
        System.out.println(p.id());
        System.out.println(p.getUUID());
    }
}
