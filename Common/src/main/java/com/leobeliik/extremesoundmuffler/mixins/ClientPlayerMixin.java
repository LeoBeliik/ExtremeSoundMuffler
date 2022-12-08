package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.utils.DataManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class ClientPlayerMixin {

    @Inject(method = "Lnet/minecraft/client/player/AbstractClientPlayer;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lcom/mojang/authlib/GameProfile;)V", at = @At("TAIL"))
    private void esm_onClientPlayerJoin(ClientLevel level, GameProfile profile, CallbackInfo ci) {
        //load muffler data when client player joins the world
        DataManager.loadData();
    }
}
