package com.leobeliik.extremesoundmuffler.gui.buttons;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;

public class PlaySoundButton extends AbstractButton {

    private final SoundEvent sound;
    private static boolean isFromPSB = false;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, TextComponent.EMPTY);
        this.setAlpha(0);
        this.sound = sound;
    }

    @Override
    public void onPress() {
        isFromPSB = false;
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        isFromPSB = true;
        soundHandler.play(SimpleSoundInstance.forUI(this.sound, 1.0F));
    }

    public static boolean isFromPSB() {
        return isFromPSB;
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}