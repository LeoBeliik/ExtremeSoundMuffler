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

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SoundMufflerScreen extends ContainerScreen<SoundMufflerContainer> {

    private final ResourceLocation GUI = new ResourceLocation("extremesoundmuffler", "textures/gui/sound_muffler.png");

    private static final Set<ResourceLocation> soundsToMuffle = new HashSet<>();
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
        Map<BlockPos, Set<ResourceLocation>> sounds = EventHandler.getSounds();
        int buttonW = getGuiW() + 10;
        int buttonH = getGuiH() + 15;
        if (sounds != null) {
            for (BlockPos pos : sounds.keySet()) {
                for (ResourceLocation s : sounds.get(pos)) {
                    String text = font.trimStringToWidth(s.getPath(), xSize - 22);
                    int fontH = font.FONT_HEIGHT;
                    Button btnSound = new Button(buttonW, buttonH, xSize - 20, fontH + 2, text, b -> soundsToMuffle.add(s));
                    addButton(new Button(buttonW + 144, getGuiH() + 114, 14, 14, "", b -> {
                        if (getSoundsToMuffle().size() > 0) {
                            SoundMufflerBlock.setToMuffle(tileEntityPos, getSoundsToMuffle());
                            Objects.requireNonNull(proxy.getClientWorld().getTileEntity(tileEntityPos)).markDirty();
                        }
                        soundsToMuffle.clear();
                    })).setAlpha(0);
                        btnSound.setAlpha(0);
                    if (pos.equals(tileEntityPos)) {
                        addButton(btnSound);
                        buttonH += fontH + 4;
                    }
                }
            }
        }
    }

    public int getGuiW() {
        return (width - xSize) / 2;
    }

    public int getGuiH() {
        return (this.height - ySize) / 2;
    }

    public static Set<ResourceLocation> getSoundsToMuffle() {
        return soundsToMuffle;
    }
}
