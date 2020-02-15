package com.leobeliik.extremesoundmuffler.blocks;

import com.leobeliik.extremesoundmuffler.utils.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SoundMufflerBlock extends Block {

    private static final Set<BlockPos> tileEntities = new HashSet<>();
    private static final Map<BlockPos, Set<ResourceLocation>> tileEntityMuffler = new HashMap<>();


    public SoundMufflerBlock() {
        super(Properties.create(Material.WOOL)
                .sound(SoundType.CLOTH)
                .harvestLevel(1)
                .hardnessAndResistance(1.0f)
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
        return new SoundMufflerTE();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), ((INamedContainerProvider) tileEntity), tileEntity.getPos());
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        tileEntities.add(pos);
    }

    @Override
    public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        EventHandler.getSounds().remove(pos);
        tileEntityMuffler.remove(pos);
        tileEntities.remove(pos);
    }

    public static Set<BlockPos> getPositions() {
        return tileEntities;
    }

    static void setMufflerOnPosition(BlockPos pos) {
        tileEntities.add(pos);
    }

    static void setToMuffle(BlockPos pos, Set<ResourceLocation> toMuffle) {
        if (toMuffle.isEmpty()) return;
        if (!tileEntityMuffler.containsKey(pos)) {
            tileEntityMuffler.put(pos, new HashSet<>(toMuffle));
        } else {
            tileEntityMuffler.get(pos).addAll(toMuffle);
        }
    }

    static Map<BlockPos, Set<ResourceLocation>> getToMuffle() {
        return tileEntityMuffler;
    }

    public static Set<ResourceLocation> getMufflerOnPosition(BlockPos pos) {
        return tileEntityMuffler.get(pos);
    }
}