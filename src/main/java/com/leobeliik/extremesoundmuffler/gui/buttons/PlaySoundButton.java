package com.leobeliik.extremesoundmuffler.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class PlaySoundButton extends AbstractButton {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final SoundEvent sound;
    private static boolean isFromPSB = false;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, ITextComponent.getTextComponentOrEmpty(null));
        this.setAlpha(0);
        this.sound = sound;
    }

    @Override
    public void onPress() {
    }

    @ParametersAreNonnullByDefault
    @Override
    public void playDownSound(SoundHandler soundHandler) {
        isFromPSB = true;
        soundHandler.play(SimpleSound.master(this.sound, 1.0F));
        isFromPSB = false;
        //it maybe a mess but it does prevent to sounds to get muted when they're player from this button
    }

    public static boolean isFromPSB() {
        return isFromPSB;
    }
}
