package com.leobeliik.extremesoundmuffler.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
        return new SoundMufflerContainer(pos, id, world, entity);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        write(compound);
        return new SUpdateTileEntityPacket(pos, -1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();
        write(compound);
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        read(tag);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) { //Save
        super.write(compound);
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
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) { //Load
        super.read(compound);
        Set<ResourceLocation> sounds = new HashSet<>();
        ListNBT mufflers = compound.getList("mufflers", 10);
        for (int i = 0; i < mufflers.size(); i++) {
            CompoundNBT mufflersCompound = mufflers.getCompound(i);
            String soundsArray = mufflersCompound.getString(pos.toString()); //this is dumb
            SoundMufflerBlock.setMufflerOnPosition(pos);
            if (!soundsArray.equals("")) {
                for (String s : soundsArray.split(", ")) {
                    sounds.add(new ResourceLocation(s.replaceAll("]|\\[|minecraft:", "")));
                }
            }
            SoundMufflerBlock.setToMuffle(pos, sounds);
            sounds.clear();
        }
    }
}

