package com.leobeliik.extremesoundmuffler.anchors;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class registry {
    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static AnchorBlock ANCHOR_BLOCK;

    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static TileEntityType<AnchorEntity> ANCHOR_ENTITY;
}
