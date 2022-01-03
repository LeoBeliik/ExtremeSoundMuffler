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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    @Unique
    private InvButton invButton = new InvButton(this, getBX(), getBY());

    public InventoryScreenMixin(InventoryMenu inventoryMenu, Inventory inventory, Component component) {
        super(inventoryMenu, inventory, component);
    }

    //Adds the inventory button
    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void InventoryScreenInit(CallbackInfo CI) {
        this.addRenderableWidget(invButton);
    }

    //Move the button when the recipe book gui opens
    @Inject(method = "renderBg", at = @At("TAIL"), remap = false)
    private void InventoryScreenRender(PoseStack ps, float tick, int mouseX, int mouseY, CallbackInfo ci) {
        if (InvButton.notHolding()) {
            invButton.setX(getBX());
            invButton.setY(getBY());
        }
    }

    //Fabric can't do shit by itself
    @Inject(method = "mouseReleased", at = @At("TAIL"), remap = false)
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (!InvButton.notHolding() && button == 1) {
            invButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    //equivalent of AbstractContainerScreen#getGuiLeft() in forge
    private int getBX() {
        return leftPos + CommonConfig.get().invButtonHorizontal().get();
    }

    //equivalent of AbstractContainerScreen#getGuiTop() in forge
    private int getBY() {
        return topPos + CommonConfig.get().invButtonVertical().get();
    }

}
