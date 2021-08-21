package com.leobeliik.extremesoundmuffler;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("CanBeFinal")
@Mod.EventBusSubscriber
public class Config {

    static final ForgeConfigSpec COMMON_CONFIG;
    private static final String CATEGORY_GENERAL = "general";
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static final ForgeConfigSpec.BooleanValue disableInventoryButton;
    private static final ForgeConfigSpec.BooleanValue disableAnchors;
    private static final ForgeConfigSpec.BooleanValue leftButtons;
    private static final ForgeConfigSpec.DoubleValue defaultMuteVolume;
    private static ForgeConfigSpec.IntValue anchorMaxRadius;
    private static ForgeConfigSpec.BooleanValue showTip;
    private static ForgeConfigSpec.BooleanValue useDarkTheme;


    static {
        COMMON_BUILDER.comment("general settings").push(CATEGORY_GENERAL);

        forbiddenSounds = COMMON_BUILDER.comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);

        disableInventoryButton = COMMON_BUILDER.comment("Disable the Muffle button in the player inventory?")
                .define("disableInventoryButton", false);

        disableAnchors = COMMON_BUILDER.comment("Disable the Anchors?")
                .define("disableAnchors", false);

        defaultMuteVolume = COMMON_BUILDER.comment("Volume set when pressed the mute button by default")
                .defineInRange("defaultMuteVolume", 0, 0, 0.9);

        leftButtons = COMMON_BUILDER.comment("Set to true to move the muffle and play buttons to the left side of the GUI")
                .define("leftButtons", false);

        showTip = COMMON_BUILDER.comment("Show a message the first time a sound is muffled indicating that you can change the volume")
                .define("showTip", true);

        useDarkTheme = COMMON_BUILDER.comment("Whether or not use the dark theme")
                .define("useDarkTheme", false);

        anchorMaxRadius = COMMON_BUILDER.comment("Set the max radius for the anchor")
                .defineInRange("anchorMaxRadius", 16, 0, 64);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    @SuppressWarnings("SameParameterValue")
    static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
        ISoundLists.forbiddenSounds.addAll(forbiddenSounds.get());
    }

    static boolean getDisableInventoryButton() {
        return disableInventoryButton.get();
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

    static boolean useDarkTheme() {
        return useDarkTheme.get();
    }

    public static int getAnchorMaxRadius() {
        return anchorMaxRadius.get();
    }

}