package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

public class MuffledSlider extends AbstractSlider {

    private static final ResourceLocation GUI = MainScreen.getGUI();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final FontRenderer font = minecraft.fontRenderer;
    private double volume;

    public MuffledSlider(int x, int y, int width, int height, ITextComponent message, double defaultValue) {
        super(x, y, width, height, message, defaultValue);
        volume = defaultValue;
    }


    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int x, int y, float partialTicks) {
        if (this.visible) {
            String message = "Volume: " + (int) (sliderValue * 100);
            minecraft.getTextureManager().bindTexture(GUI);
            blit(matrix, this.x, this.y, 0, 220, 104, 15); //Slider bg
            blit(matrix, this.x + (int) (sliderValue * (width - 8)) + 3, this.y + 3, 70, 0F, 5, 10, 80, 80); //Slider
            font.drawString(matrix, message, this.x + (this.width / 2F) - (font.getStringWidth(message) / 2F), this.y + 4F, 16777215);
        }
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    protected void func_230979_b_() {

    }

    @Override
    protected void func_230972_a_() {

    }
}
