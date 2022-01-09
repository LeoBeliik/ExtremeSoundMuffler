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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.Iterator;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.renderGui;

public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean isMuffling = true;
    private static String screenTitle;
    private static String toggleSoundsListMessage;
    private final int xSize = 256;
    private final int ySize = 202;
    private final boolean isAnchorsDisabled = CommonConfig.get().disableAnchors().get();
    private int minYButton, maxYButton, index;
    private Button btnTMS, btnDelete, btnCSL, btnSetAnchor, btnEditAnchor, btnNextSounds, btnPrevSounds, btnAccept, btnCancel, btnAnchor;
    private EditBox searchBar, editAnchorTitleBar, editAnchorRadiusBar;
    private Anchor anchor;
    private String tip;
    private MuffledSlider btnSound, firstSoundButton, lastSoundButton;

    private MufflerScreen(Component title, Anchor anchor) {
        super(title);
        screenTitle = title.getString();
        this.anchor = anchor;
        minecraft.setScreen(this);
    }

    @Override
    protected void init() {
        super.init();

        minYButton = getY() + 46;
        maxYButton = getY() + 164;

        //allows to hold a key to keep printing it. in this case i want it to easy erase text
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addButtons();
        addAnchorButtons();
        addSoundListButtons();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        renderGui();
        this.blit(stack, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        super.render(stack, mouseX, mouseY, partialTicks);
        //--------------- My Renders ---------------//
        //Screen title
        drawCenteredString(stack, font, screenTitle, getX() + 128, getY() + 8, whiteText);
        //render the tips on the bottom of the screen
        renderTips(stack);
        //render buttons tips and other textures
        renderButtons(stack, mouseX, mouseY, partialTicks);
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

    private void open(Component title, Anchor anchor) {
        new MufflerScreen(title, anchor);
    }

    public static void open() {
        new MufflerScreen(Component.nullToEmpty("ESM - Main Screen"), null);
    }

    private void addButtons() {

        //Change Sounds List
        addWidget(btnCSL = new Button(getX() + 13, getY() + 181, 52, 13, Component.nullToEmpty("Recent"), b -> {
            boolean isAnchorMuffling = anchor != null && !anchor.getMuffledSounds().isEmpty();

            switch (btnCSL.getMessage().getString()) {
                case "Recent" -> toggleSoundsListMessage = "All";
                case "All" -> toggleSoundsListMessage = "Muffled";
                default -> toggleSoundsListMessage = "Recent";
            }

            b.setMessage(Component.nullToEmpty(toggleSoundsListMessage));
            updateButtons();
        }));
        //Searchbar
        addRenderableWidget(searchBar = new EditBox(font, getX() + 74, getY() + 183, 119, 13, TextComponent.EMPTY)).setBordered(false);
        //toggle muffling sounds on/off
        addWidget(btnTMS = new Button(getX() + 229, getY() + 180, 17, 17, Component.nullToEmpty("Stop muffing"), b -> isMuffling = !isMuffling));
        //deletes current muffled list or the recent list if shifting
        addWidget(btnDelete = new Button(getX() + 205, getY() + 180, 17, 17, Component.nullToEmpty("Delete muffled sounds"), b -> {
            if (hasShiftDown()) {
                recentSoundsList.clear();
            } else if (anchor == null) {
                muffledSounds.clear();
            } else {
                anchor.deleteAnchor();
            }
            updateButtons();
        }));
        //backwards list of sounds
        addWidget(btnPrevSounds = new Button(getX() + 10, getY() + 22, 13, 20, TextComponent.EMPTY, b -> mouseScrolled(0D, 0D, 1D)));
        //forward list of sounds
        addWidget(btnNextSounds = new Button(getX() + 233, getY() + 22, 13, 20, TextComponent.EMPTY, b -> mouseScrolled(0D, 0D, -1D)));

    }

    private void addAnchorButtons() {
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            if (isAnchorsDisabled) {
                String[] disabledMsg = {"-", "D", "i", "s", "a", "b", "l", "e", "d", "-"};
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, Component.nullToEmpty(disabledMsg[i]), b -> {
                });
                btnAnchor.active = false;
            } else {
                int finalI = i;
                btnAnchor = new Button(buttonW, getY() + 24, 16, 16, Component.nullToEmpty(String.valueOf(i)), b -> {
                    anchor = anchorList.get(finalI);
                    if (screenTitle.equals(anchor.getName())) {
                        anchor = null;
                        screenTitle = "ESM - Main Screen";
                    } else {
                        screenTitle = anchor.getName();
                    }
                    updateButtons();
                });

                //Set color of the number in the button
                if (!anchorList.isEmpty()) {
                    String color = anchorList.get(Integer.parseInt(btnAnchor.getMessage().getString())).getAnchorPos() != null ? "green" : "white";
                    setFGColor(btnAnchor, color);
                }
            }
            addRenderableWidget(btnAnchor).setAlpha(0);
            buttonW += 20;
        }
    }

    private void addSoundListButtons() {
        int by = minYButton;
        //set x depending of config
        int bx = CommonConfig.get().leftButtons().get() ? getX() + 36 : getX() + 11;
        //easiest way to assure this is the first one
        firstSoundButton = null;
        soundsList.clear();

        switch (btnCSL.getMessage().getString()) {
            case "Recent" -> soundsList.addAll(recentSoundsList);
            case "All" -> soundsList.addAll(Registry.SOUND_EVENT.keySet());
            default -> soundsList.addAll(this.anchor == null ? muffledSounds.keySet() : this.anchor.getMuffledSounds().keySet());
        }

        //removes blacklisted sounds when necessary
        if (CommonConfig.get().lawfulAllList().get() && btnCSL.getMessage().getString().equals("All")) {
            forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));
        } else if (btnCSL.getMessage().getString().equals("Recent")) {
            forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));
        }

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {
            //Remove sound that are not being search, show all if searchbar is empty
            if (!sound.toString().contains(searchBar.getValue())) {
                continue;
            }
            //set volume to muffled sounds if any
            double volume;
            if (anchor != null) {
                volume = anchor.getMuffledSounds().get(sound) == null ? 1D : anchor.getMuffledSounds().get(sound);
            } else {
                volume = muffledSounds.get(sound) == null ? 1D : muffledSounds.get(sound);
            }
            //row highlight
            int bg = children().size() % 2 == 0 ? darkBG : brightBG;

            btnSound = new MuffledSlider(bx, by, bg, sound, volume, this);

            if (anchor != null) {
                setFGColor(btnSound, anchor.getMuffledSounds().containsKey(sound) ? "aqua" : "white");
            } else if (!muffledSounds.isEmpty()) {
                setFGColor(btnSound, muffledSounds.containsKey(sound) ? "aqua" : "white");
            } else {
                setFGColor(btnSound, "white");
            }

            addRenderableWidget(btnSound);

            by += btnSound.getHeight();
            btnSound.isVisible(btnSound.y < maxYButton);

            if (firstSoundButton == null) {
                firstSoundButton = btnSound;
            }

            lastSoundButton = btnSound;
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

    private void renderButtons(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        String message; //Tooltip message

        //--------------- Toggle Muffle sounds button ---------------//
        renderGui();
        //draws a "/" over the muffle button texture if muffling
        if (isMuffling) {
            blit(stack, btnTMS.x + 1, btnTMS.y + 1, 54F, 202F, 15, 15, xSize, xSize);
        }

        message = isMuffling ? "Stop Muffling" : "Start Muffling";
        if (btnTMS.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnTMS);
        }

        //--------------- Delete button ---------------//
        message = anchor == null ? "Delete Muffled List" : "Delete Anchor";

        //show texture for the deletion of the recent sounds list
        if (hasShiftDown()) {
            renderGui();
            blit(stack, btnDelete.x + 2, btnDelete.y + 1, 54F, 217F, 13, 13, xSize, xSize);
            message = "Clear recent sounds list";
        }
        //draw tooltip
        if (btnDelete.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnDelete);
        }

        //--------------- Search bar ---------------//
        //render searchbar hint
        Component searchHint = new TranslatableComponent("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        if (!this.searchBar.isFocused() && this.searchBar.getValue().isEmpty()) {
            drawString(stack, font, searchHint, searchBar.x + 1, searchBar.y + 1, -1);
        }

        //--------------- Change sounds list button ---------------//
        //show a message on the empty screen
        if (muffledSounds.isEmpty() && btnCSL.getMessage().getString().equals("Muffled")) {
            MutableComponent text = Component.nullToEmpty("Nothing to show here yet..").copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
            drawCenteredString(stack, font, text, getX() + 128, getY() + 101, whiteText);
        }
        //render btnCSL text because I don't like how the default text looks like
        float centerX = btnCSL.x + btnCSL.getWidth() / 2F - font.width(btnCSL.getMessage().getString()) / 2F;
        font.draw(stack, btnCSL.getMessage().getString(), centerX, btnCSL.y + 3, 0);
        message = "Showing " + btnCSL.getMessage().getString() + " sounds";
        if (btnCSL.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnCSL);
        }

        //--------------- Next sounds in list button ---------------//
        message = "Next Sounds";
        if (btnNextSounds.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnNextSounds);
        }

        //--------------- Previous sounds in list button ---------------//
        message = "Previous Sounds";
        if (btnPrevSounds.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnPrevSounds);
        }

        //--------------- Anchor buttons ---------------//

        if (mouseX > getX() + 30 && mouseX < btnAnchor.x + btnAnchor.getWidth()
                && mouseY > btnAnchor.y && mouseY < btnAnchor.y + btnAnchor.getHeight()
                && CommonConfig.get().disableAnchors().get()) {
            //render tooltip for disabled anchors
            renderTooltip(stack, Component.nullToEmpty("Anchors are disabled!"), getX() + 60, getY() + 40);
        }
    }

    private void renderButtonTooltip(PoseStack stack, String message, Button button) {
        int centeredMessageX = button.x - (font.width(message) / 2);
        int centeredMessageY = button.equals(btnPrevSounds) || button.equals(btnNextSounds) ? button.y - 1 : button.y + button.getHeight() + 16;
        renderTooltip(stack, Component.nullToEmpty(message), centeredMessageX, centeredMessageY);
    }

    private void renderTips(PoseStack stack) {
        if (CommonConfig.get().showTip().get()) {
            if (index % 500 == 0) {
                tip = Tips.randomTip();
                index = 0;
            }
            int h = font.lineHeight * ((font.width(tip) / 245) + 1) + 215;
            fill(stack, getX() - 2, getY() + 208, getX() + 257, getY() + h + 2, darkBG); //outer dark bg
            fill(stack, getX() - 1, getY() + 209, getX() + 256, getY() + h + 1, goldBG); //middle gold bg
            fill(stack, getX(), getY() + 210, getX() + 255, getY() + h, darkBG); //inner dark bg
            font.drawWordWrap(FormattedText.of(tip), getX() + 5, getY() + 213, 245, whiteText);
            index++;
        }
    }

    public boolean removeSoundMuffled(ResourceLocation sound) {
        if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.removeSound(sound);
                return true;
            }
        } else {
            muffledSounds.remove(sound);
            return true;
        }
        return false;
    }

    public boolean addSoundMuffled(ResourceLocation sound, double volume) {
        if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.addSound(sound, volume);
                return true;
            }
        } else {
            muffledSounds.put(sound, volume);
            return true;
        }
        return false;
    }

    public void replaceVolume(ResourceLocation sound, double volume) {
        if (this.anchor != null) {
            this.anchor.replaceSound(sound, volume);
        } else {
            muffledSounds.replace(sound, volume);
        }
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}
