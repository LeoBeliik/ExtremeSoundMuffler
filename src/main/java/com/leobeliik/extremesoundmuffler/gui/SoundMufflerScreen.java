package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@OnlyIn(Dist.CLIENT)
public class SoundMufflerScreen extends Screen {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");

    private static final int xSize = 176;
    private static final int ySize = 135;
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static SortedSet<ResourceLocation> soundList = new TreeSet<>();
    private static Set<ResourceLocation> muffledList = new HashSet<>();
    private static int colorWhite = 16777215;
    private static int colorViolet = 24523966;
    private Button toggleMuffled;
    private Button toggleSearch;
    private TextFieldWidget searchBar;
    private static boolean isMuffled = true;
    private boolean isSearching;

    private SoundMufflerScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.isMouseOver(mouseX, mouseY);
        this.renderBackground();
        minecraft.getTextureManager().bindTexture(GUI);
        this.blit(getX(), getY(), 0, 32, xSize, ySize);
        blit(getX() + 143, getY() + 115, 0, 12f, 0f, 12, 12, 95, 95);
        toggleSearch();
        toggleMuffle();
        if (toggleSearch.isHovered()) {
            toggleSearch.drawCenteredString(minecraft.fontRenderer, "Search", toggleSearch.x + 5, toggleSearch.y + 15, colorWhite);
        }
        super.render(mouseX, mouseY, partialTicks);
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

        addButton(
                toggleMuffled = new Button(getX() + 157, getY() + 115, 12, 12, "", b -> isMuffled = !isMuffled)
        ).setAlpha(0);

        addButton(
                toggleSearch = new Button(getX() + 143, getY() + 115, 12, 12, "", b -> {
                    isSearching = !isSearching;
                    updateText();
                })
        ).setAlpha(0);

        addButton(
                searchBar = new TextFieldWidget(minecraft.fontRenderer, getX() + 62, getY() + 114, 79, 14, "")
        );

        addSoundButtons();
    }

    private void addSoundButtons() {
        int buttonW = ((width - xSize) / 2) + 10;
        int buttonH = getY() + 13;
        if (!muffledList.isEmpty()) {
            soundList.addAll(muffledList);
        }

        if (!soundList.isEmpty()) {
            for (ResourceLocation sound : soundList) {
                String text = font.trimStringToWidth(sound.getPath(), xSize - 22);
                Button btnSound = new Button(buttonW, buttonH, xSize - 20, font.FONT_HEIGHT + 2, text, b -> {
                    if (b.getFGColor() == colorViolet) {
                        muffledList.remove(sound);
                        b.setFGColor(colorWhite);
                    } else {
                        muffledList.add(sound);
                        b.setFGColor(colorViolet);
                    }
                });
                if (!muffledList.isEmpty() && muffledList.contains(sound)) {
                    btnSound.setFGColor(colorViolet);
                }
                addButton(btnSound).setAlpha(0);
                buttonH += btnSound.getHeight() + 1;
                btnSound.visible = btnSound.y <= getY() + 100;
            }
        }
    }

    private void toggleSearch() {

        if (isSearching) {
            blit(getX() + 60, getY() + 111, 0, 96f, 0f, 79, 20, 256, 256);
        }

        searchBar.setEnabled(isSearching);
        searchBar.setVisible(isSearching);
        searchBar.setFocused2(isSearching);
    }

    private void toggleMuffle() {
        if (isMuffled) {
            blit(getX() + 157, getY() + 115, 0, 0f, 0f, 12, 12, 95, 95);
            if (toggleMuffled.isHovered()) {
                toggleMuffled.drawCenteredString(
                        minecraft.fontRenderer, "Stop Muffling", toggleMuffled.x + 5, toggleMuffled.y + 15, colorWhite
                );
            }
        } else {
            blit(getX() + 157, getY() + 115, 0, 23.75f, 0f, 12, 12, 95, 95);
            if (toggleMuffled.isHovered()) {
                toggleMuffled.drawCenteredString(
                        minecraft.fontRenderer, "Start Muffling", toggleMuffled.x + 5, toggleMuffled.y + 15, colorWhite
                );
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {

        if (searchBar.getText().length() > 0) {
            return false;
        }
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
            if (b.equals(toggleSearch) || b.equals(toggleMuffled) || b.equals(searchBar)) {
                continue;
            }
            b.y = direction > 0 ? b.y + (b.getHeight() + 1) : b.y - (b.getHeight() + 1);
            b.visible = b.y >= getY() + 10 && b.y <= getY() + 100;
        }
        return true;
    }

    private void updateText() {
        if (!isSearching) searchBar.setText("");
        int buttonH = getY() + 13;
        for (Widget b : buttons) {
            if (b.equals(toggleSearch) || b.equals(toggleMuffled) || b.equals(searchBar)) {
                continue;
            }
            if (b.getMessage().contains(searchBar.getText())) {
                b.active = true;
                b.y = buttonH;
                b.visible = b.y >= getY() + 10 && b.y <= getY() + 100;
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
    }

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
        return isMuffled;
    }

    public static void addSound(ResourceLocation sound) {
        soundList.add(sound);
    }

    public static Set<ResourceLocation> getMuffledList() {
        return muffledList;
    }

    public static void setMuffledList(Set<ResourceLocation> list) {
        muffledList.addAll(list);
    }
}