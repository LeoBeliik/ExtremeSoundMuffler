package com.leobeliik.extremesoundmuffler.interfaces;

import net.minecraft.util.ResourceLocation;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    Map<ResourceLocation, Double> muffledSounds = new HashMap<>();

}
