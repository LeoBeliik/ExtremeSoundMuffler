package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundMixin implements ISoundLists {

    @Inject(method = "calculateVolume", at = @At("RETURN"), cancellable = true)
    private void calculateSoundVolume(ISound sound, CallbackInfoReturnable<Float> cir) {
        if (isForbidden(sound) || PlaySoundButton.isFromPSB()) {
            return;
        }

        recentSoundsList.add(sound.getLocation());

        if (MainScreen.isMuffled()) {
            if (muffledSounds.containsKey(sound.getLocation())) {
                cir.setReturnValue(cir.getReturnValue() * muffledSounds.get(sound.getLocation()));
                return;
            }

            if (Config.getDisableAchors()) {
                return;
            }

            Anchor anchor = Anchor.getAnchor(sound);
            if (anchor != null) {
                cir.setReturnValue(cir.getReturnValue() * anchor.getMuffledSounds().get(sound.getLocation()));
            }
        }
    }

    private static boolean isForbidden(ISound sound) {
        for (String fs : forbiddenSounds) {
            if (sound.getLocation().toString().contains(fs)) {
                return true;
            }
        }
        return false;
    }

}