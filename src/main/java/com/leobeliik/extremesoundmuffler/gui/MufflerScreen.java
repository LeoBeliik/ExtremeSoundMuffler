package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.Networking.Network;
import com.leobeliik.extremesoundmuffler.Networking.PacketAnchorSounds;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class MufflerScreen extends Screen implements ISoundLists, IColorsGui {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ITextComponent mainTitle = ITextComponent.nullToEmpty("ESM - Main Screen");
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final int xSize = 256;
    private final int ySize = 202;
    private int minYButton, maxYButton, index;
    private int radius;
    private boolean isMuffling;
    private BlockPos anchorPos;
    private Widget btnToggle, btnDelete, btnNext, btnPrev, btnAccept, btnCancel;
    private TextFieldWidget searchBar;
    private SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    private Map<ResourceLocation, Float> muffledList = new HashMap<>();


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
        drawCenteredString(ms, font, this.title, getX() + 128, getY() + 8, whiteText); //Screen title
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void init() {
        minYButton = getY() + 37;
        maxYButton = getY() + 164;

        soundsList.clear();
        soundsList.addAll(ForgeRegistries.SOUND_EVENTS.getKeys()); //adds modded sounds to the list along the vanilla ones
        forbiddenSounds.forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs))); //removes blacklisted sounds

        //allows to hold a key to keep printing it. in this case i want it to easy erase text
        minecraft.keyboardHandler.setSendRepeatsToGui(true);

        addWidgets();
        addSoundButtons();
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        int tempY = minYButton;
        if (searchBar.isFocused()) {
            updateButtons();
        } else if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        //buttons.get(0) is the searchbar, the rest are all sound sliders
        if ((direction > 0 && buttons.get(1).y == minYButton) || (direction < 0 && buttons.get(buttons.size() - 1).y <= maxYButton)) {
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
        if (button == 1) {
            if (!searchBar.getValue().isEmpty() && searchBar.isMouseOver(mouseX, mouseY)) {
                searchBar.setValue("");
                searchBar.setFocus(true);
                updateButtons();
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
     * @param isMuffling should muffle the sounds or not depending of the button in GUI
     * @param title      title for the Screen
     */
    public static void open(Map<ResourceLocation, Float> ms, boolean isMuffling, ITextComponent title) {
        open(ms, null, 0, isMuffling, title);
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

    //Buttons init
    private void addSoundButtons() {
        int buttonH = minYButton;

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {

            if (!sound.toString().contains(searchBar.getValue())) continue;

            float volume = muffledList.get(sound) == null ? 1F : muffledList.get(sound);

            int x = Config.getLeftButtons() ? getX() + 38 : getX() + 11;

            MuffledSlider volumeSlider = new MuffledSlider(x, buttonH, 205, 14, volume, sound, this);

            if (!muffledList.isEmpty() && muffledList.containsKey(sound)) {
                volumeSlider.setFGColor(cyanText);
            }

            addButton(volumeSlider);

            buttonH += volumeSlider.getHeight();
            volumeSlider.isVisible(volumeSlider.y < maxYButton);
        }
    }

    private void addWidgets() {
        //toggle muffling sounds on/off
        addWidget(btnToggle = new Button(getX() + 229, getY() + 179, 17, 17, emptyText, b -> isMuffling = !isMuffling));

        //deletes current muffled list
        addWidget(btnDelete = new Button(getX() + 205, getY() + 179, 17, 17, emptyText, b -> {
            muffledList.clear();
            updateButtons();
        }));

        //backwards list of sounds
        addWidget(btnPrev = new Button(getX() + 10, getY() + 182, 10, 13, emptyText, b -> mouseScrolled(0D, 0D, 1D)));

        //forward list of sounds
        addWidget(btnNext = new Button(getX() + 22, getY() + 182, 10, 13, emptyText, b -> mouseScrolled(0D, 0D, -1D)));

        addButton(searchBar = new TextFieldWidget(font, getX() + 63, getY() + 185, 134, 11, emptyText)).setBordered(false);
    }
    //end of buttons

    //Start text rendering

    //end of text rendering

}
