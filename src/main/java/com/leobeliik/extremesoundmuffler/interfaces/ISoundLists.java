package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.anchors.AnchorEntity;
import net.minecraft.util.ResourceLocation;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    Set<AnchorEntity> anchorList = new HashSet<>();
    SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    Map<ResourceLocation, Float> muffledSoundsList = new HashMap<>();

}