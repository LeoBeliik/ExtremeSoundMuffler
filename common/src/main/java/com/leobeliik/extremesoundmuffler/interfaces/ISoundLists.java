package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    Map<ResourceLocation, Boolean> forbiddenCache = new ConcurrentHashMap<>();
    Set<String> modsMuffled = new HashSet<>();
    List<ResourceLocation> soundsList = new ArrayList<>();
    List<ResourceLocation> recentSoundsList = new ArrayList<>();
    Map<ResourceLocation, Double> muffledSounds = new HashMap<>();
    List<Anchor> anchorList = new ArrayList<>();
}