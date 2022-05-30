package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SoundMufflerCommon {

    //whether should use the dark or light textures
    public static void renderGui() {
        String texture = CommonConfig.get().useDarkTheme().get() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        RenderSystem.setShaderTexture(0, (new ResourceLocation(Constants.MOD_ID, texture)));
    }

    public static void openMainScreen(){
        MufflerScreen.open();
    }

    //create empty keybind for the mod
    static KeyMapping mufflerKey() {
        return new KeyMapping(
                new TranslatableComponent("key.open_muffler_gui").getKey(),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                "key.categories.misc");
    }
}
