package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import static com.leobeliik.extremesoundmuffler.Constants.openMufflerScreen;

public class SoundMufflerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricConfig.init();
        ISoundLists.forbiddenSounds.addAll(FabricConfig.getForbiddenSounds());
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenMouseEvents.afterMouseRelease(screen).register(SoundMufflerFabric::onMouseReleasePre));
        KeyBindingHelper.registerKeyBinding(openMufflerScreen);

        ClientTickEvents.END_WORLD_TICK.register(level -> {
            while (openMufflerScreen.consumeClick()) {
                SoundMufflerCommon.openMainScreen();
            }
        });
    }

    private static void onMouseReleasePre(Screen screen, double pMouseX, double pMouseY, int pButton) {
        if (screen instanceof InventoryScreen && pButton == 1 && InvButton.notHolding()) {
            FabricConfig.setInvButtonHorizontal(InvButton.getButtonX());
            FabricConfig.setInvButtonVertical(InvButton.getButtonY());
        }
    }


}
