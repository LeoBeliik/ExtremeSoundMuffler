package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.CommonClass;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TextComponent;

public class InvButton extends AbstractButton implements IColorsGui {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final AbstractContainerScreen<?> parent;
    private boolean hold = false;

    public InvButton(AbstractContainerScreen parentGui, int x, int y) {
        super(parentGui.width + x, parentGui.height + y, 11, 11, TextComponent.EMPTY);
        parent = parentGui;
    }

    @Override
    public void onPress() {
        MainScreen.open();
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            CommonClass.renderGui();
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
            //Config.setInvButtonHorizontal(x - parent.getGuiLeft());
            //Config.setInvButtonVertical(y - parent.getGuiTop());
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void drag(int mouseX, int mouseY) {
        x = mouseX - (this.width / 2);
        y = mouseY - (this.height / 2);
    }


    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}