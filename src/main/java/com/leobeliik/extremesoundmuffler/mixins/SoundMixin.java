package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.MufflingLogic;
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
        MufflingLogic.sound(sound, cir);
    }
}