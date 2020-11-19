package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class Anchor {

    private final int id;
    private BlockPos anchorPos;
    private String name;
    private ResourceLocation dimension;
    private int radius;
    private SortedMap<String, Double> muffledSounds = new TreeMap<>();

    public Anchor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Anchor(int id, String name, BlockPos anchorPos, ResourceLocation dimension, int radius, SortedMap<String, Double> muffledSounds) {
        this.id = id;
        this.name = name;
        this.anchorPos = anchorPos;
        this.dimension = dimension;
        this.radius = radius;
        this.muffledSounds = muffledSounds;
    }

    public BlockPos getAnchorPos() {
        return anchorPos;
    }

    private void setAnchorPos(BlockPos anchorPos) {
        this.anchorPos = anchorPos;
    }

    public int getAnchorId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private void setName(String name) {
        this.name = name;
    }

    public SortedMap<ResourceLocation, Double> getMuffledSounds() {
        SortedMap<ResourceLocation, Double> temp = new TreeMap<>();
        this.muffledSounds.forEach((R, D) -> temp.put(new ResourceLocation(R), D));
        return temp;
    }

    public void setMuffledSounds(SortedMap<ResourceLocation, Double> muffledSounds) {
        muffledSounds.forEach((R, D) -> this.muffledSounds.put(R.toString(), D));
    }

    public void addSound(ResourceLocation sound, double volume) {
        muffledSounds.put(sound.toString(), volume);
    }

    public void replaceSound(ResourceLocation sound, double volume) {
        muffledSounds.replace(sound.toString(), volume);
    }

    public String getX() {
        return anchorPos != null ? String.valueOf(anchorPos.getX()) : "";
    }

    public String getY() {
        return anchorPos != null ? String.valueOf(anchorPos.getY()) : "";
    }

    public String getZ() {
        return anchorPos != null ? String.valueOf(anchorPos.getZ()) : "";
    }

    public ResourceLocation getDimension() {
        return dimension;
    }

    private void setDimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    public void removeSound(ResourceLocation sound) {
        muffledSounds.remove(sound.toString());
    }

    public void setAnchor() {
        ClientPlayerEntity player = Objects.requireNonNull(Minecraft.getInstance().player);
        setAnchorPos(player.getPosition());
        setDimension(player.worldClient.getDimensionKey().getLocation());
        setRadius(this.getRadius() == 0 ? 32 : this.getRadius());
    }

    public void deleteAnchor() {
        setName("Anchor: " + this.getAnchorId());
        setAnchorPos(null);
        setDimension(null);
        setRadius(0);
        muffledSounds.clear();
    }

    public void editAnchor(String title, int radius) {
        setName(title);
        setRadius(radius);
    }
}
