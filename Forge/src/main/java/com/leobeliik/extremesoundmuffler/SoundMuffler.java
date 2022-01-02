package com.leobeliik.extremesoundmuffler;
//FORGE

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SoundMuffler {

    public SoundMuffler() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClass.init();

        // Some code like events require special initialization from the
        // loader specific code.

    }
}