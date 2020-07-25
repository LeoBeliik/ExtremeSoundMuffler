package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton {

    private final Minecraft minecraft = Minecraft.getInstance();

    public InvButton(ContainerScreen parentGui, int x, int y) {
        super(x + parentGui.getGuiLeft() + 11, parentGui.getGuiTop() + y - 2, 10, 10, StringTextComponent.EMPTY);
    }

    @Override
    public void onPress() {
        SoundMufflerScreen.open();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            minecraft.getTextureManager().bindTexture(SoundMufflerScreen.getGUI());
            blit(matrixStack, x, y, 0, 0f, 0f, 10, 10, 80, 80);
            this.renderToolTip(matrixStack, 0, 0);
            this.isHovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            if (isHovered) {
                this.drawCenteredString(matrixStack, minecraft.fontRenderer, "Muffler", x + 5, this.y + this.height + 1, 16777215);
            }
        }
    }
}