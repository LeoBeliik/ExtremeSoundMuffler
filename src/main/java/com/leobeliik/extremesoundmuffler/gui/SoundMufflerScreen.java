package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SoundMufflerScreen extends Screen {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final int xSize = 256;
    private static final int ySize = 224;
    private static SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    private static Set<ResourceLocation> muffledList = new HashSet<>();
    private static int colorWhite = 16777215;
    private static int colorViolet = 24523966;
    private Button btnToggleMuffled;
    private Button btnDelete;
    //private TextFieldWidget searchBar;
    private static boolean isMuffling = true;
    private boolean isSearching;
    private static TextComponent emptyText = new TranslationTextComponent("");

    private SoundMufflerScreen() {
        super(new StringTextComponent(""));
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.isMouseOver(mouseX, mouseY);
        this.renderBackground(matrixStack);
        minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, getX(), getY(), 0, 32, xSize, ySize); //Main screen bounds
        renderButtonsTextures(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open() {
        minecraft.displayGuiScreen(new SoundMufflerScreen());
    }

    @Override
    protected void init() {
        super.init();

        addButton(btnToggleMuffled = new Button(getX() + 229, getY() + 165, 12, 12, emptyText, b -> isMuffling = !isMuffling))
                .setAlpha(0);

        addButton(
                btnDelete = new Button(getX() + 11, getY() + 165, 16, 16, emptyText, b -> {
                    muffledList.clear();
                    addSoundButtons();
                    //TODO Add and detect anchor
                })
        ).setAlpha(0);

        /*addButton(
                searchBar = new TextFieldWidget(minecraft.fontRenderer, getX() + 62, getY() + 114, 79, 14, emptyText)
        );*/

        addSoundButtons();
    }

    private void addSoundButtons() {
        int buttonH = getY() + 46;
        if (!muffledList.isEmpty()) {
            recentSoundsList.addAll(muffledList);
        }

        if (recentSoundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : recentSoundsList) {

            ITextComponent soundName = new TranslationTextComponent(sound.getPath() + ":" + sound.getNamespace());
            Button btnToggleSound = new Button(getX() + 221, buttonH, 10, 10, emptyText, b -> {
                if (b.getFGColor() == colorViolet) {
                    muffledList.remove(sound);
                    b.setFGColor(colorWhite);
                } else {
                    muffledList.add(sound);
                    b.setFGColor(colorViolet);
                }
            });

            if (!muffledList.isEmpty() && muffledList.contains(sound)) {
                btnToggleSound.setFGColor(colorViolet);
            }

            addButton(btnToggleSound).setAlpha(0);
            buttonH += btnToggleSound.getHeight() + 1;
            btnToggleSound.visible = btnToggleSound.y <= getY() + 100; //TODO change to the new max height and to everything
            //TODO probably is best to make my own button class
        }
    }


    private void renderButtonsTextures(MatrixStack matrixStack) {
        //Mute sound buttons and play sound buttons; Sound names
        for (int i = 0; i < recentSoundsList.size(); i++) {
            Widget btn = buttons.get(i + 2);
            ResourceLocation rs = (ResourceLocation) recentSoundsList.toArray()[i];
            String soundName = rs.getPath() + ":" + rs.getNamespace();
            float a;
            //draws the name of the sound; sound name : mod name
            drawString(matrixStack, minecraft.fontRenderer, soundName, getX() + 14, btn.y + 2, btn.getFGColor());

            if (btn.getFGColor() == colorViolet) { //if muffled
                a = 10F;
            } else {
                a = 0;
            }
            minecraft.getTextureManager().bindTexture(GUI);
            blit(matrixStack, getX() + 221, btn.y + 1, 0, a, 0F, 10, 10, 80, 80); //muffle button
            blit(matrixStack, getX() + 233, btn.y + 1, 20F, 0F, 10, 10, 80, 80); //play button
        }

        //Delete button
        blit(matrixStack, getX() + 11, getY() + 165, 0, 48F, 0F, 16, 16, 128, 128);

        //toggle muffled button
        float vPos; //start x point of the texture
        String message;

        if (isMuffling) {
            vPos = 0;
            message = "Stop Muffling";
        } else {
            vPos = 16F;
            message = "Start Muffling";
        }
        blit(matrixStack, getX() + 229, getY() + 165, 0, vPos, 0F, 16, 16, 128, 128);

        if (btnToggleMuffled.isHovered()) {
            btnToggleMuffled.drawCenteredString(matrixStack, minecraft.fontRenderer, message, btnToggleMuffled.x + 15, btnToggleMuffled.y + 20, colorWhite);
        }
    }

    /*private void renderSearchField(MatrixStack matrixStack) {

        if (isSearching) {
            blit(matrixStack, getX() + 60, getY() + 111, 0, 96f, 0f, 79, 20, 256, 256);
        }

        searchBar.setEnabled(isSearching);
        searchBar.setVisible(isSearching);
        searchBar.setFocused2(isSearching);
    }*/

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {

        /*if (searchBar.getText().length() > 0) {
            return false;
        }*/
        if (buttons.size() <= 5) {
            return false; //enought empty screen, no need for scroll
        }
        if (buttons.get(3).y >= getY() + 12 && direction > 0f) {
            return false; //first button is on the top
        }
        if (buttons.get(buttons.size() - 1).y <= getY() + 99 && direction < 0f) {
            return false; //last button is on the bottom
        }

        for (Widget b : buttons) {
            if (b.equals(btnDelete) || b.equals(btnToggleMuffled)) {
                continue;
            }

            b.y = direction > 0 ? b.y + (b.getHeight() + 1) : b.y - (b.getHeight() + 1);
            b.visible = b.y >= getY() + 10 && b.y <= getY() + 100;
        }
        return true;
    }

    /*private void updateText() {
        if (!isSearching) searchBar.setText("");
        int buttonH = getY() + 13;
        for (Widget b : buttons) {
            if (b.equals(btnDelete) || b.equals(toggleMuffled) || b.equals(searchBar)) {
                continue;
            }
            if (b.getMessage().getString().contains(searchBar.getText())) {
                b.active = true;
                b.y = buttonH;
                b.visible = b.y >= getY() + 10 && b.y <= getY() + 100;
                buttonH += b.getHeight() + 1;
            } else {
                b.active = true;
                b.visible = false;
            }
        }
    }*/

   /* @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBar.isFocused()) {
            searchBar.charTyped(codePoint, modifiers);
            updateText();
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259 && searchBar.isFocused()) { //pressed "backspace" inside search bar
            searchBar.keyPressed(keyCode, scanCode, modifiers);
            updateText();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        if (key1 == 69 && !searchBar.isFocused()) { //pressed "E" outside the search bar
            onClose();
            return true;
        }
        return super.keyPressed(key1, key2, key3);
    }*/


    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    static ResourceLocation getGUI() {
        return GUI;
    }

    public static boolean isMuffled() {
        return isMuffling;
    }

    public static void addSound(ResourceLocation sound) {
        recentSoundsList.add(sound);
    }

    public static Set<ResourceLocation> getMuffledList() {
        return muffledList;
    }

    public static void setMuffledList(Set<ResourceLocation> list) {
        muffledList.addAll(list);
    }
}