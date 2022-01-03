package com.leobeliik.extremesoundmuffler;

import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
    public static final String MOD_ID = "extremesoundmuffler";
    public static final Logger LOG = LogManager.getLogger("Extreme Sound Muffler");
    public static final KeyMapping openMufflerScreen = SoundMufflerCommon.mufflerKey();
}
