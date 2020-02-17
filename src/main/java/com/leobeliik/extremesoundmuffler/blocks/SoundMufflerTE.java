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
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) { //Save
        ListNBT mufflersList = new ListNBT();
        Map<BlockPos, Set<ResourceLocation>> mufflerPos = SoundMufflerBlock.getToMuffle();
        if (mufflerPos != null) {
            mufflerPos.forEach((pos, sounds) -> {
                if (sounds.size() > 0) {
                    CompoundNBT map = new CompoundNBT();
                    map.putString(pos.toString(), sounds.toString());
                    mufflersList.add(map);
                }
            });
        }
        compound.put("mufflers", mufflersList);
        return super.write(compound);
    }

    @Override
    public void onLoad() {
        SoundMufflerBlock.setMufflerOnPosition(this.pos);
    }

    @Override
    public void read(CompoundNBT compound) { //Load
        Set<ResourceLocation> sounds = new HashSet<>();
        ListNBT mufflers = compound.getList("mufflers", 10);
        for (int i = 0; i < mufflers.size(); i++) {
            CompoundNBT mufflersCompound = mufflers.getCompound(i);
            BlockPos position = getPosition(mufflersCompound.keySet().toString());
            String soundsArray = mufflersCompound.getString(position.toString()); //this is dumb
            if (SoundMufflerBlock.getPositions().contains(position)) return; //Prevents unnecessary reloads
            SoundMufflerBlock.setMufflerOnPosition(position);
            if (!soundsArray.equals("")) {
                for (String s : soundsArray.split(", ")) {
                    sounds.add(new ResourceLocation(s.replaceAll("]|\\[|minecraft:", "")));
                }
            }
            SoundMufflerBlock.setToMuffle(position, sounds);
            sounds.clear();
        }
        super.read(compound);
    }

    private BlockPos getPosition(String pos) {
        String[] posArray = pos.replaceAll("[^-?,0-9]", "").split(",");
        int x = Integer.parseInt(posArray[0]);
        int y = Integer.parseInt(posArray[1]);
        int z = Integer.parseInt(posArray[2]);
        return new BlockPos(x, y, z);
    }
}

