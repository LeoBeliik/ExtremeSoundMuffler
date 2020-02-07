package com.leobeliik.extremesoundmuffler.setup;

import com.leobeliik.extremesoundmuffler.blocks.BlockReg;
import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        ScreenManager.registerFactory(BlockReg.SOUNDMUFFLERBLOCK_CONT, SoundMufflerScreen::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}