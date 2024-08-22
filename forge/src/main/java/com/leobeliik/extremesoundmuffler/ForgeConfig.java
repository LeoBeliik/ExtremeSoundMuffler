package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
class ForgeConfig {

    private static ForgeConfigSpec CLIENT_CONFIG;
    private static ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static ForgeConfigSpec.BooleanValue lawfulAllList;
    private static ForgeConfigSpec.BooleanValue disableInventoryButton;
    private static ForgeConfigSpec.BooleanValue disableCreativeInventoryButton;
    private static ForgeConfigSpec.BooleanValue disableAnchors;
    private static ForgeConfigSpec.BooleanValue leftButtons;
    private static ForgeConfigSpec.BooleanValue showTip;
    private static ForgeConfigSpec.BooleanValue useDarkTheme;
    private static ForgeConfigSpec.DoubleValue defaultMuteVolume;
    private static ForgeConfigSpec.IntValue invButtonHorizontal;
    private static ForgeConfigSpec.IntValue invButtonVertical;
    private static ForgeConfigSpec.IntValue creativeInvButtonHorizontal;
    private static ForgeConfigSpec.IntValue creativeInvButtonVertical;

    static void init() {
        buildConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.CLIENT_CONFIG);
        CommonConfig.set(new CommonConfig.ConfigAccess(
                forbiddenSounds,
                lawfulAllList,
                disableInventoryButton,
                disableCreativeInventoryButton,
                disableAnchors,
                leftButtons,
                showTip,
                useDarkTheme,
                defaultMuteVolume,
                invButtonHorizontal,
                invButtonVertical,
                creativeInvButtonHorizontal,
                creativeInvButtonVertical
        ));
    }

    private static void buildConfig() {
        String CATEGORY_GENERAL = "general";
        String CATEGORY_INVENTORY_BUTTON = "inventory_button";
        String CATEGORY_ANCHORS = "Anchors";

        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        forbiddenSounds = CLIENT_BUILDER.comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);
        lawfulAllList = CLIENT_BUILDER.comment("Allow the \"ALL\" sounds list to include the blacklisted sounds?")
                .define("lawfulAllList", false);
        defaultMuteVolume = CLIENT_BUILDER.comment("Volume set when pressed the mute button by default")
                .defineInRange("defaultMuteVolume", 0, 0, 0.9);
        leftButtons = CLIENT_BUILDER.comment("Set to true to move the muffle and play buttons to the left side of the GUI")
                .define("leftButtons", false);
        showTip = CLIENT_BUILDER.comment("Show tips in the Muffler screen?")
                .define("showTip", true);
        useDarkTheme = CLIENT_BUILDER.comment("Whether or not use the dark theme")
                .define("useDarkTheme", false);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Inventory button settings").push(CATEGORY_INVENTORY_BUTTON);

        disableInventoryButton = CLIENT_BUILDER.comment("Disable the Muffle button in the player inventory?")
                .define("disableInventoryButton", false);
        invButtonHorizontal = CLIENT_BUILDER.comment("Coordinates for the Muffler button in the player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .defineInRange("invButtonX", 75, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invButtonVertical = CLIENT_BUILDER.comment("Coordinates for the Muffler button in the player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .defineInRange("invButtonY", 7, Integer.MIN_VALUE, Integer.MAX_VALUE);
        disableCreativeInventoryButton = CLIENT_BUILDER.comment("Disable the Muffle button in the creative player inventory?")
                .define("disableCreativeInventoryButton", false);
        creativeInvButtonHorizontal = CLIENT_BUILDER.comment("Coordinates for the Muffler button in the creative player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .defineInRange("creativeInvButtonX", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        creativeInvButtonVertical = CLIENT_BUILDER.comment("Coordinates for the Muffler button in the creative player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .defineInRange("creativeInvButtonY", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Anchor settings").push(CATEGORY_ANCHORS);

        disableAnchors = CLIENT_BUILDER.comment("Disable the Anchors?")
                .define("disableAnchors", false);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    static void onLoad(ModConfigEvent.Loading event) {
        ISoundLists.forbiddenSounds.addAll(forbiddenSounds.get());
    }

    static void setInvButtonHorizontal(int x) {
        invButtonHorizontal.set(x);
    }

    static void setInvButtonVertical(int y) {
        invButtonVertical.set(y);
    }

    static void setCreativeInvButtonHorizontal(int x) {
        creativeInvButtonHorizontal.set(x);
    }

    static void setCreativeInvButtonVertical(int y) {
        creativeInvButtonVertical.set(y);
    }
}
