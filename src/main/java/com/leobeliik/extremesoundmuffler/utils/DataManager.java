package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataManager implements IAnchorList, ISoundLists {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String soundsMuffledFile = "ESM/soundsMuffled.dat";
    private static final String anchorFile = "ESM/ServerWorld/Anchors.dat";

    private static String getWorldName() {
        if (Minecraft.getInstance().getCurrentServer() != null)
            return Minecraft.getInstance().getCurrentServer().name;
        else if (Minecraft.getInstance().getSingleplayerServer() != null)
            return Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
        else
            return "ServerWorld";
    }

    public static void loadData() {
        if (muffledSounds.isEmpty()) {
            loadMuffledMap().forEach((R, F) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), F));
        }

        if (loadAnchors() != null && !loadAnchors().isEmpty()) {
            anchorList.addAll(Objects.requireNonNull(loadAnchors()));
        } else {
            setAnchors();
        }
    }

    public static void saveData() {
        saveMuffledMap();

        saveAnchors();/*
        if (Config.isClientSide()) {
        } else {
            CompoundTag anchorNBT = new CompoundTag();
            IntStream.rangeClosed(0, 9).forEach(i -> anchorNBT.put("anchor" + i, DataManager.serializeNBT(anchorList.get(i))));
            //Network.sendToServer(new PacketDataServer(anchorNBT));
        }*/
    }

    public static void setAnchors() {
        IntStream.rangeClosed(0, 9).forEach(i -> anchorList.add(i, new Anchor(i, "Anchor: " + i)));
    }


    private static CompoundTag serializeNBT(Anchor anchor) {

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

    public static Anchor deserializeNBT(CompoundTag nbt) {
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

    private static void saveMuffledMap() {
        new File("ESM/").mkdir();
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