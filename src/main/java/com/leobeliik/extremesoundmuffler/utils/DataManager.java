package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.network.PacketDataServer;
import net.minecraft.util.ResourceLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataManager implements IAnchorList, ISoundLists {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String soundsMuffledFile = "ESM/soundsMuffled.dat";
    private static final String anchorFile = "ESM/ServerWorld/Anchors.dat";

    public static void loadData() {
        if (muffledSounds.isEmpty()) {
            loadMuffledMap().forEach((R, F) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), F));
        }

        if (!Config.isClientSide()) {
            return;
        }

        if (loadAnchors() == null || Objects.requireNonNull(loadAnchors()).size() == 0) {
            setAnchors();
        } else {
            anchorList.addAll(Objects.requireNonNull(loadAnchors()));
        }
    }

    public static void saveData() {
        saveMuffledMap();

        if (Config.isClientSide()) {
            saveAnchors();
        } else {
            PacketDataServer.sendAnchorList();
        }
    }

    public static void setAnchors() {
        IntStream.rangeClosed(0, 9).forEach(i -> anchorList.add(i, new Anchor(i, "Anchor: " + i)));
    }

    private static void saveMuffledMap() {
        new File("ESM/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(ISoundLists.muffledSounds));
        } catch (IOException ignored) {
        }
    }

    private static Map<String, Float> loadMuffledMap() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(soundsMuffledFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Float>>() {}.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new HashMap<>();
        }
    }

    private static void saveAnchors() {
        new File("ESM/ServerWorld/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/ServerWorld/What is this.txt"), StandardCharsets.UTF_8)) {
            writer.write(new Gson().toJson("This is where Extreme sound muffler saves the Anchors for Server Worlds, when the mod is only loaded clientside"));
        } catch (Exception ignored) {}
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(anchorFile), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(IAnchorList.anchorList));
        } catch (IOException ignored) {}
    }

    private static List<Anchor> loadAnchors() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(anchorFile), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<List<Anchor>>() {}.getType());
        } catch (JsonSyntaxException | IOException ignored) {
            return null;
        }
    }
}
