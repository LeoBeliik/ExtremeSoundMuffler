package com.leobeliik.extremesoundmuffler.gui.buttons;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class InvButton extends AbstractButton implements IColorsGui, ISoundLists {

    private final Minecraft minecraft = Minecraft.getInstance();

    public InvButton(ContainerScreen parentGui, int x, int y) {
        super(x + parentGui.getGuiLeft() + 11, parentGui.getGuiTop() + y - 2, 11, 11, StringTextComponent.EMPTY);
    }

    @Override
    public void onPress() {
        MufflerScreen.open(playerMuffledList);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            minecraft.getTextureManager().bind(SoundMuffler.getGui());
            blit(matrix, x, y, 11, 203, 11, 11, 256, 256);
            if (this.isHovered(mouseX, mouseY)) {
                drawCenteredString(matrix, minecraft.font, "Muffler", x + 5, this.y + this.height + 1, whiteText);
            }
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
    }
}