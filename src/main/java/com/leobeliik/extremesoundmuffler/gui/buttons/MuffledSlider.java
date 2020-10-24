package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
    private final String mainTitle = "ESM - Main Screen";
    private double sliderValue;
    private final int colorWhite = 16777215;
    private final int colorViolet = 24523966;
    private Button btnToggleSound;
    private PlaySoundButton btnPlaySound;


    public MuffledSlider(int x, int y, int width, int height, ResourceLocation sound, double defaultValue, String screenTitle, Anchor anchor) {
        super(x, y, width, height, ITextComponent.getTextComponentOrEmpty(sound.getPath() + ":" + sound.getNamespace()));
        this.sliderValue = defaultValue;
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
    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {

        //RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        /*if (this.visible) {
            if (isMouseOver(x, y)) {
                text = "Volume: " + (int) (sliderValue * 100);
            } else {
                text = message;
            }*/
    }

    @ParametersAreNonnullByDefault
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.fontRenderer;
        float v = this.getFGColor() == 24523966 ? 213F : 202F;
        minecraft.getTextureManager().bindTexture(GUI);/*
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();*/
        fillGradient(matrixStack, this.x, this.y, this.x + width, this.y + height, -12574688, -11530224);
        blit(matrixStack, this.x + (int) (sliderValue * (width - 6)) + 1, this.y + 1, 32F, 224F, 5, 9, 256, 256); //Slider
        blit(matrixStack, btnToggleSound.x, btnToggleSound.y, 43F, v, 11, 11, 256, 256); //muffle button
        blit(matrixStack, btnPlaySound.x, btnPlaySound.y, 32F, 202F, 11, 11, 256, 256); //play button
        int j = getFGColor();
        font.drawString(matrixStack, getMessage().getString(), this.x + 2, this.y + 2F, j);
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
                    muffledSounds.put(sound, 100D);
                } else {
                    anchor.addSound(sound, 100D);
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

    public void onClick(double mouseX, double mouseY) {
        this.changeSliderValue(mouseX);
    }

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
        this.sliderValue = MathHelper.clamp(value, 0.0D, 1.0D);
        if (d0 != this.sliderValue) {
            this.func_230972_a_();
        }

        this.func_230979_b_();
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.changeSliderValue(mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
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
