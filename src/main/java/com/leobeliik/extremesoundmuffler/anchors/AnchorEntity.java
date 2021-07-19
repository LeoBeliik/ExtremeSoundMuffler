package com.leobeliik.extremesoundmuffler.anchors;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.SortedMap;
import java.util.TreeMap;

public class AnchorEntity extends TileEntity implements ISoundLists {

    private int radius;
    private SortedMap<ResourceLocation, Float> muffledSounds;

    public AnchorEntity() {
        super(AnchorRegistry.ANCHOR_ENTITY);
        radius = 32;
        muffledSounds = new TreeMap<>();
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (!muffledSounds.isEmpty()) {
            CompoundNBT compound = new CompoundNBT();
            muffledSounds.forEach((R, F) -> {
                compound.putFloat(R.toString(), F);
            });
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
                muffledSounds.put(new ResourceLocation(key), compound.getFloat(key));
            }
        }
        radius = nbt.getInt("AnchorRadius");
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
