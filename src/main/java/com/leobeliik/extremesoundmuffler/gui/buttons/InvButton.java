package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton implements IColorsGui {

    private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final AbstractContainerScreen<?> parent;
    private boolean hold = false;

    public InvButton(AbstractContainerScreen parentGui, int x, int y) {
        super(parentGui.getGuiLeft() + x, parentGui.getGuiTop() + y, 11, 11, TextComponent.EMPTY);
        parent = parentGui;
    }

    @Override
    public void onPress() {
        MainScreen.open();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            SoundMuffler.renderGui();
            blit(matrix, x, y, 43f, 202f, 11, 11, 256, 256);
            if (isMouseOver(mouseX, mouseY) && !hold) {
                drawCenteredString(matrix, minecraft.font, "Muffler", x + 5, this.y + this.height + 1, whiteText);
            }
            if (hold) {
                drag(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && isMouseOver(pMouseX, pMouseY)) {
            hold = true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 1 && isMouseOver(pMouseX, pMouseY)) {
            hold = false;
            Config.setInvButtonHorizontal(x - parent.getGuiLeft());
            Config.setInvButtonVertical(y - parent.getGuiTop());
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void drag(int mouseX, int mouseY) {
        x = mouseX - (this.width / 2);
        y = mouseY - (this.height / 2);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {
        elementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}