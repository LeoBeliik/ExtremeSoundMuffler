package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import net.minecraft.client.gui.GuiGraphics;
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
    private InvButton esm_invButton = new InvButton(esm_getIBX(), esm_getIBY());

    public InventoryScreenMixin(InventoryMenu inventoryMenu, Inventory inventory, Component component) {
        super(inventoryMenu, inventory, component);
    }

    //Renders the inventory button if enabled in config
    @Inject(method = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;init()V", at = @At("TAIL"))
    private void esm_inventoryScreenInit(CallbackInfo CI) {
        if (!CommonConfig.get().disableInventoryButton().get()) {
            this.addRenderableWidget(esm_invButton);
        }
    }

    //Move the button when the recipe book gui opens
    @Inject(method = "render", at = @At("HEAD"))
    private void esm_inventoryScreenRender(GuiGraphics render, int mouseX, int mouseY, float tick, CallbackInfo ci) {
        if (esm_invButton.hold) {
            esm_invButton.setX(mouseX - 6);
            esm_invButton.setY(mouseY - 6);
        } else {
            esm_invButton.setX(esm_getIBX());
            esm_invButton.setY(esm_getIBY());
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void esm_onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (esm_invButton.hold && button == 1) {
            esm_invButton.setX(esm_invButton.getX() - leftPos);
            esm_invButton.setY(esm_invButton.getY() - topPos);
            esm_invButton.hold = false;
        }
    }

    //equivalent of AbstractContainerScreen#getGuiLeft() in forge
    @Unique
    private int esm_getIBX() {
        return leftPos + CommonConfig.get().invButtonHorizontal().get();
    }

    //equivalent of AbstractContainerScreen#getGuiTop() in forge
    @Unique
    private int esm_getIBY() {
        return topPos + CommonConfig.get().invButtonVertical().get();
    }

}
