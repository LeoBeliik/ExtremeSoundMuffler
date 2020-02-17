package com.leobeliik.extremesoundmuffler.blocks;

import com.leobeliik.extremesoundmuffler.setup.ClientProxy;
import com.leobeliik.extremesoundmuffler.setup.IProxy;
import com.leobeliik.extremesoundmuffler.utils.EventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

public class SoundMufflerScreen extends ContainerScreen<SoundMufflerContainer> {

    private final ResourceLocation GUI = new ResourceLocation("extremesoundmuffler", "textures/gui/sound_muffler.png");

    private static BlockPos tileEntityPos;
    private static final IProxy proxy = new ClientProxy();
    private Map<BlockPos, Set<ResourceLocation>> sounds = new HashMap<>();

    public SoundMufflerScreen(SoundMufflerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        tileEntityPos = screenContainer.getTileEntityPos();
        this.xSize = 176;
        this.ySize = 135;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.minecraft != null) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(GUI);
            this.blit(getGuiW(), getGuiH(), 0, 0, xSize, ySize);
        }
    }

    @Override
    protected void init() {
        super.init();
        getSounds();

        Set<ResourceLocation> soundsToMuffle = new HashSet<>();
        int buttonW = getGuiW() + 10;
        int buttonH = getGuiH() + 13;
        Map<BlockPos, Set<ResourceLocation>> getToMuffle = SoundMufflerBlock.getToMuffle();
        if (!sounds.isEmpty()) {
            for (BlockPos pos : sounds.keySet()) {
                if (!pos.equals(tileEntityPos)) continue;
                for (ResourceLocation s : sounds.get(pos)) {
                    String text = font.trimStringToWidth(s.getPath(), xSize - 22);
                    Button btnSound = new Button(buttonW, buttonH, xSize - 20, font.FONT_HEIGHT + 2, text, b -> {
                        if (b.getFGColor() == 24523966) {
                            getToMuffle.get(tileEntityPos).remove(s);
                            soundsToMuffle.remove(s);
                            EventHandler.setSounds(tileEntityPos, s);
                            b.setFGColor(16777215); //white
                        } else {
                            soundsToMuffle.add(s);
                            b.setFGColor(24523966); //nice color
                        }
                        if (!soundsToMuffle.isEmpty()) {
                            SoundMufflerBlock.setToMuffle(tileEntityPos, soundsToMuffle);
                            Objects.requireNonNull(proxy.getClientWorld().getTileEntity(tileEntityPos)).markDirty();
                        }
                        soundsToMuffle.clear();
                    });
                    if (!getToMuffle.isEmpty() && getToMuffle.get(pos) != null && getToMuffle.get(pos).contains(s)) {
                        btnSound.setFGColor(24523966);
                    }
                    addButton(btnSound).setAlpha(0);
                    buttonH += btnSound.getHeight() + 1;
                    btnSound.visible = btnSound.y <= getGuiH() + 100;
                }
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {

        if (buttons.size() <= 8) return false; //enought empty screen, no need for scroll
        if (buttons.get(0).y >= getGuiH() + 12 && direction > 0f) return false; //first button is on the top
        if (buttons.get(buttons.size() - 1).y <= getGuiH() + 99 && direction < 0f) return false; //last button is on the bottom

        for (Widget b : buttons) {
            b.y = direction > 0 ? b.y + (b.getHeight() + 1) : b.y - (b.getHeight() + 1);
            b.visible = b.y >= getGuiH() + 10 && b.y <= getGuiH() + 100;
        }
        return true;
    }

    private int getGuiW() {
        return (width - xSize) / 2;
    }

    private int getGuiH() {
        return (this.height - ySize) / 2;
    }

    private void getSounds() {
        for (BlockPos pos : SoundMufflerBlock.getPositions()) {
            Set<ResourceLocation> getToMuffle = SoundMufflerBlock.getToMuffle().get(pos);
            Set<ResourceLocation> getEventSounds = EventHandler.getSounds().get(pos);
            if (sounds.containsKey(pos)) {
                if (getToMuffle != null && !getToMuffle.isEmpty()) sounds.get(pos).addAll(getToMuffle);
                if (getEventSounds != null && !getEventSounds.isEmpty()) sounds.get(pos).addAll(getEventSounds);
            } else {
                sounds.put(pos, new HashSet<>());
                getSounds();
            }
        }
    }
}