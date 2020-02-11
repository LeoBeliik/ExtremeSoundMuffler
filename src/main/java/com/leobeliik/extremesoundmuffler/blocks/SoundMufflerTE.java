package com.leobeliik.extremesoundmuffler.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.leobeliik.extremesoundmuffler.blocks.BlockReg.SOUNDMUFFLERBLOCK_TE;

public class SoundMufflerTE extends TileEntity implements INamedContainerProvider {

    public SoundMufflerTE() {
        super(SOUNDMUFFLERBLOCK_TE);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(Objects.requireNonNull(getType().getRegistryName()).getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity entity) {
        assert world != null;
        return new SoundMufflerContainer(pos, id, world, entity);
    }

    //certainly there's a better way to do this.. but i duuno how :D
    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) { //Save
        ListNBT posList = new ListNBT();
        ListNBT mapList = new ListNBT();
        Map<BlockPos, Set<ResourceLocation>> mufflerPos = SoundMufflerBlock.getToMuffle();
        if (mufflerPos != null) {
            mufflerPos.forEach((pos, sounds) -> {
                if (sounds.size() > 0) {
                    CompoundNBT map = new CompoundNBT();
                    map.putString(pos.toString(), sounds.toString());
                    mapList.add(map);
                    CompoundNBT positions = new CompoundNBT();
                    positions.putIntArray("mufflerPos", setPosition(pos));
                    posList.add(positions);
                }
            });
        }
        compound.put("mufflers", mapList);
        compound.put("mufflerPos", posList);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) { //Load
        Set<ResourceLocation> sounds = new HashSet<>();
        ListNBT mufflers = compound.getList("mufflers", 10);
        ListNBT positions = compound.getList("mufflerPos", 10);
        for (int i = 0; i < positions.size(); i++) {
            CompoundNBT mufflersCompound = mufflers.getCompound(i);
            CompoundNBT positionsCompound = positions.getCompound(i);
            BlockPos mufflersPositions = getPosition(positionsCompound.getIntArray("mufflerPos"));
            if (SoundMufflerBlock.getPositions().contains(mufflersPositions)) return; //prevents unnecessary loads (hopefully)
            SoundMufflerBlock.setMufflerOnPosition(mufflersPositions);
            SoundMufflerBlock.getPositions().forEach(pos -> {
                String soundsArray = mufflersCompound.getString(pos.toString());
                if (!soundsArray.equals("")) {
                    for (String s : soundsArray.split(", ")) {
                        sounds.add(new ResourceLocation(s.replaceAll("]|\\[|minecraft:", "")));
                    }
                }
                SoundMufflerBlock.setToMuffle(pos, sounds);
                sounds.clear();
            });
        }
        super.read(compound);
    }

    private int[] setPosition(BlockPos pos) {
        int[] coord = new int[3];
        coord[0] = pos.getX();
        coord[1] = pos.getY();
        coord[2] = pos.getZ();
        return coord;
    }

    private BlockPos getPosition(int[] pos) {
        return new BlockPos(pos[0], pos[1], pos[2]);
    }
}

