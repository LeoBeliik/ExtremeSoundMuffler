package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class WorldEventHandler implements ISoundLists, IAnchorList {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (muffledSounds.isEmpty()) {
            DataManager.loadMuffledMap().forEach((R, V) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), V));
        }

        if (!Config.isClientSide()) {
            return;
        }

        if (DataManager.loadAnchors() == null || Objects.requireNonNull(DataManager.loadAnchors()).size() == 0) {
            DataManager.setAnchors();
        } else {
            anchorList.addAll(Objects.requireNonNull(DataManager.loadAnchors()));
        }
    }
}
