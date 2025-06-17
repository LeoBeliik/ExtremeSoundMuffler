package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getTextureRL;

public class InvButton extends AbstractButton implements IColorsGui {

    public boolean hold = false;
    public InvButton(int x, int y) {
        super(x, y, 11, 11, Component.empty());
    }

    private boolean drag = false;

    @Override
    public void onPress() {
        SoundMufflerCommon.openMainScreen();
    }

    @Override
    public void renderScrollingString(@NotNull GuiGraphics render, @NotNull Font font, int mouseX, int mouseY) {
        if (this.visible) {
            render.blit(RenderPipelines.GUI_TEXTURED, getTextureRL(), getX(), getY(), 43f, 202f, 11, 11, 256, 256); //button texure
            if (isHovered && !hold) {
                render.drawStringWithBackdrop(font, Component.translatable("inventory.btn"), getX() - 8, getY() + this.height + 1, darkBG, whiteText);
            }
            drag = isMouseOver(mouseX, mouseY);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && isMouseOver(pMouseX, pMouseY)) {
            hold = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, Component.translatable("inventory.btn"));
    }

    public boolean isDrag() {
        return drag;
    }

}