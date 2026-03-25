package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AnchorButton extends Button implements IColorsGui {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private final Font font = minecraft.font;

    public AnchorButton(int x, int y, Component message, OnPress press, CreateNarration narration) {
        super(x, y, 16, 16, message, press, narration);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        guiGraphicsExtractor.centeredText(font, this.getMessage(), this.getX() + 8, this.getY() + 4, whiteText);
    }
}
