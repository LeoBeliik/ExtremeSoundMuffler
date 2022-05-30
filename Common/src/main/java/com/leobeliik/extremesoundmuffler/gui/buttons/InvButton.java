package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;


public class InvButton extends AbstractButton implements IColorsGui {

    public boolean hold = false;
    private boolean drag = false;

    public InvButton(int x, int y) {
        super(x, y, 11, 11, TextComponent.EMPTY);
    }

    @Override
    public void onPress() {
        SoundMufflerCommon.openMainScreen();
    }

    @Override
    public void renderBg(PoseStack matrix, Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            SoundMufflerCommon.renderGui();
            blit(matrix, this.x, this.y, 43f, 202f, 11, 11, 256, 256); //button texure
            if (isMouseOver(mouseX, mouseY) && !hold) {
                drawCenteredString(matrix, minecraft.font, new TranslatableComponent("inventory.btn"), this.x + 5, this.y + this.height + 1, whiteText);
            }
            drag = isMouseOver(mouseX, mouseY);
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
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, new TranslatableComponent("inventory.btn"));
    }

    public boolean isDrag() {
        return drag;
    }

}