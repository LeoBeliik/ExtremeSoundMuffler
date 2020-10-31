package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.utils.eventHandlers.SoundEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PlaySoundButton extends AbstractButton {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final SoundEvent sound;

    PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, ITextComponent.getTextComponentOrEmpty(null));
        this.setAlpha(0);
        this.sound = sound;
    }

    @Override
    public void onPress() {
        if (this.active && this.visible) {
            SoundEventHandler.isFromPlaySoundButton(true);
            Objects.requireNonNull(minecraft.player).playSound(sound, 80, 1);
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void playDownSound(SoundHandler soundHandler) {
    }
}
