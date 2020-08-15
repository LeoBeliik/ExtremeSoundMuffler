package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.utils.EventsHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PlaySoundButton extends AbstractButton {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final SoundEvent sound;

    public PlaySoundButton(int x, int y, SoundEvent sound) {
        super(x, y, 10, 10, "");
        this.setAlpha(0);
        this.sound = sound;
    }

    @Override
    public void onPress() {
        if (this.active && this.visible) {
            EventsHandler.isFromPlaySoundButton(true);
            Objects.requireNonNull(minecraft.player).playSound(sound, 100, 1);
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void playDownSound(SoundHandler soundHandler) {
    }
}
