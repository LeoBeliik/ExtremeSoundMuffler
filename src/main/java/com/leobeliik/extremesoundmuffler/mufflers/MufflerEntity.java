package com.leobeliik.extremesoundmuffler.mufflers;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

public class MufflerEntity extends TileEntity implements ISoundLists {

    private int radius;
    private boolean isMuffling;
    private Map<ResourceLocation, Float> currentMuffledSounds = new HashMap<>();;
    private String title = "Sound Muffler";

    MufflerEntity() {
        super(MufflerRegistry.ANCHOR_ENTITY);
        radius = 16;
        isMuffling = true;
    }

    public MufflerEntity(BlockPos pos, int radius, boolean isMuffling, Map<ResourceLocation, Float> currentMuffledSounds, ITextComponent title) {
        super(MufflerRegistry.ANCHOR_ENTITY);
        this.worldPosition = pos;
        this.radius = radius;
        this.isMuffling = isMuffling;
        this.currentMuffledSounds.putAll(currentMuffledSounds);
        this.setTitle(title);
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

    @Override
    public void setChanged() {
        super.setChanged();
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
        nbt.putInt("anchorRadius", radius);
        nbt.putBoolean("anchorMuffling", isMuffling);
        return super.save(nbt);
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
        radius = nbt.getInt("anchorRadius");
        isMuffling = nbt.getBoolean("anchorMuffling");
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
        this.currentMuffledSounds.putAll(currentMuffledSounds);
    }

    public void clearCurrentMuffledSounds() {
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
