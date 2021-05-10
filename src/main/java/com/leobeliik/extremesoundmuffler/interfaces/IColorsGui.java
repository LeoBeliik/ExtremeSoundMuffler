package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;

public interface IColorsGui {

    int whiteText = 0xffffff;
    int cyanText = 0x00ffff;
    int greenText = 0x00ff00;
    int darkBG = ColorHelper.PackedColor.packColor(223, 0, 0, 0);
    int brightBG = ColorHelper.PackedColor.packColor(200, 50, 50, 50);

}
