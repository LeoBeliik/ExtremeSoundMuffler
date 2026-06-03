package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMAnchor;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMButton;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMTab;
import com.leobeliik.extremesoundmuffler.gui.buttons.soundSlider.ESMSlider;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.*;

//TODO make it remember where it was when screen resized/reopen
public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

	private static final Minecraft minecraft = Minecraft.getInstance();
	private boolean isAnchorScreen, isAnchorList, isResize;
	private final CommonConfig.ConfigAccess cfg = CommonConfig.get();
	private static boolean isMuffling;
	private static Component toggleSoundsListMessage, screenTitle, tip;
	private final int maxAnchorRange = cfg.maxAnchorRange().get(), ySize = 211, xSize = 256;
	private final boolean isAnchorsDisabled = cfg.disableAnchors().get() || Constants.isCustomSkinLoader, isLawful = cfg.lawfulAllList().get(), leftButtons = cfg.leftButtons().get(), showShamelessPlug = cfg.showTip().get();
	private int minYButton, maxYButton, index, minYAnchorButton, maxYAnchorButton;
	private List<AbstractWidget> newAnchorScreenButtons = new ArrayList<>(9), anchorButtonsList = new ArrayList<>();
	private String tabCurrent = "general", btnCurrent = "recent";
	private Anchor anchor, tempAnchor;
	private EditBox barSearch, barAnchorName, barAnchorX, barAnchorY, barAnchorZ, barAnchorDim, barAnchorRange;
	private ESMTab tabGeneral, tabAnchors;
	private ESMSlider firstSoundButton, lastSoundButton;
	private ESMAnchor firstAnchorButton, lastAnchorButton;
	private ESMButton btnTMS, btnDelete, btnNextSounds, btnPrevSounds, btnRecent, btnAll;
	private ESMButton btnAnchorNew, btnAnchorEdit, btnAnchorList, btnAnchorPickCoords, btnAnchorDelete;
	private Button btnAccept, btnCancel;
	public ESMButton btnMuffled, btnMods, btnBlocks, btnGlobal;

	//TODO check if customskinloader still breaks the anchor loading and add tooltip
	private MufflerScreen(Component title, Anchor anchor) {
		super(title);
		screenTitle = title;
		this.isAnchorScreen = false;
		minecraft.setScreen(this);
		setNewAnchorButtons();
	}

	@Override
	protected void init() {
		super.init();

		minYButton = getY() + 55;
		maxYButton = getY() + 174;
		minYAnchorButton = getY() + 33;
		maxYAnchorButton = getY() + 180;

		if (!cfg.disableAnchors().get()) {
			addTabs();
		}
		addButtons();
		if (!cfg.disableAnchors().get()) {
			addAnchorButtons();
		}

		addSoundListButtons();
		//setNewAnchorButtons();
		postInit(false);
	}

	@Override
	public void resize(int width, int height) {
		Anchor tempAnchor = anchor;
		String currentButton = btnCurrent;
		String currentTab = tabCurrent;
		String searchBar = barSearch.getValue();
		String anchorBarName = barAnchorName.getValue();
		String anchorBarX = barAnchorX.getValue();
		String anchorBarY = barAnchorY.getValue();
		String anchorBarZ = barAnchorZ.getValue();
		String anchorBarDim = barAnchorDim.getValue();
		String anchorBarRange = barAnchorRange.getValue();
		boolean searchBarFocus = barSearch.isFocused();
		boolean anchorScreenOn = isAnchorScreen;
		boolean isEdit = btnAnchorEdit.isSelected();
		toggleAnchorScreen(false);

		super.resize(width, height);

		this.anchor = tempAnchor;
		tabCurrent = currentTab;
		btnCurrent = currentButton;
		barSearch.setValue(searchBar);

		postInit(true);

		btnAnchorEdit.selected(isEdit);
		toggleAnchorScreen(anchorScreenOn);

		barAnchorName.setValue(anchorBarName);
		barAnchorX.setValue(anchorBarX);
		barAnchorY.setValue(anchorBarY);
		barAnchorZ.setValue(anchorBarZ);
		barAnchorDim.setValue(anchorBarDim);
		barAnchorRange.setValue(anchorBarRange);
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
			screenTitle = Component.translatable("main_screen.main_title");
			stack.drawCenteredString(font, screenTitle, getX() + 128, getY() + 22, whiteText);
		} else if (tabAnchors.isSelected()) {
			screenTitle = anchorList.isEmpty() ? Component.translatable("main_screen.main_title.no_anchors") : Component.nullToEmpty(anchorList.get(0).getName());

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

		//render a separator for the selected anchor and the anchor list dropdown and a bottom for the list
		if (btnAnchorList.isToggled()) {
			//top
			stack.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX() + 17, getY() + 32, 0, 101, 222, 1, 256, 256);
			//bottom
			stack.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX() + 17, Math.min(getY() + 183, anchorButtonsList.getLast().getY() + 15), 0, 100, 222, 1, 256, 256);
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
		//Search bar, Edit title bar & Edit Anchor Radius bar looses focus when pressed "Enter" or "Intro"
		if (keyEvent.key() == 257 || keyEvent.key() == 335) {
			barSearch.setFocused(false);
			barAnchorName.setFocused(false);
			return true;
		}
		//Close screen when press "E" or the mod hotkey outside the search bar and when this screen is focused
		if ((minecraft.options.keyInventory.matches(keyEvent) || Constants.soundMufflerKey.matches(keyEvent))) {
			if (isAnchorList) {
				btnAnchorList.toggle();
				isAnchorList = false;
				toggleButtons(!btnAnchorList.isToggled());
				setAnchorListButtons();
			} else if (noBarsFocused()) {
				this.onClose();
			}
			return true;
		}
		return super.keyPressed(keyEvent);
	}

	private boolean noBarsFocused() {
		return !barSearch.isFocused() &&
				!barAnchorName.isFocused() &&
				!barAnchorX.isFocused() &&
				!barAnchorY.isFocused() &&
				!barAnchorZ.isFocused() &&
				!barAnchorDim.isFocused() &&
				!barAnchorRange.isFocused();
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

		if (anchor != null && btnAnchorList.isToggled()) {
			if (firstAnchorButton == null) {
				return false;
			}
			if ((dir > 0 && firstAnchorButton.getY() == minYAnchorButton) || (dir < 0 && lastAnchorButton.getY() <= maxYAnchorButton)) {
				return false;
			} //TODO add a slider and make it dragable
			children().stream().filter(b -> b instanceof ESMAnchor).map(b -> (ESMAnchor) b).forEach(b -> {
				b.setY((int) (b.getY() + b.getHeight() * Mth.clamp(dir, -1, 1)));
				b.setVisible(b.getY() >= minYAnchorButton && b.getY() <= maxYAnchorButton);
			});
		} else {
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
		}
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

	private void postInit(boolean resize) {
		swapTabs(tabCurrent.equals("anchor") ? tabAnchors : tabGeneral);
		switch (btnCurrent) {
			case "recent":
				setSelected(btnRecent);
				break;
			case "all":
				setSelected(btnAll);
				break;
			case "muffled":
				setSelected(btnMuffled);
				break;
			case "blocks":
				setSelected(btnBlocks);
				break;
			case "mods":
				setSelected(btnMods);
				break;
		}
	}

	//----------------------------------- Buttons init -----------------------------------//

	private void addTabs() {
		addRenderableWidget(tabGeneral = new ESMTab(getX() + 34, getY(), Component.translatable("main_screen.tab.general"), b -> {
			DataManager.saveData();
			swapTabs((ESMTab) b);
		})).show();
		addRenderableWidget(tabAnchors = new ESMTab(getX() + 136, getY(), Component.translatable("main_screen.tab.anchors"), b -> {
			DataManager.saveData();
			swapTabs((ESMTab) b);
		}));
	}


	private void swapTabs(ESMTab tab) {
		tabGeneral.hide();
		tabAnchors.hide();

		tab.show();

		if (tabGeneral.isSelected()) {
			btnGlobal.show();
			btnAnchorNew.hide();
			btnAnchorList.hide();
			btnAnchorEdit.hide();
			btnAnchorDelete.hide();
			this.anchor = null;
			tabCurrent = "general";
		}
		if (tabAnchors.isSelected()) {
			btnGlobal.hide();
			btnAnchorNew.show();
			updateAnchorButtons();
			tabCurrent = "anchor";
		}

		updateButtons();
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
		addRenderableWidget(barSearch = new EditBox(font, getX() + 44, getY() + 193, 128, 13, Component.empty())).setBordered(false);
		barSearch.setMaxLength(20);
		barSearch.setHint(Component.translatable("main_screen.searchbar.hint"));

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
			if (btnRecent.isSelected()) {
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

		//TODO move this to a switch case in java 23+
		if (btnRecent.isSelected()) btnCurrent = "recent";
		else if (btnAll.isSelected()) btnCurrent = "all";
		else if (btnMuffled.isSelected()) btnCurrent = "muffled";
		else if (btnBlocks.isSelected()) btnCurrent = "blocks";
		else if (btnMods.isSelected()) btnCurrent = "mods";

		updateDeleteButtonTooltip();
	}

	private void addAnchorButtons() {
		addRenderableWidget(btnAnchorList = new ESMButton(getX() + 20, getY() + 20, 66, 47, 11, b -> {
			((ESMButton) b).toggle();
			setAnchorListButtons();

			if (((ESMButton) b).isToggled()) {
				firstAnchorButton = (ESMAnchor) anchorButtonsList.getFirst();
				lastAnchorButton = (ESMAnchor) anchorButtonsList.getLast();
			}

		}, Component.translatable("main_screen.btn.anchor_list.tooltip"))).hide();

		addRenderableWidget(btnAnchorNew = new ESMButton(getX() + 33, getY() + 20, 99, 47, 11, b -> {
			toggleAnchorScreen(true);
		}, Component.translatable("main_screen.btn.anchor_new.tooltip"))).hide();

		addRenderableWidget(btnAnchorEdit = new ESMButton(getX() + 214, getY() + 20, 88, 47, 11, b -> {
			((ESMButton) b).selected(true);
			updateAnchor();
		}, Component.translatable("main_screen.btn.anchor_edit.tooltip"))).hide();

		addRenderableWidget(btnAnchorDelete = new ESMButton(getX() + 226, getY() + 20, 162, 47, 11, b -> {
			anchorList.remove(this.anchor);
			setAnchorListButtons();
			updateAnchorButtons();
		}, Component.translatable("main_screen.btn.anchor_delete.tooltip"))).hide();

	}

	public void updateAnchorButtons() {
		boolean empty = anchorList.isEmpty();
		this.anchor = empty ? null : anchorList.getFirst();
		btnAnchorList.setVisible(anchorList.size() > 1);
		btnAnchorEdit.visible = !empty;
		btnAnchorDelete.visible = !empty;
	}

	public void setAnchorListButtons() {
		if (btnAnchorList.isToggled()) {
			isAnchorList = true;
			toggleButtons(false);
			int y = 33;
			for (int i = 1, anchorListSize = anchorList.size(); i < anchorListSize; i++) {
				Anchor firstAnchor = anchorList.get(i);
				anchorButtonsList.add(new ESMAnchor(getX() + 17, getY() + y, anchorButton -> {
					//DataManager.saveData();
					Anchor selectedAnchor = ((ESMAnchor) anchorButton).getAnchor();
					if (anchorList.remove(selectedAnchor)) {
						anchorList.addFirst(selectedAnchor);
					}
					this.anchor = selectedAnchor;
					btnAnchorList.setToggle(false);
					isAnchorList = false;
					toggleButtons(!btnAnchorList.isToggled());
					setAnchorListButtons();
					updateButtons();
				}, firstAnchor, anchorList.size() > 11));
				y += 15;
			}
			// Only show 10 anchors
			for (AbstractWidget widget : anchorButtonsList) {
				var btn = ((ESMAnchor) widget);
				addRenderableWidget(btn).setVisible(widget.getY() >= minYAnchorButton && widget.getY() <= maxYAnchorButton);
			}
		} else {
			isAnchorList = false;
			firstAnchorButton = null;
			lastAnchorButton = null;
			toggleButtons(true);

			anchorButtonsList.removeIf(ESMAnchor -> {
				// remove all the buttons as the widget is closed now
				removeWidget(ESMAnchor);
				return true;
			});
		}
	}

	private void setNewAnchorButtons() {
		//anchor name bar
		addRenderableWidget(barAnchorName = new EditBox(font, (int) (getXd() + 94.5), (int) (getYd() + 57.5), 100, 13, Component.empty())).visible = isAnchorScreen;
		barAnchorName.setMaxLength(20);
		barAnchorName.setHint(Component.translatable("new_anchor.namebar.hint"));

		int coordBarsX = 77;
		//anchor X bar
		addRenderableWidget(barAnchorX = new EditBox(font, (int) (getXd() + 76.5), (int) (getYd() + 73.5), 66, 13, Component.empty())).visible = isAnchorScreen;
		//only accepts numbers
		barAnchorX.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
		barAnchorX.setMaxLength(8);
		barAnchorX.setHint(Component.translatable("new_anchor.xbar.hint"));

		//anchor Y bar
		addRenderableWidget(barAnchorY = new EditBox(font, (int) (getXd() + 76.5), (int) (getYd() + 89.5), 66, 13, Component.empty())).visible = isAnchorScreen;
		//only accepts numbers
		barAnchorY.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
		barAnchorY.setMaxLength(4);
		barAnchorY.setHint(Component.translatable("new_anchor.ybar.hint"));

		//anchor Z bar
		addRenderableWidget(barAnchorZ = new EditBox(font, (int) (getXd() + 76.5), (int) (getYd() + 105.5), 66, 13, Component.empty())).visible = isAnchorScreen;
		//only accepts numbers
		barAnchorZ.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
		barAnchorZ.setMaxLength(8);
		barAnchorZ.setHint(Component.translatable("new_anchor.zbar.hint"));

		//anchor dimension bar
		addRenderableWidget(barAnchorDim = new EditBox(font, (int) (getXd() + 81.5), (int) (getYd() + 121.5), 117, 13, Component.empty())).visible = isAnchorScreen;
		barAnchorDim.setHint(Component.translatable("new_anchor.dimensionbar.hint"));

		//anchor range bar
		addRenderableWidget(barAnchorRange = new EditBox(font, (int) (getXd() + 106.5), (int) (getYd() + 139.5), 39, 13, Component.empty())).visible = isAnchorScreen;
		//only accepts numbers
		barAnchorRange.setFilter(s -> s.matches("[0-9-]*(?:[0-9]*)?"));
		barAnchorRange.setMaxLength(4);
		barAnchorRange.setHint(Component.translatable("new_anchor.rangebar.hint"));

		//anchor coords selector
		addRenderableWidget(btnAnchorPickCoords = new ESMButton((int) (getXd() + 160.5), (int) (getYd() + 83.5), 204, 0, 22, b -> {
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
		}, Component.translatable("new_anchor.btn.anchors.set"))).visible = isAnchorScreen;

		//accept and save anchor
		addRenderableWidget(btnAccept = Button.builder(Component.literal("✔").withStyle(ChatFormatting.GREEN), b -> {
			boolean newAnchor = newAnchorScreenButtons.stream().noneMatch(widget -> widget instanceof EditBox box && box.getValue().isEmpty());

			if (newAnchor) {
				if (btnAnchorEdit.isSelected()) {
					this.anchor.editAnchor(barAnchorName.getValue(),
							new BlockPos(Integer.parseInt(barAnchorX.getValue()), Integer.parseInt(barAnchorY.getValue()), Integer.parseInt(barAnchorZ.getValue())),
							Identifier.tryParse(barAnchorDim.getValue()),
							Mth.clamp(Integer.parseInt(barAnchorRange.getValue()), 1, maxAnchorRange));
					btnAnchorEdit.selected(false);
					toggleAnchorScreen(false);
					updateAnchorButtons();
				} else {
					Anchor anchor = new Anchor(barAnchorName.getValue(),
							new BlockPos(Integer.parseInt(barAnchorX.getValue()), Integer.parseInt(barAnchorY.getValue()), Integer.parseInt(barAnchorZ.getValue())),
							Identifier.tryParse(barAnchorDim.getValue()),
							Mth.clamp(Integer.parseInt(barAnchorRange.getValue()), 1, maxAnchorRange));
					System.out.println(anchor.getDimension());

					if (anchorList.stream().noneMatch(a -> a.getName().equals(anchor.getName()))) {
						anchorList.add(anchor);
						toggleAnchorScreen(false);
						updateAnchorButtons();
					} else {
						System.out.println("NAME IN USE");
					}
				}
			} else {
				System.out.println("FILL THE THING OR CANCEL!"); //TODO replace all the souts with actual warnings/errors
			}
		}).bounds((int) (getXd() + 153.5), (int) (getYd() + 137.5), 17, 17).build()).visible = isAnchorScreen;
		btnAccept.setTooltip(Tooltip.create(Component.translatable("new_anchor.btn.accept")));

		//cancel and forget anchor
		addRenderableWidget(btnCancel = Button.builder(Component.literal("✘").withStyle(ChatFormatting.RED), b -> {
			toggleAnchorScreen(false);
		}).bounds((int) (getXd() + 173.5), (int) (getYd() + 137.5), 17, 17).build()).visible = isAnchorScreen;
		btnCancel.setTooltip(Tooltip.create(Component.translatable("new_anchor.btn.cancel")));

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

	public void updateAnchor() {
		toggleAnchorScreen(true);
		barAnchorName.setValue(anchor.getName());
		barAnchorX.setValue(anchor.getX());
		barAnchorY.setValue(anchor.getY());
		barAnchorZ.setValue(anchor.getZ());
		barAnchorDim.setValue(anchor.getDimension().toString());
		barAnchorRange.setValue(String.valueOf(anchor.getRange()));
	}

	private void toggleButtons(boolean active) {
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
			soundsList.addAll(this.anchor != null ? this.anchor.getMuffledSounds().keySet() : muffledSounds.keySet());
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

			if (tabAnchors.isSelected()) { //TODO LOOK AT **ALL** the places where the blocks sounds should be consider!!!
				if (this.anchor != null && this.anchor.getMuffledSounds().containsKey(sound.toLowerCase(Locale.ROOT)))
					volume = this.anchor.getMuffledSounds().getOrDefault(sound.toLowerCase(Locale.ROOT), 1.0);
			} else if (btnBlocks.isSelected() && muffledBlocks.contains(sound)) {
				//get one of the sounds of the block just to have the slider in the correct position
				volume = blocksList.stream().filter(block ->
								block.getName().getString().equals(sound)).findFirst().map(block ->
								muffledSounds.getOrDefault(block.defaultBlockState().getSoundType().getBreakSound().location().toString(), 1D))
						.orElse(1D);
			} else if (btnMods.isSelected() && muffledSounds.containsKey(sound)) {
				volume = muffledSounds.getOrDefault(sound.toLowerCase(Locale.ROOT), 1.0);
			} else {
				volume = muffledSounds.getOrDefault(sound.toLowerCase(Locale.ROOT), 1.0);
			}

			//row highlight
			int bg = children().size() % 2 == 0 ? darkBG : brightBG;

			ESMSlider btnSound = new ESMSlider(bx, by, bg, sound, volume, this);
			setFGColor(btnSound, "white");

			if (tabAnchors.isSelected()) {
				if (this.anchor != null && this.anchor.getMuffledSounds().containsKey(sound.toLowerCase(Locale.ROOT)))
					setFGColor(btnSound, "green");
			} else if (!muffledSounds.isEmpty()) {
				if (btnBlocks.isSelected() && muffledBlocks.contains(sound) ||
						muffledSounds.containsKey(sound.toLowerCase(Locale.ROOT)) ||
						btnMods.isSelected() && muffledSounds.containsKey(sound.toLowerCase(Locale.ROOT)))
					setFGColor(btnSound, "green");
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
		int xras = (width - 153) / 2;
		stack.blit(RenderPipelines.GUI_TEXTURED, getAnchorScreenTextureID(), xras, (height - 118) / 2, 0, 0, 153, 118, 256, 256);
		stack.drawString(font, Component.translatable("new_anchor.namebar"), xras + 10, barAnchorName.getY() + 2, blackText, false);
		stack.drawString(font, Component.translatable("new_anchor.xbar"), xras + 10, barAnchorX.getY() + 2, blackText, false);
		stack.drawString(font, Component.translatable("new_anchor.ybar"), xras + 10, barAnchorY.getY() + 2, blackText, false);
		stack.drawString(font, Component.translatable("new_anchor.zbar"), xras + 10, barAnchorZ.getY() + 2, blackText, false);
		stack.drawString(font, Component.translatable("new_anchor.dimensionbar"), xras + 10, barAnchorDim.getY() + 2, blackText, false);
		stack.drawString(font, Component.translatable("new_anchor.rangebar"), xras + 10, barAnchorRange.getY() + 2, blackText, false);
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

	public boolean removeModsMuffled(String sound) {
        /*if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.removeSound(sound);
                return true;
            }
        } else */
		{
			muffledSounds.remove(sound);
			//muffledMods.remove(sound);
			return true;
		}
		//return false;
	}

	public boolean addModsMuffled(String sound, double volume) {
        /*if (this.anchor != null) {
            if (anchor.getAnchorPos() != null) {
                this.anchor.addSound(sound, volume);
                return true;
            }
        } else*/
		{
			muffledSounds.put(sound, volume);
			//muffledMods.put(sound, volume);
			return true;
		}
		//return false;
	}

	public void replaceVolume(String sound, double volume) {
		if (this.anchor != null) {
			this.anchor.replaceSound(sound, volume);
		} else if (btnBlocks.isSelected()) { //TODO this should consider anchors TOO!
			Constants.loadBlockSounds(sound).forEach(s -> muffledSounds.replace(s, volume));
		} else {
			/*if (btnMods.isSelected()) {
				//muffledMods.replace(sound, volume);
				muffledSounds.replace(sound, volume);
			}*/
			muffledSounds.replace(sound, volume);
		}

	}

	public static boolean isMuffling() {
		return isMuffling;
	}

	public static void setMuffling(boolean muffling) {
		isMuffling = muffling;
	}

	private double getXd() {
		return (this.width - xSize) / 2D;
	}

	private double getYd() {
		return (this.height - ySize) / 2D;
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
