package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

import java.util.Objects;

@SuppressWarnings("EmptyMethod")
public class MuffledSlider extends AbstractWidget implements ISoundLists, IColorsGui {

    private final String mainTitle = "ESM - Main Screen";
    private float sliderValue;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;
    private final ResourceLocation sound;
    public static ResourceLocation tickSound;
    public static boolean showSlider = false;


    public MuffledSlider(int x, int y, int width, int height, float sliderValue, ResourceLocation sound, String screenTitle, Anchor anchor) {
        super(x, y, width, height, Component.nullToEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.sliderValue = sliderValue;
        this.sound = sound;
        setBtnToggleSound(screenTitle, sound, anchor);
        setBtnPlaySound(sound);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        SoundMufflerCommon.renderGui();
        drawGradient(stack);
        float v = getFGColor(getText()).equals("white") ? 213F : 202F;
        blit(stack, btnToggleSound.x, btnToggleSound.y, 43F, v, 11, 11, 256, 256); //muffle button bg
        blit(stack, btnPlaySound.x, btnPlaySound.y, 32F, 202F, 11, 11, 256, 256); //play button bg
        this.drawMessage(stack, minecraft);
    }

    private void drawMessage(PoseStack stack, Minecraft minecraft) {
        Font font = minecraft.font;
        int v = Math.max(this.width, font.width(getMessage().getString()));
        if (showSlider && this.isFocused() && this.isHovered) {
            drawCenteredString(stack, font, "Volume: " + (int) (sliderValue * 100), this.x + (this.width / 2), this.y + 2, whiteText); //title
        } else {
            String msgTruncated;
            if (this.isHovered) {
                msgTruncated = getMessage().getString();
                fill(stack, this.x + this.width + 3, this.y, this.x + v + 3, this.y + font.lineHeight + 2, darkBG);
            } else {
                msgTruncated = font.substrByWidth(getMessage(), 205).getString();
            }
            font.drawShadow(stack, msgTruncated, this.x + 2, this.y + 2, getFGColor(getText()).equals("aqua") ? aquaText : whiteText); //title
        }
    }

    private void drawGradient(PoseStack stack) {
        if (getFGColor(getText()).equals("aqua")) {
            blit(stack, this.x, this.y - 1, 0, 234, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            if (this.isHovered) {
                blit(stack, this.x + (int) (sliderValue * (width - 6)) + 1, this.y + 1, 32F, 224F, 5, 9, 256, 256); //Slider
            }
        }
    }

    private void setBtnToggleSound(String screenTitle, ResourceLocation sound, Anchor anchor) {
        int x = CommonConfig.get().leftButtons().get() ? this.x - 24 : this.x + width + 5;
        btnToggleSound = new Button(x, this.y, 11, 11, TextComponent.EMPTY, b -> {
            if (getFGColor(getText()).equals("aqua")) {
                if (screenTitle.equals(mainTitle)) {
                    muffledSounds.remove(sound);
                } else {
                    anchor.removeSound(sound);
                }
                setMessage(setFGColor(getText(), "white"));
            } else {
                if (screenTitle.equals(mainTitle)) {
                    setSliderValue(CommonConfig.get().defaultMuteVolume().get().floatValue());
                    muffledSounds.put(sound, sliderValue);
                } else if (anchor.getAnchorPos() != null) {
                    setSliderValue(CommonConfig.get().defaultMuteVolume().get().floatValue());
                    anchor.addSound(sound, sliderValue);
                } else {
                    return;
                }
                setMessage(setFGColor(getText(), "aqua"));
            }
        });
    }

    public Button getBtnToggleSound() {
        return btnToggleSound;
    }

    private void setBtnPlaySound(ResourceLocation sound) {
        btnPlaySound = new PlaySoundButton(btnToggleSound.x + 12, this.y, new SoundEvent(sound));
    }

    public PlaySoundButton getBtnPlaySound() {
        return btnPlaySound;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == 263;
        if (flag || keyCode == 262) {
            float f = flag ? -1.0F : 1.0F;
            this.setSliderValue(this.sliderValue + (f / (this.width - 8)));
        }
        return false;
    }

    private void changeSliderValue(float mouseX) {
        this.setSliderValue((mouseX - (this.x + 4)) / (this.width - 8));
    }

    private void setSliderValue(float value) {
        double d0 = this.sliderValue;
        this.sliderValue = Mth.clamp(value, 0.0F, 0.9F);
        if (d0 != this.sliderValue) {
            this.func_230972_a_();
        }
        this.func_230979_b_();
        updateVolume();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.changeSliderValue((float) mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered && getFGColor(getText()).equals("aqua")) {
            this.changeSliderValue((float) mouseX);
            showSlider = true;
            this.setFocused(true);
            tickSound = this.sound;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setFocused(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateVolume() {
        String screenTitle = MainScreen.getScreenTitle();

        if (screenTitle.equals(mainTitle)) {
            muffledSounds.replace(this.sound, this.sliderValue);
        } else {
            Objects.requireNonNull(MainScreen.getAnchorByName(screenTitle)).replaceSound(this.sound, this.sliderValue);
        }
    }

    private void func_230979_b_() {
    }

    private void func_230972_a_() {
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    private MutableComponent getText() {
        return this.getMessage().copy();
    }
}