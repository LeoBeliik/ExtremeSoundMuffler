package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    List<ResourceLocation> soundsList = new ArrayList<>();
    List<ResourceLocation> recentSoundsList = new ArrayList<>();
    Map<ResourceLocation, Double> muffledSounds = new HashMap<>();
    List<Anchor> anchorList = new ArrayList<>();
}