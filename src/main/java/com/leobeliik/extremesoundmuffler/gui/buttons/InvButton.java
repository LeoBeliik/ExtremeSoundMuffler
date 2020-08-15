package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ContainerScreen<?> parent;
    private final int buttonX;

    public InvButton(ContainerScreen parentGui, int x, int y) {
        super(x + parentGui.getGuiLeft() + 11, parentGui.getGuiTop() + y - 2, 10, 10, "");
        parent = parentGui;
        buttonX = x;
    }

    @Override
    public void onPress() {
        SoundMufflerScreen.open();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            x = buttonX + parent.getGuiLeft() + 11;
            minecraft.getTextureManager().bindTexture(SoundMufflerScreen.getGUI());
            blit(x, y, 0f, 0f, 10, 10, 80, 80);
            if (mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height) {
                drawCenteredString(minecraft.fontRenderer, "Muffler", x + 5, this.y + this.height + 1, 16777215);
            }
        }
    }
}