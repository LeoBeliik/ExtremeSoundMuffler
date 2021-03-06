package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.eventHandlers.SoundEventHandler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
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
import java.util.Objects;

@SuppressWarnings("EmptyMethod")
public class MuffledSlider extends Widget implements ISoundLists, IColorsGui {

    private final String mainTitle = "ESM - Main Screen";
    private double sliderValue;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;
    private ResourceLocation sound;
    public static ResourceLocation tickSound;
    public static boolean showSlider = false;

    public MuffledSlider(int x, int y, int width, int height, double sliderValue, ResourceLocation sound, String screenTitle, Anchor anchor) {
        super(x, y, width, height, ITextComponent.getTextComponentOrEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.sliderValue = sliderValue;
        this.sound = sound;
        setBtnToggleSound(screenTitle, sound, anchor);
        setBtnPlaySound(sound);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(GUI);
        drawGradient(matrixStack);
        float v = this.getFGColor() == whiteText ? 213F : 202F;
        blit(matrixStack, btnToggleSound.x, btnToggleSound.y, 43F, v, 11, 11, 256, 256); //muffle button bg
        blit(matrixStack, btnPlaySound.x, btnPlaySound.y, 32F, 202F, 11, 11, 256, 256); //play button bg
        this.drawMessage(matrixStack, minecraft);
    }

    private void drawMessage(MatrixStack matrixStack, Minecraft minecraft) {
        FontRenderer font = minecraft.fontRenderer;
        int v = Math.max(this.width, font.getStringWidth(getMessage().getString()));
        if (showSlider && this.isFocused() && this.isHovered) {
            drawCenteredString(matrixStack, font, "Volume: " + (int) (sliderValue * 100), this.x + (this.width / 2), this.y + 2, whiteText); //title
        } else {
            String msgTruncated;
            if (this.isHovered) {
                msgTruncated = getMessage().getString();
                fill(matrixStack, this.x + this.width + 3, this.y, this.x + v + 3, this.y + font.FONT_HEIGHT + 2, darkBG);
            } else {
                msgTruncated = font.func_238417_a_(getMessage(), 205).getString();
            }
            font.drawStringWithShadow(matrixStack, msgTruncated, this.x + 2, this.y + 2, getFGColor()); //title
        }
    }

    private void drawGradient(MatrixStack matrixStack) {
        if (this.getFGColor() == yellowText) {
            blit(matrixStack, this.x, this.y - 1, 0, 234, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            if (this.isHovered) {
                blit(matrixStack, this.x + (int) (sliderValue * (width - 6)) + 1, this.y + 1, 32F, 224F, 5, 9, 256, 256); //Slider
            }
        }
    }

    private void setBtnToggleSound(String screenTitle, ResourceLocation sound, Anchor anchor) {
        int x = Config.getLeftButtons() ? this.x - 24 : this.x + width + 5;
        btnToggleSound = new Button(x, this.y, 11, 11, StringTextComponent.EMPTY, b -> {
            if (getFGColor() == yellowText) {
                if (screenTitle.equals(mainTitle)) {
                    muffledSounds.remove(sound);
                } else {
                    anchor.removeSound(sound);
                }
                SoundEventHandler.reloadSound(sound);
                super.setFGColor(whiteText);
            } else {
                if (screenTitle.equals(mainTitle)) {
                    setSliderValue(Config.getDefaultMuteVolume());
                    muffledSounds.put(sound, sliderValue);
                } else if (anchor.getAnchorPos() != null) {
                    setSliderValue(Config.getDefaultMuteVolume());
                    anchor.addSound(sound, sliderValue);
                } else {
                    return;
                }
                SoundEventHandler.reloadSound(sound);
                super.setFGColor(yellowText);
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
            this.setSliderValue(this.sliderValue + (double) (f / (float) (this.width - 8)));
        }
        return false;
    }

    private void changeSliderValue(double mouseX) {
        this.setSliderValue((mouseX - (double) (this.x + 4)) / (double) (this.width - 8));
        if (Config.getShowTip()) {
            Config.setShowTip(false);
        }
    }

    private void setSliderValue(double value) {
        double d0 = this.sliderValue;
        this.sliderValue = MathHelper.clamp(value, 0.0D, 0.9D);
        if (d0 != this.sliderValue) {
            this.func_230972_a_();
        }
        this.func_230979_b_();
        updateVolume();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.changeSliderValue(mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered && this.getFGColor() == yellowText) {
            this.changeSliderValue(mouseX);
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

    private void func_230979_b_() {}

    private void func_230972_a_() {}
}
