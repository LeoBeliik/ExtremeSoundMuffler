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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import java.util.function.Predicate;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.renderGui;

public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean isMuffling = true;
    private static Component toggleSoundsListMessage, screenTitle, tip;
    private final int xSize = 256;
    private final int ySize = 202;
    private final boolean isAnchorsDisabled = CommonConfig.get().disableAnchors().get();
    private int minYButton, maxYButton, index;
    private Button btnTMS, btnDelete, btnCSL, btnSetAnchor, btnEditAnchor, btnNextSounds, btnPrevSounds, btnAccept, btnCancel, btnAnchor;
    private EditBox searchBar, editAnchorTitleBar, editRadBar;
    private MuffledSlider firstSoundButton, lastSoundButton;
    private Anchor anchor;

    private MufflerScreen(Component title, Anchor anchor) {
        super(title);
        screenTitle = title;
        this.anchor = anchor;
        minecraft.setScreen(this);
    }

    @Override
    protected void init() {
        super.init();

        minYButton = getY() + 46;
        maxYButton = getY() + 164;

        //allows to hold a key to keep printing it. in this case I want it to easy erase text
        //minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addButtons();
        addSideButtons();
        addAnchorButtons();
        addSoundListButtons();
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        renderGui();
        blit(stack, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        renderSideScreen(stack); //render side screen buttons, need to be rendered before all the other things
        super.render(stack, mouseX, mouseY, partialTicks);
        //--------------- My Renders ---------------//
        //Screen title
        drawCenteredString(stack, font, screenTitle, getX() + 128, getY() + 8, whiteText);
        //render the tips on the bottom of the screen
        renderTips(stack);
        //render buttons tips and other textures
        renderButtons(stack, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Radius only accepts numbers
        editRadBar.setFilter(s -> s.matches("[0-9]*(?:[0-9]*)?"));

        //Search bar, Edit title bar & Edit Anchor Radius bar looses focus when pressed "Enter" or "Intro"
        if (keyCode == 257 || keyCode == 335) {
            searchBar.setFocused(false);
            editAnchorTitleBar.setFocused(false);
            editRadBar.setFocused(false);
            return true;
        }
        //Close screen when press "E" or the mod hotkey outside the search bar or edit title bar
        if (!searchBar.isFocused() && !editAnchorTitleBar.isFocused() && !editRadBar.isFocused() &&
                (minecraft.options.keyInventory.matches(keyCode, scanCode) || Constants.soundMufflerKey.matches(keyCode, scanCode))) {
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
        if (!editRadBar.getValue().isEmpty()) {
            int Radius = Integer.parseInt(editRadBar.getValue());
            editRadBar.setTextColor(Radius > 32 || Radius < 1 ? aquaText : whiteText);
        } else {
            editRadBar.setTextColor(whiteText);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        if (firstSoundButton == null) {
            return false;
        }

        if ((direction > 0 && firstSoundButton.getY() == minYButton) || (direction < 0 && lastSoundButton.getY() <= maxYButton)) {
            return false;
        }
        children().stream().filter(b -> b instanceof MuffledSlider).map(b -> (MuffledSlider) b).forEach(b -> {
            //only increase / decrease from 10 to 10 to prevent the sliders going further than they should
            b.setY((int) (b.getY() + (b.getHeight() * 10) * Mth.clamp(direction, -1, 1)));
            b.isVisible(b.getY() >= minYButton && b.getY() <= maxYButton);
        });

        return super.mouseScrolled(mouseX, mouseY, direction);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //right click
        if (button == 1) {
            if (searchBar.isFocused()) {
                searchBar.setValue("");
                updateButtons();
                return true;
            }
            if (editAnchorTitleBar.isFocused()) {
                editAnchorTitleBar.setValue("");
                return true;
            }
            if (editRadBar.isHoveredOrFocused()) {
                editRadBar.setValue("");
                return true;
            }
        } else {
            searchBar.setFocused(searchBar.isMouseOver(mouseX, mouseY));
            editAnchorTitleBar.setFocused(editAnchorTitleBar.isMouseOver(mouseX, mouseY));
            editRadBar.setFocused(editRadBar.isMouseOver(mouseX, mouseY));
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
        new MufflerScreen(Component.translatable("main_screen.main_title"), null);
    }

    //----------------------------------- Buttons init -----------------------------------//

    private void addButtons() {
        //Change Sounds List
        addWidget(btnCSL = Button.builder(Component.translatable("main_screen.btn.csl.recent"), b -> {
            boolean isAnchorMuffling = anchor != null && !anchor.getMuffledSounds().isEmpty();

            Component component = btnCSL.getMessage();
            if (Component.translatable("main_screen.btn.csl.recent").equals(component)) {
                toggleSoundsListMessage = Component.translatable("main_screen.btn.csl.all");
            } else if (Component.translatable("main_screen.btn.csl.all").equals(component)) {
                toggleSoundsListMessage = Component.translatable("main_screen.btn.csl.muffled");
            } else {
                toggleSoundsListMessage = Component.translatable("main_screen.btn.csl.recent");
            }

            b.setMessage(toggleSoundsListMessage);
            updateButtons();
        }).bounds(getX() + 13, getY() + 181, 52, 13).build());
        //Searchbar
        addRenderableWidget(searchBar = new EditBox(font, getX() + 74, getY() + 183, 119, 13, Component.empty())).setBordered(false);
        //toggle muffling sounds on/off
        addWidget(btnTMS = Button.builder(Component.translatable("main_screen.btn.tms.stop"), b -> isMuffling = !isMuffling).bounds(getX() + 229, getY() + 180, 17, 17).build());
        //deletes current muffled list or the recent list if shifting
        addWidget(btnDelete = Button.builder(Component.translatable("main_screen.btn.delete.sounds"), b -> {
            if (hasShiftDown()) {
                recentSoundsList.clear();
            } else if (anchor == null) {
                muffledSounds.clear();
            } else {
                anchor.deleteAnchor();
            }
            updateButtons();
        }).bounds(getX() + 205, getY() + 180, 17, 17).build());
        //backwards list of sounds
        addWidget(btnPrevSounds = Button.builder(Component.empty(), b -> mouseScrolled(0D, 0D, 1D)).bounds(getX() + 10, getY() + 22, 13, 20).build());
        //forward list of sounds
        addWidget(btnNextSounds = Button.builder(Component.empty(), b -> mouseScrolled(0D, 0D, -1D)).bounds(getX() + 233, getY() + 22, 13, 20).build());

    }

    private void addSideButtons() {
        //set anchor's position button
        addWidget(btnSetAnchor = Button.builder(Component.empty(), b -> anchor.setAnchor()).bounds(getX() + 261, getY() + 62, 11, 11).build()).active = false;
        //edit Anchor parameters button
        addWidget(btnEditAnchor = Button.builder(Component.empty(), b -> editTitle()).bounds(getX() + 275, getY() + 62, 11, 11).build()).active = false;
        //edit anchor name bar
        addRenderableWidget(editAnchorTitleBar = new EditBox(font, getX() + 302, btnEditAnchor.getY() + 20, 84, 11, Component.empty())).visible = false;
        //edit anchor radius bar
        addRenderableWidget(editRadBar = new EditBox(font, getX() + 302, editAnchorTitleBar.getY() + 15, 30, 11, Component.empty())).visible = false;
        //accept button
        addRenderableWidget(btnAccept = Button.builder(Component.translatable("main_screen.btn.accept"), b -> {
            if (!editAnchorTitleBar.getValue().isEmpty() && !editRadBar.getValue().isEmpty() && anchor != null) {
                anchor.editAnchor(editAnchorTitleBar.getValue(), Mth.clamp(Integer.parseInt(editRadBar.getValue()), 1, 32));
                screenTitle = Component.nullToEmpty(editAnchorTitleBar.getValue());
                editTitle();
            }
        }).bounds(getX() + 259, editRadBar.getY() + 15, 40, 20).build()).visible = false;
        //cancel button
        addRenderableWidget(btnCancel = Button.builder(Component.translatable("main_screen.btn.cancel"), b -> editTitle()).bounds(getX() + 300, editRadBar.getY() + 15, 40, 20).build()).visible = false;
    }

    private void addAnchorButtons() {
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            if (isAnchorsDisabled) {
                String[] disabledMsg = {"-", "D", "i", "s", "a", "b", "l", "e", "d", "-"};
                btnAnchor = Button.builder(Component.nullToEmpty(disabledMsg[i]), b -> {
                }).bounds(buttonW, getY() + 24, 16, 16).build();
                btnAnchor.active = false;
            } else {
                int finalI = i;
                btnAnchor = Button.builder(Component.nullToEmpty(String.valueOf(i)), b -> {
                    anchor = anchorList.get(finalI);
                    hideSideButtons();
                    if (screenTitle.getString().equals(anchor.getName())) {
                        anchor = null;
                        screenTitle = Component.translatable("main_screen.main_title");
                    } else {
                        screenTitle = Component.nullToEmpty(anchor.getName());
                        btnSetAnchor.active = true;
                    }
                    updateButtons();
                }).bounds(buttonW, getY() + 24, 16, 16).build();
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

        Component component = btnCSL.getMessage();
        if (Component.translatable("main_screen.btn.csl.recent").equals(component)) {
            soundsList.addAll(recentSoundsList);
        } else if (Component.translatable("main_screen.btn.csl.all").equals(component)) {
            BuiltInRegistries.SOUND_EVENT.forEach(k -> soundsList.add(k.getLocation()));
        } else {
            soundsList.addAll(this.anchor == null ? muffledSounds.keySet() : this.anchor.getMuffledSounds().keySet());
        }

        //removes blacklisted sounds when necessary
        if ((CommonConfig.get().lawfulAllList().get() && btnCSL.getMessage().equals(Component.translatable("main_screen.btn.csl.all"))) || btnCSL.getMessage().equals(Component.translatable("main_screen.btn.csl.recent"))) {
            forbiddenSounds.stream().<Predicate<? super ResourceLocation>>map(fs -> sl -> sl.toString().contains(fs)).forEach(soundsList::removeIf);
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
            double volume = anchor == null ? muffledSounds.get(sound) == null ? 1D : muffledSounds.get(sound) : anchor.getMuffledSounds().get(sound) == null ? 1D : anchor.getMuffledSounds().get(sound);
            //row highlight
            int bg = children().size() % 2 == 0 ? darkBG : brightBG;

            MuffledSlider btnSound = new MuffledSlider(bx, by, bg, sound, volume, this);

            if (anchor != null) {
                setFGColor(btnSound, anchor.getMuffledSounds().containsKey(sound) ? "aqua" : "white");
            } else if (!muffledSounds.isEmpty()) {
                setFGColor(btnSound, muffledSounds.containsKey(sound) ? "aqua" : "white");
            } else {
                setFGColor(btnSound, "white");
            }

            addRenderableWidget(btnSound);

            by += btnSound.getHeight();
            btnSound.isVisible(btnSound.getY() < maxYButton);

            if (firstSoundButton == null) {
                firstSoundButton = btnSound;
            }

            lastSoundButton = btnSound;
        }

    }

    private void updateButtons() {
        for (Iterator<? extends GuiEventListener> iterator = children().iterator(); iterator.hasNext(); ) {
            if (iterator.next() instanceof MuffledSlider button) {
                button.isVisible(false);
                iterator.remove();
            }
        }
        addSoundListButtons();
    }

    //----------------------------------- Rendering -----------------------------------//

    private void renderButtons(PoseStack stack, int mouseX, int mouseY) {
        Component message; //Tooltip message

        //--------------- Toggle Muffle sounds button ---------------//
        renderGui();
        //draws a "/" over the muffle button texture if muffling
        if (isMuffling) {
            blit(stack, btnTMS.getX() + 1, btnTMS.getY() + 1, 54F, 202F, 15, 15, xSize, xSize);
        }

        message = isMuffling ? Component.translatable("main_screen.btn.tms.stop") : Component.translatable("main_screen.btn.tms.start");
        if (btnTMS.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnTMS);
        }

        //--------------- Delete button ---------------//
        message = anchor == null ? Component.translatable("main_screen.btn.delete.sounds") : Component.translatable("main_screen.btn.delete.anchor");

        //show texture for the deletion of the recent sounds list
        if (hasShiftDown()) {
            renderGui();
            blit(stack, btnDelete.getX() + 2, btnDelete.getY() + 1, 54F, 217F, 13, 13, xSize, xSize);
            message = Component.translatable("main_screen.btn.delete.list");
        }
        //draw tooltip
        if (btnDelete.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnDelete);
        }

        //--------------- Search bar ---------------//
        //render searchbar hint
        Component searchHint = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
        if (!this.searchBar.isFocused() && this.searchBar.getValue().isEmpty()) {
            drawString(stack, font, searchHint, searchBar.getX() + 1, searchBar.getY() + 1, -1);
        }

        //--------------- Change sounds list button ---------------//
        //show a message on the empty screen
        boolean notMuffling = this.anchor == null ? muffledSounds.isEmpty() : this.anchor.getMuffledSounds().isEmpty();
        if (notMuffling && btnCSL.getMessage().equals(Component.translatable("main_screen.btn.csl.muffled"))) {
            MutableComponent text = Component.translatable("main_screen.empty").copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
            drawCenteredString(stack, font, text, getX() + 128, getY() + 101, whiteText);
        }
        //render btnCSL text because I don't like how the default text looks like
        float centerX = btnCSL.getX() + btnCSL.getWidth() / 2F - font.width(btnCSL.getMessage().getString()) / 2F;
        font.draw(stack, btnCSL.getMessage().getString(), centerX, btnCSL.getY() + 3, 0);
        message = Component.translatable("main_screen.btn.csl.tooltip", btnCSL.getMessage().getString());
        if (btnCSL.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnCSL);
        }

        //--------------- Next sounds in list button ---------------//
        message = Component.translatable("main_screen.btn.next_sounds");
        if (btnNextSounds.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnNextSounds);
        }

        //--------------- Previous sounds in list button ---------------//
        message = Component.translatable("main_screen.btn.previous_sounds");
        if (btnPrevSounds.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, message, btnPrevSounds);
        }

        //--------------- Anchor buttons ---------------//

        if (mouseX > getX() + 30 && mouseX < btnAnchor.getX() + btnAnchor.getWidth()
                && mouseY > btnAnchor.getY() && mouseY < btnAnchor.getY() + btnAnchor.getHeight()
                && CommonConfig.get().disableAnchors().get()) {
            //render tooltip for disabled anchors
            renderTooltip(stack, Component.translatable("main_screen.btn.anchors.disabled"), getX() + 60, getY() + 40);
        }

        //render message for when Anchor pos is not setted
        for (GuiEventListener widget : children()) {
            if (widget instanceof AbstractWidget btn) {
                if (btn instanceof MuffledSlider) {
                    if (anchor != null && anchor.getAnchorPos() == null && ((MuffledSlider) widget).getBtnToggleSound().isMouseOver(mouseX, mouseY)) {
                        renderButtonTooltip(stack, Component.translatable("main_screen.btn.anchors.set_message"), ((MuffledSlider) widget).getBtnToggleSound());
                    }
                } else if (btn.getMessage().getString().matches("[0-9]")) {
                    //Set color of the number in the button
                    if (!anchorList.isEmpty()) {
                        String color = anchorList.get(Integer.parseInt(btn.getMessage().getString())).getAnchorPos() != null ? "green" : "white";
                        setFGColor(btn, color);
                        if (anchor != null && btn.getMessage().getString().equals(String.valueOf(anchor.getAnchorId()))) {
                            renderGui();
                            blit(stack, btn.getX() - 5, btn.getY() - 2, 71F, 202F, 27, 22, xSize, xSize); //fancy selected Anchor indicator
                        }
                    }
                }
            }
        }
        //--------------- Side screen buttons ---------------//

        if (editRadBar.isHoveredOrFocused()) {
            renderButtonTooltip(stack, Component.translatable("main_screen.btn.anchors.set_range"), editRadBar);
        }
        if (editAnchorTitleBar.isHoveredOrFocused()) {
            renderButtonTooltip(stack, Component.translatable("main_screen.btn.anchors.set_title"), editAnchorTitleBar);
        }
        if (btnSetAnchor.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, Component.translatable("main_screen.btn.anchors.set"), btnSetAnchor);
        }
        if (btnEditAnchor.isMouseOver(mouseX, mouseY)) {
            renderButtonTooltip(stack, Component.translatable("main_screen.btn.anchors.edit"), btnEditAnchor);
        }
    }

    private void renderSideScreen(PoseStack stack) {
        if (anchor == null) return; //everything here depends of the Anchor
        //Anchor coordinates and set coord button
        String dimensionName = "";
        String Radius;
        int x = btnSetAnchor.getX();
        int y = btnSetAnchor.getY();
        int stringW;
        String message;

        stringW = font.width(Component.translatable("main_screen.side_screen.dimension"));
        Radius = anchor.getRadius() == 0 ? "" : String.valueOf(anchor.getRadius());
        if (anchor.getDimension() != null) {
            stringW += font.width(anchor.getDimension().getPath());
            dimensionName = anchor.getDimension().getPath();
        }
        fill(stack, x - 5, y - 57, x + stringW + 7, y + 17, whiteBG); //light background border
        fill(stack, x - 5, y - 56, x + stringW + 6, y + 16, darkBG); //dark background
        drawString(stack, font, Component.translatable("main_screen.side_screen.x", anchor.getX()), x + 1, y - 50, whiteText);
        drawString(stack, font, Component.translatable("main_screen.side_screen.y", anchor.getY()), x + 1, y - 40, whiteText);
        drawString(stack, font, Component.translatable("main_screen.side_screen.z", anchor.getZ()), x + 1, y - 30, whiteText);
        drawString(stack, font, Component.translatable("main_screen.side_screen.radius", Radius), x + 1, y - 20, whiteText);
        drawString(stack, font, Component.translatable("main_screen.side_screen.dimension", dimensionName), x + 1, y - 10, whiteText);
        renderGui();
        blit(stack, x, y, 0, 69.45F, 11, 11, 88, 88); //set coordinates button

        if (anchor.getAnchorPos() != null) {
            btnEditAnchor.active = true;
            blit(stack, btnEditAnchor.getX(), btnEditAnchor.getY(), 32F, 213F, 11, 11, xSize, xSize); //set edit anchor button texture
        } else {
            btnEditAnchor.active = false;
        }

        //Show Radius and Title text when editing Anchor and bg
        x = btnSetAnchor.getX();
        y = editAnchorTitleBar.getY();
        if (editRadBar.visible) {
            fill(stack, x + stringW + 7, y - 5, editAnchorTitleBar.getX() + editAnchorTitleBar.getWidth() + 4, btnAccept.getY() + 23, whiteBG);//light top background border
            fill(stack, x - 5, btnAccept.getY() + 23, editAnchorTitleBar.getX() + editAnchorTitleBar.getWidth() + 4, btnAccept.getY() + 24, whiteBG);//light bottom background border
            fill(stack, x - 6, y - 4, editAnchorTitleBar.getX() + editAnchorTitleBar.getWidth() + 3, btnAccept.getY() + 23, darkBG);//dark background
            font.draw(stack, Component.translatable("main_screen.side_screen.title"), x - 2, y + 1, whiteText);
            font.draw(stack, Component.translatable("main_screen.side_screen.radius"), x - 2, editRadBar.getY() + 1, whiteText);
        }
    }

    private void renderButtonTooltip(PoseStack stack, Component message, AbstractWidget button) {
        int centeredMessageX = button.getX() - (font.width(message) / 2);
        int centeredMessageY = button.equals(btnPrevSounds) || button.equals(btnNextSounds) ? button.getY() - 1 : button.getY() + button.getHeight() + 16;
        renderTooltip(stack, message, centeredMessageX, centeredMessageY);
    }

    private void renderTips(PoseStack stack) {
        if (CommonConfig.get().showTip().get()) {
            if (index % 500 == 0) {
                tip = Component.translatable(Tips.randomTip());
                index = 0;
            }
            int h = font.lineHeight * ((font.width(Component.translatable("main_screen.tip", tip)) / 240) + 1) + 215;
            fill(stack, getX() - 2, getY() + 208, getX() + 257, getY() + h + 2, darkBG); //outer dark bg
            fill(stack, getX() - 1, getY() + 209, getX() + 256, getY() + h + 1, goldBG); //middle gold bg
            fill(stack, getX(), getY() + 210, getX() + 255, getY() + h, darkBG); //inner dark bg
            font.drawWordWrap(stack, Component.translatable("main_screen.tip", tip), getX() + 5, getY() + 213, 245, whiteText);
            index++;
        }
    }

    //----------------------------------- Other functions -----------------------------------//

    private void hideSideButtons() {
        //hide buttons when no anchor
        btnSetAnchor.active = false;
        btnEditAnchor.active = false;
        editAnchorTitleBar.visible = false;
        editRadBar.visible = false;
        btnAccept.visible = false;
        btnCancel.visible = false;
    }

    private void editTitle() {
        editAnchorTitleBar.setValue(anchor.getName());
        editAnchorTitleBar.visible = !editAnchorTitleBar.visible;

        editRadBar.setValue(String.valueOf(anchor.getRadius()));
        editRadBar.visible = !editRadBar.visible;

        btnAccept.visible = !btnAccept.visible;
        btnCancel.visible = !btnCancel.visible;

        editRadBar.setTextColor(whiteText);
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

    public static boolean isMuffling() {
        return isMuffling;
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}
