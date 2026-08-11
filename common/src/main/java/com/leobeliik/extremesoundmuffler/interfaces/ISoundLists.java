package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface ISoundLists {

    Map<String, Boolean> forbiddenCache = new ConcurrentHashMap<>();
    Map<String, Double> muffledSounds = new HashMap<>();
    Map<String, String> modsList = new HashMap<>();
    List<Anchor> anchorList = new ArrayList<>();
    List<String> soundsList = new ArrayList<>();
    List<String> recentSoundsList = new ArrayList<>();
    List<String> muffledBlocks = new ArrayList<>();
    Set<String> forbiddenSounds = new HashSet<>();
    Set<String> forbiddenMods = new HashSet<>();
}