package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class SoundMuffler implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricConfig.init();
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenMouseEvents.afterMouseRelease(screen).register(SoundMuffler::onMouseReleasePre));
    }

    private static void onMouseReleasePre(Screen screen, double pMouseX, double pMouseY, int pButton) {
        if (screen instanceof InventoryScreen && pButton == 1 && InvButton.notHolding()) {
            FabricConfig.setInvButtonHorizontal(InvButton.getButtonX());
            FabricConfig.setInvButtonVertical(InvButton.getButtonY());
        }
    }


}
