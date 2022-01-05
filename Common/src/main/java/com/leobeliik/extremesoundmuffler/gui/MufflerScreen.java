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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.renderGui;

public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

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
    private MuffledSlider btnSound, firstSoundButton, lastSoundButton;

    @Override
    protected void init() {
        super.init();

        minYButton = getY() + 46;
        maxYButton = getY() + 164;

        //allows to hold a key to keep printing it. in this case i want it to easy erase text
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addButtons();
        addSoundListButtons();
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Radius only accepts numbers
        //editAnchorRadiusBar.setFilter(s -> s.matches("[0-9]*(?:[0-9]*)?"));

        //Search bar, Edit title bar & Edit Anchor Radius bar looses focus when pressed "Enter" or "Intro"
        if (keyCode == 257 || keyCode == 335) {
            searchBar.setFocus(false);
            return true;
        }

        //Close screen when press "E" or the mod hotkey outside the search bar or edit title bar
        if (!searchBar.isFocused() /*&& !editAnchorTitleBar.isFocused() && !editAnchorRadiusBar.isFocused()*/ &&
                (minecraft.options.keyInventory.matches(keyCode, scanCode) || keyCode == Constants.soundMufflerKey.getDefaultKey().getValue())) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (searchBar.isFocused()) {
            updateButtons();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        if (firstSoundButton == null) {
            return false;
        }

        if ((direction > 0 && firstSoundButton.y == minYButton) || (direction < 0 && lastSoundButton.y <= maxYButton)) {
            return false;
        }
        children().stream().filter(b -> b instanceof MuffledSlider).map(b -> (MuffledSlider) b).forEach(b -> {
            //only increase / decrease from 10 to 10 to prevent the sliders going further than they should
            b.setY((int) (b.y + (b.getHeight() * 10) * Mth.clamp(direction, -1, 1)));
            b.isVisible(b.y >= minYButton && b.y <= maxYButton);
        });

        return super.mouseScrolled(mouseX, mouseY, direction);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //right click
        if (button == 1) {
            //clear searchbar with rmb
            if (!searchBar.getValue().isEmpty() && searchBar.isMouseOver(mouseX, mouseY)) {
                searchBar.setValue("");
                searchBar.setFocus(true);
                updateButtons();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        DataManager.saveData();
        super.onClose();
    }

    //-----------------------------------My functions-----------------------------------//

    private static void open(Component title) {
        screenTitle = title.getString();
        minecraft.setScreen(new MufflerScreen(title));
    }

    private void addButtons() {

        addWidget(btnToggleSoundsList = new Button(getX() + 13, getY() + 181, 52, 13, Component.nullToEmpty("Recent"), b -> {
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
            b.setMessage(toggleSoundsListMessage);
            updateButtons();
        }));
        //Searchbar
        addRenderableWidget(searchBar = new EditBox(font, getX() + 74, getY() + 183, 119, 13, emptyText)).setBordered(false);
        //toggle muffling sounds on/off
        addWidget(btnToggleMuffled = new Button(getX() + 229, getY() + 180, 17, 17, Component.nullToEmpty("Stop muffing"), b -> isMuffling = !isMuffling));
        //deletes current muffled list
        addWidget(new Button(getX() + 205, getY() + 180, 17, 17, Component.nullToEmpty("Delete muffled sounds"), b -> {
            muffledSounds.clear();
            updateButtons();
        }));
        //backwards list of sounds
/*
        addWidget(new Button(getX() + 10, getY() + 182, 10, 13, Component.nullToEmpty("Previous sounds"), b -> mouseScrolled(0D, 0D, 1D)));
        //forward list of sounds
        addWidget(new Button(getX() + 22, getY() + 182, 10, 13, Component.nullToEmpty("Next sounds"), b -> mouseScrolled(0D, 0D, -1D)));
*/
    }

    private void addSoundListButtons() {
        int by = minYButton;
        //set x depending of config
        int bx = CommonConfig.get().leftButtons().get() ? getX() + 36 : getX() + 11;
        anchor = getAnchorByName(screenTitle);
        //easiest way to assure this is the first one
        firstSoundButton = null;
        soundsList.clear();

        switch (btnToggleSoundsList.getMessage().getString()) {
            case "Recent" -> soundsList.addAll(recentSoundsList);
            case "All" -> soundsList.addAll(Registry.SOUND_EVENT.keySet());
            default -> soundsList.addAll(muffledSounds.keySet());
        }
        //removes blacklisted sounds
        forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {
            //Remove sound that are not being search, show all if searchbar is empty
            if (!sound.toString().contains(searchBar.getValue())) {
                continue;
            }
            //set volume to muffled sounds if any
            double volume = muffledSounds.get(sound) == null ? 1F : muffledSounds.get(sound);
            //row highlight
            int bg = children().size() % 2 == 0 ? darkBG : brightBG;

            btnSound = new MuffledSlider(bx, by, bg, sound, volume, this);

            if (!muffledSounds.isEmpty() && muffledSounds.containsKey(sound)) {
                setFGColor(btnSound, "aqua");
            }

            addRenderableWidget(btnSound);

            by += btnSound.getHeight();
            btnSound.isVisible(btnSound.y < maxYButton);

            if (firstSoundButton == null) {
                firstSoundButton = btnSound;
            }

            lastSoundButton = btnSound;
            if (soundsList.last().equals(sound)) {
            }
        }

    }

    private void updateButtons() {
        for (Iterator<? extends GuiEventListener> iterator = children().iterator(); iterator.hasNext(); ) {
            Widget button = (Widget) iterator.next();
            if (button instanceof MuffledSlider) {
                ((MuffledSlider) button).isVisible(false);
                iterator.remove();
            }
        }
        addSoundListButtons();
    }

    public void removeSoundMuffled(ResourceLocation sound) {
        muffledSounds.remove(sound);
    }

    public void addSoundMuffled(ResourceLocation sound, double volume) {
        muffledSounds.put(sound, volume);
    }

    public void replaceVolume(ResourceLocation sound, double volume) {
        muffledSounds.replace(sound, volume);
    }

    private MufflerScreen(Component title) {
        super(title);
    }

    public static void open() {
        open(Component.nullToEmpty("ESM - Main Screen"));
    }

    private void renderButtonsTextures(PoseStack stack, int mouseX, int mouseY, float partialTicks) {

    }

    private static Anchor getAnchorByName(String name) {
        return anchorList.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}
