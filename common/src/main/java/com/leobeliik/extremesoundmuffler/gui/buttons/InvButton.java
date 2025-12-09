package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getTextureRL;

public class InvButton extends AbstractButton implements IColorsGui {

    public boolean hold = false;
    public InvButton(int x, int y) {
        super(x, y, 11, 11, Component.empty());
    }

    private boolean drag = false;

    @Override
    public void onPress(@NotNull InputWithModifiers inputWithModifiers) {
        SoundMufflerCommon.openMainScreen();
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics render, int i, int i1, float v) {
        renderScrollingString(render, Minecraft.getInstance().font, i, i1);
    }


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
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean success) {
        if (mouseButtonEvent.button() == 1 && isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y())) {
            hold = true;
        }
        return super.mouseClicked(mouseButtonEvent, success);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, Component.translatable("inventory.btn"));
    }

    public boolean isDrag() {
        return drag;
    }

}