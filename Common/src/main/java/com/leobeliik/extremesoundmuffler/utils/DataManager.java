package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataManager implements ISoundLists {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadData() {
        loadMuffledMap().forEach((R, D) -> muffledSounds.put(new ResourceLocation(R), D));

        if (!CommonConfig.get().disableAnchors().get()) {
            anchorList.clear();
            anchorList.addAll(loadAnchors());
        }

        saveData();
    }

    public static void saveData() {
        saveMuffledMap();

        if (!CommonConfig.get().disableAnchors().get()) {
            saveAnchors();
        }
    }

    private static String getWorldName() {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        String name = server != null ? server.getWorldData().getLevelName().strip() : "ServerWorld";

        //prevent to create a directory with reserved characters
        try {
            return FileUtil.findAvailableName(Path.of(""), name, "");
        } catch (IOException e) {
            Constants.LOG.error("ESM: error trying to create a folder with the name of the world " + name, e);
            return "ServerWorld";
        }
    }

    private static void saveMuffledMap() {
        new File("ESM/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/soundsMuffled.dat"), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(muffledSounds));
        } catch (IOException e) {
            Constants.LOG.error(Component.translatable("log.error.saveMuffledList", e).getString());
        }
    }

    private static Map<String, Double> loadMuffledMap() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream("ESM/soundsMuffled.dat"), StandardCharsets.UTF_8)) {
            return Objects.requireNonNullElseGet(gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Double>>() {
            }.getType()), HashMap::new);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Constants.LOG.warn(Component.translatable("log.warn.loadMuffledList").getString());
            } else {
                Constants.LOG.error(Component.translatable("log.error.loadMuffledList", e).getString());
            }
            return new HashMap<>();
        }
    }

    private static void saveAnchors() {
        new File("ESM/", getWorldName()).mkdirs();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/" + getWorldName() + "/anchors.dat"), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(anchorList));
        } catch (IOException e) {
            Constants.LOG.error(Component.translatable("log.error.saveAnchorList", e).getString());
        }
    }

    private static List<Anchor> loadAnchors() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream("ESM/" + getWorldName() + "/anchors.dat"), StandardCharsets.UTF_8)) {
            return Objects.requireNonNullElseGet(gson.fromJson(new JsonReader(reader), new TypeToken<List<Anchor>>() {
            }.getType()), DataManager::emptyAnchorList);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Constants.LOG.warn(Component.translatable("log.warn.loadAnchorList").getString());
            } else {
                Constants.LOG.error(Component.translatable("log.error.loadAnchorList", e).getString());
            }
            return emptyAnchorList();
        }
    }

    private static List<Anchor> emptyAnchorList() {
        return IntStream.range(0, 10).mapToObj(i -> new Anchor(i, "Anchor " + i)).collect(Collectors.toList());
    }
}