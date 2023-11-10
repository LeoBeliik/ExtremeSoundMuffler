package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundMixin implements ISoundLists {

    /* CREDITS to botania:
     https://github.com/VazkiiMods/Botania/blob/3c14a69486d58ab6da860998ddd4ce7558481286/Xplat/src/main/java/vazkii/botania/mixin/client/SoundEngineMixin.java
    */

    @Unique
    @Nullable
    private SoundInstance esmSound;

    @Inject(at = @At("HEAD"), method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F")
    private void esm_captureSoundVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        esmSound = sound;
    }

    //Capture non tickable sounds
    @Inject(at = @At("HEAD"), method = "play")
    private void esm_captureTickableSoundVolume(SoundInstance sound, CallbackInfo ci) {
        esmSound = sound;
    }

    @ModifyArg(index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"),
            method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F")
    private float esm_setVolume(float volume) {
        SoundInstance tempSound = esmSound;
        //don't care about forbidden sounds or from the psb
        if (tempSound != null && !esm_isForbidden(tempSound) && !PlaySoundButton.isFromPSB()) {
            ResourceLocation soundLocation = tempSound.getLocation();

            //remove sound to prevent repeated sounds and maintains the desired order
            recentSoundsList.remove(soundLocation);
            //add sound to recent sounds list
            recentSoundsList.add(soundLocation);

            if (MufflerScreen.isMuffling()) {
                if (muffledSounds.containsKey(soundLocation)) {
                    return (float) (tempSound.getVolume() * muffledSounds.get(soundLocation));
                }

                //don't continue if the anchors are disabled
                if (CommonConfig.get() == null || !CommonConfig.get().disableAnchors().get()) {
                    Anchor anchor = Anchor.getAnchor(tempSound);
                    if (anchor != null) {
                        return (float) (tempSound.getVolume() * anchor.getMuffledSounds().get(soundLocation));
                    }
                }
            }
        }

        return volume;
    }

    @Inject(at = @At("RETURN"), method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F")
    private void esm_clearTickableSound(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        esmSound = null;
    }

    @Inject(at = @At("RETURN"), method = "play")
    private void esm_clearSound(SoundInstance sound, CallbackInfo ci) {
        esmSound = null;
    }

    @Unique
    private static boolean esm_isForbidden(SoundInstance sound) {
        return forbiddenSounds.stream().anyMatch(fs -> sound.getLocation().toString().contains(fs));
    }

}