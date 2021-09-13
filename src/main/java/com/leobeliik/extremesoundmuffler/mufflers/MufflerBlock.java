package com.leobeliik.extremesoundmuffler.mufflers;

import com.leobeliik.extremesoundmuffler.networking.Network;
import com.leobeliik.extremesoundmuffler.networking.PacketMufflers;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
public class MufflerBlock extends Block implements IWaterLoggable {

    private static final VoxelShape SHAPE = VoxelShapes.box(.2, .2, .2, .8, .8, .8);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private MufflerEntity entity;

    //TODO: make the block small and give the texure; also remeber to make the texture and size dinamic
    MufflerBlock() {
        super(Properties.of(Material.WOOL)
                .sound(SoundType.WOOL)
                .harvestLevel(1)
                .strength(1.0f)
        );
        setRegistryName("sound_muffler");
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
        list.add(new TranslationTextComponent("block.extremesoundmuffler.sound_muffler.description"));
        super.appendHoverText(stack, blockReader, list, flag);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MufflerEntity();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        ITextComponent title = itemStack.getHoverName();
        if (blockEntity instanceof MufflerEntity) {
            ((MufflerEntity) blockEntity).setTitle(title);
        }
        super.setPlacedBy(world, pos, blockState, livingEntity, itemStack);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MufflerEntity) {
            ItemStack item = player.getItemInHand(hand);
            if (world.isClientSide()) {
                return ActionResultType.SUCCESS;
            } else if (!item.isEmpty() && item.getItem() instanceof BlockItem) {
                if (((BlockItem) item.getItem()).getBlock() != Blocks.AIR && ((BlockItem) item.getItem()).getBlock() != this ) {
                    BlockState mimicState = ((BlockItem) item.getItem()).getBlock().defaultBlockState();
                    ((MufflerEntity) blockEntity).setMimic(mimicState);
                    return ActionResultType.SUCCESS;
                }
            } else {
                Network.sendToClient(new PacketMufflers(
                                ((MufflerEntity) blockEntity).getCurrentMuffledSounds(),
                                pos,
                                ((MufflerEntity) blockEntity).getRadius(),
                                ((MufflerEntity) blockEntity).isMuffling(),
                                ((MufflerEntity) blockEntity).getTitle(),
                                true),
                        (ServerPlayerEntity) player);
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, result);
    }
}
