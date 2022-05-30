package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class PlaySoundButton extends AbstractButton {

    private static boolean isFromPSB = false;
    private SoundInstance buttonSound;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, TextComponent.EMPTY);
        this.setAlpha(0);
        buttonSound = SimpleSoundInstance.forUI(sound, 1.0F);
    }

    void onCLick(double mouseX, double mouseY, int button) {
        //if the button is clicked with the LMB (button = 0) set isFromPSB to true and play the buttonSound
        //if not stop the current sound if playing
        if (this.isMouseOver(mouseX, mouseY)) {
            isFromPSB = button != 0;
            super.mouseClicked(mouseX, mouseY, 0);
        }
    }

    @Override
    public void onPress() {
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        if (!isFromPSB) {
            isFromPSB = true;
            soundHandler.play(buttonSound);
        } else if (soundHandler.isActive(buttonSound)) {
            soundHandler.stop(buttonSound);
            soundHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }


    public static boolean isFromPSB() {
        return isFromPSB;
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}