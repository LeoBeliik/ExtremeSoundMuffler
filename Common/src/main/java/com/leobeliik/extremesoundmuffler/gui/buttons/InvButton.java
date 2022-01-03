package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TextComponent;


public class InvButton extends AbstractButton implements IColorsGui {

    private static boolean hold = false;
    private static int buttonX = CommonConfig.get().invButtonHorizontal().get();
    private static int buttonY = CommonConfig.get().invButtonVertical().get();
    private final AbstractContainerScreen<?> parent;

    public InvButton(AbstractContainerScreen parent, int x, int y) {
        super(x, y, 11, 11, TextComponent.EMPTY);
        this.parent = parent;
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
                drawCenteredString(matrix, minecraft.font, "Muffler", this.x + 5, this.y + this.height + 1, whiteText);
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
            buttonX = x - ((parent.width - 176) / 2); //forge's getLeft()
            buttonY = y - ((parent.height - 166) / 2); //forge's getTop()
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void drag(int mouseX, int mouseY) {
        //puts the cursor in the middle of the button
        this.x = mouseX - (this.width / 2);
        this.y = mouseY - (this.height / 2);
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, "Muffler");
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static boolean notHolding() {
        return !hold;
    }

    public static int getButtonX() {
        return buttonX;
    }

    public static int getButtonY() {
        return buttonY;
    }
}