package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundMixin implements ISoundLists {

    @Inject(method = "calculateVolume", at = @At("RETURN"), cancellable = true)
    private void esm_calculateSoundVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        //don't care about forbidden sounds or from the psb
        if (esm_isForbidden(sound) || PlaySoundButton.isFromPSB()) {
            return;
        }

        //add sound to recent sounds list
        recentSoundsList.add(sound.getLocation());

        if (MufflerScreen.isMuffling()) {
            if (muffledSounds.containsKey(sound.getLocation())) {
                cir.setReturnValue((float) (cir.getReturnValue() * muffledSounds.get(sound.getLocation())));
                return;
            }

            //don't continue if the anchors are disabled
            if (CommonConfig.get() != null && CommonConfig.get().disableAnchors().get()) {
                return;
            }

            Anchor anchor = Anchor.getAnchor(sound);
            if (anchor != null) {
                cir.setReturnValue((float) (cir.getReturnValue() * anchor.getMuffledSounds().get(sound.getLocation())));
            }
        }
    }

    @Unique
    private static boolean esm_isForbidden(SoundInstance sound) {
        return forbiddenSounds.stream().anyMatch(fs -> sound.getLocation().toString().contains(fs));
    }

}