package com.leobeliik.extremesoundmuffler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

public class CommonClass {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {
    }

    public static void renderGui() {
        String texture = true ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        RenderSystem.setShaderTexture(0, (new ResourceLocation(Constants.MOD_ID, texture)));
    }
}
