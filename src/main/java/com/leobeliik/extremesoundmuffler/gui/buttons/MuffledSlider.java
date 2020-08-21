package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.ResourceLocation;

public class MuffledSlider extends AbstractSlider {

    private static final ResourceLocation GUI = SoundMufflerScreen.getGUI();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final FontRenderer font = minecraft.fontRenderer;
    private float volume;

    public MuffledSlider(int xIn, int yIn, float valueIn) {
        super(xIn, yIn, 104, 12, valueIn);
        volume = valueIn;
    }

    @Override
    public void render(int x, int y, float partialTicks) {
        if (this.visible) {
            String message = "Volume: " + (int) (value * 100);
            minecraft.getTextureManager().bindTexture(GUI);
            blit(this.x, this.y, 0, 220, 104, 15); //Slider bg
            blit(this.x + (int) (value * (width - 8)) + 3, this.y + 3, 70, 0F, 5, 10, 80, 80); //Slider
            font.drawString(message, this.x + (this.width / 2F) - (font.getStringWidth(message) / 2F), this.y + 4F, 16777215);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {

    }
}
