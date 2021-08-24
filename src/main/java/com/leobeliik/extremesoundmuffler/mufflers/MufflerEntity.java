package com.leobeliik.extremesoundmuffler.mufflers;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

public class MufflerEntity extends TileEntity implements ISoundLists {

    private int radius;
    private boolean isMuffling;
    private Map<ResourceLocation, Float> currentMuffledSounds;
    private String title = "Sound Muffler";
    ;

    MufflerEntity() {
        super(MufflerRegistry.ANCHOR_ENTITY);
        radius = 16;
        isMuffling = true;
        currentMuffledSounds = new HashMap<>();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            mufflerList.add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        clearCurrentMuffledSounds();
        mufflerList.remove(this);
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
}
