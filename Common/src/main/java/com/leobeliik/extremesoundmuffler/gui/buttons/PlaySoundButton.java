package com.leobeliik.extremesoundmuffler.gui.buttons;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.sounds.SoundEvent;

public class PlaySoundButton extends AbstractButton {

    private static boolean isFromPSB = false;
    private SoundInstance buttonSound;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, CommonComponents.EMPTY);
        this.setAlpha(0);
        buttonSound = SimpleSoundInstance.forUI(sound, 1.0F);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isMouseOver(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        isFromPSB = false;
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        isFromPSB = true;
        if (!soundHandler.isActive(buttonSound)) {
            soundHandler.play(buttonSound);
        } else {
            soundHandler.stop(buttonSound);
        }
    }


    public static boolean isFromPSB() {
        return isFromPSB;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}