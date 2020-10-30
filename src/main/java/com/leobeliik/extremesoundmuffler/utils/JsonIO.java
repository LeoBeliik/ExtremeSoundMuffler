package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonIO {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File anchorFile = new File("saves/ESM/ServerWorld/");

    public static void saveMuffledMap(File file, Map<ResourceLocation, Double> list) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(list));
        } catch (IOException ignored) { }
    }

    public static Map<String, Double> loadMuffledMap(File file) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Double>>() {}.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new HashMap<>();
        }
    }

    public static void saveAnchors(Anchor anchor) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(anchorFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(anchor));
        } catch (IOException ignored) { }
    }

    public static Anchor loadAnchors(int id) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(anchorFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Anchor>() {}.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new Anchor(id, "Anchor: " + id);
        }
    }
}
