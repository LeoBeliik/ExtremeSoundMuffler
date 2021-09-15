package com.leobeliik.extremesoundmuffler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    private static ForgeConfigSpec CLIENT_CONFIG;
    private static ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static ForgeConfigSpec.BooleanValue disableInventoryButton;
    private static ForgeConfigSpec.BooleanValue disableAnchors;
    private static ForgeConfigSpec.BooleanValue leftButtons;
    private static ForgeConfigSpec.DoubleValue defaultMuteVolume;
    private static ForgeConfigSpec.BooleanValue showTip;
    private static ForgeConfigSpec.BooleanValue useDarkTheme;

    static void init() {
        buildConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    }


    private static void buildConfig() {
        String CATEGORY_GENERAL = "general";
        CLIENT_BUILDER.comment("general settings").push(CATEGORY_GENERAL);

        forbiddenSounds = CLIENT_BUILDER.comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);

        disableInventoryButton = CLIENT_BUILDER.comment("Disable the Muffle button in the player inventory?")
                .define("disableInventoryButton", false);

        disableAnchors = CLIENT_BUILDER.comment("Disable the Anchors?")
                .define("disableAnchors", false);

        defaultMuteVolume = CLIENT_BUILDER.comment("Volume set when pressed the mute button by default")
                .defineInRange("defaultMuteVolume", 0, 0, 0.9);

        leftButtons = CLIENT_BUILDER.comment("Set to true to move the muffle and play buttons to the left side of the GUI")
                .define("leftButtons", false);

        showTip = CLIENT_BUILDER.comment("Show a message the first time a sound is muffled indicating that you can change the volume")
                .define("showTip", true);

        useDarkTheme = CLIENT_BUILDER.comment("Whether or not use the dark theme")
                .define("useDarkTheme", false);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    static boolean getDisableInventoryButton() {
        return disableInventoryButton.get();
    }

    static boolean useDarkTheme() {
        return useDarkTheme.get();
    }

    static List<? extends String> getForbiddenSounds() {
        return forbiddenSounds.get();
    }

    public static boolean getDisableAchors() {
        return disableAnchors.get();
    }

    public static float getDefaultMuteVolume() {
        return defaultMuteVolume.get().floatValue();
    }

    public static boolean getLeftButtons() {
        return leftButtons.get();
    }

    public static boolean getShowTip() {
        return showTip.get();
    }

    public static void setShowTip(boolean showTip) {
        Config.showTip.set(showTip);
    }
}