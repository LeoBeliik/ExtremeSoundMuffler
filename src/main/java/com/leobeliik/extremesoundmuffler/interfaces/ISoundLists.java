package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.mufflers.MufflerEntity;
import net.minecraft.util.ResourceLocation;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    Set<MufflerEntity> mufflerList = new HashSet<>();
    Set<MufflerEntity> mufflerClientList = new HashSet<>();
    Map<ResourceLocation, Float> playerMuffledList = new HashMap<>();
    SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();

}