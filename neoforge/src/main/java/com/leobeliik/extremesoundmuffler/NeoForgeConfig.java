package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.*;

@EventBusSubscriber(modid = Constants.MOD_ID)
class NeoForgeConfig {

    private static ModConfigSpec CLIENT_CONFIG;
    private static ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static ModConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static ModConfigSpec.ConfigValue<List<? extends String>> forbiddenMods;
    private static ModConfigSpec.BooleanValue useGlobalMuffledSounds;
    private static ModConfigSpec.BooleanValue lawfulAllList;
    private static ModConfigSpec.BooleanValue disableInventoryButton;
    private static ModConfigSpec.BooleanValue disableCreativeInventoryButton;
    private static ModConfigSpec.BooleanValue disableAnchors;
    private static ModConfigSpec.BooleanValue leftButtons;
    private static ModConfigSpec.BooleanValue showTip;
    private static ModConfigSpec.BooleanValue useDarkTheme;
    private static ModConfigSpec.DoubleValue  DefaultMuteVolume;
    private static ModConfigSpec.IntValue invButtonHorizontal;
    private static ModConfigSpec.IntValue invButtonVertical;
    private static ModConfigSpec.IntValue creativeInvButtonHorizontal;
    private static ModConfigSpec.IntValue creativeInvButtonVertical;
    private static ModConfigSpec.IntValue maxAnchorRange;

    static void init(ModContainer container) {
        buildConfig();
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CLIENT_CONFIG);
        CommonConfig.set(new CommonConfig.ConfigAccess(
                forbiddenSounds,
                forbiddenMods,
                lawfulAllList,
                useGlobalMuffledSounds,
                disableInventoryButton,
                disableCreativeInventoryButton,
                disableAnchors,
                leftButtons,
                showTip,
                useDarkTheme,
                 DefaultMuteVolume,
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
                .comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma \n")
                .comment(" Default: \"ui.\", \"music.\", \"ambient.\"")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), () -> "",  o -> o instanceof String);
        
        forbiddenMods = CLIENT_BUILDER
                .comment("Mods that shouldn't appear in the Mods section, sepparated by comma. \n")
                .comment(" Default: fabricloader, neoforge, java, mixinextras, fiber, extremesoundmuffler")
                .defineList("forbiddenMods", List.of("fabricloader", "neoforge", "java", "mixinextras", "fiber", "extremesoundmuffler"), () -> "", o -> o instanceof String);

        useGlobalMuffledSounds = CLIENT_BUILDER
                .comment("Use global muffled sounds?")
                .comment("Global muffled sounds are stored in the .minecraft folder while local muffled sounds are stored in the modpack folder \n")
                .comment(" Default: false")
                .define("useGlobalMuffledSounds", false);

        lawfulAllList = CLIENT_BUILDER
                .comment("Allow the \"ALL\" sounds list to include the blacklisted sounds? \n")
                .comment(" Default: false")
                .define("lawfulAllList", false);
        
         DefaultMuteVolume = CLIENT_BUILDER
                .comment("Volume set when pressed the mute button by  Default \n")
                .defineInRange(" DefaultMuteVolume", 0, 0, 0.9);
        
        leftButtons = CLIENT_BUILDER
                .comment("Set to true to move the muffle and play buttons to the left side of the GUI \n")
                .comment(" Default: false")
                .define("leftButtons", false);
        
        showTip = CLIENT_BUILDER
                .comment("Show tips in the Muffler screen? \n")
                .comment(" Default: true")
                .define("showTip", true);
        
        useDarkTheme = CLIENT_BUILDER
                .comment("Whether or not use the dark theme \n")
                .comment(" Default: false")
                .define("useDarkTheme", false);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Inventory button settings").push(CATEGORY_INVENTORY_BUTTON);

        disableInventoryButton = CLIENT_BUILDER
                .comment("Disable the Muffle button in the player inventory? \n")
                .comment(" Default: false")
                .define("disableInventoryButton", false);
        
        invButtonHorizontal = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around \n")
                .defineInRange("invButtonX", 75, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        invButtonVertical = CLIENT_BUILDER
            .comment("Coordinates for the Muffler button in the player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around \n")
                .defineInRange("invButtonY", 7, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        disableCreativeInventoryButton = CLIENT_BUILDER
                .comment("Disable the Muffle button in the creative player inventory? \n")
                .comment(" Default: false")
                .define("disableCreativeInventoryButton", false);
        
        creativeInvButtonHorizontal = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the creative player inventory.\n " +
                        "You can change this in game by holding the RMB over the button and draging it around \n")
                .defineInRange("creativeInvButtonX", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        creativeInvButtonVertical = CLIENT_BUILDER
                .comment("Coordinates for the Muffler button in the creative player inventory. \n" +
                        "You can change this in game by holding the RMB over the button and draging it around")
                .defineInRange("creativeInvButtonY", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.comment("Anchor settings").push(CATEGORY_ANCHORS);

        disableAnchors = CLIENT_BUILDER
                .comment("Disable the Anchors? \n")
                .comment(" Default: false")
                .define("disableAnchors", false);
        
        maxAnchorRange = CLIENT_BUILDER
                .comment("Set max size for anchors (Warning: high values may cause LAG!). \n")
                .defineInRange("maxAnchorRange", 32, 1, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        fillForbiddenSoundsList();
        fillForbiddenModsList();
    }

    @SubscribeEvent
    static void onReload(ModConfigEvent.Reloading event) {
        fillForbiddenSoundsList();
        fillForbiddenModsList();
        Constants.useGlobalConfig = getGlobalConfig();
        Constants.darkMode = getDarkMode();
        if (ISoundLists.anchorList.isEmpty()) {
            DataManager.loadData();
        }
    }

    private static void fillForbiddenSoundsList() {
        ISoundLists.forbiddenSounds.clear();
        ISoundLists.forbiddenSounds.addAll(forbiddenSounds.get());
        ISoundLists.forbiddenCache.clear();
    }

    private static void fillForbiddenModsList() {
        ISoundLists.forbiddenMods.clear();
        ISoundLists.forbiddenMods.addAll(forbiddenMods.get());
    }

    static void setGlobalConfig(boolean global) {
        useGlobalMuffledSounds.set(global);
        CLIENT_CONFIG.save();
    }

    static boolean getGlobalConfig() {
        return useGlobalMuffledSounds.get();
    }

    static boolean getDarkMode() {
        return useDarkTheme.getAsBoolean();
    }

    static void setInvButtonHorizontal(int x, int y) {
        invButtonHorizontal.set(x);
        invButtonVertical.set(y);
        CLIENT_CONFIG.save();
    }

    static void setCreativeInvButtonHorizontal(int x, int y) {
        creativeInvButtonHorizontal.set(x);
        creativeInvButtonVertical.set(y);
        CLIENT_CONFIG.save();
    }
}
