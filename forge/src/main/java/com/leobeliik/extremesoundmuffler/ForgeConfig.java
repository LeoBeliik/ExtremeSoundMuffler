package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
class ForgeConfig {

    private static ForgeConfigSpec CLIENT_CONFIG;
    private static ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> modsMuffled;
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
    private static ForgeConfigSpec.IntValue maxAnchorRange;

    static void init() {
        buildConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.CLIENT_CONFIG);
        CommonConfig.set(new CommonConfig.ConfigAccess(
                forbiddenSounds,
                modsMuffled,
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
                creativeInvButtonVertical,
                maxAnchorRange
        ));
    }

    private static void buildConfig() {
        String CATEGORY_GENERAL = "general";
        String CATEGORY_INVENTORY_BUTTON = "inventory_button";
        String CATEGORY_ANCHORS = "Anchors";

        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        forbiddenSounds = CLIENT_BUILDER
                .comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .comment("Default: \"ui.\", \"music.\", \"ambient.\"")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);

        modsMuffled = CLIENT_BUILDER
                .comment("General mod muffling, any sound from these mods will be muffled down to the provided volume. \n" +
                        "Name of the mod and desired volume, separated by \":\" \nExample: \"minecraft:50\", \"extremesoundmuffler:0\"")
                .comment("Default: empty")
                .defineList("modsMuffled", Collections.emptyList(), o -> o instanceof String);

        lawfulAllList = CLIENT_BUILDER
                .comment("Allow the \"ALL\" sounds list to include the blacklisted sounds?")
                .comment("Default: false")
                .define("lawfulAllList", false);

        defaultMuteVolume = CLIENT_BUILDER
                .comment("Volume set when pressed the mute button by default")
                .comment("Default: 0")
                .defineInRange("defaultMuteVolume", 0, 0, 0.9);

        leftButtons = CLIENT_BUILDER
                .comment("Set to true to move the muffle and play buttons to the left side of the GUI")
                .comment("Default: false")
                .define("leftButtons", false);

        showTip = CLIENT_BUILDER
                .comment("Show tips in the Muffler screen?")
                .comment("Default: true")
                .define("showTip", true);

        useDarkTheme = CLIENT_BUILDER
                .comment("Whether or not use the dark theme")
                .comment("Default: false")
                .define("useDarkTheme", false);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Inventory button settings").push(CATEGORY_INVENTORY_BUTTON);

        disableInventoryButton = CLIENT_BUILDER
                .comment("Disable the Muffle button in the player inventory?")
                .comment("Default: false")
                .define("disableInventoryButton", false);

        invButtonHorizontal = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .comment("Default: 75")
                .defineInRange("invButtonX", 75, Integer.MIN_VALUE, Integer.MAX_VALUE);

        invButtonVertical = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .comment("Default: 7")
                .defineInRange("invButtonY", 7, Integer.MIN_VALUE, Integer.MAX_VALUE);

        disableCreativeInventoryButton = CLIENT_BUILDER
                .comment("Disable the Muffle button in the creative player inventory?")
                .comment("Default: false")
                .define("disableCreativeInventoryButton", false);

        creativeInvButtonHorizontal = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the creative player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .comment("Default: 2")
                .defineInRange("creativeInvButtonX", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);

        creativeInvButtonVertical = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the creative player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .comment("Default: 2")
                .defineInRange("creativeInvButtonY", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Anchor settings").push(CATEGORY_ANCHORS);

        disableAnchors = CLIENT_BUILDER
                .comment("Disable the Anchors?")
                .comment("Default: false")
                .define("disableAnchors", false);

        maxAnchorRange = CLIENT_BUILDER
                .comment("Set max size for anchors (Warning: high values may cause LAG!).")
                .comment("Default: 32")
                .defineInRange("anchorRange", 32, 1, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    static void onLoad(ModConfigEvent.Loading event) {
        ISoundLists.forbiddenSounds.addAll(forbiddenSounds.get());
        ISoundLists.modsMuffled.addAll(modsMuffled.get());
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
