package com.leobeliik.extremesoundmuffler.mixins;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.buttons.InvButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Shadow
    private static CreativeModeTab selectedTab;
    @Unique
    private InvButton esm_creativeInvButton = new InvButton(esm_getCIBX(), esm_getCIBY());

    public CreativeInventoryScreenMixin(Player player, FeatureFlagSet flagSet, boolean b) {
        super(new CreativeModeInventoryScreen.ItemPickerMenu(player), player.getInventory(), CommonComponents.EMPTY);
    }

    //Renders the inventory button in the Creative Screen if enabled in config
    @Inject(method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;init()V", at = @At("TAIL"))
    private void esm_creativeInventoryScreenInit(CallbackInfo CI) {
        if (!CommonConfig.get().disableCreativeInventoryButton().get()) {
            this.addRenderableWidget(esm_creativeInvButton);
        }
    }

    //Move the button when the recipe book gui opens
    @Inject(method = "render", at = @At("HEAD"))
    private void esm_creativeInventoryScreenRender(PoseStack ps, int mouseX, int mouseY, float tick, CallbackInfo ci) {
        esm_creativeInvButton.visible = selectedTab.getType() == CreativeModeTab.Type.INVENTORY;

        if (esm_creativeInvButton.visible) {
            if (!esm_creativeInvButton.hold) {
                esm_creativeInvButton.setX(esm_getCIBX());
                esm_creativeInvButton.setY(esm_getCIBY());
            } else {
                esm_creativeInvButton.setX(mouseX - 6);
                esm_creativeInvButton.setY(mouseY - 6);
            }
        }
    }

    //Fabric can't do shit by itself
    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void esm_onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (esm_creativeInvButton.visible && esm_creativeInvButton.hold && button == 1) {
            esm_creativeInvButton.setX(esm_creativeInvButton.getX() - leftPos);
            esm_creativeInvButton.setY(esm_creativeInvButton.getY() - topPos);
            esm_creativeInvButton.hold = false;
        }
    }

    //equivalent of AbstractContainerScreen#getGuiLeft() in forge
    @Unique
    private int esm_getCIBX() {
        return leftPos + CommonConfig.get().creativeInvButtonHorizontal().get();
    }

    //equivalent of AbstractContainerScreen#getGuiTop() in forge
    @Unique
    private int esm_getCIBY() {
        return topPos + CommonConfig.get().creativeInvButtonVertical().get();
    }

}
