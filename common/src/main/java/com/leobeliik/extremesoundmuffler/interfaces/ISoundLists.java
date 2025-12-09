package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.resources.Identifier;
import java.util.*;

public interface ISoundLists {

    Set<String> forbiddenSounds = new HashSet<>();
    Set<String> modsMuffled = new HashSet<>();
    List<Identifier> soundsList = new ArrayList<>();
    List<Identifier> recentSoundsList = new ArrayList<>();
    Map<Identifier, Double> muffledSounds = new HashMap<>();
    List<Anchor> anchorList = new ArrayList<>();
}