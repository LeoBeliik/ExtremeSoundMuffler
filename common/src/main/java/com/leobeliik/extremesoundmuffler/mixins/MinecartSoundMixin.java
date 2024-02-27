package com.leobeliik.extremesoundmuffler.mixins;

import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RidingMinecartSoundInstance.class)
public abstract class MinecartSoundMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void addPositionToInstance(Player player, AbstractMinecart minecart, boolean insideWater, CallbackInfo ci) {
        BlockPos minecartPos = minecart.getOnPos();
        ((AbstractSoundInstanceMixin)this).setX(minecartPos.getX());
        ((AbstractSoundInstanceMixin)this).setY(minecartPos.getY());
        ((AbstractSoundInstanceMixin)this).setZ(minecartPos.getZ());
    }
}
