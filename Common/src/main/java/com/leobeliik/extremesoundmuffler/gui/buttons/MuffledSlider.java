package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

@SuppressWarnings("EmptyMethod")
public class MuffledSlider extends AbstractWidget implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private final Font font = minecraft.font;
    private static boolean showSlider = false;
    private final ResourceLocation sound;
    private double sliderValue;
    private int bg;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;
    private MufflerScreen screen;
    private boolean isMuffling = false;

    public MuffledSlider(int x, int y, int bg, ResourceLocation sound, double sliderValue, MufflerScreen screen) {
        super(x, y, 205, 14, Component.nullToEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.bg = bg;
        this.sound = sound;
        this.sliderValue = sliderValue;
        this.screen = screen;
        setBtnToggleSound(sound);
        setBtnPlaySound(sound);
    }


    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        isMuffling = getFGColor(getText(), "aqua");
        SoundMufflerCommon.renderGui();
        //row highlight
        fill(stack, x, y - 1, x + width + 4, y + height - 2, bg);
        drawGradient(stack);
        float v = isMuffling ? 202F : 213F;
        //--------------- Render buttons BG ---------------//
        blit(stack, btnToggleSound.x, btnToggleSound.y, 43F, v, 11, 11, 256, 256); //muffle button bg
        blit(stack, btnPlaySound.x, btnPlaySound.y, 32F, 202F, 11, 11, 256, 256); //play button bg

        //--------------- Render Tooltips ---------------//
        if (btnToggleSound.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, btnToggleSound, isMuffling ? new TranslatableComponent("slider.btn.muffler.unmuffle") : new TranslatableComponent("slider.btn.muffler.muffle"));

        }
        if (btnPlaySound.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, btnPlaySound, new TranslatableComponent("slider.btn.play.play_sound"));
        }

        //--------------- Render Slider Text ---------------//
        this.drawMessage(stack);
    }

    private void renderButtonTooltip(PoseStack stack, AbstractButton btn, Component text) {
        int lengthierText = font.width(text);
        int x1 = btn.x + (btn.getHeight() / 2) - (font.width(text) / 2);
        int x2 = x1 + lengthierText + 2;
        int y1 = btn.y - font.lineHeight - 2;
        int y2 = btn.y - 1;

        fill(stack, x1 - 3, y1 - 5, x2, y2 + 1, darkBG);
        font.draw(stack, text, x1, y1 - 2, whiteText);
    }

    private void drawMessage(PoseStack stack) {
        int v = Math.max(width, font.width(getMessage().getString()));
        if (showSlider && isFocused() && isHovered) {
            drawCenteredString(stack, font, new TranslatableComponent("slider.btn.volume").getKey() + (int) (sliderValue * 100), x + (width / 2), y + 2, aquaText); //title
        } else {
            String msgTruncated;
            if (this.isHovered) {
                msgTruncated = getMessage().getString();
                fill(stack, this.x + this.width + 3, this.y, this.x + v + 3, this.y + font.lineHeight + 2, darkBG);
            } else {
                msgTruncated = font.substrByWidth(getMessage(), 205).getString();
            }
            font.drawShadow(stack, msgTruncated, this.x + 2, this.y + 2, isMuffling ? aquaText : whiteText); //title
        }
    }

    //draws the "rainbow" gradient in the background
    private void drawGradient(PoseStack stack) {
        if (isMuffling) {
            blit(stack, this.x, this.y - 1, 0, 234, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            if (this.isHovered) {
                blit(stack, this.x + (int) (sliderValue * (width - 6)) + 1, this.y + 1, 32F, 224F, 5, 9, 256, 256); //Slider
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
        int x = CommonConfig.get().leftButtons().get() ? this.x - 26 : this.x + width + 4;
        btnToggleSound = new Button(x, y, 11, 11, TextComponent.EMPTY, b -> {
            if (isMuffling) {
                if (screen.removeSoundMuffled(sound)) {
                    setFGColor(this, "white");
                }
            } else {
                setSliderValue(CommonConfig.get().defaultMuteVolume().get());
                if (screen.addSoundMuffled(sound, sliderValue)) {
                    setFGColor(this, "aqua");
                }
            }
        });
    }

    public Button getBtnToggleSound() {
        return btnToggleSound;
    }

    private void setBtnPlaySound(ResourceLocation sound) {
        btnPlaySound = new PlaySoundButton(btnToggleSound.x + 13, y, new SoundEvent(sound));
    }

    private PlaySoundButton getBtnPlaySound() {
        return btnPlaySound;
    }

    private void changeSliderValue(double mouseX) {
        setSliderValue((mouseX - (x + 4)) / (width - 8));
    }

    //from vanilla
    private void setSliderValue(double value) {
        double d0 = sliderValue;
        sliderValue = Mth.clamp(value, 0.0D, 0.9D);
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

        if (isHovered && isMuffling) {
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

    private MutableComponent getText() {
        return this.getMessage().copy();
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, isMuffling ? new TranslatableComponent("slider.btn.volume").getKey() + (int) (sliderValue * 100) : this.sound.toString());
    }
}