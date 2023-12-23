package me.goldenshadow.poseidon.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.goldenshadow.poseidon.Poseidon;
import me.goldenshadow.poseidon.utils.DataProvider;
import me.goldenshadow.wynnapi.v3.guild.WynncraftGuild;
import me.goldenshadow.wynnapi.v3.player.WynncraftPlayer;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class ProfileManager extends ListenerAdapter {

    private static HashMap<String, Profile> profileHashMap = new HashMap<>();

    public static void registerProfile(Profile profile) {
        if (!profileHashMap.containsKey(profile.getMemberID())) {
            profileHashMap.put(profile.getMemberID(), profile);
            ProfileManager.saveToFile(profile);
        }
    }

    @Nullable
    public static Profile getProfile(String memberID) {
        return profileHashMap.getOrDefault(memberID, null);
    }

    @Nullable
    public static Profile getProfileOverIGN(String ign) {
        for (Profile p : profileHashMap.values()) {
            if (p.getInGameName().equals(ign)) return p;
        }
        return null;
    }

    public static List<Profile> getProfiles(boolean includeWithoutUUID) {
        if (includeWithoutUUID) {
            return new ArrayList<>(profileHashMap.values());
        } else {
            return profileHashMap.values().stream().filter(Profile::isUUIDPresent).collect(Collectors.toList());
        }
    }

    public static List<Profile> getGuildMemberProfiles(boolean includeWithoutUUID) {
        return getProfiles(includeWithoutUUID).stream().filter(p -> Poseidon.getDataProvider().isInGuild(p.getMcUUID())).collect(Collectors.toList());
    }


    public static void loadFromFile() throws FileNotFoundException, URISyntaxException {
        File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File dataFolder = new File(jar.getParentFile().getPath() + "/poseidon");
        File profileFolder = new File(dataFolder.getPath() + "/profiles");
        if (dataFolder.exists() && dataFolder.isDirectory() && profileFolder.exists() && profileFolder.isDirectory()) {
            List<File> fileList = new ArrayList<>();
            File[] files = profileFolder.listFiles();
            Gson gson = new GsonBuilder().create();
            if (files != null) {
                fileList = Arrays.asList(files);
            }
            HashMap<String, Profile> map = new HashMap<>();
            for (File file : fileList) {
                if (file.getName().contains(".json")) {
                    Reader reader = new FileReader(file);

                    Profile profile = gson.fromJson(reader, Profile.class);



                    map.put(profile.getMemberID(), profile);

                    System.out.println("Loaded profile of " + profile.getInGameName() + " (UUID: " + profile.isUUIDPresent() + ")");
                }
            }
            profileHashMap = map;





        } else {
            System.out.println("Data folder could not be found. A new one will be created when data in memory is saved!");
        }
    }

    public static void saveToFile(Profile p) {

        try {
            Gson gson = new GsonBuilder().create();
            File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File dataFolder = new File(jar.getParentFile().getPath() + "/poseidon");
            File profileFolder = new File(dataFolder.getPath() + "/profiles");
            dataFolder.mkdir();
            profileFolder.mkdir();
            File profileFile = new File(profileFolder.getPath() + "/" + p.getMemberID() + ".json");
            profileFile.createNewFile();
            Writer writer = new FileWriter(profileFile, false);
            gson.toJson(p, writer);
            writer.close();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            for (Profile p : profileHashMap.values()) {
                saveToFile(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayData() {
        for (Profile profile : getProfiles(false)) {
            if (Poseidon.getDataProvider().isInGuild(profile.getMcUUID())) {
                Poseidon.getDataProvider().updateData(profile);
            }
        }
    }

    public static void clearPlayData() {
        for (Profile profile : getProfiles(false)) {
            if (Poseidon.getDataProvider().isInGuild(profile.getMcUUID())) {
                profile.clearData();
            }
        }
    }
}
