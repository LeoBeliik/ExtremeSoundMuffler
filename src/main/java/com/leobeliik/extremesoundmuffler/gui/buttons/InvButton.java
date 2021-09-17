package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton implements IColorsGui {

    private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ContainerScreen<?> parent;
    private boolean hold = false;

    public InvButton(ContainerScreen parentGui, int x, int y) {
        super(parentGui.getGuiLeft() + x, parentGui.getGuiTop() + y, 11, 11, StringTextComponent.EMPTY);
        parent = parentGui;
    }

    @Override
    public void onPress() {
        MainScreen.open();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            minecraft.getTextureManager().bind(SoundMuffler.getGui());
            blit(matrix, x, y, 43f, 202f, 11, 11, 256, 256);
            if (isMouseOver(mouseX, mouseY) && !hold) {
                drawCenteredString(matrix, minecraft.font, "Muffler", x + 5, this.y + this.height + 1, whiteText);
            }
            if (hold) {
                drag(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && isMouseOver(pMouseX, pMouseY)) {
            hold = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && isMouseOver(pMouseX, pMouseY)) {
            hold = false;
            Config.setInvButtonHorizontal(x - parent.getGuiLeft());
            Config.setInvButtonVertical(y - parent.getGuiTop());
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void drag(int mouseX, int mouseY) {
        x = mouseX - (this.width / 2);
        y = mouseY - (this.height / 2);
    }
}