package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.GradientButton;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.gui.buttons.MufflerListButton;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.mufflers.MufflerEntity;
import com.leobeliik.extremesoundmuffler.networking.Network;
import com.leobeliik.extremesoundmuffler.networking.PacketMufflers;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import com.leobeliik.extremesoundmuffler.utils.Tips;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ITextComponent mainTitle = ITextComponent.nullToEmpty("ESM - Main Screen");
    private final ITextComponent searchHint = (new TranslationTextComponent("gui.recipebook.search_hint")).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final int xSize = 256;
    private final int ySize = 202;
    private static boolean isPlayerMuffling = true;
    private int minYButton, maxYButton, minMufflerListY, maxMufflerListY;
    private boolean isMuffling, showMufflerList, showMuffled;
    private int radius;
    private ITextComponent tip = ITextComponent.nullToEmpty(Tips.randomTip());
    private BlockPos mufflerPos;
    private Widget btnToggle, btnAccept, btnCancel, btnMufflerList;
    private GradientButton btnRecent, btnAll, btnMuffled;
    private TextFieldWidget searchBar, rangeBar;
    private MuffledSlider firstSoundButton, lastSoundButton;
    private MufflerListButton firstMufflerListButton, lastMufflerListButton;
    private SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    private Map<ResourceLocation, Float> muffledList = new HashMap<>();

    private MufflerScreen(Map<ResourceLocation, Float> ms, BlockPos mufflerPos, int radius, boolean isMuffling, ITextComponent title) {
        super(title);
        this.mufflerPos = mufflerPos;
        this.radius = radius;
        this.isMuffling = isMuffling;
        muffledList.putAll(mufflerPos == null ? playerMuffledList : ms);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        showMuffled = mufflerPos != null && isMuffling || mufflerPos == null && isPlayerMuffling;
        this.renderBackground(ms);
        this.bindTexture();
        this.blit(ms, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        renderButtons(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderButtonDescriptions(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        minYButton = getY() + 37;
        maxYButton = getY() + 164;
        minMufflerListY = getY() + 25;
        maxMufflerListY = getY() + 160;

        //allows to hold a key to keep printing it. in this case i want it to easy erase text
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addButtons();
        btnRecent.setActive(true);
        addSoundButtons();
        rangeBar.setValue(String.valueOf(radius));
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Only numbers lower than 100
        rangeBar.setFilter(s -> s.matches("([0-9][0-9]?$|^100$)?"));
        if (!searchBar.isFocused() && !rangeBar.isFocused()) {
            //close screen when the inventory key or the mod hotkey is pressed
            if (minecraft.options.keyInventory.matches(keyCode, scanCode) || keyCode == SoundMuffler.getHotkey()) {
                this.onClose();
                return true;
            }
        }
        //Searchbar & Rangebar looses focus when pressed "Enter" or "Intro"
        if (keyCode == 257 || keyCode == 335) {
            updateButtons();
            searchBar.setFocus(false);
            setRange();
            rangeBar.setValue(String.valueOf(radius));
            rangeBar.setTextColor(whiteText);
            rangeBar.setFocus(false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (searchBar.isFocused()) {
            updateButtons();
        }
        if (rangeBar.isFocused()) {
            setRange();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        //getX(), getY() + 5, getX() - 102, getY() + 180
        if (mouseX > getX() - 102 && mouseX < getX() && mouseY > getY() + 5 && mouseY < getY() + 180 && firstMufflerListButton != null) {
            scrollMufflerListButtons((int) (direction * 15));
            return true;
        }
        if (firstSoundButton == null) {
            return false;
        }
        if (direction > 0 && firstSoundButton.y == minYButton || (direction < 0 && lastSoundButton.y <= maxYButton)) {
            return false;
        }
        buttons.stream().filter(b -> b instanceof MuffledSlider).forEach(b -> {
            ((MuffledSlider) b).setY((int) (b.y + (b.getHeight() * 10) * direction));
            ((MuffledSlider) b).isVisible(b.y >= minYButton && b.y <= maxYButton);
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
            //clear rangeBar with rmb
            if (!rangeBar.getValue().isEmpty() && rangeBar.isMouseOver(mouseX, mouseY)) {
                rangeBar.setValue("");
                rangeBar.setTextColor(whiteText);
                rangeBar.setFocus(false);
            }
        }
        if (rangeBar.visible && !rangeBar.isMouseOver(mouseX, mouseY)) {
            rangeBar.setValue(String.valueOf(radius));
            rangeBar.setTextColor(whiteText);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (mufflerPos == null) {
            DataManager.saveData(muffledList);
        } else { //TODO this doesn't work on client for some reason
            Network.sendToServer(new PacketMufflers(muffledList, mufflerPos, radius, isMuffling, title, false));
            clearMufflerData();
        }
        super.onClose();
    }

    //-----------------------------------My functions-----------------------------------//

    /**
     * Open function for Anchors.
     *
     * @param ms         Map of muffled sounds
     * @param mufflerPos position of the muffler block
     * @param radius     radius of the muffler
     * @param isMuffling should muffle the sounds or not depending of the button in GUI
     * @param title      title for the Screen
     */
    public static void open(Map<ResourceLocation, Float> ms, BlockPos mufflerPos, int radius, boolean isMuffling, ITextComponent title) {
        minecraft.setScreen(new MufflerScreen(ms, mufflerPos, radius, isMuffling, title));
    }

    /**
     * Open function for Inventory button / assigned key
     *
     * @param ms Map of muffled sounds
     */
    public static void open(Map<ResourceLocation, Float> ms) {
        open(ms, null, 0, true, mainTitle);
    }

    public void removeSoundMuffled(ResourceLocation sound) {
        muffledList.remove(sound);
    }

    public void addSoundMuffled(ResourceLocation sound, float volume) {
        muffledList.put(sound, volume);
    }

    public void replaceVolume(ResourceLocation sound, float volume) {
        muffledList.replace(sound, volume);
    }

    private void bindTexture() {
        minecraft.getTextureManager().bind(SoundMuffler.getGui());
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    private void clearMufflerData() {
        muffledList.clear();
        mufflerPos = null;
        radius = -1;
    }

    private void updateButtons() {
        for (Iterator<Widget> iterator = buttons.iterator(); iterator.hasNext(); ) {
            Widget button = iterator.next();
            if (button instanceof MuffledSlider) {
                ((MuffledSlider) button).isVisible(false);
                iterator.remove();
            }
        }
        addSoundButtons();
    }

    private void toggleTopButtons(GradientButton button) {
        //buttons 0 to 2 are top buttons
        for (int i = 0; i <= 2; i++) {
            ((GradientButton) buttons.get(i)).setActive(false);
        }
        button.setActive(true);
        updateButtons();
    }

    private void setRange() {
        if (!rangeBar.getValue().isEmpty()) {
            int value = Integer.parseInt(rangeBar.getValue());
            int maxValue = Config.getMufflersMaxRadius();
            rangeBar.setTextColor(whiteText);
            if (value < 1) {
                value = 1;
                rangeBar.setTextColor(redText);
            }
            if (value > maxValue) {
                value = maxValue;
                rangeBar.setTextColor(redText);
            }
            radius = value;
        }
    }

    public static boolean isMuffling() {
        return isPlayerMuffling;
    }

    //Buttons init
    private void addSoundButtons() {
        int buttonH = minYButton;

        soundsList.clear();

        //adds all the sounds (modded and vanilla) to the list
        if (btnAll.isActive()) {
            soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys());
        } else if (btnRecent.isActive()) { //adds all the recent sounds
            soundsList.addAll(recentSoundsList);
        } else if (btnMuffled.isActive()) { //add all the muffled sounds
            soundsList.addAll(muffledList.keySet());
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
            float volume = muffledList.get(sound) == null ? 1F : muffledList.get(sound);
            //set x depending of config
            int x = Config.getLeftButtons() ? getX() + 38 : getX() + 11;
            //row highlight
            int bg = buttons.size() % 2 == 0 ? darkBG : brightBG;

            MuffledSlider volumeSlider = new MuffledSlider(x, buttonH, volume, bg, sound, this);

            if (!muffledList.isEmpty() && muffledList.containsKey(sound)) {
                volumeSlider.setFGColor(cyanText);
            }

            addButton(volumeSlider);

            buttonH += volumeSlider.getHeight();
            volumeSlider.isVisible(volumeSlider.y < maxYButton);

            if (soundsList.first().equals(sound)) {
                firstSoundButton = volumeSlider;
            }

            if (soundsList.last().equals(sound)) {
                lastSoundButton = volumeSlider;
            }
        }
    }

    private void addButtons() {
        //recent sounds // 41 is the lenght of the text + 2
        addButton(btnRecent = new GradientButton(getX() + 77, getY() + 24, 41, ITextComponent.nullToEmpty("Recent"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //all sounds // 16 is the lenght of the text + 2
        addButton(btnAll = new GradientButton(getX() + 119, getY() + 24, 16, ITextComponent.nullToEmpty("All"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //muffled sounds // 42 is the lenght of the text + 2
        addButton(btnMuffled = new GradientButton(getX() + 136, getY() + 24, 42, ITextComponent.nullToEmpty("Muffled"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //Searchbar
        addButton(searchBar = new TextFieldWidget(font, getX() + 63, getY() + 185, 134, 11, emptyText)).setBordered(false);
        //Muffler range editor
        addButton(rangeBar = new TextFieldWidget(font, getX() + xSize + 38, getY() + 46, 22, 11, emptyText))
                .visible = mufflerPos != null;
        //Muffler List button; shows / hides the list of mufflers
        addWidget(btnMufflerList = new Button(getX() + 41, getY() + 183, 10, 12, ITextComponent.nullToEmpty("Show Muffler list"), b ->
                hideMufflerButtons()));
        //List of mufflers
        addMufflerListButtons();
        //toggle muffling sounds on/off
        addWidget(btnToggle = new Button(getX() + 229, getY() + 180, 17, 17, ITextComponent.nullToEmpty("Stop muffing"), b -> {
            if (mufflerPos != null) {
                isMuffling = !isMuffling;
            } else {
                isPlayerMuffling = !isPlayerMuffling;
            }
        }));
        //deletes current muffled list
        addWidget(new Button(getX() + 205, getY() + 180, 17, 17, ITextComponent.nullToEmpty("Delete muffled sounds"), b -> {
            muffledList.clear();
            updateButtons();
        }));
        //backwards list of sounds
        addWidget(new Button(getX() + 10, getY() + 182, 10, 13, ITextComponent.nullToEmpty("Previous sounds"), b -> mouseScrolled(0D, 0D, 1D)));
        //forward list of sounds
        addWidget(new Button(getX() + 22, getY() + 182, 10, 13, ITextComponent.nullToEmpty("Next sounds"), b -> mouseScrolled(0D, 0D, -1D)));
        //backwards list of mufflers
        addWidget(new Button(getX() - 11, getY() + 78, 9, 12, emptyText, b -> scrollMufflerListButtons(15)));
        //forward list of mufflers
        addWidget(new Button(getX() - 11, getY() + 93, 9, 12, emptyText, b -> scrollMufflerListButtons(-15)));
    }

    private void addMufflerListButtons() {
        if (!mufflerList.isEmpty() && mufflerPos != null) {
            int buttonH = minMufflerListY;
            MufflerListButton mlb = null;
            for (MufflerEntity muffler : mufflerList) {
                if (muffler.getBlockPos().compareTo(mufflerPos) != 0) {
                    mlb = new MufflerListButton(getX() - 95, buttonH, muffler.getTitle(), b -> {
                        this.onClose();
                        open(muffler.getCurrentMuffledSounds(), muffler.getBlockPos(), muffler.getRadius(), muffler.isMuffling(), muffler.getTitle());
                    });
                    addButton(mlb).visible = false;
                    if (mlb.y == minMufflerListY) {
                        firstMufflerListButton = mlb;
                    }
                    buttonH += 15;
                }
            }
            if (mlb != null) {
                lastMufflerListButton = mlb;
            }
        }
    }

    private void scrollMufflerListButtons(int i) {
        if (!mufflerList.isEmpty() && mufflerList.size() > 11) {
            if ((i == -15 || firstMufflerListButton.y != minMufflerListY) && (i == 15 || lastMufflerListButton.y > maxMufflerListY)) {
                for (Widget button : buttons) {
                    if (button instanceof MufflerListButton) {
                        button.y += i;
                        if (button.y >= minMufflerListY && button.y <= maxMufflerListY) {
                            button.visible = showMufflerList;
                        } else {
                            button.visible = false;
                        }
                    }
                }
            }
        }
    }

    private void hideMufflerButtons() {
        String s = showMufflerList ? "Show" : "Hide";
        btnMufflerList.setMessage(ITextComponent.nullToEmpty(s + " Muffler list"));
        if (!mufflerList.isEmpty()) {
            for (Widget button : buttons) {
                if (button instanceof MufflerListButton) {
                    if (button.y >= minMufflerListY && button.y <= maxMufflerListY) {
                        button.visible = !showMufflerList;
                    } else {
                        button.visible = false;
                    }
                }
            }
        }
        showMufflerList = !showMufflerList;
    }
    //end of buttons

    //Start text rendering
    private void renderButtons(MatrixStack ms, int mouseX, int mouseY) {
        //Screen title
        drawCenteredString(ms, font, this.title, getX() + 128, getY() + 8, whiteText);
        //Render red diagonal on toggle muffling button
        if (showMuffled) {
            bindTexture();
            blit(ms, btnToggle.x, btnToggle.y, 0F, 214F, 17, 17, xSize, xSize);
        }
        //searchbar prompt text
        if (!this.searchBar.isFocused() && this.searchBar.getValue().isEmpty()) {
            drawString(ms, font, searchHint, searchBar.x, searchBar.y, grayText);
        }
        //Muffler information
        if (mufflerPos != null) {
            int x = getX() + xSize + 3;
            int y = getY() + 5;
            TileEntity muffler = minecraft.level != null ? minecraft.level.getBlockEntity(mufflerPos) : null;
            if (muffler instanceof MufflerEntity) {
                fill(ms, x - 3, y, x + 60, y + 54, darkBG);
                drawString(ms, font, "X: " + mufflerPos.getX(), x, getY() + 10, whiteText);
                drawString(ms, font, "Y: " + mufflerPos.getY(), x, getY() + 22, whiteText);
                drawString(ms, font, "Z: " + mufflerPos.getZ(), x, getY() + 34, whiteText);
                drawString(ms, font, "Range: ", x, getY() + 46, whiteText);
            }

            if (rangeBar.isHovered()) {
                //tooltips does not like when the screen is smol
                fill(ms, rangeBar.x - 18, rangeBar.y + 14, rangeBar.x + 56, rangeBar.y + 26, darkBG);
                font.draw(ms, "Range: 1 - " + Config.getMufflersMaxRadius(), rangeBar.x - 15, rangeBar.y + 16, whiteText);
            }
        }
        //Muffler list pannel
        if (showMufflerList) {
            //muffler list screen title
            if (mufflerList.size() > 1) {
                fill(ms, getX(), getY() + 5, getX() - 102, getY() + 180, darkBG);
                drawCenteredString(ms, font, "Mufflers:", getX() - 50, getY() + 10, whiteText);
                //up and down buttons for muffler list
                bindTexture();
                blit(ms, getX() - 11, getY() + 78, 118F, 203F, 9, 12, xSize, xSize);
                blit(ms, getX() - 11, getY() + 93, 127F, 203F, 9, 12, xSize, xSize);
            } else {
                fill(ms, getX(), getY() + 5, getX() - 102, getY() + 35, darkBG);
                drawCenteredString(ms, font, "No more Mufflers", getX() - 50, getY() + 10, whiteText);
                drawCenteredString(ms, font, "to show", getX() - 50, getY() + 22, whiteText);
            }
        }
        //tips
        renderTips(ms, Collections.singletonList(tip));
    }

    private void renderTips(MatrixStack ms, List<? extends net.minecraft.util.text.ITextComponent> tips) {
        int h = height < 334 ? 334 : height;
        GuiUtils.drawHoveringText(ms, tips, getX() - 5, getY() + 223, width, h, 245, font);
    }

    //button tooltips
    private void renderButtonDescriptions(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        for (IGuiEventListener widget : children) {
            if (widget instanceof MufflerListButton) {
                MufflerListButton mb = (MufflerListButton) widget;
                if (widget.isMouseOver(mouseX, mouseY)) {
                    if (font.width(((MufflerListButton) widget).getMessage()) > 76) {
                        String m = mb.getMessage().getString();
                        renderTooltip(ms, ITextComponent.nullToEmpty(m), mb.x + 28 - (font.width(m) / 2), mb.y + 15);
                    } else {
                        mb.setFGColor(cyanText);
                    }
                } else {
                    mb.setFGColor(whiteText);
                }
            } else if (widget instanceof Button && widget.isMouseOver(mouseX, mouseY)) {
                Button b = (Button) widget;
                String message = b.getMessage().getString();
                if (b.getMessage().equals(emptyText)) {
                    continue;
                }
                if (b == btnToggle) {
                    b.setMessage(showMuffled ? ITextComponent.nullToEmpty("Stop muffing") : ITextComponent.nullToEmpty("Resume muffing"));
                }
                if (widget instanceof GradientButton) {
                    message = "Show " + b.getMessage().getString() + " sounds";
                }
                renderTooltip(ms, ITextComponent.nullToEmpty(message), b.x - (font.width(message) / 2), b.y);
            }
        }
    }
    //end of text rendering

}
