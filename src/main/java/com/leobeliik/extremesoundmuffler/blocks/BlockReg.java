package com.leobeliik.extremesoundmuffler.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class BlockReg {
    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static SoundMufflerBlock SOUNDMUFFLERBLOCK;

    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static TileEntityType<SoundMufflerTE> SOUNDMUFFLERBLOCK_TE;

    @ObjectHolder("extremesoundmuffler:sound_muffler")
    public static ContainerType<SoundMufflerContainer> SOUNDMUFFLERBLOCK_CONT;
}
