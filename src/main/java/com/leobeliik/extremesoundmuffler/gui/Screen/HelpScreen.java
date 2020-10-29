package com.leobeliik.extremesoundmuffler.gui.Screen;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

public class HelpScreen extends Screen {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/help_gui.png");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private final int xSize = 408;
    private final int ySize = 202;

    private HelpScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static void open() {
        minecraft.displayGuiScreen(new HelpScreen(ITextComponent.getTextComponentOrEmpty("")));
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        minecraft.getTextureManager().bindTexture(GUI);
        blit(matrixStack, getX() + 76, getY(), 0, 0, xSize, ySize, xSize, ySize);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}
