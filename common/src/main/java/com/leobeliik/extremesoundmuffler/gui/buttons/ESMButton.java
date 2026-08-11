package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

import static com.leobeliik.extremesoundmuffler.Constants.darkMode;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getIconsTextureID;

public class ESMButton extends Button implements IColorsGui {


    private int textureX, textureY, width, height;
    private boolean toggle = false;
    private boolean canHover = true;
    private boolean selected = false;
    private Component tooltip;

    public ESMButton(int x, int y, int textureX, int textureY, int width, int height, Component title, ESMButton.OnPress onPress, Component tooltip) {
        super(x, y, width, height, title, onPress, Supplier::get);
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
        this.tooltip = tooltip;
    }

    public ESMButton(int x, int y, int textureX, int textureY, int size, ESMButton.OnPress onPress, Component tooltip) {
        this(x, y, textureX, textureY, size, size, Component.empty(), onPress, tooltip);
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        int hovered = this.isHovered() && this.canHover && this.active ? this.height : 0; //if hovered use the hover texture
        int toggled = this.toggle ? this.width : 0; //if the button is toggled (disabled/enabled)
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX(), getY(), textureX + toggled, textureY + hovered , this.width, this.height, 256, 256); //button texure

        //render text like this because I don't like how the default text looks like
        Font font = Minecraft.getInstance().font;
        if (!this.message.equals(Component.empty())) {
            float centerX = this.getX() + this.getWidth() / 2F - font.width(this.getMessage()) / 2F;
            int textColor = this.isHovered ? aquaText : darkMode ? grayText : blackText;
            if (this.selected) textColor = greenText;
            guiGraphics.drawString(font, this.getMessage(), (int) centerX, this.getY() + 3, textColor, this.selected);
        }

        //render tooltip
        if (this.isHovered() && this.isActive()) {
            guiGraphics.setTooltipForNextFrame(font, tooltip, getX() - (font.width(tooltip)) / 2, getY() > 200 ? getY() + (height + font.lineHeight * 2) : getY() - 1);
        }
    }

    public void setTooltip(Component tooltip) { this.tooltip = tooltip; }

    public void hide() {
        this.visible = false;
    }

    public void show() {
        this.visible = true;
    }

    public void setVisible(boolean visibility) {
        this.visible = visibility;
    }

    public void toggle() {
        this.toggle = !toggle;
    }

    public boolean isToggled() { return this.toggle;}

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public void canHover(boolean b) {
        this.canHover = b;
    }

    public void selected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public int getTextureX() {
        return textureX;
    }

    int getTextureY() {
        return textureY;
    }
}
