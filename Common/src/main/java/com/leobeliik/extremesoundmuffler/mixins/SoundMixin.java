package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundMixin implements ISoundLists {

    @Redirect(method = "Lnet/minecraft/client/sounds/SoundEngine;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getVolume()F"))
    private float esm_calculateSoundVolume(SoundInstance sound) {
        //Non TickableSounds go to esm_calculateTickableSoundVolume
        return sound instanceof TickableSoundInstance ? sound.getVolume() : esm_setVolume(sound);
    }

    @Inject(method = "calculateVolume", at = @At("RETURN"), cancellable = true)
    private void esm_calculateTickableSoundVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        //TickableSounds go to esm_calculateSoundVolume
        if (sound instanceof TickableSoundInstance) {
            cir.setReturnValue(esm_setVolume(sound));
        }
    }

    @Unique
    private float esm_setVolume(SoundInstance sound) {
        //don't care about forbidden sounds or from the psb
        if (!esm_isForbidden(sound) && !PlaySoundButton.isFromPSB()) {

            //add sound to recent sounds list
            recentSoundsList.add(sound.getLocation());

            if (MufflerScreen.isMuffling()) {
                if (muffledSounds.containsKey(sound.getLocation())) {
                    return (float) (sound.getVolume() * muffledSounds.get(sound.getLocation()));
                }

                //don't continue if the anchors are disabled
                if (CommonConfig.get() == null || !CommonConfig.get().disableAnchors().get()) {
                    Anchor anchor = Anchor.getAnchor(sound);
                    if (anchor != null) {
                        return (float) (sound.getVolume() * anchor.getMuffledSounds().get(sound.getLocation()));
                    }
                }
            }
        }

        return sound.getVolume();
    }

    @Unique
    private static boolean esm_isForbidden(SoundInstance sound) {
        return forbiddenSounds.stream().anyMatch(fs -> sound.getLocation().toString().contains(fs));
    }

}