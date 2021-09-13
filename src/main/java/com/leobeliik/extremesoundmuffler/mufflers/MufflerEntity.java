package com.leobeliik.extremesoundmuffler.mufflers;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MufflerEntity extends TileEntity implements ISoundLists {

    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();
    private BlockState mimic;
    private int radius;
    private boolean isMuffling;
    private Map<ResourceLocation, Float> currentMuffledSounds = new HashMap<>();
    private String title = "Sound Muffler";

    MufflerEntity() {
        super(MufflerRegistry.MUFFLER_ENTITY);
        radius = 16;
        isMuffling = true;
    }

    public MufflerEntity(BlockPos pos, int radius, boolean isMuffling, Map<ResourceLocation, Float> currentMuffledSounds, ITextComponent title) {
        super(MufflerRegistry.MUFFLER_ENTITY);
        this.worldPosition = pos;
        this.radius = radius;
        this.isMuffling = isMuffling;
        this.currentMuffledSounds.putAll(currentMuffledSounds);
        this.setTitle(title);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(MIMIC, mimic).build();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            if (!level.isClientSide()) {
                mufflerList.add(this);
            } else {
                mufflerClientList.add(this);
            }
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        mufflerList.remove(this);
        mufflerClientList.remove(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        clearCurrentMuffledSounds();
        mufflerList.remove(this);
        mufflerClientList.remove(this);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        saveMimic(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        BlockState oldMimic = mimic;
        CompoundNBT nbt = pkt.getTag();
        handleUpdateTag(getBlockState(), nbt);
        if (!Objects.equals(oldMimic, mimic) && level != null) {
            ModelDataManager.requestModelDataRefresh(this);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (!currentMuffledSounds.isEmpty()) {
            CompoundNBT compound = new CompoundNBT();
            currentMuffledSounds.forEach((R, F) -> compound.putFloat(R.toString(), F));
            nbt.put("muffledSounds", compound);
        }
        nbt.putString("title", title);
        nbt.putInt("radius", radius);
        nbt.putBoolean("isMuffling", isMuffling);
        saveMimic(nbt);
        return super.save(nbt);
    }

    private void saveMimic(CompoundNBT nbt) {
        if (mimic != null) {
            nbt.put("mimic", NBTUtil.writeBlockState(mimic));
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        CompoundNBT compound = nbt.getCompound("muffledSounds");
        if (!compound.isEmpty()) {
            compound.getAllKeys().forEach(key -> currentMuffledSounds.put(new ResourceLocation(key), compound.getFloat(key)));
        }
        title = nbt.getString("title");
        radius = nbt.getInt("radius");
        isMuffling = nbt.getBoolean("isMuffling");
        loadMimic(nbt);
    }

    private void loadMimic(CompoundNBT nbt) {
        if (nbt.contains("mimic")) {
            mimic = NBTUtil.readBlockState(nbt.getCompound("mimic"));
        }
    }

    void setMimic(BlockState mimic) {
        this.mimic = mimic;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    BlockState getMimic() {
        return mimic;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isMuffling() {
        return isMuffling;
    }

    public void setMuffling(boolean muffling) {
        isMuffling = muffling;
    }

    public Map<ResourceLocation, Float> getCurrentMuffledSounds() {
        return currentMuffledSounds;
    }

    public void setCurrentMuffledSounds(Map<ResourceLocation, Float> currentMuffledSounds) {
        clearCurrentMuffledSounds();
        this.currentMuffledSounds.putAll(currentMuffledSounds);
    }

    private void clearCurrentMuffledSounds() {
        currentMuffledSounds.clear();
    }

    void setTitle(ITextComponent title) {
        this.title = title.getString();
    }

    public ITextComponent getTitle() {
        return new StringTextComponent(title);
    }

    public void updateMuffler(Map<ResourceLocation, Float> muffledList, int radius, boolean isMuffling, ITextComponent title) {
        this.setCurrentMuffledSounds(muffledList);
        this.setRadius(radius);
        this.setMuffling(isMuffling);
        this.setTitle(title);
    }
}
