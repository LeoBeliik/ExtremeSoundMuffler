package com.leobeliik.extremesoundmuffler.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class PlaySoundButton extends AbstractButton {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final SoundEvent sound;
    private static boolean isFromPSB = false;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, TextComponent.EMPTY);
        this.setAlpha(0);
        this.sound = sound;
    }

    @Override
    public void onPress() {}

    @ParametersAreNonnullByDefault
    @Override
    public void playDownSound(SoundManager soundHandler) {
        isFromPSB = true;
        soundHandler.play(SimpleSoundInstance.forUI(this.sound, 1.0F));
        isFromPSB = false;
        //it maybe a mess but it does prevent to sounds to get muted when they're player from this button
    }

    public static boolean isFromPSB() {
        return isFromPSB;
    }
}