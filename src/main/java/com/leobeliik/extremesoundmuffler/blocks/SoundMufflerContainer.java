package com.leobeliik.extremesoundmuffler.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.leobeliik.extremesoundmuffler.blocks.BlockReg.SOUNDMUFFLERBLOCK_CONT;

public class SoundMufflerContainer extends Container {

    private final TileEntity tileEntity;
    private final PlayerEntity playerEntity;


    public SoundMufflerContainer(BlockPos pos, int id, World world, PlayerEntity player) {
        super(SOUNDMUFFLERBLOCK_CONT, id);
        tileEntity = world.getTileEntity(pos);
        this.playerEntity = player;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(Objects.requireNonNull(tileEntity.getWorld()), tileEntity.getPos()), playerEntity, BlockReg.SOUNDMUFFLERBLOCK);
    }

    public BlockPos getTileEntityPos() {
        return tileEntity.getPos();
    }
}
