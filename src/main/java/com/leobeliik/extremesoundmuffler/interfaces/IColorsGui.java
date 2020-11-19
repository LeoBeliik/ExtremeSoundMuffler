package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;

public interface IColorsGui {

    int whiteText = 0xffffff;
    int yellowText = 0xffff00;
    int greenText = 0x00ff00;
    int darkBG = ColorHelper.PackedColor.packColor(223, 0, 0, 0);
    ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");

}
