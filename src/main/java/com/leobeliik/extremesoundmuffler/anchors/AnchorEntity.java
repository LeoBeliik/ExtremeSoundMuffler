package com.leobeliik.extremesoundmuffler.anchors;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.SortedMap;
import java.util.TreeMap;

public class AnchorEntity extends TileEntity implements ISoundLists {
    private static final Logger LOGGER = LogManager.getLogger();

    private int radius;
    private SortedMap<ResourceLocation, Float> currentMuffledSounds;

    AnchorEntity() {
        super(AnchorRegistry.ANCHOR_ENTITY);
        radius = 32;
        currentMuffledSounds = new TreeMap<>();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (!currentMuffledSounds.isEmpty()) {
            CompoundNBT compound = new CompoundNBT();
            currentMuffledSounds.forEach((R, F) -> compound.putFloat(R.toString(), F));
            nbt.put("muffledSounds", compound);
            nbt.putInt("AnchorRadius", radius);
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
        radius = nbt.getInt("AnchorRadius");
    }

    @Override
    public void setChanged() {
        currentMuffledSounds.clear();
        currentMuffledSounds.putAll(muffledSounds);
        LOGGER.error(currentMuffledSounds.size() + " " + muffledSounds.size());

        super.setChanged();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public SortedMap<ResourceLocation, Float> getCurrentMuffledSounds() {
        return currentMuffledSounds;
    }

    public void setCurrentMuffledSounds(SortedMap<ResourceLocation, Float> currentMuffledSounds) {
        this.currentMuffledSounds.putAll(currentMuffledSounds);
    }
}
