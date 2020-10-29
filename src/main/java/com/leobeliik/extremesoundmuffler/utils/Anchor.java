package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    private SortedMap<ResourceLocation, Double> muffledSounds = new TreeMap<>();

    public Anchor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public BlockPos getAnchorPos() {
        return anchorPos;
    }

    public void setAnchorPos(BlockPos anchorPos) {
        this.anchorPos = anchorPos;
    }

    public int getId() {
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

    public void setName(String name) {
        this.name = name;
    }

    public SortedMap<ResourceLocation, Double> getMuffledSounds() {
        return muffledSounds;
    }

    public void setMuffledSounds(SortedMap<ResourceLocation, Double> muffledSounds) {
        this.muffledSounds = muffledSounds;
    }

    public void addSound(ResourceLocation sound, double volume) {
        muffledSounds.put(sound, volume);
    }

    public void replaceSound(ResourceLocation sound, double volume) {
        muffledSounds.replace(sound,volume);
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

    public void setDimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    public void removeSound(ResourceLocation sound) {
        muffledSounds.remove(sound);
    }

    public void setAnchor() {
        ClientPlayerEntity player = Objects.requireNonNull(Minecraft.getInstance().player);
        this.setAnchorPos(player.getPosition());
        this.setDimension(player.worldClient.getDimensionKey().getLocation());
        this.setRadius(this.getRadius() == 0 ? 32 : this.getRadius());
    }

    public void deleteAnchor() {
        this.setName("Anchor: " + this.getId());
        this.setAnchorPos(null);
        this.setDimension(null);
        this.setRadius(0);
        this.getMuffledSounds().clear();
    }

    public void editAnchor(String title, int radious) {
        this.setName(title);
        this.setRadius(radious);
    }
}
