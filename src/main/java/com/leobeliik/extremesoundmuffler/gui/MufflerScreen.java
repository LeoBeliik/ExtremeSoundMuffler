package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.anchors.AnchorEntity;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

//@OnlyIn(Dist.CLIENT)
public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private final int xSize = 256;
    private final int ySize = 202;
    private int minYButton, maxYButton, index;
    private static AnchorEntity anchor;


    MufflerScreen(ITextComponent title) {
        super(title);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        this.bindTexture();
        this.blit(matrix, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        drawCenteredString(matrix, font, this.title, getX() + 128, getY() + 8, whiteText); //Screen title
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        minYButton = getY() + 36;
        maxYButton = getY() + 164;

        //allows to hold a key to keep printing it. in this case i want it to easy erase text
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addSoundButtons();

        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Close Screen
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (anchor != null) {
            anchor.setChanged();
            anchor = null;
        }
        super.onClose();
    }

    //-----------------------------------My functions-----------------------------------//

    //Buttons init
    private void addSoundButtons() {
        int buttonH = minYButton;

        soundsList.clear();
        soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys());
        forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {

            float maxVolume = 1F;
            float volume = muffledSounds.get(sound) == null ? maxVolume : muffledSounds.get(sound);

            int x = Config.getLeftButtons() ? getX() + 36 : getX() + 11;

            MuffledSlider volumeSlider = new MuffledSlider(x, buttonH, 205, 14, volume, sound);

            if (!muffledSounds.isEmpty() && muffledSounds.containsKey(sound)) {
                volumeSlider.setFGColor(cyanText);
            }

            buttonH += volumeSlider.getHeight();
            addButton(volumeSlider);
            volumeSlider.visible = buttons.indexOf(volumeSlider) < index + 10;
            addWidget(volumeSlider.getBtnToggleSound());
            addWidget(volumeSlider.getBtnPlaySound());

        }
    }
    //end of buttons

    public static void open(AnchorEntity anchorEntity) {
        muffledSounds.putAll(anchorEntity.getCurrentMuffledSounds());
        anchor = anchorEntity;
        minecraft.setScreen(new MufflerScreen(new TranslationTextComponent("test")));
    }

    private void bindTexture() {
        minecraft.getTextureManager().bind(SoundMuffler.getGui());
    }

    int getX() {
        return (this.width - xSize) / 2;
    }

    int getY() {
        return (this.height - ySize) / 2;
    }
}
