package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.util.ResourceLocation;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    Map<ResourceLocation, Float> muffledSounds = new HashMap<>();
    List<Anchor> anchorList = new ArrayList<>();
}