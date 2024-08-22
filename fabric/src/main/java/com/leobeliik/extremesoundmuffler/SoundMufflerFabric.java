package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import static com.leobeliik.extremesoundmuffler.Constants.soundMufflerKey;

public class SoundMufflerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricConfig.init();
        ISoundLists.forbiddenSounds.addAll(FabricConfig.getForbiddenSounds());
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenMouseEvents.afterMouseRelease(screen).register(SoundMufflerFabric::onMouseReleasePre));
        KeyBindingHelper.registerKeyBinding(soundMufflerKey);

        //on mod keybind press
        ClientTickEvents.END_WORLD_TICK.register(level -> {
            while (soundMufflerKey.consumeClick()) {
                SoundMufflerCommon.openMainScreen();
            }
        });

        //load data when player joins the world
        ClientPlayConnectionEvents.JOIN.register((h, s, c) -> DataManager.loadData());
    }

    //save the new coordinates for the inv button
    private static void onMouseReleasePre(Screen screen, double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1) {
            for (GuiEventListener widget : screen.children()) {
                if (widget instanceof InvButton btn && btn.isHovered()) {
                    if (screen instanceof CreativeModeInventoryScreen) {
                        FabricConfig.setCreativeInvButtonHorizontal(btn.getX());
                        FabricConfig.setCreativeInvButtonVertical(btn.getY());
                        FabricConfig.updateConfig(new JanksonValueSerializer(false));
                    } else {
                        FabricConfig.setInvButtonHorizontal(btn.getX());
                        FabricConfig.setInvButtonVertical(btn.getY());
                        FabricConfig.updateConfig(new JanksonValueSerializer(false));
                    }
                    break;
                }
            }
        }
    }


}
