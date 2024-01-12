import ch.qos.logback.classic.jul.JULHelper;
import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.poseidon.profile.PlayData;
import me.goldenshadow.poseidon.profile.Profile;
import me.goldenshadow.poseidon.profile.ProfileManager;
import me.goldenshadow.poseidon.profile.Rank;
import me.goldenshadow.poseidon.utils.DataProvider;
import me.goldenshadow.wynnapi.WynncraftAPI;
import me.goldenshadow.wynnapi.exceptions.APIException;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTests {

    private static WynncraftAPI api = new WynncraftAPI();

    @Test
    void getDataWorksCorrectly() {
        //Poseidon.wynnAPI = new WynncraftAPI();
        //Poseidon.dataProvider = new DataProvider();
        ProfileManager.registerProfile(new Profile("494589147873542175", "_GoldenShadow", Rank.NARWHAL));
        ProfileManager.registerProfile(new Profile("1154411794836754542", "PokemonGamer11", Rank.NARWHAL));
        ProfileManager.registerProfile(new Profile("244187630462435338", "Barled", Rank.NARWHAL));
        ProfileManager.updatePlayData();
        System.out.println((ProfileManager.getProfile("494589147873542175").getPlayData(-1).wars()));
    }

    @Test
    void friendlyXpWorksCorrectly() {
        PlayData p1 = new PlayData(0, 0, 673_456L, 0);
        Assertions.assertEquals("673.5 Thousand" ,p1.getFriendlyXP());

        PlayData p2 = new PlayData(0, 0, 31, 0);
        Assertions.assertEquals("31", p2.getFriendlyXP());

        PlayData p3 = new PlayData(0, 0, 4_456_752, 0);
        Assertions.assertEquals("4.5 Million", p3.getFriendlyXP());

        PlayData p4 = new PlayData(0, 0, 76_485_388_999L, 0);
        Assertions.assertEquals("76.5 Billion", p4.getFriendlyXP());

        PlayData p5 = new PlayData(0, 0, 32_346_444_657_314L, 0);
        Assertions.assertEquals("32.3 Trillion", p5.getFriendlyXP());
    }
}
