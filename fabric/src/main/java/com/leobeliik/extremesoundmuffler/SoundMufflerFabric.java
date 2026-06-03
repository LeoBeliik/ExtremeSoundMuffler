package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMInv;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.leobeliik.extremesoundmuffler.Constants.soundMufflerKey;

public class SoundMufflerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricConfig.init();
        ISoundLists.forbiddenSounds.addAll(FabricConfig.getForbiddenSounds());
        ISoundLists.forbiddenMods.addAll(FabricConfig.getForbiddenMods());
        ISoundLists.forbiddenCache.clear();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenMouseEvents.afterMouseRelease(screen).register(SoundMufflerFabric::onMouseReleasePre));
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenMouseEvents.beforeMouseClick(screen).register(SoundMufflerFabric::onMouseClickPre));
        KeyBindingHelper.registerKeyBinding(soundMufflerKey);
        Constants.isCustomSkinLoader = FabricLoader.getInstance().isModLoaded("customskinloader");

        //on mod keybind press
        ClientTickEvents.END_WORLD_TICK.register(level -> {
            while (soundMufflerKey.consumeClick()) {
                SoundMufflerCommon.openMainScreen();
            }
        });

        //load data when player joins the world
        ClientPlayConnectionEvents.JOIN.register((h, s, c) -> {
            Constants.useGlobalConfig = FabricConfig.getGlobalConfig();
            DataManager.loadData();
            if (ISoundLists.modsList.isEmpty()) {
                var mods = FabricLoader.getInstance().getAllMods();
                Map<String, String> modNames = mods.stream().collect(Collectors.toMap(mod ->
                        mod.getMetadata().getName(), mod ->
                        mod.getMetadata().getId(), (a, b) ->
                        b, () -> new HashMap<>(mods.size())));
                DataManager.loadMods(modNames);
            }
        });
    }

    //save the new coordinates for the inv button
    private static boolean onMouseReleasePre(Screen screen, MouseButtonEvent mouseButtonEvent, boolean b) {
        if (mouseButtonEvent.button() == 1) {
            for (GuiEventListener widget : screen.children()) {
                if (widget instanceof ESMInv btn && btn.isHovered()) {
                    if (screen instanceof CreativeModeInventoryScreen) {
                        FabricConfig.setCreativeInvButtonHorizontal(btn.getX());
                        FabricConfig.setCreativeInvButtonVertical(btn.getY());
                        FabricConfig.updateConfig(new JanksonValueSerializer(false));
                    } else {
                        FabricConfig.setInvButtonHorizontal(btn.getX());
                        FabricConfig.setInvButtonVertical(btn.getY());
                        FabricConfig.updateConfig(new JanksonValueSerializer(false));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    //change global and local config save
    private static void onMouseClickPre(Screen screen, MouseButtonEvent mouse) {
        if (mouse.button() == 0 && screen instanceof MufflerScreen MS && MS.btnGlobal.isHovered()) {
            FabricConfig.setGlobalConfig(!MS.btnGlobal.isToggled());
            Constants.useGlobalConfig = FabricConfig.getGlobalConfig();
            FabricConfig.updateConfig(new JanksonValueSerializer(false));
            DataManager.reload();
        }
    }

}
