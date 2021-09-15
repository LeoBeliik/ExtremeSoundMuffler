package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton implements IColorsGui {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final AbstractContainerScreen<?> parent;
    private final int buttonX;

    public InvButton(AbstractContainerScreen parentGui, int x, int y) {
        super(x + parentGui.getGuiLeft() + 11, parentGui.getGuiTop() + y - 2, 11, 11, TextComponent.EMPTY);
        parent = parentGui;
        buttonX = x;
    }

    @Override
    public void onPress() {
        MainScreen.open();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            x = buttonX + parent.getGuiLeft() + 11;
            SoundMuffler.renderGui();
            blit(matrix, x, y, 43f, 202f, 11, 11, 256, 256);
            if (this.isHovered(mouseX, mouseY)) {
                drawCenteredString(matrix, minecraft.font, "Muffler", x + 5, this.y + this.height + 1, whiteText);
            }
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {}
}