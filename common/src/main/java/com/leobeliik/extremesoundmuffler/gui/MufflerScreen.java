package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMAnchor;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMButton;
import com.leobeliik.extremesoundmuffler.gui.buttons.slider.ESMSlider;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMTab;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.function.Predicate;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.*;

public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    static final Minecraft minecraft = Minecraft.getInstance();
    public boolean isAnchorScreen, isAnchorList;
    private final CommonConfig.ConfigAccess cfg = CommonConfig.get();
    private static boolean isMuffling;
    private static Component toggleSoundsListMessage, screenTitle, tip;
    private final int maxAnchorRange = cfg.maxAnchorRange().get(), ySize = 211, xSize = 256;
    private final boolean isAnchorsDisabled = cfg.disableAnchors().get() || Constants.isCustomSkinLoader, isLawful = cfg.lawfulAllList().get(), leftButtons = cfg.leftButtons().get(), showShamelessPlug = cfg.showTip().get();
    private int minYButton, maxYButton, index;
    private List<AbstractWidget> newAnchorScreenButtons = new ArrayList<>(9), anchorButtonsList = new ArrayList<>();
    private Anchor anchor, tempAnchor;
    private EditBox barSearch, barAnchorName, barAnchorX, barAnchorY, barAnchorZ, barAnchorDim, barAnchorRange;
    private ESMTab tabGeneral, tabAnchors;
    private ESMSlider firstSoundButton, lastSoundButton;
    private ESMButton btnTMS, btnDelete, btnNextSounds, btnPrevSounds, btnRecent, btnAll, btnNewAnchor, btnAnchorEdit, btnAnchorList, btnAnchorPickCoords;
    private Button btnAccept, btnCancel;
    public ESMButton btnMuffled, btnMods, btnBlocks, btnGlobal;
