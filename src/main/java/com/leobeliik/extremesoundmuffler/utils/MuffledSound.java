package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class MuffledSound implements ISound {

    private ISound sound;
    private float volume;

    public MuffledSound(ISound sound, float volume) {
        this.sound = sound;
        this.volume = volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Nonnull
    @Override
    public ResourceLocation getSoundLocation() {
        return sound.getSoundLocation();
    }

    @ParametersAreNonnullByDefault
    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler) {
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
    public float getVolume() {
        return sound.getVolume() * volume;
    }

    @Override
    public float getPitch() {
        return sound.getPitch();
    }

    @Override
    public double getX() {
        return sound.getX();
    }

    @Override
    public double getY() {
        return sound.getY();
    }

    @Override
    public double getZ() {
        return sound.getZ();
    }

    @Nonnull
    @Override
    public AttenuationType getAttenuationType() {
        return sound.getAttenuationType();
    }

    @OnlyIn(Dist.CLIENT)
    public static class MuffledTickableSound extends MuffledSound implements ITickableSound {

        private final ITickableSound sound;

        public MuffledTickableSound(ITickableSound sound, float volume) {
            super(sound, volume);
            this.sound = sound;
        }

        public void setVolume (float volume) {
            super.setVolume(volume);
        }

        @Override
        public boolean isDonePlaying() {
            return this.sound.isDonePlaying();
        }

        @Override
        public void tick() {
            this.sound.tick();
        }

        @Nonnull
        @Override
        public ResourceLocation getSoundLocation() {
            return sound.getSoundLocation();
        }

        @Nonnull
        @Override
        public Sound getSound() {
            return sound.getSound();
        }

        @Override
        public boolean canRepeat() {
            return sound.canRepeat();
        }

        @Override
        public int getRepeatDelay() {
            return sound.getRepeatDelay();
        }

        @Nonnull
        @Override
        public AttenuationType getAttenuationType() {
            return sound.getAttenuationType();
        }

        @Override
        public boolean shouldPlaySound() {
            return sound.shouldPlaySound();
        }
    }
}
