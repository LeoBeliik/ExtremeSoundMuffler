package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.SoundMufflerCommon;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getTextureRL;

@SuppressWarnings("EmptyMethod")
public class MuffledSlider extends AbstractWidget implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean showSlider = false;
    private final Font font = minecraft.font;
    private final ResourceLocation sound;
    private final MufflerScreen screen;
    private final int bg;
    private double sliderValue;
    private boolean isMuffling = false;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;

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
    public void renderWidget(@NotNull GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
        isMuffling = getFGColor(getText(), "aqua");
        SoundMufflerCommon.renderGui();
        //row highlight
        stack.fill(getX(), getY() - 1, getX() + width + 4, getY() + height - 2, bg);
        drawGradient(stack);
        float v = isMuffling ? 202F : 213F;
        //--------------- Render buttons BG ---------------//
        stack.blit(getTextureRL(), btnToggleSound.getX(), btnToggleSound.getY(), 43F, v, 11, 11, 256, 256); //muffle button bg
        stack.blit(getTextureRL(), btnPlaySound.getX(), btnPlaySound.getY(), 32F, 202F, 11, 11, 256, 256); //play button bg

        //--------------- Render Tooltips ---------------//
        if (btnToggleSound.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, btnToggleSound, isMuffling ? Component.translatable("slider.btn.muffler.unmuffle") : Component.translatable("slider.btn.muffler.muffle"));

        }
        if (btnPlaySound.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, btnPlaySound, Component.translatable("slider.btn.play.play_sound"));
        }

        //--------------- Render Slider Text ---------------//
        this.drawMessage(stack);
    }

    private void renderButtonTooltip(GuiGraphics stack, AbstractButton button, Component message) {
        int centeredMessageX = button.getX() - (font.width(message) / 2);
        int centeredMessageY = button.getY() - 1;

        stack.pose().pushPose();
        stack.renderTooltip(font, message, centeredMessageX, centeredMessageY);
        stack.pose().popPose();

        //stack.fill(x1 - 3, y1 - 5, x2, y2 + 1, darkBG);
        //stack.drawString(font, text, x1, y1 - 2, whiteText);
    }

    private void drawMessage(GuiGraphics stack) {
        int v = Math.max(width, font.width(getMessage().getString()));
        if (showSlider && isFocused() && isHovered) {
            stack.drawCenteredString(font, Component.translatable("slider.btn.volume", (int) (sliderValue * 100)), getX() + (width / 2), getY() + 2, aquaText); //title
        } else {
            String msgTruncated;
            if (this.isHovered) {
                msgTruncated = getMessage().getString();
                stack.fill(getX() + this.width + 3, getY(), getX() + v + 3, getY() + font.lineHeight + 2, darkBG);
            } else {
                msgTruncated = font.substrByWidth(getMessage(), 205).getString();
            }
            stack.drawString(font, msgTruncated, getX() + 2, getY() + 2, isMuffling ? aquaText : whiteText, true); //title
        }
    }

    //draws the "rainbow" gradient in the background
    private void drawGradient(GuiGraphics stack) {
        if (isMuffling) {
            stack.blit(getTextureRL(), getX(), getY() - 1, 0, 234, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            if (this.isHovered) {
                stack.blit(getTextureRL(), getX() + (int) (sliderValue * (width - 6)) + 1, getY() + 1, 32F, 224F, 5, 9, 256, 256); //Slider
            }
        }
    }

    public void isVisible(boolean b) {
        this.visible = b;
        this.getBtnToggleSound().visible = b;
        this.getBtnPlaySound().visible = b;
    }

    public void setY(int y) {
        super.setY(y);
        this.getBtnToggleSound().setY(y);
        this.getBtnPlaySound().setY(y);
    }

    private void setBtnToggleSound(ResourceLocation sound) {
        int x = CommonConfig.get().leftButtons().get() ? getX() - 26 : getX() + width + 4;
        btnToggleSound = Button.builder(Component.empty(), b -> {
            if (isMuffling) {
                if (screen.removeSoundMuffled(sound)) {
                    setFGColor(this, "white");
                    if (screen.getBtnCSLTitle().equals(Component.translatable("main_screen.btn.csl.muffled"))) {
                        this.visible = false;
                    }
                }
            } else {
                setSliderValue(CommonConfig.get().defaultMuteVolume().get());
                if (screen.addSoundMuffled(sound, sliderValue)) {
                    setFGColor(this, "aqua");
                }
            }
        }).bounds(x, getY(), 11, 11).build();
    }

    public Button getBtnToggleSound() {
        return btnToggleSound;
    }

    private void setBtnPlaySound(ResourceLocation sound) {
        btnPlaySound = new PlaySoundButton(btnToggleSound.getX() + 13, getY(), SoundEvent.createVariableRangeEvent(sound));
    }

    private PlaySoundButton getBtnPlaySound() {
        return btnPlaySound;
    }

    private void changeSliderValue(double mouseX) {
        setSliderValue((mouseX - (getX() + 4)) / (width - 8));
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
        if (this.visible) {
            this.btnToggleSound.mouseClicked(mouseX, mouseY, button);
            this.btnPlaySound.mouseClicked(mouseX, mouseY, button);

            if (isHovered && isMuffling) {
                changeSliderValue((float) mouseX);
                showSlider = true;
                setFocused(true);
            }
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
    public void updateWidgetNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, isMuffling ? Component.translatable("slider.btn.volume").toString() + (int) (sliderValue * 100) : this.sound.toString());
    }
}