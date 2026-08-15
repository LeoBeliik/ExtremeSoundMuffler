package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import static com.leobeliik.extremesoundmuffler.Constants.font;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getIconsTextureID;

public class ESMAnchor extends ESMButton implements IColorsGui {

    private final Anchor anchor;
    private final boolean scroll;

    public ESMAnchor(int x, int y, OnPress onPress, Anchor anchor, boolean scroll) {
        super(x, y, 0, scroll ? 102 : 85, 222, 15, Component.nullToEmpty(anchor.getName()), onPress, null);
        this.anchor = anchor;
        this.scroll = scroll;
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX(), getY(), 0, this.getTextureY(), this.getWidth(), this.getHeight(), 256, 256); //button texure

        //render text like this because I don't like how the default text looks like
        if (!this.message.equals(Component.empty())) {
            float centerX = this.getX() + this.getWidth() / 2F - font.width(this.getMessage()) / 2F;
            int textColor = this.isHovered() ? aquaText : whiteText;
            guiGraphics.drawString(font, this.getMessage(), (int) centerX, this.getY() + 3, textColor, this.isSelected());
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean click) {
        if (scroll && event.x() < this.getX() + 15)
            return false;
        return super.mouseClicked(event, click);
    }

    public Anchor getAnchor() {
        return anchor;
    }
}
