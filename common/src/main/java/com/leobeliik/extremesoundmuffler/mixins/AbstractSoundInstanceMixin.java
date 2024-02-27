package com.leobeliik.extremesoundmuffler.mixins;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceMixin {

    @Accessor("x")
    void setX(double x);
    @Accessor("y")
    void setY(double y);
    @Accessor("z")
    void setZ(double z);
}
