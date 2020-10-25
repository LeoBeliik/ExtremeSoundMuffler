package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MuffledSlider extends Widget implements ISoundLists {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private final int colorWhite = 16777215;
    private final int colorViolet = 0xffff00;
    private final String mainTitle = "ESM - Main Screen";
    private double sliderValue;
    private boolean showVolume = false;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;

    public MuffledSlider(int x, int y, int width, int height, ResourceLocation sound, String screenTitle, Anchor anchor) {
        super(x, y, width, height, ITextComponent.getTextComponentOrEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.sliderValue = 0.9;
        setMuffleButton(screenTitle, sound, anchor);
        setPlaySoundButton(sound);
    }

    protected int getYImage(boolean isHovered) {
        return 0;
    }

    @Nonnull
    protected IFormattableTextComponent getNarrationMessage() {
        return new TranslationTextComponent("gui.narrate.slider", this.getMessage());
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(GUI);
        drawGradient(matrixStack);
        float v = this.getFGColor() == 0xffff00 ? 213F : 202F;
        blit(matrixStack, btnToggleSound.x, btnToggleSound.y, 43F, v, 11, 11, 256, 256); //muffle button
        blit(matrixStack, btnPlaySound.x, btnPlaySound.y, 32F, 202F, 11, 11, 256, 256); //play button

        if (showVolume) {
            drawCenteredString(matrixStack, font, "Volume: " + (int) (sliderValue * 100), this.x + (this.width / 2), this.y + 2, 0xffffff); //title
        } else {
            font.drawStringWithShadow(matrixStack, getMessage().getString(), this.x + 2, this.y + 2F, getFGColor()); //title
        }
    }

    private void drawGradient(MatrixStack matrixStack) {
        if (getFGColor() == colorViolet) {
            blit(matrixStack, this.x, this.y - 1, 0, 234, (int) (sliderValue * (width - 6)) + 5, height + 1, 256, 256); //draw bg
            blit(matrixStack, this.x + (int) (sliderValue * (width - 6)) + 1, this.y + 1, 32F, 224F, 5, 9, 256, 256); //Slider
        }
    }

    private void setMuffleButton(String screenTitle, ResourceLocation sound, Anchor anchor) {
        btnToggleSound = new Button(this.x + width + 3, this.y, 11, 11, StringTextComponent.EMPTY, b -> {
            if (getFGColor() == colorViolet) {
                if (screenTitle.equals(mainTitle)) {
                    muffledSounds.remove(sound);
                } else {
                    anchor.removeSound(sound);
                }
                super.setFGColor(colorWhite);
            } else {
                if (screenTitle.equals(mainTitle)) {
                    setSliderValue(0.9);
                    muffledSounds.put(sound, sliderValue);
                } else {
                    setSliderValue(0.9);
                    anchor.addSound(sound, sliderValue);
                }
                super.setFGColor(colorViolet);
            }
        });
    }

    private void setPlaySoundButton(ResourceLocation sound) {
        btnPlaySound = new PlaySoundButton(this.x + width + 15, this.y, new SoundEvent(sound));
    }

    public Button getBtnToggleSound() {
        return btnToggleSound;
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
    }

    private void setSliderValue(double value) {
        double d0 = this.sliderValue;
        this.sliderValue = MathHelper.clamp(value, 0.0D, 0.9D);
        if (d0 != this.sliderValue) {
            this.func_230972_a_();
        }
        this.func_230979_b_();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.changeSliderValue(mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX > this.x && mouseX < this.x + width && mouseY > this.y && mouseY < this.y + height && this.getFGColor() == colorViolet) {
            this.changeSliderValue(mouseX);
            showVolume = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseX > this.x && mouseX < this.x + width && mouseY > this.y && mouseY < this.y + height && this.getFGColor() == colorViolet) {
            showVolume = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @ParametersAreNonnullByDefault
    public void playDownSound(SoundHandler handler) {
    }

    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(Minecraft.getInstance().getSoundHandler());
    }

    private void func_230979_b_() {

    }

    private void func_230972_a_() {

    }
}
