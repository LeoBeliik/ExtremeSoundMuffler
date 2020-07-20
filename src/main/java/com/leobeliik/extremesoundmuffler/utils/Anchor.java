package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.util.ResourceLocation;

import java.util.SortedSet;
import java.util.TreeSet;

public class Anchor {

    private int id;
    private String name;
    private SortedSet<ResourceLocation> muffledSounds = new TreeSet<>();
    private double x;
    private double y;
    private double z;

    public Anchor(int id, double x, double y, double z) {
        this.id = id;
        this.name = "Anchor " + id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedSet<ResourceLocation> getMuffledSounds() {
        return muffledSounds;
    }

    public void setMuffledSounds(SortedSet<ResourceLocation> muffledSounds) {
        this.muffledSounds = muffledSounds;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void addSound(ResourceLocation sound) {
        muffledSounds.add(sound);
    }

    public void removeSound(ResourceLocation sound) {
        muffledSounds.remove(sound);
    }
}
