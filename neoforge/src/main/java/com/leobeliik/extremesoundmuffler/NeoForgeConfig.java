package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
class NeoForgeConfig {

    private static ModConfigSpec CLIENT_CONFIG;
    private static ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static ModConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static ModConfigSpec.BooleanValue lawfulAllList;
    private static ModConfigSpec.BooleanValue disableInventoryButton;
    private static ModConfigSpec.BooleanValue disableCreativeInventoryButton;
    private static ModConfigSpec.BooleanValue disableAnchors;
    private static ModConfigSpec.BooleanValue leftButtons;
    private static ModConfigSpec.BooleanValue showTip;
    private static ModConfigSpec.BooleanValue useDarkTheme;
    private static ModConfigSpec.DoubleValue defaultMuteVolume;
    private static ModConfigSpec.IntValue invButtonHorizontal;
    private static ModConfigSpec.IntValue invButtonVertical;
    private static ModConfigSpec.IntValue creativeInvButtonHorizontal;
    private static ModConfigSpec.IntValue creativeInvButtonVertical;

    static void init(ModContainer container) {
        buildConfig();
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CLIENT_CONFIG);
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

    @SubscribeEvent
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
