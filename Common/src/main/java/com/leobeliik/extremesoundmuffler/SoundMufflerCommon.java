package com.leobeliik.extremesoundmuffler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public class SoundMufflerCommon {
    public static void renderGui() {
        String texture = CommonConfig.get().useDarkTheme().get() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        RenderSystem.setShaderTexture(0, (new ResourceLocation(Constants.MOD_ID, texture)));
    }
}
