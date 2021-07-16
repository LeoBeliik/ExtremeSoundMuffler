package com.leobeliik.extremesoundmuffler.anchors;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class AnchorEntity extends TileEntity {
    private ITextComponent name;
    private static final Logger LOGGER = LogManager.getLogger();


    public AnchorEntity(ITextComponent name) {
        super(registry.ANCHOR_ENTITY);
        this.name = name;
    }

    public AnchorEntity() {
        super(registry.ANCHOR_ENTITY);
        name = new TranslationTextComponent("new");
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putString("ANCHOR_NAME", name.getString());

        LOGGER.error("SAVING: " + nbt.getString("ANCHOR_NAME"));
        return super.save(nbt);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (!nbt.getString("ANCHOR_NAME").isEmpty()) {
            LOGGER.error("LOADING: " + nbt.getString("ANCHOR_NAME"));
            name = new TranslationTextComponent(nbt.getString("ANCHOR_NAME"));
        }
    }
}
