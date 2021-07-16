package com.leobeliik.extremesoundmuffler.anchors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AnchorBlock extends Block implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
//TODO: make the block small and give the texure; also remeber to make the texture and size dinamic
    public AnchorBlock() {
        super(Properties.of(Material.WOOL)
                .sound(SoundType.WOOD)
                .harvestLevel(1)
                .strength(1.0f)
        );
        setRegistryName("sound_muffler");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AnchorEntity(new TranslationTextComponent("dunno"));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (world.isClientSide()) return ActionResultType.PASS;
        TileEntity entity = world.getBlockEntity(pos);
        /*if (entity instanceof AnchorEntity) {
            if (player.isCrouching())
                ((AnchorEntity) entity).setName(new TranslationTextComponent("new name"));
            else
                ((AnchorEntity) entity).showName();
        }*/
        return super.use(state, world, pos, player, hand, result);
    }
}
