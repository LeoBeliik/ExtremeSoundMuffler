package com.leobeliik.extremesoundmuffler.anchors;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.networking.Network;
import com.leobeliik.extremesoundmuffler.networking.PacketAnchorSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class AnchorBlock extends Block implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private AnchorEntity entity;

    //TODO: make the block small and give the texure; also remeber to make the texture and size dinamic
    AnchorBlock() {
        super(Properties.of(Material.WOOL)
                .sound(SoundType.WOOL)
                .harvestLevel(1)
                .strength(1.0f)
        );
        setRegistryName("sound_muffler");
    }

    @ParametersAreNonnullByDefault
    @Override
    public void destroy(IWorld world, BlockPos pos, BlockState state) {
        super.destroy(world, pos, state);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AnchorEntity();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        ITextComponent title = itemStack.getHoverName();
        if (blockEntity instanceof AnchorEntity && !title.equals(this.getName())) {
            ((AnchorEntity) blockEntity).setTitle(title);
        }
        super.setPlacedBy(world, pos, blockState, livingEntity, itemStack);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (world.isClientSide()) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AnchorEntity) {
                Network.sendToClient(new PacketAnchorSounds(
                                ((AnchorEntity) blockEntity).getCurrentMuffledSounds(),
                                pos,
                                ((AnchorEntity) blockEntity).getRadius(),
                                ((AnchorEntity) blockEntity).isMuffling(),
                                ((AnchorEntity) blockEntity).getTitle()),
                        (ServerPlayerEntity) player);
                return ActionResultType.CONSUME;
            }
        }
        return super.use(state, world, pos, player, hand, result);
    }
}
