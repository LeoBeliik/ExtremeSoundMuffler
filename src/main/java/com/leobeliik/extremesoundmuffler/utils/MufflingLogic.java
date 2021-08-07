package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.anchors.AnchorEntity;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

public class MufflingLogic implements ISoundLists {

    public static CallbackInfoReturnable<Float> sound(ISound sound, CallbackInfoReturnable<Float> cir) {
        if (isForbidden(sound) || PlaySoundButton.isFromPSB()) {
            return cir;
        }

        recentSoundsList.add(sound.getLocation());
        //if (!MufflerScreen.isMuffled()) return;

        if (playerMuffledList.containsKey(sound.getLocation())) {
            cir.setReturnValue(cir.getReturnValue() * playerMuffledList.get(sound.getLocation()));
            return cir;
        }

        //anchor muffling
        BlockPos soundPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
        for (AnchorEntity anchor : anchorList) {
            if (Minecraft.getInstance().level != null && !Minecraft.getInstance().level.dimension().equals(Objects.requireNonNull(anchor.getLevel()).dimension())) {
                return cir;
            }
            if (anchor.isMuffling() && anchor.getCurrentMuffledSounds().containsKey(sound.getLocation()) && soundPos.closerThan(anchor.getBlockPos(), anchor.getRadius())) {
                cir.setReturnValue(cir.getReturnValue() * anchor.getCurrentMuffledSounds().get(sound.getLocation()));
                return cir;
            }
        }
        return cir;
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