//TODO check if customskinloader still breaks the anchor loading and add tooltip
    private MufflerScreen(Component title, Anchor anchor) {
        super(title);
        screenTitle = title;
        this.isAnchorScreen = false;
        minecraft.setScreen(this);
    }

    @Override
    protected void init() {
        super.init();
        minYButton = getY() + 55;
        maxYButton = getY() + 174;

        if (!cfg.disableAnchors().get()) {
            addTabs();
        }
        addButtons();
        setSelected(btnRecent); //REMEMBER WHAT WAS SELECTED??? TODO
        if (!cfg.disableAnchors().get()) {
            addAnchorButtons();
        }

        addSoundListButtons();
        setNewAnchorButtons();
    }

    @Override
    public void render(@NotNull GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
        stack.blit(RenderPipelines.GUI_TEXTURED, getMainScreenTextureID(), getX(), getY(), 0, 0, xSize, ySize, 256, 256); //Main screen bounds
        if (cfg.disableAnchors().get() || tabGeneral.isSelected()) {
            //render backgroung behind the local/global muffling config
            stack.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), btnGlobal.getX() - 2, btnGlobal.getY() - 2, 95, 13, 15, 15, 256, 256);
        }

        super.render(stack, mouseX, mouseY, partialTicks);

        //--------------- My Renders ---------------//
        //Screen title
        if (cfg.disableAnchors().get() || tabGeneral.isSelected()) {
            stack.drawCenteredString(font, screenTitle, getX() + 128, getY() + 22, whiteText);
        } else if (tabAnchors.isSelected()) {
            System.out.println(anchorList + " --------------------------------");
            screenTitle = anchorList.isEmpty() ? Component.translatable("main_screen.msg.no_anchors") : Component.nullToEmpty(anchorList.get(0).getName());

            stack.drawCenteredString(font, screenTitle, getX() + 128, getY() + 22, whiteText);
        }

        //if we are at the start don't try going back and disable the button
        renderNavButtons(btnPrevSounds, firstSoundButton == null || firstSoundButton.getY() == minYButton);

        //if we are at the end don't try going forward and disable the button
        renderNavButtons(btnNextSounds, lastSoundButton == null || lastSoundButton.getY() <= maxYButton);

        //render anchor fake "screen"
        if (isAnchorScreen) {
            renderAnchorScreen(stack, mouseX, mouseY);
        }

        //render the tips on the bottom of the screen
        //renderTips(stack);
        //render buttons tips and other textures
        //renderButtons(stack, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent keyEvent) {
        //Radius only accepts numbers
        //editRadBar.setFilter(s -> s.matches("[0-9]*(?:[0-9]*)?"));

        //Search bar, Edit title bar & Edit Anchor Radius bar looses focus when pressed "Enter" or "Intro"
        if (keyEvent.key() == 257 || keyEvent.key() == 335) {
            barSearch.setFocused(false);
            return true;
        }
        //Close screen when press "E" or the mod hotkey outside the search bar and when this screen is focused
        if ((minecraft.options.keyInventory.matches(keyEvent) || Constants.soundMufflerKey.matches(keyEvent))) {
            if (isAnchorScreen) { //TODO check if all the bars are not in focus
                toggleAnchorScreen(false);
            } else if (isAnchorList) {
                btnAnchorList.toggle();
                isAnchorList = false;
                toggleButtons(!btnAnchorList.isToggled());
                setAnchorListButtons();
            } else if (!barSearch.isFocused()) {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent keyEvent) {
        if (barSearch.isFocused()) {
            updateButtons();
        }
        /*if (!editRadBar.getValue().isEmpty()) {
            int radius = Integer.parseInt(editRadBar.getValue());
            editRadBar.setTextColor(radius > maxAnchorRange || radius < 1 ? redText : whiteText);
        } else {
            editRadBar.setTextColor(whiteText);
        }*/
        return super.keyReleased(keyEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double directionH, double directionV) {
        double dir = directionH == 0 ? directionV : directionH;
        if (firstSoundButton == null) {
            return false;
        }

        if ((dir > 0 && firstSoundButton.getY() == minYButton) || (dir < 0 && lastSoundButton.getY() <= maxYButton)) {
            return false;
        }
        children().stream().filter(b -> b instanceof ESMSlider).map(b -> (ESMSlider) b).forEach(b -> {
            //only increase / decrease from 10 to 10 to prevent the sliders going further than they should
            b.setY((int) (b.getY() + (b.getHeight() * 10) * Mth.clamp(dir, -1, 1)));
            b.isVisible(b.getY() >= minYButton && b.getY() <= maxYButton);
        });
        return super.mouseScrolled(mouseX, mouseY, directionH, directionV);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean success) {
        //right click
        if (mouseButtonEvent.button() == 1) {
            if (barSearch.isFocused()) {
                barSearch.setValue("");
                updateButtons();
                return true;
            }
            /*if (editAnchorTitleBar.isFocused()) {
                //editAnchorTitleBar.setValue("");
                return true;
            }*/
            /*if (editRadBar.isHoveredOrFocused()) {
                editRadBar.setValue("");
                return true;
            }*/
        } else {
            barSearch.setFocused(barSearch.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y()));
            //editAnchorTitleBar.setFocused(editAnchorTitleBar.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y()));
            //editRadBar.setFocused(editRadBar.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y()));
        }

        return super.mouseClicked(mouseButtonEvent, success);
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

    private void addTabs() {
        addRenderableWidget(tabGeneral = new ESMTab(getX() + 34, getY(), Component.translatable("main_screen.tab.general"), b -> {
            swapTabs((ESMTab) b);
            btnGlobal.show();
            btnNewAnchor.hide();
            btnAnchorList.hide();
            btnAnchorEdit.hide();
        })).show();
        addRenderableWidget(tabAnchors = new ESMTab(getX() + 136, getY(), Component.translatable("main_screen.tab.anchors"), b -> {
            swapTabs((ESMTab) b);
            btnGlobal.hide();
            btnNewAnchor.show();
            if (!anchorList.isEmpty()) {
                btnAnchorList.show();
                btnAnchorEdit.show();
            }
        }));
    }


    private void swapTabs(ESMTab tab) {
        tabGeneral.hide();
        tabAnchors.hide();

        tab.show();
    }

    private void addButtons() {
        addRenderableWidget(btnGlobal = new ESMButton(getX() + 233, getY() + 187, 0, 47, 11, b -> {
            ((ESMButton) b).toggle();
            ((ESMButton) b).setTooltip(((ESMButton) b).isToggled() ?
                    Component.translatable("main_screen.btn.local.tooltip") : Component.translatable("main_screen.btn.global.tooltip"));
            updateButtons();
        }, Constants.useGlobalConfig ? Component.translatable("main_screen.btn.local.tooltip") : Component.translatable("main_screen.btn.global.tooltip")));
        btnGlobal.setToggle(Constants.useGlobalConfig);
        btnGlobal.visible = tabGeneral.isSelected();

        //Searchbar TODO look what's going on with the cursor
        addRenderableWidget(barSearch = new EditBox(font, getX() + 43, getY() + 193, 128, 13, Component.translatable("main_screen.searchbar.hint"))).setBordered(false);
        barSearch.setMaxLength(20);

        //toggle muffling sounds on/off
        addRenderableWidget(btnTMS = new ESMButton(getX() + 210, getY() + 189, 0, 13, 17, b -> {
            ((ESMButton) b).toggle();
            setMuffling(!isMuffling);
            ((ESMButton) b).setTooltip(getTMSTooltip());
        }, getTMSTooltip())).setToggle(!isMuffling);

        //backwards list of sounds
        addRenderableWidget(btnPrevSounds = new ESMButton(getX() + 12, getY() + 187, 22, 47, 11, b ->
                mouseScrolled(0D, 0D, 1D, 0D), Component.translatable("main_screen.btn.previous_sounds.tooltip")));
        //forward list of sounds
        addRenderableWidget(btnNextSounds = new ESMButton(getX() + 24, getY() + 187, 44, 47, 11, b ->
                mouseScrolled(0D, 0D, -1D, 0D), Component.translatable("main_screen.btn.next_sounds.tooltip")));

        //deletes current muffled list or the recent list if shifting
        addRenderableWidget(btnDelete = new ESMButton(getX() + 189, getY() + 189, 34, 13, 17, b -> {
            if (tabAnchors.isSelected()) {
                System.out.println(anchor.getName());
                anchorList.remove(anchor);
                setAnchorListButtons();
            } else if (btnRecent.isSelected()) {
                recentSoundsList.clear();
            } else {
                muffledSounds.clear();
                muffledBlocks.clear();
                DataManager.saveData();
            }
            updateButtons();
        }, Component.empty()));

        int smallTabsXTexture = 51, smallTabsYTexture = 13;
        //Recent sounds button
        addRenderableWidget(btnRecent = new ESMButton(getX() + 12, getY() + 38, smallTabsXTexture, smallTabsYTexture, 44, 13, Component.translatable("main_screen.btn.recent"), b -> {
            setSelected((ESMButton) b);
            updateButtons();
        }, Component.translatable("main_screen.btn.recent.tooltip")));

        //All sounds button
        addRenderableWidget(btnAll = new ESMButton(getX() + 59, getY() + 38, smallTabsXTexture, smallTabsYTexture, 44, 13, Component.translatable("main_screen.btn.all"), b -> {
            setSelected((ESMButton) b);
            updateButtons();
        }, Component.translatable("main_screen.btn.all.tooltip")));

        //Muffled sounds button
        addRenderableWidget(btnMuffled = new ESMButton(getX() + 106, getY() + 38, smallTabsXTexture, smallTabsYTexture, 44, 13, Component.translatable("main_screen.btn.muffled"), b -> {
            setSelected((ESMButton) b);
            updateButtons();
        }, Component.translatable("main_screen.btn.muffled.tooltip")));

        //Mods list button
        addRenderableWidget(btnMods = new ESMButton(getX() + 153, getY() + 38, smallTabsXTexture, smallTabsYTexture, 44, 13, Component.translatable("main_screen.btn.mods"), b -> {
            setSelected((ESMButton) b);
            updateButtons();
        }, Component.translatable("main_screen.btn.mods.tooltip")));

        //Blocks list button
        addRenderableWidget(btnBlocks = new ESMButton(getX() + 200, getY() + 38, smallTabsXTexture, smallTabsYTexture, 44, 13, Component.translatable("main_screen.btn.blocks"), b -> {
            setSelected((ESMButton) b);
            updateButtons();
        }, Component.translatable("main_screen.btn.blocks.tooltip")));
    }

    private Component getTMSTooltip() {
        return isMuffling ?
                Component.translatable("main_screen.btn.tms.tooltip.stop") : Component.translatable("main_screen.btn.tms.tooltip.start");
    }

    private void updateDeleteButtonTooltip() {
        btnDelete.setTooltip(btnRecent.isSelected() ?
                Component.translatable("main_screen.btn.delete.recent.tooltip") : Component.translatable("main_screen.btn.delete.muffled.tooltip"));
    }

    private void setSelected(ESMButton button) {
        btnRecent.selected(false);
        btnAll.selected(false);
        btnMuffled.selected(false);
        btnMods.selected(false);
        btnBlocks.selected(false);

        button.selected(true);
        updateDeleteButtonTooltip();
    }
//TODO need to select anchor
    private void addAnchorButtons() {
        addRenderableWidget(btnAnchorList = new ESMButton(getX() + 20, getY() + 20, 66, 47, 11, b -> {
            ((ESMButton) b).toggle();
            setAnchorListButtons();
        }, Component.translatable("main_screen.btn.newanchor.tooltip"))).hide();

        addRenderableWidget(btnAnchorEdit = new ESMButton(getX() + 214, getY() + 20, 88, 47, 11, b ->
                System.out.println("EDIT ANCHOR"), Component.translatable("main_screen.btn.newanchor.tooltip"))).hide();

        addRenderableWidget(btnNewAnchor = new ESMButton(getX() + 226, getY() + 20, 99, 47, 11, b -> {
            toggleAnchorScreen(true);
        }, Component.translatable("main_screen.btn.newanchor.tooltip"))).hide();

    }

    private void setAnchorListButtons() {
        if (btnAnchorList.isToggled()) {
            isAnchorList = true;
            toggleButtons(false);
            int y = 33;
            for (int i = 1, anchorListSize = anchorList.size(); i < anchorListSize; i++) {
                Anchor a = anchorList.get(i);
                anchorButtonsList.add(new ESMAnchor(getX() + 17, getY() + y, 15, ab -> {
                    this.anchor = ((ESMAnchor) ab).getAnchor();
                    btnAnchorList.setToggle(false);
                    isAnchorList = false;
                    toggleButtons(!btnAnchorList.isToggled());
                    setAnchorListButtons();
                }, a));
                y += 15;
            }
            for (AbstractWidget widget : anchorButtonsList) {
                addRenderableWidget(widget).visible = widget.getY() <= 33 + 150;
            }
        } else {
            isAnchorList = false;
            toggleButtons(true);
            for (Iterator<? extends GuiEventListener> iterator = anchorButtonsList.iterator(); iterator.hasNext(); ) {
                ((AbstractWidget) iterator.next()).visible = false;
                iterator.remove();
            }
        }
    }

    private void setNewAnchorButtons() {

        //anchor name bar
        addRenderableWidget(barAnchorName = new EditBox(font, getX() + 107, getY() + 58, 87, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;
        barAnchorName.setMaxLength(20);

        int coordBarsX = 77;
        //anchor X bar
        addRenderableWidget(barAnchorX = new EditBox(font, getX() + 77, getY() + 74, 66, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;
        //only accepts numbers
        barAnchorX.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
        barAnchorX.setMaxLength(8);

        //anchor Y bar
        addRenderableWidget(barAnchorY = new EditBox(font, getX() + 77, getY() + 90, 66, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;
        //only accepts numbers
        barAnchorY.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
        barAnchorY.setMaxLength(4);

        //anchor Z bar
        addRenderableWidget(barAnchorZ = new EditBox(font, getX() + 77, getY() + 106, 66, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;
        //only accepts numbers
        barAnchorZ.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
        barAnchorZ.setMaxLength(8);

        //anchor dimension bar
        addRenderableWidget(barAnchorDim = new EditBox(font, getX() + 91, getY() + 122, 103, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;

        //anchor range bar
        addRenderableWidget(barAnchorRange = new EditBox(font, getX() + 107, getY() + 140, 39, 13, Component.translatable("main_screen.searchbar.hint"))).visible = false;
        //only accepts numbers
        barAnchorRange.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
        barAnchorRange.setMaxLength(4);

        //anchor coords selector x + 52 y + 47
        addRenderableWidget(btnAnchorPickCoords = new ESMButton(getX() + 161, getY() + 84, 0, 101, 22, b -> {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                var pos = player.blockPosition();
                barAnchorX.setValue(String.valueOf(pos.getX()));
                barAnchorY.setValue(String.valueOf(pos.getY()));
                barAnchorZ.setValue(String.valueOf(pos.getZ()));
                barAnchorDim.setValue(player.level().dimension().identifier().toString());
                barAnchorRange.setValue(String.valueOf(maxAnchorRange));
            } else {
                System.out.println("ERROR!");
            }
        }, Component.translatable("main_screen.btn.anchors.set"))).hide();

        //accept and save anchor
        addRenderableWidget(btnAccept = Button.builder(Component.literal("✔").withStyle(ChatFormatting.GREEN), b -> {
            boolean newAnchor = newAnchorScreenButtons.stream().noneMatch(widget -> widget instanceof EditBox box && box.getValue().isEmpty());

            if (newAnchor) {
                if (btnAnchorEdit.isToggled()) {
                    System.out.println("EDIT THE ANCHOR"); //fillAnchor
                } else {
                    Anchor anchor = new Anchor(barAnchorName.getValue(),
                            new BlockPos(Integer.parseInt(barAnchorX.getValue()), Integer.parseInt(barAnchorY.getValue()), Integer.parseInt(barAnchorZ.getValue())),
                            Identifier.tryParse(barAnchorDim.getValue()),
                            Mth.clamp(Integer.parseInt(barAnchorRange.getValue()), 1, maxAnchorRange));
                    System.out.println(anchor.getDimension());

                    if (anchorList.stream().noneMatch(a -> a.getName().equals(anchor.getName()))) {
                        anchorList.add(anchor);
                        toggleAnchorScreen(false);
                    } else {
                        System.out.println("NAME IN USE");
                    }
                }
            } else {
                System.out.println("FILL THE THING OR CANCEL!");
            }
        }).bounds(getX() + 154, getY() + 138, 17, 17).build()).visible = false;
        btnAccept.setTooltip(Tooltip.create(Component.translatable("main_screen.btn.accept")));

        //cancel and forget anchor
        addRenderableWidget(btnCancel = Button.builder(Component.literal("✘").withStyle(ChatFormatting.RED), b -> {
            this.tempAnchor = null;
            toggleAnchorScreen(false);
        }).bounds(getX() + 174, getY() + 138, 17, 17).build()).visible = false;
        btnCancel.setTooltip(Tooltip.create(Component.translatable("main_screen.btn.cancel")));

        newAnchorScreenButtons.clear();
        newAnchorScreenButtons.addAll(List.of(btnAnchorPickCoords, btnAccept, btnCancel, barAnchorName, barAnchorX, barAnchorY, barAnchorZ, barAnchorDim, barAnchorRange));
    }

    private void toggleAnchorScreen(boolean toggle) {
        this.isAnchorScreen = toggle;
        newAnchorScreenButtons.forEach(widget -> {
            if (widget instanceof EditBox box) box.setValue("");
            widget.visible = isAnchorScreen;
        });
        this.toggleButtons(!toggle);
    }

    private void updateAnchor(Anchor anchor) {
        double radius = anchor.getRadius();
        if (radius > maxAnchorRange) anchor.setRadius((int) Math.min(radius, maxAnchorRange));
        //this.anchor = anchor;
    }

    void toggleButtons(boolean active) {
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget btn && !newAnchorScreenButtons.contains(btn)) {
                btn.active = btn.equals(btnAnchorList) && ((ESMButton) btn).isToggled() || active;
            }
        }
    }

    private void addSoundListButtons() {
        int by = minYButton;
        //set x depending of config
        int bx = leftButtons ? getX() + 38 : getX() + 11;
        //easiest way to assure this is the first one
        firstSoundButton = null;
        lastSoundButton = null;
        soundsList.clear();

        //remove muffled blocks if all the sounds from said block are not muffled.
        if (!muffledBlocks.isEmpty()) {
            muffledBlocks.removeIf(muffledBlock -> !muffledSounds.keySet().containsAll(Constants.loadBlockSounds(muffledBlock)));
        }

        if (btnRecent.isSelected()) {
            soundsList.addAll(recentSoundsList);
        } else if (btnAll.isSelected()) {
            BuiltInRegistries.SOUND_EVENT.forEach(k -> soundsList.add(k.location().toString()));
        } else if (btnMuffled.isSelected()) {
            soundsList.addAll(muffledSounds.keySet());
        } else if (btnMods.isSelected()) {
            soundsList.addAll(modsList.keySet());
        } else if (btnBlocks.isSelected()) {
            soundsList.addAll(blocksList.stream().map(b -> b.getName().getString()).toList());
        }

        //removes blacklisted sounds when necessary
        if ((isLawful && btnAll.isSelected())) {
            forbiddenSounds.stream().<Predicate<? super String>>map(fs -> s -> s.contains(fs)).forEach(soundsList::removeIf);
        }

        if (soundsList.isEmpty()) {
            return;
        }

        if (btnRecent.isSelected())
            Collections.reverse(soundsList); //makes the recent sounds sort in chronological order
        else
            Collections.sort(soundsList); //makes the list sort in alphabetically order

        for (var sound : soundsList) {

            if (!sound.toLowerCase(Locale.ROOT).contains(barSearch.getValue().toLowerCase(Locale.ROOT))) {
                continue;
            }

            double volume = 1D;

            if (btnBlocks.isSelected()) {
                if (muffledBlocks.contains(sound)) {
                    //get one of the sounds of the block just to have the slider in the correct position
                    volume = blocksList.stream().filter(block ->
                                    block.getName().getString().equals(sound)).findFirst().map(block ->
                                    muffledSounds.getOrDefault(block.defaultBlockState().getSoundType().getBreakSound().location().toString(), 1D))
                            .orElse(1D);
                }
            } else {
                volume = muffledSounds.getOrDefault(sound.toLowerCase(Locale.ROOT), 1.0);
            }

            //row highlight
            int bg = children().size() % 2 == 0 ? darkBG : brightBG;

            ESMSlider btnSound = new ESMSlider(bx, by, bg, sound, volume, this);
            setFGColor(btnSound, "white");

            if (!muffledSounds.isEmpty()) {
                if (btnBlocks.isSelected() && muffledBlocks.contains(sound) || muffledSounds.containsKey(sound.toLowerCase(Locale.ROOT))) {
                    setFGColor(btnSound, "green");
                }
            } else {
                muffledBlocks.clear();
            }

            addRenderableWidget(btnSound);
            addRenderableWidget(btnSound.getBtnToggleSound());
            addRenderableWidget(btnSound.getBtnPlaySound());
            by += btnSound.getHeight();
            btnSound.isVisible(btnSound.getY() < maxYButton);
            if (firstSoundButton == null) {
                firstSoundButton = btnSound;
            }

            lastSoundButton = btnSound;
        }
    }

    public void updateButtons() {
        children().removeIf(child -> {
            if (child instanceof AbstractWidget widget) {
                if (widget instanceof ESMSlider button) {
                    button.isVisible(false);
                    return true;
                }
                return newAnchorScreenButtons.contains(widget);
            }
            return false;
        });
        addSoundListButtons();
        setNewAnchorButtons();
    }

    //----------------------------------- Rendering -----------------------------------//
    //render previous and next buttons textures depending on if they can be used
    private void renderNavButtons(ESMButton btn, boolean b) {
        if (btn == null) return;
        btn.setToggle(b);
        btn.canHover(!b);
        btn.active = !b;
    }

    private void renderAnchorScreen(GuiGraphics stack, int mouseX, int mouseY) {
        stack.blit(RenderPipelines.GUI_TEXTURED, getAnchorScreenTextureID(), (width - 153) / 2, (height - 118) / 2, 0, 0, 153, 118, 256, 256); //Main screen bounds
    }


    //----------------------------------- Other functions -----------------------------------//

    private void editTitle() {
        //editAnchorTitleBar.setValue(anchor.getName());
        barAnchorName.visible = !barAnchorName.visible;

        //editRadBar.setValue(String.valueOf(anchor.getRadius()));
        barAnchorX.visible = !barAnchorX.visible;

        /*btnAccept.visible = !btnAccept.visible;
        btnCancel.visible = !btnCancel.visible;*/

        barAnchorX.setTextColor(whiteText);
    }

    public boolean removeSoundMuffled(String sound) {
        /*if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.removeSound(sound);
                return true;
            }
        } else */
        {
            muffledSounds.remove(sound);
            return true;
        }
        //return false;
    }

    public boolean addSoundMuffled(String sound, double volume) {
        /*if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.addSound(sound, volume);
                return true;
            }
        } else*/
        {
            muffledSounds.put(sound, volume);
            return true;
        }
        //return false;
    }

    public void replaceVolume(String sound, double volume) {
        /*if (this.anchor != null) {
            this.anchor.replaceSound(sound, volume);
        } else */
        if (btnBlocks.isSelected()) {
            Constants.loadBlockSounds(sound).forEach(s -> muffledSounds.replace(s, volume));
        } else {
            muffledSounds.replace(sound, volume);
        }

    }

    public static boolean isMuffling() {
        return isMuffling;
    }

    public static void setMuffling(boolean muffling) {
        isMuffling = muffling;
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    /*public Component getBtnCSLTitle() {
        return btnCSL.getMessage();
    }*/
}
