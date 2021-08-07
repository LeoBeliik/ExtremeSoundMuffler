package com.leobeliik.extremesoundmuffler.anchors;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

public class AnchorEntity extends TileEntity implements ISoundLists {

    private int radius;
    private boolean isMuffling;
    private Map<ResourceLocation, Float> currentMuffledSounds;

    AnchorEntity() {
        super(AnchorRegistry.ANCHOR_ENTITY);
        radius = 16;
        isMuffling = true;
        currentMuffledSounds = new HashMap<>();
        anchorList.add(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        clearCurrentMuffledSounds();
        anchorList.remove(this);
    }

    @Override
    public void setChanged() {
        //System.out.println(currentMuffledSounds);
        super.setChanged();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (!currentMuffledSounds.isEmpty()) {
            CompoundNBT compound = new CompoundNBT();
            currentMuffledSounds.forEach((R, F) -> compound.putFloat(R.toString(), F));
            nbt.putInt("anchorRadius", radius);
            nbt.putBoolean("anchorMuffling", isMuffling);
            nbt.put("muffledSounds", compound);
        }
        return super.save(nbt);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        CompoundNBT compound = nbt.getCompound("muffledSounds");
        if (!compound.isEmpty()) {
            for (String key : compound.getAllKeys()) {
                currentMuffledSounds.put(new ResourceLocation(key), compound.getFloat(key));
            }
        }
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
}
