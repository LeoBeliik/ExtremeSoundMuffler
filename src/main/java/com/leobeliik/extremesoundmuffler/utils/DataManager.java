package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataManager implements ISoundLists {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadData() {
        loadMuffledMap().forEach((R, F) -> muffledSounds.put(new ResourceLocation(R), F));
        if (!Config.getDisableAchors()) {
            anchorList.clear();
            anchorList.addAll(loadAnchors());
        }
    }

    public static void saveData() {
        if (!muffledSounds.isEmpty()) {
            saveMuffledMap();
        }
        if (!Config.getDisableAchors() && anchorList.stream().anyMatch(A -> A.getAnchorPos() != null)) {
            saveAnchors();
        }
    }

    private static String getWorldName() {
        if (Minecraft.getInstance().getCurrentServer() != null) {
            return Minecraft.getInstance().getCurrentServer().name;
        } else if (Minecraft.getInstance().getSingleplayerServer() != null) {
            return Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
        } else {
            return "ServerWorld";
        }
    }

    private static CompoundTag serializeAnchor(Anchor anchor) {

        CompoundTag anchorNBT = new CompoundTag();
        CompoundTag muffledNBT = new CompoundTag();

        anchorNBT.putInt("ID", anchor.getAnchorId());
        anchorNBT.putString("NAME", anchor.getName());

        if (anchor.getAnchorPos() == null) {
            return anchorNBT;
        }

        anchorNBT.put("POS", NbtUtils.writeBlockPos(anchor.getAnchorPos()));
        anchorNBT.putString("DIM", anchor.getDimension().toString());
        anchorNBT.putInt("RAD", anchor.getRadius());
        anchor.getMuffledSounds().forEach((R, F) -> muffledNBT.putFloat(R.toString(), F));
        anchorNBT.put("MUFFLED", muffledNBT);

        return anchorNBT;
    }

    public static Anchor deserializeAnchor(CompoundTag nbt) {
        SortedMap<String, Float> muffledSounds = new TreeMap<>();
        CompoundTag muffledNBT = nbt.getCompound("MUFFLED");

        for (String key : muffledNBT.getAllKeys()) {
            muffledSounds.put(key, muffledNBT.getFloat(key));
        }

        if (!nbt.contains("POS")) {
            return new Anchor(nbt.getInt("ID"), nbt.getString("NAME"));
        } else {
            return new Anchor(nbt.getInt("ID"),
                    nbt.getString("NAME"),
                    NbtUtils.readBlockPos(nbt.getCompound("POS")),
                    new ResourceLocation(nbt.getString("DIM")),
                    nbt.getInt("RAD"),
                    muffledSounds);
        }
    }
//TODO anchors are creating more than 9
    private static void saveMuffledMap() {
        new File("ESM/").mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/soundsMuffled.dat"), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(muffledSounds));
        } catch (IOException ignored) {}
    }

    private static Map<String, Float> loadMuffledMap() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream("ESM/soundsMuffled.dat"), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Float>>() {
            }.getType());
        } catch (JsonSyntaxException | IOException e) {
            return new HashMap<>();
        }
    }

    private static void saveAnchors() {
        new File("ESM/" + getWorldName()).mkdir();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/" + getWorldName() + "/anchors.dat"), StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(anchorList));
        } catch (IOException ignored) {}
    }

    private static List<Anchor> loadAnchors() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream("ESM/" + getWorldName() + "/anchors.dat"), StandardCharsets.UTF_8)) {
            return gson.fromJson(new JsonReader(reader), new TypeToken<List<Anchor>>() {}.getType());
        } catch (JsonSyntaxException | IOException ignored) {
            return IntStream.range(0, 10).mapToObj(i -> new Anchor(i, "Anchor " + i)).collect(Collectors.toList());
        }
    }
}