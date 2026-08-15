package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import java.util.function.Supplier;
import static com.leobeliik.extremesoundmuffler.Constants.font;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getIconsTextureID;

public class ESMTab extends Button implements IColorsGui {

    private int x,y;
    private boolean showing;

    public ESMTab(int x, int y, Component name, OnPress press) {
        super(x, y, 86, 16, name, press, Supplier::get);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        int color = this.isHovered() ? aquaText : whiteText;
        if (this.showing) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), x, y, 0f, 69f, 86, 16, 256, 256); //tab selected texure
            color = greenText; //text color
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), x, y, 88f, 69f, 86, 16, 256, 256); //tab deselected texture
        }
        //Indicate if the button is being tagged
        guiGraphics.drawCenteredString(font, this.getMessage(), this.x + (this.width / 2), this.y + (this.height / 4), color);
    }

    public void hide() {
        this.showing = false;
    }

    public void show() {
        this.showing = true;
    }

    public boolean isSelected() {
        return this.showing;
    }
}
