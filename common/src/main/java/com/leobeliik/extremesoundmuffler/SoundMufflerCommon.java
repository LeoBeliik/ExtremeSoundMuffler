package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import static com.leobeliik.extremesoundmuffler.Constants.MOD_ID;
import static com.leobeliik.extremesoundmuffler.Constants.darkMode;

public class SoundMufflerCommon {

    //whether should use the dark or light textures
    public static Identifier getMainScreenTextureID() {
        String mainScreenTexture = darkMode ? "textures/gui/dark/esm_gui.png" : "textures/gui/light/esm_gui.png";
        return Identifier.fromNamespaceAndPath(MOD_ID, mainScreenTexture);
    }

    public static Identifier getIconsTextureID() {
        String iconsTexture = darkMode ? "textures/gui/dark/icons.png" : "textures/gui/light/icons.png";
        return Identifier.fromNamespaceAndPath(MOD_ID, iconsTexture);
    }

    public static Identifier getAnchorScreenTextureID() {
        String anchorScreenTexture = darkMode ? "textures/gui/dark/new_anchor_gui.png" : "textures/gui/light/new_anchor_gui.png";
        return Identifier.fromNamespaceAndPath(MOD_ID, anchorScreenTexture);
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
