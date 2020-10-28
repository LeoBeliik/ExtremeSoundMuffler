package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class MainScreen extends Screen implements ISoundLists {

    public static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final List<Anchor> anchors = new ArrayList<>();
    private List<Widget> filteredButtons = new ArrayList<>();
    private static boolean isMuffling = true;
    private static String screenTitle = "";
    private static ITextComponent toggleSoundsListMessage;
    private final int xSize = 256;
    private final int ySize = 202;
    private final int colorWhite = 0xffffff;
    private final boolean isAnchorsDisabled = Config.getDisableAchors();
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final String mainTitle = "ESM - Main Screen";

    private int minYButton, maxYButton, index;
    private Button btnToggleMuffled, btnDelete, btnToggleSoundsList, btnSetAnchor, btnEditAnchor, nextSounds, prevSounds;
    private Button btnAccept, btnCancel, btnAnchor;
    private TextFieldWidget searchBar, editAnchorTitleBar, editAnchorRadious;
    private MuffledSlider volumeSlider;
    private Anchor anchor;

    private MainScreen() {
        super(new StringTextComponent(""));
    }

    private static void open(String title, ITextComponent message) {
        toggleSoundsListMessage = message;
        screenTitle = title;
        minecraft.displayGuiScreen(new MainScreen());
    }

    public static void open() {
        open("ESM - Main Screen", ITextComponent.getTextComponentOrEmpty("Recent"));
    }

    public static boolean isMuffled() {
        return isMuffling;
    }

    public static List<Anchor> getAnchors() {
        return anchors;
    }

    public static void setAnchor(Anchor anchor) {
        anchors.add(anchor);
    }

    @Nullable
    private static Anchor getAnchorByName(String name) {
        return anchors.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrix, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        drawCenteredString(matrix, font, screenTitle, getX() + 128, getY() + 8, colorWhite); //Screen title
        renderButtonsTextures(matrix, mouseX, mouseY, partialTicks);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        minYButton = getY() + 46;
        maxYButton = getY() + 164;

        addListener(btnToggleSoundsList = new Button(getX() + 23, getY() + 181, 43, 13, toggleSoundsListMessage, b -> {
            if (btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("Recent"))) {
                toggleSoundsListMessage = ITextComponent.getTextComponentOrEmpty("All");
            } else if (!muffledSounds.isEmpty() && btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("All"))){
                toggleSoundsListMessage = ITextComponent.getTextComponentOrEmpty("Muffled");
            } else {
                toggleSoundsListMessage = ITextComponent.getTextComponentOrEmpty("Recent");
            }
            btnToggleSoundsList.setMessage(toggleSoundsListMessage);
            buttons.clear();
            open(screenTitle, toggleSoundsListMessage);
        }));

        addSoundButtons();

        addAnchors();

        addButton(btnToggleMuffled = new Button(getX() + 229, getY() + 179, 17, 17, emptyText, b -> isMuffling = !isMuffling)).setAlpha(0);

        addButton(btnDelete = new Button(getX() + 205, getY() + 179, 17, 17, emptyText, b -> {
                    anchor = getAnchorByName(screenTitle);
                    if (screenTitle.equals(mainTitle)) {
                        muffledSounds.clear();
                        open(mainTitle, btnToggleSoundsList.getMessage());
                    } else {
                        if (anchor == null) {
                            return;
                        }
                        anchor.deleteAnchor();
                        buttons.clear();
                        open(anchor.getName(), btnToggleSoundsList.getMessage());
                    }
                })
        ).setAlpha(0);

        addButton(btnSetAnchor = new Button(getX() + 260, getY() + 62, 11, 11, emptyText, b ->
                Objects.requireNonNull(getAnchorByName(screenTitle)).setAnchor())).setAlpha(0);

        addButton(btnEditAnchor = new Button(getX() + 274, getY() + 62, 11, 11, emptyText, b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).setAlpha(0);

        addEditAnchorButtons();

        if (screenTitle.equals(mainTitle)) {
            btnSetAnchor.visible = false;
            btnEditAnchor.visible = false;
        }

        addButton(searchBar = new TextFieldWidget(font, getX() + 74, getY() + 183, 119, 13, emptyText));
        searchBar.setEnableBackgroundDrawing(false);

        addListener(prevSounds = new Button(getX() + 10, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getText().length() > 0 ? filteredButtons : buttons, -1)));

        addListener(nextSounds = new Button(getX() + 233, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getText().length() > 0 ? filteredButtons : buttons, 1)));

        updateText();
    }

    private void addSoundButtons() {
        int buttonH = minYButton;
        anchor = getAnchorByName(screenTitle);

        if (!screenTitle.equals(mainTitle) && anchor == null) {
            return;
        }

        if (btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("Recent"))) {
            soundsList.clear();
            if (screenTitle.equals(mainTitle) && !muffledSounds.isEmpty()) {
                soundsList.addAll(muffledSounds.keySet());
            } else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds().keySet());
            }
            soundsList.addAll(recentSoundsList);
        } else if (btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("All"))){
            soundsList.clear();
            soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys());
        } else {
            soundsList.clear();
            soundsList.addAll(muffledSounds.keySet());
        }

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {

            double volume;

            if (screenTitle.equals(mainTitle)) {
                volume = muffledSounds.get(sound) == null ? 1D : muffledSounds.get(sound);
            } else if (anchor != null) {
                volume = anchor.getMuffledSounds().get(sound) == null ? 1D : muffledSounds.get(sound);
            } else {
                volume = 1D;
            }

            volumeSlider = new MuffledSlider(getX() + 11, buttonH, 205, 11, volume, sound, screenTitle, anchor);

            boolean muffledAnchor = anchor != null && screenTitle.equals(anchor.getName()) && !anchor.getMuffledSounds().isEmpty() && anchor.getMuffledSounds().containsKey(sound);
            boolean muffledScreen = screenTitle.equals(mainTitle) && !muffledSounds.isEmpty() && muffledSounds.containsKey(sound);

            if (muffledAnchor || muffledScreen) {
                volumeSlider.setFGColor(0xffff00);
            }

            buttonH += volumeSlider.getHeightRealms() + 2;
            addButton(volumeSlider);
            volumeSlider.visible = buttons.indexOf(volumeSlider) < index + 10;
            addListener(volumeSlider.getBtnToggleSound());
            addListener(volumeSlider.getBtnPlaySound());

        }
    }

    private void addAnchors() {
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            if (isAnchorsDisabled) {
                String[] disabledMsg = {"-", "D", "i", "s", "a", "b", "l", "e", "d", "-"};
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, ITextComponent.getTextComponentOrEmpty(String.valueOf(i)), b -> {
                });
                btnAnchor.setMessage(ITextComponent.getTextComponentOrEmpty(disabledMsg[i]));
                btnAnchor.active = false;
            } else {
                int finalI = i;
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, ITextComponent.getTextComponentOrEmpty(String.valueOf(i)), b -> {
                    anchor = anchors.get(finalI);
                    if (anchor == null) return;
                    if (screenTitle.equals(anchor.getName())) {
                        screenTitle = mainTitle;
                    } else {
                        screenTitle = anchor.getName();
                    }
                    buttons.clear();
                    open(screenTitle, btnToggleSoundsList.getMessage());
                });
                int colorGreen = 3010605;
                if (!anchors.isEmpty()) {
                    btnAnchor.setFGColor(anchors.get(Integer.parseInt(btnAnchor.getMessage().getString())).getAnchorPos() != null ? colorGreen : colorWhite);
                }
            }
            addButton(btnAnchor).setAlpha(0);
            buttonW += 20;
        }
    }

    private void addEditAnchorButtons() {

        addButton(editAnchorTitleBar = new TextFieldWidget(font, getX() + 302, btnEditAnchor.y + 20, 84, 11, emptyText)).visible = false;

        addButton(editAnchorRadious = new TextFieldWidget(font, getX() + 302, editAnchorTitleBar.y + 15, 30, 11, emptyText)).visible = false;

        addButton(btnAccept = new Button(getX() + 259, editAnchorRadious.y + 15, 40, 20, ITextComponent.getTextComponentOrEmpty("Accept"), b -> {
            anchor = getAnchorByName(screenTitle);
            if (!editAnchorTitleBar.getText().isEmpty() && !editAnchorRadious.getText().isEmpty() && anchor != null) {
                int radious = Integer.parseInt(editAnchorRadious.getText());

                if (radious > 32) {
                    radious = 32;
                }

                if (radious < 1) {
                    radious = 1;
                }

                anchor.editAnchor(editAnchorTitleBar.getText(), radious);
                anchor.setName(editAnchorTitleBar.getText());
                screenTitle = editAnchorTitleBar.getText();
                editTitle(anchor);
            }
        })).visible = false;

        addButton(btnCancel = new Button(getX() + 300, editAnchorRadious.y + 15, 40, 20, ITextComponent.getTextComponentOrEmpty("Cancel"), b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).visible = false;

    }

    private void renderButtonsTextures(MatrixStack matrix, double mouseX, double mouseY, float partialTicks) {
        int x; //start x point of the button
        int y; //start y point of the button
        int i = 0;
        float v; //start x point of the texture
        String message; //Button message
        int stringW; //text width
        int darkBG = -1325400064; //background color for Screen::fill()

        //Mute sound buttons and play sound buttons; Sound names
        if (buttons.size() < soundsList.size()) {
            return;
        }

        //Delete button
        x = btnDelete.x + 8;
        y = btnDelete.y;
        message = screenTitle.equals(mainTitle) ? "Delete Muffled List" : "Delete Anchor";
        stringW = font.getStringWidth(message) / 2;
        if (btnDelete.isHovered()) {
            fill(matrix, x - stringW - 2, y + 20, x + stringW + 2, y + 31, darkBG);
            drawCenteredString(matrix, font, message, x, y + 22, colorWhite);
        }

        //toggle muffled button
        x = btnToggleMuffled.x + 8;
        y = btnToggleMuffled.y;
        minecraft.getTextureManager().bindTexture(GUI);

        if (isMuffling) {
            blit(matrix, x - 8, y, 54F, 202F, 17, 17, xSize, xSize); //muffle button
        }

        message = isMuffling ? "Stop Muffling" : "Start Muffling";
        stringW = font.getStringWidth(message) / 2;
        if (btnToggleMuffled.isHovered()) {
            fill(matrix, x - stringW - 2, y + 20, x + stringW + 2, y + 31, darkBG);
            drawCenteredString(matrix, font, message, x, y + 22, colorWhite);
        }

        //Anchor coordinates and set coord button
        Anchor anchor = getAnchorByName(screenTitle);
        String dimensionName = "";
        String radious;
        x = btnSetAnchor.x;
        y = btnSetAnchor.y;

        if (anchor != null) {
            stringW = font.getStringWidth("Dimension: ");
            radious = anchor.getRadius() == 0 ? "" : String.valueOf(anchor.getRadius());
            if (anchor.getDimension() != null) {
                stringW += font.getStringWidth(anchor.getDimension().getPath());
                dimensionName = anchor.getDimension().getPath();
            }
            fill(matrix, x - 5, y - 56, x + stringW + 6, y + 16, darkBG);
            drawString(matrix, font, "X: " + anchor.getX(), x + 1, y - 50, colorWhite);
            drawString(matrix, font, "Y: " + anchor.getY(), x + 1, y - 40, colorWhite);
            drawString(matrix, font, "Z: " + anchor.getZ(), x + 1, y - 30, colorWhite);
            drawString(matrix, font, "Radious: " + radious, x + 1, y - 20, colorWhite);
            drawString(matrix, font, "Dimension: " + dimensionName, x + 1, y - 10, colorWhite);
            minecraft.getTextureManager().bindTexture(GUI);
            blit(matrix, x, y, 0, 69.45F, 11, 11, 88, 88); //set coordinates button

            if (anchor.getAnchorPos() != null) {
                btnEditAnchor.active = true;
                blit(matrix, btnEditAnchor.x, btnEditAnchor.y, 32F, 213F, 11, 11, xSize, xSize); //change title button
            } else {
                btnEditAnchor.active = false;
            }

            for (Widget button : buttons) {
                if (!(button instanceof MuffledSlider)) {
                    if (button.getMessage().getString().equals(String.valueOf(anchor.getId()))) {
                        blit(matrix, button.x - 5, button.y - 2, 71F, 202F, 27, 22, xSize, xSize); //fancy selected Anchor indicator
                        break;
                    }
                }
            }
        }

        message = "Set Anchor";
        stringW = font.getStringWidth(message) + 2;

        //Set Anchor tooltip
        if (btnSetAnchor.isHovered() && !editAnchorTitleBar.visible) {
            fill(matrix, x - 5, y + 16, x + stringW, y + 29, darkBG);
            font.drawString(matrix, message, x, y + 18, colorWhite);
        }

        message = "Edit Anchor";
        stringW = font.getStringWidth(message) + 2;

        if (btnEditAnchor.visible && !editAnchorTitleBar.visible && btnEditAnchor.isHovered()) {
            fill(matrix, x - 5, y + 16, x + stringW + 2, y + 29, darkBG);
            font.drawString(matrix, message, x, y + 18, colorWhite);
        }

        //draw anchor buttons tooltip
        for (int j = 0; j <= 9; j++) {
            Widget btn = buttons.get(soundsList.size() + j);
            x = btn.x + 8;
            y = btn.y;
            message = isAnchorsDisabled ? "Anchors are disabled" : anchors.get(j).getName();
            stringW = font.getStringWidth(message) / 2;

            if (btn.isHovered()) {
                fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
                drawCenteredString(matrix, font, message, x, y - 11, colorWhite);
            }
        }

        //Toggle List button draw message
        x = btnToggleSoundsList.x;
        y = btnToggleSoundsList.y;
        if (btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("Recent"))) {
            message = "All";
        } else if (!muffledSounds.isEmpty() && btnToggleSoundsList.getMessage().equals(ITextComponent.getTextComponentOrEmpty("All"))){
            message = "Muffled";
        } else {
            message = "Recent";
        }
        int centerText = x + (btnToggleSoundsList.getWidth() / 2) - (font.getStringWidth(message) / 2);
        font.drawString(matrix, message, centerText, y + 3, 0);
        String text = "Show " + message + " sounds";
        int textW = font.getStringWidth(text);
        int textX = x + (btnToggleSoundsList.getWidth() / 2) - (textW / 2) + 6;

        if (mouseX > x && mouseX < x + 43 && mouseY > y && mouseY < y + 13) {
            fill(matrix, textX - 2, y + 20, textX + textW + 2, y + 22 + font.FONT_HEIGHT, darkBG);
            font.drawString(matrix, text, textX, y + 22, colorWhite);
        }

        //Show Radious and Title text when editing Anchor and bg
        x = btnSetAnchor.x;
        y = editAnchorTitleBar.y;
        if (editAnchorRadious.visible) {
            fill(matrix, x - 4, y - 4, editAnchorTitleBar.x + editAnchorTitleBar.getWidth() + 3, btnAccept.y + 23, darkBG);
            font.drawString(matrix, "Title: ", x - 2, y + 1, colorWhite);
            font.drawString(matrix, "Radious: ", x - 2, editAnchorRadious.y + 1, colorWhite);
        }

        //Draw Searchbar prompt text
        x = searchBar.x;
        y = searchBar.y;
        ITextComponent searchHint = (new TranslationTextComponent("gui.recipebook.search_hint")).mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.GRAY); //Stolen from Vanilla ;)
        if (!this.searchBar.isFocused() && this.searchBar.getText().isEmpty()) {
            drawString(matrix, font, searchHint, x + 1, y, -1);
        }

        //next sounds button tooltip
        x = nextSounds.x;
        y = nextSounds.y;
        message = "Next Sounds";
        stringW = font.getStringWidth(message) / 2;

        if (mouseX > x && mouseX < x + nextSounds.getWidth() && mouseY > y && mouseY < y + nextSounds.getHeightRealms()) {
            fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
            drawCenteredString(matrix, font, message, x, y - 11, colorWhite);
        }

        //previuos sounds button tooltip
        x = prevSounds.x;
        y = prevSounds.y;
        message = "Previuos Sounds";
        stringW = font.getStringWidth(message) / 2;

        if (mouseX > x && mouseX < x + prevSounds.getWidth() && mouseY > y && mouseY < y + prevSounds.getHeightRealms()) {
            fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
            drawCenteredString(matrix, font, message, x, y - 11, colorWhite);
        }
    }

    private void editTitle(Anchor anchor) {
        editAnchorTitleBar.setText(anchor.getName());
        editAnchorTitleBar.visible = !editAnchorTitleBar.visible;

        editAnchorRadious.setText(String.valueOf(anchor.getRadius()));
        editAnchorRadious.visible = !editAnchorRadious.visible;

        btnAccept.visible = !btnAccept.visible;
        btnCancel.visible = !btnCancel.visible;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        return searchBar.getText().length() > 0 ? listScroll(filteredButtons, direction * -1) : listScroll(buttons, direction * -1);
    }

    private boolean listScroll(List<Widget> buttonList, double direction) {
        int buttonH = minYButton;

        if (index <= 0 && direction < 0) {
            return false;
        }

        if ((index >= buttonList.size() - 10 || index >= soundsList.size() - 10) && direction > 0) {
            return false;
        }

        index += direction > 0 ? 10 : -10;

        for (Widget button : buttonList) {
            if (button instanceof MuffledSlider) {
                int buttonIndex = buttonList.indexOf(button);
                button.visible = buttonIndex < index + 10 && buttonIndex >= index;

                if (button.visible) {
                    button.y = buttonH;
                    buttonH += button.getHeightRealms() + 2;
                }

                ((MuffledSlider) button).getBtnToggleSound().y = button.y;
                ((MuffledSlider) button).getBtnToggleSound().active = button.visible;
                ((MuffledSlider) button).getBtnPlaySound().y = button.y;
                ((MuffledSlider) button).getBtnPlaySound().active = button.visible;
            }
        }

        return true;
    }

    private void updateText() {
        int buttonH = minYButton;
        filteredButtons.clear();

        for (Widget button : buttons) {
            if (button instanceof MuffledSlider) {
                if (button.getMessage().toString().contains(searchBar.getText())) {
                    if (!filteredButtons.contains(button))
                        filteredButtons.add(button);

                    button.y = buttonH;
                    buttonH += button.getHeightRealms() + 2;

                    button.visible = button.y < maxYButton;
                } else {
                    button.visible = false;
                }

                ((MuffledSlider) button).getBtnToggleSound().y = button.y;
                ((MuffledSlider) button).getBtnToggleSound().active = button.visible;
                ((MuffledSlider) button).getBtnPlaySound().y = button.y;
                ((MuffledSlider) button).getBtnPlaySound().active = button.visible;

            }
        }

    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        //pressed "backspace" inside search bar
        if (keyCode == 259 && searchBar.isFocused()) {
            updateText();
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
        //Type inside the search bar
        if (searchBar != null && searchBar.isFocused()) {
            updateText();
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        //Search bar, Edit title bar & Edit Anchor radious bar looses focus when pressed "Enter" or "Intro"
        if (key1 == 257 || key1 == 335) {
            searchBar.setFocused2(false);
            editAnchorTitleBar.setFocused2(false);
            editAnchorRadious.setFocused2(false);
            return true;
        }

        //Close screen when press "E" or the mod hotkey outside the search bar or edit title bar
        if (!searchBar.isFocused() && !editAnchorTitleBar.isFocused() && !editAnchorRadious.isFocused() && (key1 == 69 || key1 == SoundMuffler.getHotkey())) {
            closeScreen();
            filteredButtons.clear();
            return true;
        }

        return super.keyPressed(key1, key2, key3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            if (searchBar.isFocused()) {
                searchBar.setText("");
                updateText();
            }
            if (editAnchorTitleBar.isFocused()) {
                editAnchorTitleBar.setText("");
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        MuffledSlider.showSlider = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        updateText();
        super.resize(minecraft, width, height);
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

}