package com.leobeliik.extremesoundmuffler.blocks;

import com.leobeliik.extremesoundmuffler.setup.ClientProxy;
import com.leobeliik.extremesoundmuffler.setup.IProxy;
import com.leobeliik.extremesoundmuffler.utils.EventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
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
        Set<ResourceLocation> soundsToMuffle = new HashSet<>();
        int buttonW = getGuiW() + 10;
        int buttonH = getGuiH() + 15;
        if (!getSounds().isEmpty()) {
            for (BlockPos pos : getSounds().keySet()) {
                if (!pos.equals(tileEntityPos)) continue;
                for (ResourceLocation s : getSounds().get(pos)) {
                    String text = font.trimStringToWidth(s.getPath(), xSize - 22);
                    int fontH = font.FONT_HEIGHT;
                    Button btnSound = new Button(buttonW, buttonH, xSize - 20, fontH + 2, text, b -> {
                        soundsToMuffle.add(s);
                        this.insertText("aaa", true);
                    });
                    addButton(new Button(buttonW + 144, getGuiH() + 114, 14, 14, "", b -> {
                        if (!soundsToMuffle.isEmpty()) {
                            SoundMufflerBlock.setToMuffle(tileEntityPos, soundsToMuffle);
                            Objects.requireNonNull(proxy.getClientWorld().getTileEntity(tileEntityPos)).markDirty();
                        }
                        soundsToMuffle.clear();
                    })).setAlpha(0);
                    btnSound.setAlpha(0);
                    addButton(btnSound);
                    buttonH += fontH + 4;
                }
            }
        }
    }

    private int getGuiW() {
        return (width - xSize) / 2;
    }

    private int getGuiH() {
        return (this.height - ySize) / 2;
    }

    private Map<BlockPos, Set<ResourceLocation>> getSounds() {
        Map<BlockPos, Set<ResourceLocation>> sounds = new HashMap<>();
        if (!SoundMufflerBlock.getPositions().isEmpty() || !EventHandler.getSounds().isEmpty()) {
            sounds.putAll(new HashMap<>(SoundMufflerBlock.getToMuffle()));
            sounds.putAll(EventHandler.getSounds());
        }
        return sounds;
    }
}
