package com.leobeliik.extremesoundmuffler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CommonConfig {

    private static ConfigAccess access = null;
    private static final Map<String, Boolean> CONFIG_FLAGS = new ConcurrentHashMap<>();

    public record ConfigAccess(
            Supplier<List<? extends String>> forbiddenSounds,
            Supplier<Boolean> lawfulAllList,
            Supplier<Boolean> disableInventoryButton,
            Supplier<Boolean> disableCreativeInventoryButton,
            Supplier<Boolean> disableAnchors,
            Supplier<Boolean> leftButtons,
            Supplier<Boolean> showTip,
            Supplier<Boolean> useDarkTheme,
            Supplier<Double> defaultMuteVolume,
            Supplier<Integer> invButtonHorizontal,
            Supplier<Integer> invButtonVertical,
            Supplier<Integer> creativeInvButtonHorizontal,
            Supplier<Integer> creativeInvButtonVertical
            ) {
    }

    public static ConfigAccess get() {
        return access;
    }

    public static void set(ConfigAccess configAccess) {
        if (access != null) {
            throw new IllegalStateException("ConfigAccess already set");
        }
        access = configAccess;
    }

}
