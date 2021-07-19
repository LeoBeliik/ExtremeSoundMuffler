package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class MainScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private final List<Widget> filteredButtons = new ArrayList<>();
    private static boolean isMuffling = true;
    private static String searchBarText = "";
    private static String screenTitle = "";
    private static ITextComponent toggleSoundsListMessage;
    private final int xSize = 256;
    private final int ySize = 202;
    private final boolean isAnchorsDisabled = Config.getDisableAchors();
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final String mainTitle = "ESM - Main Screen";

    private int minYButton, maxYButton, index;
    private Button btnToggleMuffled, btnDelete, btnToggleSoundsList, btnSetAnchor, btnEditAnchor, btnNextSounds, btnPrevSounds, btnAccept, btnCancel;
    private TextFieldWidget searchBar, editAnchorTitleBar, editAnchorRadiusBar;

    public MainScreen() {
        super(StringTextComponent.EMPTY);
    }

    private static void open(String title, ITextComponent message, String searchMessage) {
        toggleSoundsListMessage = message;
        screenTitle = title;
        searchBarText = searchMessage;
        minecraft.setScreen(new MainScreen());
    }

    public static void open() {
        open("ESM - Main Screen", ITextComponent.nullToEmpty("Recent"), "");
    }

    private void bindTexture() {
        minecraft.getTextureManager().bind(SoundMuffler.getGui());
    }

    public static boolean isMuffled() {
        return isMuffling;
    }

    /*@Nullable
    public static Anchor getAnchorByName(String name) {
        return anchorList.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }*/

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        this.bindTexture();
        this.blit(matrix, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        drawCenteredString(matrix, font, screenTitle, getX() + 128, getY() + 8, whiteText); //Screen title
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
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        minYButton = getY() + 46;
        maxYButton = getY() + 164;


        addWidget(btnToggleSoundsList = new Button(getX() + 13, getY() + 181, 52, 13, toggleSoundsListMessage, b -> {
            boolean isAnchorMuffling = false;

           /* if (!screenTitle.equals(mainTitle)) {
                isAnchorMuffling = !Objects.requireNonNull(getAnchorByName(screenTitle)).getMuffledSounds().isEmpty();
            }*/

            if (btnToggleSoundsList.getMessage().equals(ITextComponent.nullToEmpty("Recent"))) {
                toggleSoundsListMessage = ITextComponent.nullToEmpty("All");
            } else if (btnToggleSoundsList.getMessage().equals(ITextComponent.nullToEmpty("All"))) {
                if (!muffledSounds.isEmpty() || isAnchorMuffling) {
                    toggleSoundsListMessage = ITextComponent.nullToEmpty("Muffled");
                } else {
                    toggleSoundsListMessage = ITextComponent.nullToEmpty("Recent");
                }
            } else {
                toggleSoundsListMessage = ITextComponent.nullToEmpty("Recent");
            }

            btnToggleSoundsList.setMessage(toggleSoundsListMessage);
            buttons.clear();
            open(screenTitle, toggleSoundsListMessage, searchBar.getValue());
        }));

        addSoundButtons();

        addButton(btnToggleMuffled = new Button(getX() + 229, getY() + 179, 17, 17, emptyText, b -> isMuffling = !isMuffling)).setAlpha(0);

        addButton(btnDelete = new Button(getX() + 205, getY() + 179, 17, 17, emptyText, b -> {
                    //anchor = getAnchorByName(screenTitle);
                    if (screenTitle.equals(mainTitle)) {
                        muffledSounds.clear();
                        open(mainTitle, btnToggleSoundsList.getMessage(), searchBar.getValue());
                    } /*else {
                        if (anchor == null) {
                            return;
                        }
                        anchor.deleteAnchor();
                        buttons.clear();
                        open(anchor.getName(), btnToggleSoundsList.getMessage(), searchBar.getValue());
                    }*/
                })
        ).setAlpha(0);

        /*addButton(btnSetAnchor = new Button(getX() + 260, getY() + 62, 11, 11, emptyText, b ->
                Objects.requireNonNull(getAnchorByName(screenTitle)).setAnchor())).setAlpha(0);

        addButton(btnEditAnchor = new Button(getX() + 274, getY() + 62, 11, 11, emptyText, b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).setAlpha(0);*/

        addEditAnchorButtons();

        if (screenTitle.equals(mainTitle)) {
            btnSetAnchor.visible = false;
            btnEditAnchor.visible = false;
        }

        addButton(searchBar = new TextFieldWidget(font, getX() + 74, getY() + 183, 119, 13, emptyText));
        searchBar.setBordered(false);
        searchBar.insertText(searchBarText);

        addWidget(btnPrevSounds = new Button(getX() + 10, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getValue().length() > 0 ? filteredButtons : buttons, -1)));

        addWidget(btnNextSounds = new Button(getX() + 233, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getValue().length() > 0 ? filteredButtons : buttons, 1)));

        updateText();
    }

    private void addSoundButtons() {
        int buttonH = minYButton;
        //anchor = getAnchorByName(screenTitle);

        /*if (!screenTitle.equals(mainTitle) && anchor == null) {
            return;
        }*/

        if (btnToggleSoundsList.getMessage().equals(ITextComponent.nullToEmpty("Recent"))) {
            soundsList.clear();
            if (screenTitle.equals(mainTitle) && !muffledSounds.isEmpty()) {
                soundsList.addAll(muffledSounds.keySet());
            } /*else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds().keySet());
            }*/
            soundsList.addAll(recentSoundsList);
        } else if (btnToggleSoundsList.getMessage().equals(ITextComponent.nullToEmpty("All"))) {
            soundsList.clear();
            soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys());
            forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));
        } else {
            soundsList.clear();
            if (screenTitle.equals(mainTitle) && !muffledSounds.isEmpty()) {
                soundsList.addAll(muffledSounds.keySet());
            } /*else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds().keySet());
            }*/
        }

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {

            float volume;
            float maxVolume = 1F;

            if (screenTitle.equals(mainTitle)) {
                volume = muffledSounds.get(sound) == null ? maxVolume : muffledSounds.get(sound);
            } /*else if (anchor != null) {
                volume = anchor.getMuffledSounds().get(sound) == null ? maxVolume : anchor.getMuffledSounds().get(sound);
            } */else {
                volume = maxVolume;
            }

            int x = Config.getLeftButtons() ? getX() + 36 : getX() + 11;

            //MuffledSlider volumeSlider = new MuffledSlider(x, buttonH, 205, 11, volume, sound, screenTitle, anchor);

            //boolean muffledAnchor = anchor != null && screenTitle.equals(anchor.getName()) && !anchor.getMuffledSounds().isEmpty() && anchor.getMuffledSounds().containsKey(sound);
            boolean muffledScreen = screenTitle.equals(mainTitle) && !muffledSounds.isEmpty() && muffledSounds.containsKey(sound);

           /* if (muffledAnchor || muffledScreen) {
                volumeSlider.setFGColor(cyanText);
            }

            buttonH += volumeSlider.getHeight() + 2;
            addButton(volumeSlider);
            volumeSlider.visible = buttons.indexOf(volumeSlider) < index + 10;
            addWidget(volumeSlider.getBtnToggleSound());
            addWidget(volumeSlider.getBtnPlaySound());*/

        }
    }

    private void addEditAnchorButtons() {

        addButton(editAnchorTitleBar = new TextFieldWidget(font, getX() + 302, btnEditAnchor.y + 20, 84, 11, emptyText)).visible = false;

        addButton(editAnchorRadiusBar = new TextFieldWidget(font, getX() + 302, editAnchorTitleBar.y + 15, 30, 11, emptyText)).visible = false;

        addButton(btnAccept = new Button(getX() + 259, editAnchorRadiusBar.y + 15, 40, 20, ITextComponent.nullToEmpty("Accept"), b -> {
            //anchor = getAnchorByName(screenTitle);
            if (!editAnchorTitleBar.getValue().isEmpty() && !editAnchorRadiusBar.getValue().isEmpty() /*&& anchor != null*/) {
                int Radius = Integer.parseInt(editAnchorRadiusBar.getValue());

                if (Radius > 32) {
                    Radius = 32;
                } else if (Radius < 1) {
                    Radius = 1;
                }

                //anchor.editAnchor(editAnchorTitleBar.getValue(), Radius);
                screenTitle = editAnchorTitleBar.getValue();
                //editTitle(anchor);
            }
        })).visible = false;

/*
        addButton(btnCancel = new Button(getX() + 300, editAnchorRadiusBar.y + 15, 40, 20, ITextComponent.nullToEmpty("Cancel"), b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).visible = false;
*/

    }

    private void renderButtonsTextures(MatrixStack matrix, double mouseX, double mouseY, float partialTicks) {
        int x; //start x point of the button
        int y; //start y point of the button
        int mX; //start x point for mouse hovering
        int mY; //start y point for mouse hovering
        float v; //start x point of the texture
        String message; //Button message
        int stringW; //text width


        //TODO: page number next to the arrows?
        drawCenteredString(matrix, font, "All", getX() + 90, getY() + 26, whiteText);
        fillGradient(matrix, getX() + 108, getY() + 24, getX() + 147, getY() + 37, brightBG, darkBG);

        drawCenteredString(matrix, font, "Recent", getX() + 128, getY() + 26, whiteText);
        drawCenteredString(matrix, font, "Muffled", getX() + 170, getY() + 26, whiteText);

        if (buttons.size() < soundsList.size()) {
            return;
        }

        //Delete button
        x = btnDelete.x + 8;
        y = btnDelete.y;
        message = screenTitle.equals(mainTitle) ? "Delete Muffled List" : "Delete Anchor";
        stringW = font.width(message) / 2;
        if (btnDelete.isHovered()) {
            fill(matrix, x - stringW - 2, y + 20, x + stringW + 2, y + 31, darkBG);
            drawCenteredString(matrix, font, message, x, y + 22, whiteText);
        }

        //toggle muffled button
        x = btnToggleMuffled.x + 8;
        y = btnToggleMuffled.y;
        this.bindTexture();

        if (isMuffling) {
            blit(matrix, x - 8, y, 54F, 202F, 17, 17, xSize, xSize); //muffle button
        }

        message = isMuffling ? "Stop Muffling" : "Start Muffling";
        stringW = font.width(message) / 2;
        if (btnToggleMuffled.isHovered()) {
            fill(matrix, x - stringW - 2, y + 20, x + stringW + 2, y + 31, darkBG);
            drawCenteredString(matrix, font, message, x, y + 22, whiteText);
        }

        //Anchor coordinates and set coord button
        //Anchor anchor = getAnchorByName(screenTitle);
        String dimensionName = "";
        String Radius;
        x = btnSetAnchor.x;
        y = btnSetAnchor.y;

        /*if (anchor != null) {
            stringW = font.width("Dimension: ");
            Radius = anchor.getRadius() == 0 ? "" : String.valueOf(anchor.getRadius());
            if (anchor.getDimension() != null) {
                stringW += font.width(anchor.getDimension().getPath());
                dimensionName = anchor.getDimension().getPath();
            }
            fill(matrix, x - 5, y - 56, x + stringW + 6, y + 16, darkBG);
            drawString(matrix, font, "X: " + anchor.getX(), x + 1, y - 50, whiteText);
            drawString(matrix, font, "Y: " + anchor.getY(), x + 1, y - 40, whiteText);
            drawString(matrix, font, "Z: " + anchor.getZ(), x + 1, y - 30, whiteText);
            drawString(matrix, font, "Radius: " + Radius, x + 1, y - 20, whiteText);
            drawString(matrix, font, "Dimension: " + dimensionName, x + 1, y - 10, whiteText);
            this.bindTexture();
            blit(matrix, x, y, 0, 69.45F, 11, 11, 88, 88); //set coordinates button

            if (anchor.getAnchorPos() != null) {
                btnEditAnchor.active = true;
                blit(matrix, btnEditAnchor.x, btnEditAnchor.y, 32F, 213F, 11, 11, xSize, xSize); //change title button
            } else {
                btnEditAnchor.active = false;
            }

            //Indicates the Anchor has to be set before muffling sounds
            for (Widget button : buttons) {
                if (button instanceof MuffledSlider) {
                    if (((MuffledSlider) button).getBtnToggleSound().isMouseOver(mouseX, mouseY) && anchor.getAnchorPos() == null) {
                        fill(matrix, x - 5, y + 16, x + 65, y + 40, darkBG);
                        font.draw(matrix, "Set the", x, y + 18, whiteText);
                        font.draw(matrix, "Anchor first", x, y + 29, whiteText);
                    }
                }
                minecraft.getTextureManager().bind(SoundMuffler.getGui());
                if (!(button instanceof MuffledSlider)) {
                    if (button.getMessage().getString().equals(String.valueOf(anchor.getAnchorId()))) {
                        blit(matrix, button.x - 5, button.y - 2, 71F, 202F, 27, 22, xSize, xSize); //fancy selected Anchor indicator
                        break;
                    }
                }
            }
        }*/


        message = "Set Anchor";
        stringW = font.width(message) + 2;

        //Set Anchor tooltip
        if (btnSetAnchor.isHovered() && !editAnchorTitleBar.visible) {
            fill(matrix, x - 5, y + 16, x + stringW, y + 29, darkBG);
            font.draw(matrix, message, x, y + 18, whiteText);
        }

        message = "Edit Anchor";
        stringW = font.width(message) + 2;

        if (btnEditAnchor.visible && !editAnchorTitleBar.visible && btnEditAnchor.isHovered()) {
            fill(matrix, x - 5, y + 16, x + stringW + 2, y + 29, darkBG);
            font.draw(matrix, message, x, y + 18, whiteText);
        }

        //draw anchor buttons tooltip
       /* if (anchorList.isEmpty()) {
            DataManager.setAnchors();
        }*/
        /*for (int i = 0; i <= 9; i++) {
            Widget btn = buttons.get(soundsList.size() + i);
            x = btn.x + 8;
            y = btn.y + 5;
            message = isAnchorsDisabled ? "Anchors are disabled" : anchorList.get(i).getName();
            stringW = font.width(message) / 2;

            if (btn.isHovered()) {
                fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
                drawCenteredString(matrix, font, message, x, y - 11, whiteText);
            }
        }*/

        //Toggle List button draw message
/*
        x = btnToggleSoundsList.x;
        y = btnToggleSoundsList.y;
        message = btnToggleSoundsList.getMessage().getString();
        int centerText = x + (btnToggleSoundsList.getWidth() / 2) - (font.width(message) / 2);
        font.draw(matrix, message, centerText, y + 3, 0);
        String text = "Showing " + message + " sounds";
        int textW = font.width(text);
        int textX = x + (btnToggleSoundsList.getWidth() / 2) - (textW / 2) + 6;

        if (btnToggleSoundsList.isMouseOver(mouseX, mouseY)) {
            fill(matrix, textX - 2, y + 20, textX + textW + 2, y + 22 + font.lineHeight, darkBG);
            font.draw(matrix, text, textX, y + 22, whiteText);
        }
*/

        //Show Radius and Title text when editing Anchor and bg
        x = btnSetAnchor.x;
        y = editAnchorTitleBar.y;
        if (editAnchorRadiusBar.visible) {
            fill(matrix, x - 4, y - 4, editAnchorTitleBar.x + editAnchorTitleBar.getWidth() + 3, btnAccept.y + 23, darkBG);
            font.draw(matrix, "Title: ", x - 2, y + 1, whiteText);
            font.draw(matrix, "Radius: ", x - 2, editAnchorRadiusBar.y + 1, whiteText);

            x = editAnchorRadiusBar.x + editAnchorRadiusBar.getWidth();
            y = editAnchorRadiusBar.y;
            message = "Range: 1 - 32";
            stringW = font.width(message);
            if (editAnchorRadiusBar.isHovered()) {
                fill(matrix, x + 3, y, x + stringW + 6, y + 12, darkBG);
                font.draw(matrix, message, x + 5, y + 2, whiteText);
            }
        }

        //Draw Searchbar prompt text
        x = searchBar.x;
        y = searchBar.y;
        ITextComponent searchHint = (new TranslationTextComponent("gui.recipebook.search_hint")).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY); //from Vanilla recipebook GUI
        if (!this.searchBar.isFocused() && this.searchBar.getValue().isEmpty()) {
            drawString(matrix, font, searchHint, x + 1, y + 1, -1);
        }

        //next sounds button tooltip
        x = btnNextSounds.x;
        y = btnNextSounds.y;
        message = "Next Sounds";
        stringW = font.width(message) / 2;

        if (btnNextSounds.isMouseOver(mouseX, mouseY)) {
            fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
            drawCenteredString(matrix, font, message, x, y - 11, whiteText);
        }

        //previous sounds button tooltip
        x = btnPrevSounds.x;
        y = btnPrevSounds.y;
        message = "Previous Sounds";
        stringW = font.width(message) / 2;

        if (btnPrevSounds.isMouseOver(mouseX, mouseY)) {
            fill(matrix, x - stringW - 2, y - 2, x + stringW + 2, y - 13, darkBG);
            drawCenteredString(matrix, font, message, x, y - 11, whiteText);
        }

        //Show a tip the first time a sound is muffled
        x = this.getX();
        y = getY() + ySize;
        ITextProperties tipMessage = ITextProperties.of("TIP: you can set the volume for muffled sounds by dragging the slider around");

        if (muffledSounds.size() == 1 && Config.getShowTip()) {
            if (mouseX > x + 3 && mouseX < x + xSize - 3 && mouseY > y - 30 && mouseY < y - 3) {
                return;
            }

            fill(matrix, x + 3, y, x + xSize - 2, y + 22, darkBG);
            font.drawWordWrap(tipMessage, x + 7, y + 2, xSize, whiteText);
        }

        //highlight every other row
        for (int i = 0; i < buttons.size(); i++) {
            Widget button = buttons.get(i);
            if (button instanceof MuffledSlider) {
                x = Config.getLeftButtons() ? button.x - 3 : button.x + 1;
                y = button.y;
                int bW = Config.getLeftButtons() ? x + button.getWidth() + 5 : x + button.getWidth() + 28;

                if (i % 2 == 0 && button.visible) {
                    fill(matrix, x, y, bW, y + button.getHeight(), brightBG);
                }
            }
        }
    }

