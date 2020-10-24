package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();

}
