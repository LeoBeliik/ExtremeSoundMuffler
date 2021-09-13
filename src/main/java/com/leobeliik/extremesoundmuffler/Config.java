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

    static final ForgeConfigSpec CLIENT_CONFIG;
    private static final String CATEGORY_GENERAL = "General";
    private static final String CATEGORY_MUFFLER = "Mufflers";
    private static final String CATEGORY_CLIENT = "Client";
    private static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static final ForgeConfigSpec.BooleanValue disableInventoryButton;
    private static final ForgeConfigSpec.BooleanValue disableMufflers;
    private static final ForgeConfigSpec.BooleanValue leftButtons;
    private static final ForgeConfigSpec.DoubleValue defaultMuteVolume;
    private static ForgeConfigSpec.IntValue MufflersMaxRadius;
    private static ForgeConfigSpec.BooleanValue showTip;
    private static ForgeConfigSpec.BooleanValue useDarkTheme;


    static {
        GENERAL_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        forbiddenSounds = GENERAL_BUILDER.comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);
        defaultMuteVolume = GENERAL_BUILDER.comment("Volume set when pressed the mute button by default")
                .defineInRange("defaultMuteVolume", 0, 0, 0.9);
        leftButtons = GENERAL_BUILDER.comment("Set to true to move the muffle and play buttons to the left side of the GUI")
                .define("leftButtons", false);
        showTip = GENERAL_BUILDER.comment("Show a message the first time a sound is muffled indicating that you can change the volume")
                .define("showTip", true);
        useDarkTheme = GENERAL_BUILDER.comment("Whether or not use the dark theme")
                .define("useDarkTheme", false);
        GENERAL_BUILDER.pop();

        GENERAL_BUILDER.comment("Muffler settings").push(CATEGORY_MUFFLER);
        disableMufflers = GENERAL_BUILDER.comment("Disable the Mufflers?")
                .define("disableMufflers", false);
        MufflersMaxRadius = GENERAL_BUILDER.comment("Set the max radius for the mufflers")
                .defineInRange("MufflersMaxRadius", 16, 0, 64);
        GENERAL_BUILDER.pop();

        GENERAL_BUILDER.comment("Client settings").push(CATEGORY_CLIENT);
        disableInventoryButton = GENERAL_BUILDER.comment("Disable the Muffle button in the player inventory?")
                .define("disableInventoryButton", false);
        GENERAL_BUILDER.pop();

        CLIENT_CONFIG = GENERAL_BUILDER.build();
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

    public static boolean getDisableInventoryButton() {
        return disableInventoryButton.get();
    }

    public static boolean getDisableAchors() {
        return disableMufflers.get();
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

    public static boolean useDarkTheme() {
        return useDarkTheme.get();
    }

    public static int getMufflersMaxRadius() {
        return MufflersMaxRadius.get();
    }

}