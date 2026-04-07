package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class Anchor {

    private final int id;
    private BlockPos anchorPos;
    private String name;
    private Identifier dimension;
    private int radius;
    private SortedMap<String, Double> muffledSounds = new TreeMap<>();

    public Anchor(int id, String name) {
        this.id = id;
        this.name = name;
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

    public SortedMap<Identifier, Double> getMuffledSounds() {
        SortedMap<Identifier, Double> temp = new TreeMap<>();
        this.muffledSounds.forEach((R, D) -> temp.put(Identifier.parse(R), D));
        return temp;
    }

    public void setMuffledSounds(SortedMap<Identifier, Double> muffledSounds) {
        muffledSounds.forEach((R, D) -> this.muffledSounds.put(R.toString(), D));
    }

    public void addSound(Identifier sound, double volume) {
        muffledSounds.put(sound.toString(), volume);
    }

    public void replaceSound(Identifier sound, double volume) {
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

    public Identifier getDimension() {
        return dimension;
    }

    private void setDimension(Identifier dimension) {
        this.dimension = dimension;
    }

    public void removeSound(Identifier sound) {
        muffledSounds.remove(sound.toString());
    }

    public void setAnchor(int radius) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        setAnchorPos(player.blockPosition());
        setDimension(player.level().dimension().identifier());
        setRadius(this.getRadius() == 0 ? radius : this.getRadius());
    }

    public void deleteAnchor() {
        setName("Anchor " + this.getAnchorId());
        setAnchorPos(null);
        setDimension(null);
        setRadius(0);
        muffledSounds.clear();
    }

    public void editAnchor(String title, int radius) {
        setName(title);
        setRadius(radius);
    }

    public static Anchor getAnchor(SoundInstance sound) {
        BlockPos soundPos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel world = minecraft.level;

        if (player != null && sound.getIdentifier().getPath().contains("entity.minecart.inside")) {
            //give player coordinates if it's in the minecart, minecart.inside sound pos is always at 0
            soundPos = player.getOnPos();
        }
        for (Anchor anchor : ISoundLists.anchorList) {
            if (anchor.getAnchorPos() != null
                    && world != null
                    && world.dimension().identifier().equals(anchor.getDimension())
                    && soundPos.closerThan(anchor.getAnchorPos(), anchor.getRadius())
                    && anchor.getMuffledSounds().containsKey(sound.getIdentifier())) {
                return anchor;
            }
        }
        return null;
    }
}