package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JsonIO {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String soundsMuffledFile = "ESM/soundsMuffled.dat";
    private static final String anchorFile = "ESM/Anchors.dat";
    private static final Logger LOGGER = LogManager.getLogger();


    public static void saveMuffledMap(Map<ResourceLocation, Double> muffledList) {
        new File("ESM/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(muffledList));
        } catch (IOException ignored) {
        }
    }

    public static Map<String, Double> loadMuffledMap() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Double>>() {
            }.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new HashMap<>();
        }
    }

    public static void saveAnchors(List<Anchor> anchor) {
        new File("ESM/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(anchorFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(anchor));
        } catch (IOException e) {
            LOGGER.error("SAVE ANCHOR: " + e);
        }
    }

    public static List<Anchor> loadAnchors() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(anchorFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<List<Anchor>>() {
            }.getType());
        } catch (JsonSyntaxException | IOException ignored) {
            return null;
        }
    }
}
