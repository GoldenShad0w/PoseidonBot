package me.goldenshadow.poseidon.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.goldenshadow.poseidon.Poseidon;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileManager extends ListenerAdapter {

    private static HashMap<String, Profile> profileHashMap = new HashMap<>();

    public static void registerProfile(Profile profile) {
        if (!profileHashMap.containsKey(profile.getMemberID())) {
            profileHashMap.put(profile.getMemberID(), profile);
            ProfileManager.saveToFile();
        }
    }

    @Nullable
    public static Profile getProfile(String memberID) {
        return profileHashMap.getOrDefault(memberID, null);
    }

    public static List<Profile> getProfiles() {
        return new ArrayList<>(profileHashMap.values());
    }


    public static void loadFromFile() throws FileNotFoundException, URISyntaxException {
        File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File dataFolder = new File(jar.getParentFile().getPath() + "/poseidon");

        if (dataFolder.exists()) {
            File profileFile = new File(dataFolder.getPath() + "/profiles.json");
            if (profileFile.exists()) {

                Reader reader = new FileReader(profileFile);
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<HashMap<String, Profile>>(){}.getType();
                profileHashMap = gson.fromJson(reader, type);
                if (profileHashMap == null) profileHashMap = new HashMap<>();

            } else {
                System.out.println("Profiles file could not be found. A new one will be created when data in memory is saved!");
            }
        } else {
            System.out.println("Data folder could not be found. A new one will be created when data in memory is saved!");
        }
    }

    public static void saveToFile() {
        try {
            Gson gson = new GsonBuilder().create();
            File jar = new File(Poseidon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            File folder = new File(jar.getParentFile().getPath() + "/poseidon");
            folder.mkdir();
            File profileFile = new File(folder.getPath() + "/profiles.json");
            profileFile.createNewFile();
            Writer writer = new FileWriter(profileFile, false);
            gson.toJson(profileHashMap, writer);
            writer.close();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
