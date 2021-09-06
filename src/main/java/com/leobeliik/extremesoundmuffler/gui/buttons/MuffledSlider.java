package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("EmptyMethod")
public class MuffledSlider extends Widget implements IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private final FontRenderer font = minecraft.font;
    private static boolean showSlider = false;
    private final ResourceLocation sound;
    private float sliderValue;
    private int bg;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;
    private MufflerScreen screen;

    public MuffledSlider(int x, int y, float sliderValue, int bg, ResourceLocation sound, MufflerScreen screen) {
        super(x, y, 205, 14, ITextComponent.nullToEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.sliderValue = sliderValue;
        this.sound = sound;
        this.screen = screen;
        this.bg = bg;
        setBtnToggleSound(sound);
        setBtnPlaySound(sound);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bind(SoundMuffler.getGui());
        //row highlight
        fill(ms, x, y - 1, x + width + 4, y + height - 2, bg);
        drawGradient(ms);
        float v = getFGColor() == whiteText ? 214F : 203F;
        blit(ms, btnToggleSound.x, btnToggleSound.y, 11F, v, 11, 11, 256, 256); //muffle button bg
        blit(ms, btnPlaySound.x, btnPlaySound.y, 0F, 203F, 11, 11, 256, 256); //play button bg
        drawMessage(ms);
    }

    private void drawMessage(MatrixStack ms) {
        int v = Math.max(width, font.width(getMessage().getString()));
        if (showSlider && isFocused() && isHovered) {
            drawCenteredString(ms, font, "Volume: " + (int) (sliderValue * 100), x + (width / 2), y + 2, yellowText); //title
        } else {
            String msgTruncated;
            if (isHovered) {
                msgTruncated = getMessage().getString();
                fill(ms, x + width + 3, y, x + v + 3, y + font.lineHeight + 2, bg);
            } else {
                msgTruncated = font.substrByWidth(getMessage(), 205).getString();
            }
            font.drawShadow(ms, msgTruncated, x + 2, y + 2, getFGColor()); //title
        }
    }

    private void drawGradient(MatrixStack ms) {
        if (getFGColor() == cyanText) {
            blit(ms, x, y - 1, 39, 203, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            if (isHovered) {
                blit(ms, x + (int) (sliderValue * (width - 6)) + 1, y, 0F, 214F, 5, 11, 256, 256); //Slider
            }
        }
    }

    public void isVisible(boolean b) {
        this.visible = b;
        this.getBtnToggleSound().visible = b;
        this.getBtnPlaySound().visible = b;
    }

    public void setY(int y) {
        this.y = y;
        this.getBtnToggleSound().y = y;
        this.getBtnPlaySound().y = y;
    }

    private void setBtnToggleSound(ResourceLocation sound) {
        int x = Config.getLeftButtons() ? this.x - 26 : this.x + width + 4;
        btnToggleSound = new Button(x, y, 11, 11, StringTextComponent.EMPTY, b -> {
            if (getFGColor() == cyanText) {
                screen.removeSoundMuffled(sound);
                super.setFGColor(whiteText);
            } else {
                setSliderValue(Config.getDefaultMuteVolume());
                screen.addSoundMuffled(sound, sliderValue);
                super.setFGColor(cyanText);
            }
        });
    }

    private Button getBtnToggleSound() {
        return btnToggleSound;
    }

    private void setBtnPlaySound(ResourceLocation sound) {
        btnPlaySound = new PlaySoundButton(btnToggleSound.x + 13, y, new SoundEvent(sound));
    }

    private PlaySoundButton getBtnPlaySound() {
        return btnPlaySound;
    }

    private void changeSliderValue(float mouseX) {
        setSliderValue((mouseX - (x + 4)) / (width - 8));
    }

    private void setSliderValue(float value) {
        double d0 = sliderValue;
        sliderValue = MathHelper.clamp(value, 0.0F, 0.9F);
        if (d0 != sliderValue) {
            func_230972_a_();
        }
        func_230979_b_();
        screen.replaceVolume(sound, sliderValue);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        changeSliderValue((float) mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.btnToggleSound.mouseClicked(mouseX, mouseY, button);
        this.btnPlaySound.mouseClicked(mouseX, mouseY, button);

        if (isHovered && getFGColor() == cyanText) {
            changeSliderValue((float) mouseX);
            showSlider = true;
            setFocused(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setFocused(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void func_230979_b_() {
    }

    private void func_230972_a_() {
    }
}