package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.util.ResourceLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DataManager implements ISoundLists {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String soundsMuffledFile = "soundsMuffled.dat";

   /* public static void loadData() {
        if (muffledSounds.isEmpty()) {
            loadMuffledMap().forEach((R, F) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), F));
        }
    }

    public static void saveData() {
        saveMuffledMap();
    }

    private static void saveMuffledMap() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(ISoundLists.muffledSounds));
        } catch (IOException ignored) {}
    }

    private static Map<String, Float> loadMuffledMap() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Float>>() {}.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new HashMap<>();
        }
    }*/
}