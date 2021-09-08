package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import javax.annotation.ParametersAreNonnullByDefault;

public class MufflerListButton extends Button implements IColorsGui {
    private final Minecraft minecraft = Minecraft.getInstance();
    private final FontRenderer font = minecraft.font;

    public MufflerListButton(int x, int y, ITextComponent message, Button.IPressable onPress) {
        super(x, y, 80, 12, message, onPress);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float delta) {
        String message = font.substrByWidth(getMessage(), 76).getString();
        minecraft.getTextureManager().bind(SoundMuffler.getGui());
        blit(ms, x, y, 39F, 216F, getWidth(), getHeight(), 256, 256);
        String msgTruncated;
        msgTruncated = font.width(getMessage()) > 76 ? font.substrByWidth(getMessage(), 70).getString() + ".." : getMessage().getString();
        drawCenteredString(ms, font, msgTruncated, x + (getWidth() / 2), y + 2, getFGColor());
    }
}
