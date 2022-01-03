package com.leobeliik.extremesoundmuffler;

/*
 * Based on Patchouli config
 * https://github.com/VazkiiMods/Patchouli/blob/b7883eee34d5b80954c3435dee25d3376a661f4b/Fabric/src/main/java/vazkii/patchouli/fabric/common/FiberPatchouliConfig.java
 */

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FabricConfig {
    private static PropertyMirror<List<String>> forbiddenSounds = PropertyMirror.create(ConfigTypes.makeList(ConfigTypes.STRING));
    private static PropertyMirror<Boolean> lawfulAllList = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Boolean> disableInventoryButton = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Boolean> disableAnchors = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Boolean> leftButtons = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Boolean> showTip = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Boolean> useDarkTheme = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private static PropertyMirror<Double> defaultMuteVolume = PropertyMirror.create(ConfigTypes.DOUBLE);
    private static PropertyMirror<Integer> invButtonHorizontal = PropertyMirror.create(ConfigTypes.INTEGER);
    private static PropertyMirror<Integer> invButtonVertical = PropertyMirror.create(ConfigTypes.INTEGER);

    static void init() {
        CommonConfig.set(new CommonConfig.ConfigAccess(
                forbiddenSounds::getValue,
                lawfulAllList::getValue,
                disableInventoryButton::getValue,
                disableAnchors::getValue,
                leftButtons::getValue,
                showTip::getValue,
                useDarkTheme::getValue,
                defaultMuteVolume::getValue,
                invButtonHorizontal::getValue,
                invButtonVertical::getValue
        ));

        JanksonValueSerializer serializer = new JanksonValueSerializer(false);
        Path p = Paths.get("config", Constants.MOD_ID + ".json5");

        writeDefaultConfig(p, serializer);

        try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(CONFIG, s, serializer);
        } catch (IOException | ValueDeserializationException e) {
            Constants.LOG.error("Error loading ESM config", e);
        }
    }

    private static final ConfigTree CONFIG = ConfigTree.builder()

            .fork("General settings")

            .beginValue("forbiddenSounds", ConfigTypes.makeList(ConfigTypes.STRING), Arrays.asList("ui.", "music.", "ambient."))
            .withComment("General settings: ").withComment("") // general "category"
            .withComment("Blacklisted Sounds - add the name of the sounds to blacklist, separated with comma")
            .finishValue(forbiddenSounds::mirror)

            .beginValue("lawfulAllList", ConfigTypes.BOOLEAN, false)
            .withComment("Allow the \"ALL\" sounds list to include the blacklisted sounds?")
            .finishValue(lawfulAllList::mirror)

            .beginValue("defaultMuteVolume", ConfigTypes.DOUBLE, 0.0D)
            .withComment("Range: 0.0 ~ 0.9")
            .withComment("Volume set when pressed the mute button by default")
            .finishValue(defaultMuteVolume::mirror)

            .beginValue("leftButtons", ConfigTypes.BOOLEAN, false)
            .withComment("Set to true to move the muffle and play buttons to the left side of the GUI")
            .finishValue(leftButtons::mirror)

            .beginValue("showTip", ConfigTypes.BOOLEAN, true)
            .withComment("Show tips in the Muffler screen?")
            .finishValue(showTip::mirror)

            .beginValue("useDarkTheme", ConfigTypes.BOOLEAN, false)
            .withComment("Whether or not use the dark theme")
            .finishValue(useDarkTheme::mirror)

            .finishBranch()
            .fork("Inventory button settings")

            .beginValue("disableInventoryButton", ConfigTypes.BOOLEAN, false)
            .withComment("").withComment("Inventory button settings").withComment("") //inv button "category"
            .withComment("Disable the Muffle button in the player inventory?")
            .finishValue(disableInventoryButton::mirror)

            .beginValue("invButtonHorizontal", ConfigTypes.INTEGER, 75)
            .withComment("Coordinates for the Muffler button in the player inventory. \n" +
                    "You can change this in game by holding the RMB over the button and draging it around")
            .finishValue(invButtonHorizontal::mirror)

            .beginValue("invButtonVertical", ConfigTypes.INTEGER, 7)
            .withComment("Coordinates for the Muffler button in the player inventory. \n" +
                    "You can change this in game by holding the RMB over the button and draging it around")
            .finishValue(invButtonVertical::mirror)

            .finishBranch()
            .fork("Anchor settings")

            .beginValue("disableAnchors", ConfigTypes.BOOLEAN, false)
            .withComment("Disable the Anchors?")
            .finishValue(disableAnchors::mirror)

            .finishBranch()

            .build();

    private static void writeDefaultConfig(Path path, JanksonValueSerializer serializer) {
        try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(CONFIG, s, serializer);
        } catch (IOException e) {
            Constants.LOG.warn("error saving esm config!", e);
        }

    }

    static void updateConfig(Path path, JanksonValueSerializer serializer) {
        try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.SYNC))) {
            FiberSerialization.serialize(CONFIG, s, serializer);
        } catch (IOException e) {
            Constants.LOG.warn("esm config failed to update!", e);
        }

    }

    static List<String> getForbiddenSounds() {
        return forbiddenSounds.getValue();
    }

    static void setInvButtonHorizontal(int x) {
        invButtonHorizontal.setValue(x);
    }

    static void setInvButtonVertical(int y) {
        invButtonVertical.setValue(y);
    }
}
