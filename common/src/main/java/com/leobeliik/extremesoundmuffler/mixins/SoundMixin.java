package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.soundSlider.ESMPlay;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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
    private void esm_captureTickableSoundVolume(SoundInstance sound, CallbackInfoReturnable cir) {
        esmSound = sound;
    }

    @ModifyArg(index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"),
            method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F")
    private float esm_setVolume(float volume) {
        //if we are not muffling, sounds return the normal volume
        if (volume == 0 || !MufflerScreen.isMuffling()) return volume;

        //save sound in temporary variable because there's a small chance to the sound to change when it shouldn't
        SoundInstance tempSound = esmSound;

        //don't care about forbidden sounds or from the psb
        if (tempSound != null && tempSound.getSound() != null && !ESMPlay.isFromPSB()) {
            String soundIdentifier = tempSound.getIdentifier().toString();

            if (!esm_isForbidden(tempSound)) { //TODO find a better way to do this
                //remove sound to prevent repeated sounds and maintains the desired order
                recentSoundsList.remove(soundIdentifier);
                //add sound to recent sounds list
                recentSoundsList.add(soundIdentifier);
            }

            float tempVolume = tempSound.getVolume();
            String soundName = tempSound.getIdentifier().getPath();
            String modName = tempSound.getIdentifier().getNamespace();

            //global sounds like thunder or dragon growl has too high volume to be properly muffled, so first we temporarily lower the max volume
            if (soundName.contains("entity.lightning_bolt.thunder") || soundName.contains("entity.ender_dragon.growl")) {
                tempVolume = 1F;
            }

            Double muffledValue = muffledSounds.get(soundIdentifier);
            if (muffledValue != null) { // normal sounds, full identifier (minecraft:break)
                return (float) (tempVolume * muffledValue);
            }

            muffledValue = muffledSounds.get(modName);
            if (muffledValue != null) { // for mods, non-working identifiers
                return (float) (tempVolume * muffledValue);
            }

            //don't continue if the anchors are disabled
            if (!CommonConfig.get().disableAnchors().get()) {
                return (float) (tempVolume * Anchor.getMuffling(tempSound));
            }

        }

        return volume;
    }

    @Inject(at = @At("RETURN"), method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F")
    private void esm_clearTickableSound(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        esmSound = null;
    }

    @Inject(at = @At("RETURN"), method = "play")
    private void esm_clearSound(SoundInstance sound, CallbackInfoReturnable cir) {
        esmSound = null;
    }

    @Unique
    private static boolean esm_isForbidden(SoundInstance sound) {
        if (forbiddenSounds.isEmpty()) return false;

        return forbiddenCache.computeIfAbsent(sound.getIdentifier().toString(), loc -> {
            for (String fs : forbiddenSounds) {
                if (loc.contains(fs)) return true;
            }
            return false;
        });
    }
}