package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import com.leobeliik.extremesoundmuffler.utils.Tips;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.renderGui;

//TODO REWRITE THIS
public class MainScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private final List<GuiEventListener> filteredButtons = new ArrayList<>();
    private static boolean isMuffling = true;
    private static String searchBarText = "";
    private static String screenTitle = "";
    private static Component toggleSoundsListMessage;
    private final int xSize = 256;
    private final int ySize = 202;
    private final boolean isAnchorsDisabled = CommonConfig.get().disableAnchors().get();
    private final Component emptyText = TextComponent.EMPTY;
    private final String mainTitle = "ESM - Main Screen";
    private String tip = Tips.randomTip();
    private int minYButton, maxYButton, index;
    private Button btnToggleMuffled, btnDelete, btnToggleSoundsList, btnSetAnchor, btnEditAnchor, btnNextSounds, btnPrevSounds, btnAccept, btnCancel;
    private EditBox searchBar, editAnchorTitleBar, editAnchorRadiusBar;
    private Anchor anchor;

    private MainScreen() {
        super(TextComponent.EMPTY);
    }

    private static void open(String title, Component message, String searchMessage) {
        toggleSoundsListMessage = message;
        screenTitle = title;
        searchBarText = searchMessage;
        minecraft.setScreen(new MainScreen());
    }

    public static void open() {
        open("ESM - Main Screen", Component.nullToEmpty("Recent"), "");
    }

    public static boolean isMuffling() {
        return isMuffling;
    }

    public static Anchor getAnchorByName(String name) {
        return anchorList.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        renderGui();
        this.blit(stack, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        drawCenteredString(stack, font, screenTitle, getX() + 128, getY() + 8, whiteText); //Screen title
        renderButtonsTextures(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
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

            if (!screenTitle.equals(mainTitle)) {
                isAnchorMuffling = !Objects.requireNonNull(getAnchorByName(screenTitle)).getMuffledSounds().isEmpty();
            }

            if (btnToggleSoundsList.getMessage().equals(Component.nullToEmpty("Recent"))) {
                toggleSoundsListMessage = Component.nullToEmpty("All");
            } else if (btnToggleSoundsList.getMessage().equals(Component.nullToEmpty("All"))) {
                if (!muffledSounds.isEmpty() || isAnchorMuffling) {
                    toggleSoundsListMessage = Component.nullToEmpty("Muffled");
                } else {
                    toggleSoundsListMessage = Component.nullToEmpty("Recent");
                }
            } else {
                toggleSoundsListMessage = Component.nullToEmpty("Recent");
            }

            btnToggleSoundsList.setMessage(toggleSoundsListMessage);
            clearWidgets();
            open(screenTitle, toggleSoundsListMessage, searchBar.getValue());
        }));

        addSoundButtons();

        addAnchorButtons();

        addWidget(btnToggleMuffled = new Button(getX() + 229, getY() + 179, 17, 17, emptyText, b -> isMuffling = !isMuffling)).setAlpha(0);

        addWidget(btnDelete = new Button(getX() + 205, getY() + 179, 17, 17, emptyText, b -> {
                    anchor = getAnchorByName(screenTitle);
                    if (clearRecentSounds()) {
                        recentSoundsList.clear();
                        if (screenTitle.equals(mainTitle)) {
                            open(mainTitle, btnToggleSoundsList.getMessage(), searchBar.getValue());
                        } else if (anchor != null) {
                            open(anchor.getName(), btnToggleSoundsList.getMessage(), searchBar.getValue());
                        }
                        return;
                    }
                    if (screenTitle.equals(mainTitle)) {
                        muffledSounds.clear();
                        open(mainTitle, btnToggleSoundsList.getMessage(), searchBar.getValue());
                    } else {
                        if (anchor != null) {
                            anchor.deleteAnchor();
                            clearWidgets();
                            open(anchor.getName(), btnToggleSoundsList.getMessage(), searchBar.getValue());
                        }
                    }
                })
        ).setAlpha(0);

        addWidget(btnSetAnchor = new Button(getX() + 260, getY() + 62, 11, 11, emptyText, b ->
                Objects.requireNonNull(getAnchorByName(screenTitle)).setAnchor())).setAlpha(0);

        addWidget(btnEditAnchor = new Button(getX() + 274, getY() + 62, 11, 11, emptyText, b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).setAlpha(0);

        addEditAnchorButtons();

        if (screenTitle.equals(mainTitle)) {
            btnSetAnchor.visible = false;
            btnEditAnchor.visible = false;
        }

        addRenderableWidget(searchBar = new EditBox(font, getX() + 74, getY() + 183, 119, 13, emptyText));
        searchBar.setBordered(false);
        searchBar.insertText(searchBarText);

        addWidget(btnPrevSounds = new Button(getX() + 10, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getValue().length() > 0 ? filteredButtons : children(), -1)));

        addWidget(btnNextSounds = new Button(getX() + 233, getY() + 22, 13, 20, emptyText, b ->
                listScroll(searchBar.getValue().length() > 0 ? filteredButtons : children(), 1)));

        updateText();
    }

    private void addSoundButtons() {
        int buttonH = minYButton;
        anchor = getAnchorByName(screenTitle);

        if (!screenTitle.equals(mainTitle) && anchor == null) {
            return;
        }

        if (btnToggleSoundsList.getMessage().equals(Component.nullToEmpty("Recent"))) {
            soundsList.clear();
            if (screenTitle.equals(mainTitle) && !muffledSounds.isEmpty()) {
                soundsList.addAll(muffledSounds.keySet());
            } else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds().keySet());
            }
            soundsList.addAll(recentSoundsList);
        } else if (btnToggleSoundsList.getMessage().equals(Component.nullToEmpty("All"))) {
            soundsList.clear();
            //soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys());
            if (CommonConfig.get().lawfulAllList().get()) {
                forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));
            }
        } else {
            soundsList.clear();
            if (screenTitle.equals(mainTitle) && !muffledSounds.isEmpty()) {
                soundsList.addAll(muffledSounds.keySet());
            } else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds().keySet());
            }
        }

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {

            double volume;
            double maxVolume = 1D;

            if (screenTitle.equals(mainTitle)) {
                volume = muffledSounds.get(sound) == null ? maxVolume : muffledSounds.get(sound);
            } else if (anchor != null) {
                volume = anchor.getMuffledSounds().get(sound) == null ? maxVolume : anchor.getMuffledSounds().get(sound);
            } else {
                volume = maxVolume;
            }

            int x = CommonConfig.get().leftButtons().get() ? getX() + 36 : getX() + 11;

           /* MuffledSlider volumeSlider = new MuffledSlider(x, buttonH, 0, sound, volume, screenTitle, anchor);

            boolean muffledAnchor = anchor != null && screenTitle.equals(anchor.getName()) && !anchor.getMuffledSounds().isEmpty() && anchor.getMuffledSounds().containsKey(sound);
            boolean muffledScreen = screenTitle.equals(mainTitle) && !muffledSounds.isEmpty() && muffledSounds.containsKey(sound);

            if (muffledAnchor || muffledScreen) {
                volumeSlider.setMessage(setFGColor(volumeSlider.getMessage().copy(), "aqua"));
            }

            buttonH += volumeSlider.getHeight() + 2;
            addRenderableWidget(volumeSlider);
            volumeSlider.visible = children().indexOf(volumeSlider) < index + 10;
            addWidget(volumeSlider.getBtnToggleSound());
            addWidget(volumeSlider.getBtnPlaySound());*/

        }
    }

    private void addAnchorButtons() {
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            Button btnAnchor;
            if (isAnchorsDisabled) {
                String[] disabledMsg = {"-", "D", "i", "s", "a", "b", "l", "e", "d", "-"};
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, Component.nullToEmpty(disabledMsg[i]), b -> {
                });
                btnAnchor.active = false;
            } else {
                int finalI = i;
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, Component.nullToEmpty(String.valueOf(i)), b -> {
                    anchor = anchorList.get(finalI);
                    if (anchor == null) return;
                    if (screenTitle.equals(anchor.getName())) {
                        screenTitle = mainTitle;
                    } else {
                        screenTitle = anchor.getName();
                    }
                    clearWidgets();
                    open(screenTitle, btnToggleSoundsList.getMessage(), searchBar.getValue());
                });
                if (!anchorList.isEmpty()) {
                    MutableComponent message = btnAnchor.getMessage().copy();
                    String color = anchorList.get(Integer.parseInt(message.getString())).getAnchorPos() != null ? "green" : "white";
                    setFGColor(btnAnchor, color);
                }
            }
            addRenderableWidget(btnAnchor).setAlpha(0);
            buttonW += 20;
        }
    }

    private void addEditAnchorButtons() {

        addRenderableWidget(editAnchorTitleBar = new EditBox(font, getX() + 302, btnEditAnchor.y + 20, 84, 11, emptyText)).visible = false;

        addRenderableWidget(editAnchorRadiusBar = new EditBox(font, getX() + 302, editAnchorTitleBar.y + 15, 30, 11, emptyText)).visible = false;

        addRenderableWidget(btnAccept = new Button(getX() + 259, editAnchorRadiusBar.y + 15, 40, 20, Component.nullToEmpty("Accept"), b -> {
            anchor = getAnchorByName(screenTitle);
            if (!editAnchorTitleBar.getValue().isEmpty() && !editAnchorRadiusBar.getValue().isEmpty() && anchor != null) {
                int Radius = Integer.parseInt(editAnchorRadiusBar.getValue());

                if (Radius > 32) {
                    Radius = 32;
                } else if (Radius < 1) {
                    Radius = 1;
                }

                anchor.editAnchor(editAnchorTitleBar.getValue(), Radius);
                screenTitle = editAnchorTitleBar.getValue();
                editTitle(anchor);
            }
        })).visible = false;

        addRenderableWidget(btnCancel = new Button(getX() + 300, editAnchorRadiusBar.y + 15, 40, 20, Component.nullToEmpty("Cancel"), b ->
                editTitle(Objects.requireNonNull(getAnchorByName(screenTitle))))).visible = false;

    }

    private void renderButtonsTextures(PoseStack matrix, double mouseX, double mouseY, float partialTicks) {
        int x; //start x point of the button
        int y; //start y point of the button
        int mX; //start x point for mouse hovering
        int mY; //start y point for mouse hovering
        float v; //start x point of the texture
        String message; //Button message
        int stringW; //text width

        if (children().size() < soundsList.size()) {
            return;
        }

        //Anchor coordinates and set coord button
        Anchor anchor = getAnchorByName(screenTitle);
        String dimensionName = "";
        String Radius;
        x = btnSetAnchor.x;
        y = btnSetAnchor.y;

        if (anchor != null) {
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
            renderGui();
            blit(matrix, x, y, 0, 69.45F, 11, 11, 88, 88); //set coordinates button

            if (anchor.getAnchorPos() != null) {
                btnEditAnchor.active = true;
                blit(matrix, btnEditAnchor.x, btnEditAnchor.y, 32F, 213F, 11, 11, xSize, xSize); //change title button
            } else {
                btnEditAnchor.active = false;
            }

            //Indicates the Anchor has to be set before muffling sounds
            for (GuiEventListener button : children()) {
                AbstractWidget btn = (AbstractWidget) button;
                if (btn instanceof MuffledSlider) {
                    if (((MuffledSlider) btn).getBtnToggleSound().isMouseOver(mouseX, mouseY) && anchor.getAnchorPos() == null) {
                        fill(matrix, x - 5, y + 16, x + 65, y + 40, darkBG);
                        font.draw(matrix, "Set the", x, y + 18, whiteText);
                        font.draw(matrix, "Anchor first", x, y + 29, whiteText);
                    }
                } else {
                    renderGui();
                    if (btn.getMessage().getString().equals(String.valueOf(anchor.getAnchorId()))) {
                        blit(matrix, btn.x - 5, btn.y - 2, 71F, 202F, 27, 22, xSize, xSize); //fancy selected Anchor indicator
                        break;
                    }
                }
            }
        }


        message = "Set Anchor";
        stringW = font.width(message) + 2;

        //Set Anchor tooltip
        if (btnSetAnchor.isHoveredOrFocused() && !editAnchorTitleBar.visible) {
            fill(matrix, x - 5, y + 16, x + stringW, y + 29, darkBG);
            font.draw(matrix, message, x, y + 18, whiteText);
        }

        message = "Edit Anchor";
        stringW = font.width(message) + 2;

        if (btnEditAnchor.visible && !editAnchorTitleBar.visible && btnEditAnchor.isHoveredOrFocused()) {
            fill(matrix, x - 5, y + 16, x + stringW + 2, y + 29, darkBG);
            font.draw(matrix, message, x, y + 18, whiteText);
        }

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
            if (editAnchorRadiusBar.isHoveredOrFocused()) {
                fill(matrix, x + 3, y, x + stringW + 6, y + 12, darkBG);
                font.draw(matrix, message, x + 5, y + 2, whiteText);
            }
        }
    }

    private boolean clearRecentSounds() {
        return btnToggleSoundsList.getMessage().equals(Component.nullToEmpty("Recent")) && Screen.hasShiftDown();
    }

    private void renderTips(PoseStack ms, String tips) {
        int h = font.lineHeight * ((font.width(tips) / 245) + 1) + 215;
        fill(ms, getX(), getY() + 210, getX() + 255, getY() + h, darkBG);
        font.drawWordWrap(FormattedText.of(tips), getX() + 5, getY() + 213, 245, whiteText);
    }

    private void editTitle(Anchor anchor) {
        editAnchorTitleBar.insertText(anchor.getName());
        editAnchorTitleBar.visible = !editAnchorTitleBar.visible;

        editAnchorRadiusBar.insertText(String.valueOf(anchor.getRadius()));
        editAnchorRadiusBar.visible = !editAnchorRadiusBar.visible;

        btnAccept.visible = !btnAccept.visible;
        btnCancel.visible = !btnCancel.visible;

        editAnchorRadiusBar.setTextColor(whiteText);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        return searchBar.getValue().length() > 0 ? listScroll(filteredButtons, direction * -1) : listScroll(children(), direction * -1);
    }

    private boolean listScroll(List<? extends GuiEventListener> buttonList, double direction) {
        int buttonH = minYButton;

        if (index <= 0 && direction < 0) {
            return false;
        }

        if ((index >= buttonList.size() - 10 || index >= soundsList.size() - 10) && direction > 0) {
            return false;
        }

        index += direction > 0 ? 10 : -10;

        for (GuiEventListener button : buttonList) {
            AbstractWidget btn = (AbstractWidget) button;
            if (btn instanceof MuffledSlider) {
                int buttonIndex = buttonList.indexOf(btn);
                btn.visible = buttonIndex < index + 10 && buttonIndex >= index;

                if (btn.visible) {
                    btn.y = buttonH;
                    buttonH += btn.getHeight() + 2;
                }

                /*((MuffledSlider) btn).getBtnToggleSound().y = btn.y;
                ((MuffledSlider) btn).getBtnToggleSound().active = btn.visible;
                ((MuffledSlider) btn).getBtnPlaySound().y = btn.y;
                ((MuffledSlider) btn).getBtnPlaySound().active = btn.visible;*/
            }
        }

        return true;
    }

    private void updateText() {
        int buttonH = minYButton;
        filteredButtons.clear();

        for (GuiEventListener button : children()) {
            if (button instanceof MuffledSlider) {
                MuffledSlider btn = (MuffledSlider) button;
                if (btn.getMessage().toString().contains(searchBar.getValue().toLowerCase())) {
                    if (!filteredButtons.contains(btn)) {
                        filteredButtons.add(btn);
                    }

                    btn.y = buttonH;
                    buttonH += btn.getHeight() + 2;

                    btn.visible = btn.y < maxYButton;
                } else {
                    btn.visible = false;
                }

                /*btn.getBtnToggleSound().y = btn.y;
                btn.getBtnToggleSound().active = btn.visible;
                btn.getBtnPlaySound().y = btn.y;
                btn.getBtnPlaySound().active = btn.visible;*/

            }
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

        if (!editAnchorRadiusBar.getValue().isEmpty()) {
            int Radius = Integer.parseInt(editAnchorRadiusBar.getValue());
            editAnchorRadiusBar.setTextColor(Radius > 32 || Radius < 1 ? aquaText : whiteText);
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
                (minecraft.options.keyInventory.matches(keyCode, scanCode) || keyCode == Constants.soundMufflerKey.getDefaultKey().getValue())) {
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
            if (editAnchorRadiusBar.isHoveredOrFocused()) {
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
        /*MuffledSlider.showSlider = false;
        MuffledSlider.tickSound = null;*/
        return super.mouseReleased(mouseX, mouseY, button);
    }

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