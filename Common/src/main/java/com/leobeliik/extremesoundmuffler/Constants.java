package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Constants {
    public static final String MOD_ID = "extremesoundmuffler";
    public static final Logger LOG = LogManager.getLogger("Extreme Sound Muffler");
    public static final KeyMapping soundMufflerKey = SoundMufflerCommon.mufflerKey();
    public static final List<Anchor> emptyAnchorList = IntStream.range(0, 10).mapToObj(i -> new Anchor(i, "Anchor " + i)).collect(Collectors.toList());
}
