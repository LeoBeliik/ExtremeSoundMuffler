package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.LocatableSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("WeakerAccess")
@Mixin(LocatableSound.class)
public abstract class SoundMixin implements ISound, ISoundLists {

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void getSoundVolume(CallbackInfoReturnable<Float> cir) {
        if (isForbidden(this) || PlaySoundButton.isFromPSB()) {
            return;
        }

        recentSoundsList.add(getLocation());

        if (MainScreen.isMuffled()) {
            if (muffledSounds.containsKey(getLocation())) {
                cir.setReturnValue(cir.getReturnValue() * muffledSounds.get(getLocation()));
                return;
            }

            if (Config.getDisableAchors()) {
                return;
            }

            Anchor anchor = Anchor.getAnchor(this);
            if (anchor != null) {
                cir.setReturnValue(cir.getReturnValue() * anchor.getMuffledSounds().get(getLocation()));
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