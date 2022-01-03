package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    @Unique
    private InvButton button = new InvButton(this, getBX(), getBY());

    public InventoryScreenMixin(InventoryMenu inventoryMenu, Inventory inventory, Component component) {
        super(inventoryMenu, inventory, component);
    }

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void InventoryScreenInit(CallbackInfo CI) {
        this.addRenderableWidget(button);
    }

    @Inject(method = "renderBg", at = @At("TAIL"), remap = false)
    private void InventoryScreenRender(PoseStack ps, float tick, int mouseX, int mouseY, CallbackInfo ci) {
        if (InvButton.notHolding()) {
            button.setX(getBX());
            button.setY(getBY());
        }
    }

    private int getBX() {
        return leftPos + CommonConfig.get().invButtonHorizontal().get();
    }

    private int getBY() {
        return topPos + CommonConfig.get().invButtonVertical().get();
    }

}
