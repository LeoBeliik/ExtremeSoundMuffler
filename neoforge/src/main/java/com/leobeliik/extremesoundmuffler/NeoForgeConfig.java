package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
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
    private static ModConfigSpec.ConfigValue<List<? extends String>> modsMuffled;
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
    private static ModConfigSpec.IntValue maxAnchorRange;

    static void init(ModContainer container) {
        buildConfig();
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CLIENT_CONFIG);
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
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), () -> "",  o -> o instanceof String);
        
        modsMuffled = CLIENT_BUILDER
                .comment("General mod muffling, any sound from these mods will be muffled down to the provided volume. \n" +
                        "Name of the mod and desired volume, separated by \":\" \nExample: \"minecraft:50\", \"extremesoundmuffler:0\"")
                .comment("Default: Empty")
                .defineList("modsMuffled", new ArrayList<>(), () -> "", o -> o instanceof String);
        
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

    @SubscribeEvent
    static void onLoad(ModConfigEvent.Loading event) {
        fillForbiddenList();
        getModsMuffled();
    }

    @SubscribeEvent
    static void onReload(ModConfigEvent.Reloading event) {
        fillForbiddenList();
        getModsMuffled();
    }

    private static void fillForbiddenList() {
        ISoundLists.forbiddenSounds.clear();
        ISoundLists.forbiddenSounds.addAll(forbiddenSounds.get());
    }

    private static void getModsMuffled() {
        ISoundLists.modsMuffled.clear();
        ISoundLists.modsMuffled.addAll(modsMuffled.get());
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
