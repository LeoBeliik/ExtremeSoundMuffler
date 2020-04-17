package com.leobeliik.extremesoundmuffler.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton {

    private final Minecraft minecraft = Minecraft.getInstance();

    public InvButton(ContainerScreen parentGui, int x, int y, int width, int height) {
        super(x + parentGui.getGuiLeft() + 11, parentGui.getGuiTop() + y - 2, width, height, "Sound Muffler");
    }

    @Override
    public void onPress() {
        SoundMufflerScreen.open();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            minecraft.getTextureManager().bindTexture(SoundMufflerScreen.getGUI());
            blit(x, y, 0, 0f, 0f, 10, 10, 82, 82);
            this.isHovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
            if (isHovered) {
                this.drawCenteredString(minecraft.fontRenderer, "Muffler", x + 5, this.y + this.height + 1, 16777215);
            }
        }
    }
}