/*
    private void editTitle(Anchor anchor) {
        editAnchorTitleBar.insertText(anchor.getName());
        editAnchorTitleBar.visible = !editAnchorTitleBar.visible;

        editAnchorRadiusBar.insertText(String.valueOf(anchor.getRadius()));
        editAnchorRadiusBar.visible = !editAnchorRadiusBar.visible;

        btnAccept.visible = !btnAccept.visible;
        btnCancel.visible = !btnCancel.visible;

        editAnchorRadiusBar.setTextColor(whiteText);
    }
*/

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        return searchBar.getValue().length() > 0 ? listScroll(filteredButtons, direction * -1) : listScroll(buttons, direction * -1);
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
                    buttonH += button.getHeight() + 2;
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
                if (button.getMessage().toString().contains(searchBar.getValue().toLowerCase())) {
                    if (!filteredButtons.contains(button))
                        filteredButtons.add(button);

                    button.y = buttonH;
                    buttonH += button.getHeight() + 2;

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

        if (!editAnchorRadiusBar.getValue().isEmpty()) {
            int Radius = Integer.parseInt(editAnchorRadiusBar.getValue());
            editAnchorRadiusBar.setTextColor(Radius > 32 || Radius < 1 ? cyanText : whiteText);
        } else {
            editAnchorRadiusBar.setTextColor(whiteText);
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searchBar.charTyped(codePoint, modifiers)) {
            updateText();
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Radius only accepts numbers
        editAnchorRadiusBar.setFilter(s -> s.matches("[0-9]*(?:[0-9]*)?"));

        //Type inside the search bar
        if (searchBar.keyPressed(keyCode, scanCode, modifiers)) {
            updateText();
            return true;
        }

        //Search bar, Edit title bar & Edit Anchor Radius bar looses focus when pressed "Enter" or "Intro"
        if (keyCode == 257 || keyCode == 335) {
            searchBar.setFocus(false);
            editAnchorTitleBar.setFocus(false);
            editAnchorRadiusBar.setFocus(false);
            return true;
        }

        //Close screen when press "E" or the mod hotkey outside the search bar or edit title bar
        if (!searchBar.isFocused() && !editAnchorTitleBar.isFocused() && !editAnchorRadiusBar.isFocused() &&
                (minecraft.options.keyInventory.matches(keyCode, scanCode) || keyCode == SoundMuffler.getHotkey())) {
            this.onClose();
            filteredButtons.clear();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            if (searchBar.isFocused()) {
                searchBar.setValue("");
                updateText();
                return true;
            }
            if (editAnchorTitleBar.isFocused()) {
                editAnchorTitleBar.setValue("");
                return true;
            }
            if (editAnchorRadiusBar.isHovered()) {
                editAnchorRadiusBar.setValue("");
                return true;
            }
        } else {
            if (searchBar.isFocused() && !searchBar.mouseClicked(mouseX, mouseY, button)) {
                searchBar.setFocus(false);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        MuffledSlider.showSlider = false;
        MuffledSlider.tickSound = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        updateText();
        super.resize(minecraft, width, height);
    }

    @Override
    public void onClose() {
        DataManager.saveData();
        super.onClose();
    }

    public static String getScreenTitle() {
        return screenTitle;
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}