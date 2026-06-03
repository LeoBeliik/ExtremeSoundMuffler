package com.leobeliik.extremesoundmuffler.gui.buttons.soundSlider;

import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Random;

public class ESMPlay extends ESMButton {

    private static boolean isFromPSB = false;
    private final String buttonSound;
    private final ESMSlider slider;

    ESMPlay(int x, int y, String sound, ESMSlider slider, Component tooltip) {
        super(x, y, 110, 47, 11, null, tooltip);
        buttonSound = sound;
        this.slider = slider;
    }

    @Override
    public void onPress(@NotNull InputWithModifiers inputWithModifiers) {
        isFromPSB = false;
    }

    @Override
    public void playDownSound(@NonNull SoundManager soundHandler) {
        isFromPSB = true;
        SoundEvent sound;

        if (slider.screen.btnBlocks.isSelected()) {
            Random rand = new Random();
            //get a random sound from the block
            sound = SoundEvent.createVariableRangeEvent(Identifier.parse(Constants.loadBlockSounds(buttonSound).get(rand.nextInt(5))));
        } else {
            sound = SoundEvent.createVariableRangeEvent(Identifier.parse(buttonSound));
        }

        var handler = SimpleSoundInstance.forUI(sound, 1.0F);
        if (!soundHandler.isActive(handler)) {
            soundHandler.play(handler);
        } else {
            soundHandler.stop(handler);
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