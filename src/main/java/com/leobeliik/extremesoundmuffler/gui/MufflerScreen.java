package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.networking.Network;
import com.leobeliik.extremesoundmuffler.networking.PacketAnchorSounds;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.anchors.AnchorEntity;
import com.leobeliik.extremesoundmuffler.gui.buttons.GradientButton;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    private int minYButton, maxYButton, index;
    private boolean isMuffling;
    private int radius;
    private BlockPos anchorPos;
    private Widget btnToggle, btnAccept, btnCancel;
    private GradientButton btnRecent, btnAll, btnMuffled;
    private TextFieldWidget searchBar, rangeBar;
    private MuffledSlider firstSoundButton, lastSoundButton;
    private SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    private Map<ResourceLocation, Float> muffledList = new HashMap<>();

    //TODO show anchorList (always) and anchor info (if anchor screen)

    private MufflerScreen(Map<ResourceLocation, Float> ms, BlockPos anchorPos, int radius, boolean isMuffling, ITextComponent title) {
        super(title);
        this.anchorPos = anchorPos;
        this.radius = radius;
        this.isMuffling = isMuffling;
        muffledList.putAll(anchorPos == null ? playerMuffledList : ms);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        this.bindTexture();
        this.blit(ms, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        renderButtons(ms, mouseX, mouseY);
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        minYButton = getY() + 37;
        maxYButton = getY() + 164;

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
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

        if (searchBar.isFocused()) {
            updateButtons();
        }
        if (rangeBar.isFocused()) {
            setRange();
        }
        //Searchbar& Rangebar looses focus when pressed "Enter" or "Intro"
        if (keyCode == 257 || keyCode == 335) {
            searchBar.setFocus(false);
            rangeBar.setFocus(false);
        }
        //close screen when the inventory key is pressed
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        //buttons.get, 0 is the searchbar, 1 - 3 the top buttons, the rest are all sound sliders
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (anchorPos == null) {
            playerMuffledList.clear();
            playerMuffledList.putAll(muffledList);
        } else {
            Network.sendToServer(new PacketAnchorSounds(muffledList, anchorPos, radius, isMuffling, title));
            clearAnchorData();
        }
        super.onClose();
    }

    //-----------------------------------My functions-----------------------------------//

    /**
     * Open function for Anchors.
     *
     * @param ms         Map of muffled sounds
     * @param anchorPos  position of the anchor block
     * @param radius     radius of the anchor
     * @param isMuffling should muffle the sounds or not depending of the button in GUI
     * @param title      title for the Screen
     */
    public static void open(Map<ResourceLocation, Float> ms, BlockPos anchorPos, int radius, boolean isMuffling, ITextComponent title) {
        minecraft.setScreen(new MufflerScreen(ms, anchorPos, radius, isMuffling, title));
    }

    /**
     * Open function for Inventory button / assigned key
     *
     * @param ms         Map of muffled sounds
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

    private void clearAnchorData() {
        muffledList.clear();
        anchorPos = null;
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
        //buttons 0 = searchbar, 1 = rangebar; 2 to 4 top buttons
        for (int i = 2; i <= 4; i++) {
            ((GradientButton) buttons.get(i)).setActive(false);
        }
        button.setActive(true);
        updateButtons();
    }

    private void setRange() {
        //Only numbers
        rangeBar.setFilter(s -> s.matches("[0-9]*(?:[0-9]*)?"));
        if (!rangeBar.getValue().isEmpty()) {
            int value = Integer.parseInt(rangeBar.getValue());
            int maxValue = Config.getAnchorMaxRadius();
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
        //Searchbar button
        addButton(searchBar = new TextFieldWidget(font, getX() + 63, getY() + 185, 134, 11, emptyText)).setBordered(false);
        //Anchor range button
        addButton(rangeBar = new TextFieldWidget(font, getX() + xSize + 38, getY() + 46, 22, 11, emptyText))
                .visible = anchorPos != null;
        //recent sounds // 41 is the lenght of the text + 2
        addButton(btnRecent = new GradientButton(getX() + 77, getY() + 24, 41, ITextComponent.nullToEmpty("Recent"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //all sounds // 16 is the lenght of the text + 2
        addButton(btnAll = new GradientButton(getX() + 119, getY() + 24, 16, ITextComponent.nullToEmpty("All"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //muffled sounds // 42 is the lenght of the text + 2
        addButton(btnMuffled = new GradientButton(getX() + 136, getY() + 24, 42, ITextComponent.nullToEmpty("Muffled"), b ->
                toggleTopButtons((GradientButton) b))).setActive(false);
        //TODO: Buttons for all the anchors

        //toggle muffling sounds on/off
        addWidget(btnToggle = new Button(getX() + 229, getY() + 179, 17, 17, emptyText, b -> isMuffling = !isMuffling));
        //deletes current muffled list
        addWidget(new Button(getX() + 205, getY() + 179, 17, 17, ITextComponent.nullToEmpty("Delete muffled sounds"), b -> {
            muffledList.clear();
            updateButtons();
        }));
        //backwards list of sounds
        addWidget(new Button(getX() + 10, getY() + 182, 10, 13, ITextComponent.nullToEmpty("Previous sounds"), b -> mouseScrolled(0D, 0D, 1D)));
        //forward list of sounds
        addWidget(new Button(getX() + 22, getY() + 182, 10, 13, ITextComponent.nullToEmpty("Next sounds"), b -> mouseScrolled(0D, 0D, -1D)));
    }
    //end of buttons

    //Start text rendering
    private void renderButtons(MatrixStack ms, int mouseX, int mouseY) {
        //Screen title
        drawCenteredString(ms, font, this.title, getX() + 128, getY() + 8, whiteText);
        //Render red diagonal on toggle muffling button
        if (isMuffling) {
            bindTexture();
            blit(ms, btnToggle.x, btnToggle.y + 1, 22F, 203F, 17, 17, xSize, xSize);
        }

        //button tooltips
        for (IGuiEventListener widget : children) {
            //TODO: top buttons
            if (widget instanceof Button && widget.isMouseOver(mouseX, mouseY)) {
                Button b = (Button) widget;
                if (b == btnToggle) {
                    b.setMessage(isMuffling ? ITextComponent.nullToEmpty("Stop muffing") : ITextComponent.nullToEmpty("Resume muffing"));
                }
                String message;
                int buttonY = b.y;
                if (widget instanceof GradientButton) {
                    message = "Show " + b.getMessage().getString() + " sounds";
                } else {
                    message = b.getMessage().getString();
                    buttonY = b.y + b.getHeight() * 2 + 5;
                }
                renderTooltip(ms, ITextComponent.nullToEmpty(message), b.x - (font.width(message) / 2), buttonY);
            }
        }
        //searchbar prompt text
        if (!this.searchBar.isFocused() && this.searchBar.getValue().isEmpty()) {
            drawString(ms, font, searchHint, searchBar.x, searchBar.y, grayText);
        }

        //Anchor information
        if (anchorPos != null) {
            int x = getX() + xSize + 3;
            int y = getY() + 5;
            TileEntity anchor = minecraft.level != null ? minecraft.level.getBlockEntity(anchorPos) : null;
            if (anchor instanceof AnchorEntity) {
                fill(ms, x - 3, y, x + 60, y + 54, darkBG);
                drawString(ms, font, "X: " + anchorPos.getX(), x, getY() + 10, whiteText);
                drawString(ms, font, "Y: " + anchorPos.getY(), x, getY() + 22, whiteText);
                drawString(ms, font, "Z: " + anchorPos.getZ(), x, getY() + 34, whiteText);
                drawString(ms, font, "Range: ", x, getY() + 46, whiteText);
            }

            if (rangeBar.isHovered()) {
                fill(ms, rangeBar.x - 18, rangeBar.y + 14, rangeBar.x + 56, rangeBar.y + 26, darkBG);
                font.draw(ms, "Range: 1 - " + Config.getAnchorMaxRadius(), rangeBar.x - 15, rangeBar.y + 16, whiteText);
            }
        }

        //Anchor lists
        //TODO
    }
    //end of text rendering

}
