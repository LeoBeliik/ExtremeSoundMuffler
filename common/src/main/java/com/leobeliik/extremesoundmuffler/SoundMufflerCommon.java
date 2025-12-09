package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import static com.leobeliik.extremesoundmuffler.Constants.MOD_ID;

public class SoundMufflerCommon {

    //whether should use the dark or light textures
    public static Identifier getTextureRL() {
        String texture = CommonConfig.get().useDarkTheme().get() ? "textures/gui/sm_gui_dark.png" : "textures/gui/sm_gui.png";
        return Identifier.fromNamespaceAndPath(MOD_ID, texture);
    }

    public static void openMainScreen() {
        MufflerScreen.open();
    }

    //create empty keybind for the mod
    static KeyMapping mufflerKey() {
        return new KeyMapping(
                new TranslatableContents("key.open_muffler_gui", "Open Muffler GUI", TranslatableContents.NO_ARGS).getKey(),
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                KeyMapping.Category.MISC);
    }


}
