package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.eventHandlers.SoundEventHandler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocatableSound.class)
public abstract class TickableSoundMixin implements ISound {

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void getSoundVolume(CallbackInfoReturnable<Float> cir) {
        if (SoundEventHandler.isForbidden(this)) {
            return;
        }
        if (MainScreen.isMuffled() && this instanceof ITickableSound) {
            if (SoundEventHandler.muffledSounds.containsKey(getSoundLocation())) {
                cir.setReturnValue(cir.getReturnValue() * SoundEventHandler.muffledSounds.get(getSoundLocation()).floatValue());
                return;
            }
            if (Config.getDisableAchors()) {
                return;
            }
            Anchor anchor = SoundEventHandler.getAnchor(this);
            if (anchor != null) {
                cir.setReturnValue(cir.getReturnValue() * anchor.getMuffledSounds().get(getSoundLocation()).floatValue());
            }
        }
    }

}
