package com.leobeliik.extremesoundmuffler.gui;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.buttons.PlaySoundButton;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.EventsHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SoundMufflerScreen extends Screen {

    private static final ResourceLocation GUI = new ResourceLocation(SoundMuffler.MODID, "textures/gui/sm_gui.png");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean isMuffling = true;
    private static SortedSet<ResourceLocation> soundsList = new TreeSet<>();
    private static SortedSet<ResourceLocation> recentSoundsList = new TreeSet<>();
    private static Set<ResourceLocation> muffledList = new HashSet<>();
    private static List<Anchor> anchors = new ArrayList<>();
    private static String screenTitle = "";
    private static String toggleSoundsListMessage;
    private final int xSize = 256;
    private final int ySize = 224;
    private final int colorWhite = 16777215;
    private final int colorViolet = 24523966;
    private final int colorGreen = 3010605;
    private final ITextComponent emptyText = StringTextComponent.EMPTY;
    private final String mainTitle = "ESM - Main Screen";
    private int minYButton;
    private int maxYButton;
    private Button btnToggleMuffled;
    private Button btnDelete;
    private Button btnToggleSoundsList;
    private Button btnSetCoord;
    private Button btnAnchor;
    private TextFieldWidget searchBar;
    private Anchor anchor;
    //TODO clean this mess; add a button for change anchor title

    private SoundMufflerScreen() {
        super(StringTextComponent.EMPTY);
    }

    private static void open(String title, String message) {
        toggleSoundsListMessage = message;
        screenTitle = title;
        minecraft.displayGuiScreen(new SoundMufflerScreen());
    }

    public static void open(String title) {
        open(title, "R");
    }

    public static ResourceLocation getGUI() {
        return GUI;
    }

    public static boolean isMuffled() {
        return isMuffling;
    }

    public static void addSound(ResourceLocation sound) {
        recentSoundsList.add(sound);
    }

    public static Set<ResourceLocation> getMuffledList() {
        return muffledList;
    }

    public static void setMuffledList(Set<ResourceLocation> list) {
        muffledList.addAll(list);
    }

    public static List<Anchor> getAnchors() {
        return anchors;
    }

    public static void setAnchors(List<Anchor> list) {
        anchors.addAll(list);
    }

    private static Anchor getAnchorByName(String name) {
        return anchors.stream().filter(anchor -> anchor.getName().equals(name)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        minecraft.getTextureManager().bindTexture(GUI);
        this.blit(matrixStack, getX(), getY(), 0, 32, xSize, ySize); //Main screen bounds
        drawCenteredString(matrixStack, font, screenTitle, getX() + 128, getY() + 8, colorWhite);
        renderButtonsTextures(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        minYButton = getY() + 37;
        maxYButton = getY() + 148;

        addListener(btnToggleSoundsList = new Button(getX() + 10, getY() + 34, 10, 10, ITextComponent.func_241827_a_(toggleSoundsListMessage), b -> {
            if (btnToggleSoundsList.getMessage().getString().equals("R")) {
                toggleSoundsListMessage = "A";
            } else {
                toggleSoundsListMessage = "R";
            }
            btnToggleSoundsList.setMessage(ITextComponent.func_241827_a_(toggleSoundsListMessage));
            buttons.clear();
            open(screenTitle, toggleSoundsListMessage);
        }));

        addSoundButtons();

        addAnchors();

        addButton(btnToggleMuffled = new Button(getX() + 229, getY() + 165, 16, 16, emptyText, b -> isMuffling = !isMuffling)).setAlpha(0);

        addButton(btnDelete = new Button(getX() + 11, getY() + 165, 16, 16, emptyText, b -> {
                    anchor = getAnchorByName(screenTitle);
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.clear();
                    } else {
                        if (anchor == null) {
                            return;
                        }
                        anchor.getMuffledSounds().clear();
                        anchor.setAnchorPos(null);
                    }
                    for (int i = 0; i < soundsList.size(); i++) {
                        buttons.get(i).setFGColor(colorWhite);
                    }
                })
        ).setAlpha(0);

        addButton(btnSetCoord = new Button(getX() + 258, getY() + 40, 12, 12, emptyText, b -> getAnchorByName(screenTitle).setAnchorPos(getPlayerPos())));
        if (screenTitle.equals(mainTitle)) {
            btnSetCoord.visible = false;
        }
        addButton(searchBar = new TextFieldWidget(font, getX() + 75, getY() + 168, 105, 10, emptyText));
        searchBar.setEnableBackgroundDrawing(false);
    }

    private void addSoundButtons() {
        int buttonH = getY() + 46;
        anchor = getAnchorByName(screenTitle);
        if (btnToggleSoundsList.getMessage().getString().equals("R")) {
            soundsList.clear();
            soundsList.addAll(recentSoundsList);
            if (screenTitle.equals(mainTitle) && !muffledList.isEmpty()) {
                soundsList.addAll(muffledList);
            } else if (anchor != null && !anchor.getMuffledSounds().isEmpty()) {
                soundsList.addAll(anchor.getMuffledSounds());
            }
        } else {
            soundsList.clear();
            soundsList.addAll(EventsHandler.getAllSounds());
        }

        if (soundsList.isEmpty()) {
            return;
        }

        for (ResourceLocation sound : soundsList) {
            PlaySoundButton btnPlaySound = new PlaySoundButton(getX() + 233, buttonH, new SoundEvent(sound));
            Button btnToggleSound = new Button(getX() + 221, buttonH, 10, 10, emptyText, b -> {
                if (b.getFGColor() == colorViolet) {
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.remove(sound);
                    } else if (anchor != null) {
                        anchor.removeSound(sound);
                    }
                    b.setFGColor(colorWhite);
                    btnPlaySound.active = true;
                } else {
                    if (screenTitle.equals(mainTitle)) {
                        muffledList.add(sound);
                    } else if (anchor != null) {
                        anchor.addSound(sound);
                    }
                    b.setFGColor(colorViolet);
                    btnPlaySound.active = false;
                }
            });
            if (screenTitle.equals(mainTitle) && !muffledList.isEmpty() && muffledList.contains(sound)) {
                btnToggleSound.setFGColor(colorViolet);
            }

            if (anchor != null && screenTitle.equals(anchor.getName()) && !anchor.getMuffledSounds().isEmpty() && anchor.getMuffledSounds().contains(sound)) {
                btnToggleSound.setFGColor(colorViolet);
            }
            addListener(btnPlaySound);
            addButton(btnToggleSound).setAlpha(0);
            buttonH += btnToggleSound.getHeight() + 1;
            btnToggleSound.visible = btnToggleSound.y <= maxYButton;
        }
    }

    private void addAnchors() {
        int buttonW = getX() + 30;
        for (int i = 0; i <= 9; i++) {
            int finalI = i;
            btnAnchor = new Button(buttonW, getY() + 24, 16, 16, new TranslationTextComponent(String.valueOf(i)), b -> {
                anchor = anchors.get(finalI);
                if (anchor == null) return;
                if (screenTitle.equals(anchor.getName())) {
                    screenTitle = mainTitle;
                } else {
                    screenTitle = anchor.getName();
                }
                buttons.clear();
                open(screenTitle);
            });
            btnAnchor.setFGColor(anchors.get(Integer.parseInt(btnAnchor.getMessage().getString())).getAnchorPos() != null ? colorGreen : colorWhite);
            addButton(btnAnchor).setAlpha(0);
            buttonW += 20;
        }
    }

    //TODO add tooltips render
    private void renderButtonsTextures(MatrixStack matrixStack) {
        int x; //start x point of the button
        int y; //start y point of the button
        float v; //start x point of the texture
        String message; //Button message
        int stringW; //text width

        //Mute sound buttons and play sound buttons; Sound names

        if (buttons.size() < soundsList.size()) {
            return;
        }
        for (int i = 0; i < soundsList.size(); i++) {
            Widget btn = buttons.get(i);

            if (!btn.visible) {
                continue;
            }

            ResourceLocation rs = (ResourceLocation) soundsList.toArray()[i];
            message = rs.getPath() + ":" + rs.getNamespace();
            //draws the name of the sound; sound name : mod name
            drawString(matrixStack, font, message, getX() + 14, btn.y + 2, btn.getFGColor());
            minecraft.getTextureManager().bindTexture(GUI);

            //if muffled
            v = btn.getFGColor() == colorViolet ? 10F : 0F;
            blit(matrixStack, getX() + 221, btn.y + 1, v, 0F, 10, 10, 80, 80); //muffle button
            blit(matrixStack, getX() + 233, btn.y + 1, v + 20F, 0F, 10, 10, 80, 80); //play button
        }

        //Delete button
        x = btnDelete.x + 8;
        y = btnDelete.y;
        minecraft.getTextureManager().bindTexture(GUI);

        blit(matrixStack, btnDelete.x, y, 0, 64F, 0F, 16, 16, 128, 128);
        message = screenTitle.equals(mainTitle) ? "Delete Muffled List" : "Delete Anchor";
        stringW = font.getStringWidth(message) / 2;
        if (btnDelete.isHovered()) {
            fill(matrixStack, x - stringW - 2, y + 17, x + stringW + 2, y + 31, -16777216);
            btnDelete.drawCenteredString(matrixStack, font, message, x, y + 20, colorWhite);
        }


        //toggle muffled button
        x = btnToggleMuffled.x + 8;
        y = btnToggleMuffled.y;
        if (isMuffling) {
            v = 0;
            message = "Stop Muffling";
        } else {
            v = 16F;
            message = "Start Muffling";
        }
        stringW = font.getStringWidth(message) / 2;
        minecraft.getTextureManager().bindTexture(GUI);
        blit(matrixStack, btnToggleMuffled.x, y, 0, v, 0F, 16, 16, 128, 128);

        if (btnToggleMuffled.isHovered()) {
            fill(matrixStack, x - stringW - 2, y + 18, x + stringW + 2, y + 31, -16777216);
            btnToggleMuffled.drawCenteredString(matrixStack, font, message, x, y + 20, colorWhite);
        }

        //Anchor coordinates and set coord button
        Anchor anchor = getAnchorByName(screenTitle);
        x = btnSetCoord.x;
        y = btnSetCoord.y;

        if (anchor != null) {
            fill(matrixStack, x - 3, y - 34, x + 32, y + 16, -16777216);
            drawString(matrixStack, font, "X: " + anchor.getX(), x + 2, y - 30, colorWhite);
            drawString(matrixStack, font, "Y: " + anchor.getY(), x + 2, y - 20, colorWhite);
            drawString(matrixStack, font, "Z: " + anchor.getZ(), x + 2, y - 10, colorWhite);
        }

        if (btnSetCoord.isHovered()) {
            fill(matrixStack, x - 2, y + 16, x + 62, y + 40, -16777216);
            btnSetCoord.drawString(matrixStack, font, "Set", x, y + 20, colorWhite);
            btnSetCoord.drawString(matrixStack, font, "coordinates", x, y + 30, colorWhite);
        }

        for (int i = soundsList.size(), j = 0; i <= soundsList.size() + 9; i++, j++) {
            x = buttons.get(i).x + 8;
            y = buttons.get(i).y;
            stringW = font.getStringWidth(anchors.get(j).getName()) / 2;

            if (buttons.get(i).isHovered()) {
                fill(matrixStack, x - stringW - 2, y - 2, x + stringW + 2, y - 13, -16777216);
                btnAnchor.drawCenteredString(matrixStack, font, anchors.get(j).getName(), x, y - 11, colorWhite);
            }
        }

        //Toggle List button draw message
        font.drawString(matrixStack, btnToggleSoundsList.getMessage().getString(), btnToggleSoundsList.x + 2, btnToggleSoundsList.y, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        int totalMuffButtons = soundsList.size();
        if (searchBar.getText().length() > 0) {
            return false;
        }
        if (totalMuffButtons <= 10) {
            return false; //enough empty screen, no need for scroll
        }
        if (buttons.get(0).y >= minYButton && direction > 0f) {
            return false; //first button is on the top
        }
        if (buttons.get(totalMuffButtons - 1).y <= maxYButton && direction < 0f) {
            return false; //last button is on the bottom
        }

        for (int i = 0; i < totalMuffButtons; i++) {
            Widget b = buttons.get(i);
            b.y = direction > 0 ? b.y + (b.getHeight() + 1) : b.y - (b.getHeight() + 1);
            b.visible = b.y >= minYButton && b.y <= maxYButton;
        }
        return true;
    }

    private void updateText() {
        int buttonH = getY() + 46;
        Object[] soundName = soundsList.toArray();
        for (int i = 0; i < soundsList.size(); i++) {
            Widget b = buttons.get(i);
            if (soundName[i].toString().contains(searchBar.getText())) {
                b.active = true;
                b.y = buttonH;
                b.visible = b.y >= minYButton && b.y <= maxYButton;
                buttonH += b.getHeight() + 1;
            } else {
                b.active = true;
                b.visible = false;
            }
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBar.isFocused()) {
            searchBar.charTyped(codePoint, modifiers);
            updateText();
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        //pressed "backspace" inside search bar
        if (keyCode == 259 && searchBar.isFocused()) {
            updateText();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key1, int key2, int key3) {
        //SearchBar looses focus when pressed "Enter" or "Intro"
        if (key1 == 257 || key1 == 335) {
            searchBar.setFocused2(false);
            return true;
        }
        //Close screen when press "E" or the mod hotkey outside the search bar
        if (!searchBar.isFocused() && (key1 == 69 || key1 == SoundMuffler.getHotkey())) {
            onClose();
            return true;
        }
        return super.keyPressed(key1, key2, key3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        searchBar.setFocused2(searchBar.mouseClicked(mouseX, mouseY, button));
        return true;
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    private BlockPos getPlayerPos() {
        ClientPlayerEntity player = Objects.requireNonNull(minecraft.player);
        return new BlockPos(player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
    }
}