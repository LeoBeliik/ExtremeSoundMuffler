package com.leobeliik.extremesoundmuffler.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class BlockReg {
    private static final String HOLDER = "extremesoundmuffler:sound_muffler";
    @ObjectHolder(HOLDER)
    public static SoundMufflerBlock SOUNDMUFFLERBLOCK;

    @ObjectHolder(HOLDER)
    public static TileEntityType<SoundMufflerTE> SOUNDMUFFLERBLOCK_TE;

    @ObjectHolder(HOLDER)
    public static ContainerType<SoundMufflerContainer> SOUNDMUFFLERBLOCK_CONT;
}
