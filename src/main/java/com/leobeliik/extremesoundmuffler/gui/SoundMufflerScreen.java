package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SoundMufflerScreen extends Screen {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private static boolean isMuffling = true;
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    private static Set<ResourceLocation> muffledList = new HashSet<>();
    private final int xSize = 256;
    private final int ySize = 224;
    private final int colorWhite = 16777215;
    private final int colorViolet = 24523966;
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final String mainTitle = "ESM - Main Screen";
    private int minYButton;
    private int maxYButton;
    private Button btnToggleMuffled;
    private Button btnDelete;
    private TextFieldWidget searchBar;
    private Anchor anchor;
    private String screenTitle;

    private SoundMufflerScreen() {
        super(StringTextComponent.EMPTY);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        //this.isMouseOver(mouseX, mouseY);
        this.renderBackground(matrixStack);
        minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, getX(), getY(), 0, 32, xSize, ySize); //Main screen bounds
        renderButtonsTextures(matrixStack);
        drawCenteredString(matrixStack, font, screenTitle, getX() + 128, getY() + 8, colorWhite);
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
        screenTitle = mainTitle;
        minYButton = getY() + 37;
        maxYButton = getY() + 148;

        addListener(btnToggleMuffled = new Button(getX() + 229, getY() + 165, 12, 12, emptyText, b -> isMuffling = !isMuffling));

        addListener(btnDelete = new Button(getX() + 11, getY() + 165, 16, 16, emptyText, b -> {
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.clear();
                    } else {
                        anchor.getMuffledSounds().clear();
                    }
                    buttons.forEach(btn -> btn.setFGColor(colorWhite));
                    //TODO Add and detect anchor; update screen
                })
        );
        addSoundButtons();
        addAnchors();
        addButton(searchBar = new TextFieldWidget(font, getX() + 75, getY() + 168, 105, 10, emptyText));
        searchBar.setEnableBackgroundDrawing(false);
    }

    private void addSoundButtons() {
        int buttonH = getY() + 46;

        if (screenTitle.equals(mainTitle) && !muffledList.isEmpty()) {
            recentSoundsList.addAll(muffledList);
        } else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
            recentSoundsList.addAll(anchor.getMuffledSounds());
        }

        if (recentSoundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : recentSoundsList) {

            PlaySoundButton btnPlaySound = new PlaySoundButton(getX() + 233, buttonH, new SoundEvent(sound));
            Button btnToggleSound = new Button(getX() + 221, buttonH, 10, 10, emptyText, b -> {
                if (b.getFGColor() == colorViolet) {
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.remove(sound);
                    } else {
                        anchor.removeSound(sound);
                    }
                    b.setFGColor(colorWhite);
                    btnPlaySound.active = true;
                } else {
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.add(sound);
                    } else {
                        anchor.addSound(sound);
                        System.out.println(anchor.getId());
                        System.out.println(Arrays.toString(anchor.getMuffledSounds().toArray()));
                    }
                    b.setFGColor(colorViolet);
                    btnPlaySound.active = false;
                }
            });
            if (screenTitle.equals(mainTitle) && !muffledList.isEmpty() && muffledList.contains(sound)) {
                btnToggleSound.setFGColor(colorViolet);
            }

            if (anchor != null && screenTitle.equals(anchor.getName()) && !anchor.getMuffledSounds().isEmpty() && anchor.getMuffledSounds().contains(sound)) {
                btnToggleSound.setFGColor(colorViolet);
                System.out.println("this is violet");
            }

            addListener(btnPlaySound);
            addButton(btnToggleSound).setAlpha(0);
            buttonH += btnToggleSound.getHeight() + 1;
            btnToggleSound.visible = btnToggleSound.y <= maxYButton;
        }
    }

    private void addAnchors() { //TODO redo this, im creating a new anchor every time i press the button, what was i thinking?
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            int finalI = i;
            Button btnAnchor = new Button(buttonW, getY() + 24, 16, 16, new TranslationTextComponent(String.valueOf(i)), b -> {
                if (minecraft.player == null) return;
                anchor = new Anchor(finalI, minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
                if (screenTitle.equals(anchor.getName())) {
                    screenTitle = mainTitle;
                } else {
                    screenTitle = anchor.getName();
                    buttons.forEach(btn -> btn.setFGColor(colorWhite));
                }
                addSoundButtons();
            });
            addButton(btnAnchor).setAlpha(0);
            buttonW += 20;
        }
    }

    private void renderButtonsTextures(MatrixStack matrixStack) {
        //Mute sound buttons and play sound buttons; Sound names
        float v; //start x point of the texture
        for (int i = 0; i < recentSoundsList.size(); i++) {
            Widget btn = buttons.get(i);

            if (!btn.visible) {
                continue;
            }

            ResourceLocation rs = (ResourceLocation) recentSoundsList.toArray()[i];
            String soundName = rs.getPath() + ":" + rs.getNamespace();
            //draws the name of the sound; sound name : mod name
            drawString(matrixStack, font, soundName, getX() + 14, btn.y + 2, btn.getFGColor());
            minecraft.getTextureManager().bindTexture(GUI);

            if (btn.getFGColor() == colorViolet) { //if muffled
                v = 10F;
            } else {
                v = 0F;
            }
            blit(matrixStack, getX() + 221, btn.y + 1, v, 0F, 10, 10, 80, 80); //muffle button
            blit(matrixStack, getX() + 233, btn.y + 1, v + 20F, 0F, 10, 10, 80, 80); //play button

        }

        //Delete button
        blit(matrixStack, getX() + 11, getY() + 165, 0, 64F, 0F, 16, 16, 128, 128);

        //toggle muffled button
        String message;

        if (isMuffling) {
            v = 0;
            message = "Stop Muffling";
        } else {
            v = 16F;
            message = "Start Muffling";
        }
        blit(matrixStack, getX() + 229, getY() + 165, 0, v, 0F, 16, 16, 128, 128);

        if (btnToggleMuffled.isHovered()) {
            btnToggleMuffled.drawCenteredString(matrixStack, font, message, btnToggleMuffled.x + 15, btnToggleMuffled.y + 20, colorWhite);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        int totalMuffButtons = recentSoundsList.size();
        if (searchBar.getText().length() > 0) {
            return false;
        }
        if (totalMuffButtons <= 10) {
            return false; //enought empty screen, no need for scroll
        }
        if (buttons.get(0).y >= minYButton && direction > 0f) {
            return false; //first button is on the top
        }
        if (buttons.get(totalMuffButtons - 1).y <= maxYButton && direction < 0f) {
            return false; //last button is on the bottom
        }

        for (int i = 0; i < totalMuffButtons; i++) {
            Widget b = buttons.get(i);
            b.y = direction > 0 ? b.y + (b.getHeight() + 1) : b.y - (b.getHeight() + 1);
            b.visible = b.y >= minYButton && b.y <= maxYButton;
        }
        return true;
    }

    private void updateText() {
        int buttonH = getY() + 46;
        Object[] soundName = recentSoundsList.toArray();
        for (int i = 0; i < recentSoundsList.size(); i++) {
            Widget b = buttons.get(i);
            if (soundName[i].toString().contains(searchBar.getText())) {
                b.active = true;
                b.y = buttonH;
                b.visible = b.y >= minYButton && b.y <= maxYButton;
                buttonH += b.getHeight() + 1;
            } else {
                b.active = true;
                b.visible = false;
            }
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBar.isFocused()) {
            searchBar.charTyped(codePoint, modifiers);
            updateText();
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        //pressed "backspace" inside search bar
        if (keyCode == 259 && searchBar.isFocused()) {
            updateText();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        //SearchBar looses focus when pressed "Enter" or "Intro"
        if (key1 == 257 || key1 == 335) {
            searchBar.setFocused2(false);
            return true;
        }
        //Close screen when press "E" or the mod hotkey outside the search bar
        if (!searchBar.isFocused() && (key1 == 69 || key1 == SoundMuffler.getHotkey())) {
            onClose();
            return true;
        }
        return super.keyPressed(key1, key2, key3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        searchBar.setFocused2(searchBar.mouseClicked(mouseX, mouseY, button));
        return true;
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    public static ResourceLocation getGUI() {
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