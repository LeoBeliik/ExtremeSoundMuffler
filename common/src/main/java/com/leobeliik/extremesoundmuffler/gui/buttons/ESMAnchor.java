package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getIconsTextureID;

public class ESMAnchor extends ESMButton implements IColorsGui {

    private final Anchor anchor;

    public ESMAnchor(int x, int y, int height, OnPress onPress, Anchor anchor) {
        super(x, y, 0, 85, 222, height, Component.nullToEmpty(anchor.getName()), onPress, null);
        this.anchor = anchor;
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        boolean hovered = this.isHovered() && this.canHover && this.active; //if hovered use the hover texture
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX(), getY(), 0, 85 , this.getWidth(), this.getHeight(), 256, 256); //button texure

        //render text like this because I don't like how the default text looks like
        Font font = Minecraft.getInstance().font;
        if (!this.message.equals(Component.empty())) {
            float centerX = this.getX() + this.getWidth() / 2F - font.width(this.getMessage()) / 2F;
            int textColor = hovered ? aquaText : whiteText;
            guiGraphics.drawString(font, this.getMessage(), (int) centerX, this.getY() + 3, textColor, this.isSelected());
        }
    }

    public Anchor getAnchor() {
        return anchor;
    }
}
