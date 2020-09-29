package com.leobeliik.extremesoundmuffler;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.leobeliik.extremesoundmuffler.utils.EventsHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    private static final String CATEGORY_GENERAL = "general";
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec CLIENT_CONFIG;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenSounds;
    private static final ForgeConfigSpec.BooleanValue disableInventoryButton;
    private static final ForgeConfigSpec.BooleanValue disableAnchors;
    private static final ForgeConfigSpec.DoubleValue anchorRadius;

    static {
        CLIENT_BUILDER.comment("general settings").push(CATEGORY_GENERAL);

        forbiddenSounds = CLIENT_BUILDER.comment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
                .defineList("forbiddenSounds", Arrays.asList("ui.", "music.", "ambient."), o -> o instanceof String);

        disableInventoryButton = CLIENT_BUILDER.comment("Disable the Muffle button in the player inventory?")
                .define("disableInventoryButton", false);

        disableAnchors = CLIENT_BUILDER.comment("Disable the anchors?")
                .define("disableAnchors", false);

        anchorRadius = CLIENT_BUILDER.comment("Sets the radius for the Anchors, from your feet X blocks in the 6 directions")
                .defineInRange("anchorRadius", 16D, 2D, 16D);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
        EventsHandler.forbiddenSounds().addAll(forbiddenSounds.get());
    }

    static ForgeConfigSpec.BooleanValue getDisableInventoryButton() {
        return disableInventoryButton;
    }

    public static ForgeConfigSpec.BooleanValue getDisableAchors() {
        return disableAnchors;
    }

    public static ForgeConfigSpec.DoubleValue getAnchorRadius() {
        return anchorRadius;
    }
}
