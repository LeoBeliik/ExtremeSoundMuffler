package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class Muffler implements ISound {

    private final ISound sound;

    Muffler(ISound muttedSound) {
        sound = muttedSound;
    }

    @Override
    public float getVolume() {
        return sound.getVolume() * 0;
    }

    @Nonnull
    @Override
    public ResourceLocation getSoundLocation() {
        return sound.getSoundLocation();
    }

    @Nullable
    @Override
    public SoundEventAccessor createAccessor(@Nonnull SoundHandler handler) {
        return sound.createAccessor(handler);
    }

    @Nonnull
    @Override
    public Sound getSound() {
        return sound.getSound();
    }

    @Nonnull
    @Override
    public SoundCategory getCategory() {
        return sound.getCategory();
    }

    @Override
    public boolean canRepeat() {
        return sound.canRepeat();
    }

    @Override
    public boolean isGlobal() {
        return sound.isGlobal();
    }

    @Override
    public int getRepeatDelay() {
        return sound.getRepeatDelay();
    }

    @Override
    public float getPitch() {
        return sound.getPitch();
    }

    @Override
    public float getX() {
        return sound.getX();
    }

    @Override
    public float getY() {
        return sound.getY();
    }

    @Override
    public float getZ() {
        return sound.getZ();
    }

    @Nonnull
    @Override
    public AttenuationType getAttenuationType() {
        return sound.getAttenuationType();
    }


    @OnlyIn(Dist.CLIENT)
    public static class TickableMuffler extends Muffler implements ITickableSound {

        private final ITickableSound sound;

        TickableMuffler(ITickableSound muttedSound) {
            super(muttedSound);
            sound = muttedSound;
        }

        @Override
        public boolean isDonePlaying() {
            return sound.isDonePlaying();
        }

        @Override
        public void tick() {
            sound.tick();
        }
    }
}