package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

public class GradientButton extends Button implements IColorsGui {
    private static final FontRenderer font = Minecraft.getInstance().font;
    private boolean active = false;

    public GradientButton(int x, int y, int width, ITextComponent message, Button.IPressable onPress) {
        super(x, y, width, 14, message, onPress);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float delta) {
        //I couldn't help myself I really need the symmetry
        int messagePos = getMessage().getString().equals("All") ? x + width / 2 + 1 : x + width / 2;
        if (this.active) {
            fillGradient(ms, x, y - 1, x + width + 1, y + height - 2, brightBG, darkBG);
            drawCenteredString(ms, font, getMessage(), messagePos , y + 2, whiteText);
        } else {
            fillGradient(ms, x, y - 1, x + width + 1, y + height - 2, darkBG, brightBG);
            drawCenteredString(ms, font, getMessage(), messagePos, y + 2, grayText);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

}
