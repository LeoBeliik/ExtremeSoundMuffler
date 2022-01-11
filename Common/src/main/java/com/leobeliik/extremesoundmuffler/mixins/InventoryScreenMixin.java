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
    private InvButton esm_invButton;

    public InventoryScreenMixin(InventoryMenu inventoryMenu, Inventory inventory, Component component) {
        super(inventoryMenu, inventory, component);
    }

    //Adds the inventory button
    @Inject(method = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;init()V", at = @At("TAIL"))
    private void esm_inventoryScreenInit(CallbackInfo CI) {
        esm_invButton = new InvButton(esm_getBX(), esm_getBY());
        this.addRenderableWidget(esm_invButton);
    }

    //Move the button when the recipe book gui opens
    @Inject(method = "render", at = @At("HEAD"))
    private void esm_inventoryScreenRender(PoseStack ps, int mouseX, int mouseY, float tick, CallbackInfo ci) {
        if (!InvButton.hold) {
            esm_invButton.x = esm_getBX();
            esm_invButton.y = esm_getBY();
        } else {
            esm_invButton.x = (mouseX - 6);
            esm_invButton.y = (mouseY - 6);
        }
    }

    //Fabric can't do shit by itself
    @Inject(method = "mouseReleased", at = @At("TAIL"))
    private void esm_onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (InvButton.hold && button == 1) {
            InvButton.hold = false;
            esm_invButton.x -= leftPos;
            esm_invButton.y -= topPos;
        }
    }

    //equivalent of AbstractContainerScreen#getGuiLeft() in forge
    @Unique
    private int esm_getBX() {
        return leftPos + CommonConfig.get().invButtonHorizontal().get();
    }

    //equivalent of AbstractContainerScreen#getGuiTop() in forge
    @Unique
    private int esm_getBY() {
        return topPos + CommonConfig.get().invButtonVertical().get();
    }

}
