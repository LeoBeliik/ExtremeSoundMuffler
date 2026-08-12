package com.leobeliik.extremesoundmuffler.gui.buttons.soundSlider;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMButton;
import com.leobeliik.extremesoundmuffler.interfaces.IColorsGui;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import java.util.Locale;
import static com.leobeliik.extremesoundmuffler.Constants.CACHE_BLOCK_SOUNDS;
import static com.leobeliik.extremesoundmuffler.Constants.EVERYTHING;
import static com.leobeliik.extremesoundmuffler.SoundMufflerCommon.getIconsTextureID;

@SuppressWarnings("EmptyMethod")
public class ESMSlider extends AbstractWidget implements ISoundLists, IColorsGui {

	private static final Minecraft minecraft = Minecraft.getInstance();
	private static boolean showSlider = false;
	private final Font font = minecraft.font;
	private final String sound;
	final MufflerScreen screen;
	private final int bg;
	private double sliderValue;
	private boolean isMuffling = false;
	private ESMButton btnToggleSound;
	private ESMPlay btnPlaySound;

	public ESMSlider(int x, int y, int bg, String sound, double sliderValue, MufflerScreen screen) {
		super(x, y, 211, 13, Component.nullToEmpty(sound));
		this.bg = bg;
		this.sound = sound;
		this.sliderValue = sliderValue;
		this.screen = screen;
		setBtnToggleSound(sound);
		setBtnPlaySound(sound);
	}


	@Override
	public void renderWidget(@NotNull GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
		//--------------- Render Button when not hovering ---------------//
		isMuffling = getFGColor(getText(), "green");
		//row highlight
		stack.fill(getX(), getY(), getX() + width - 1, getY() + height, bg);
		drawGradient(stack);
		float v = isMuffling ? 202F : 213F;

		//--------------- Render buttons BG ---------------//
		btnToggleSound.setToggle(isMuffling);
		//--------------- Render Slider Text ---------------//
		this.drawMessage(stack);

		//render slider icon when hovered
		if (this.isHovered() && this.isMuffling) {
			stack.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX() + (int) (sliderValue * (width - 3)) + 1, getY() + 2, 154, 59, 4, 9, 256, 256); //Slider
		}
	}

	private void drawMessage(GuiGraphics stack) {
		int v = Math.max(width, font.width(getMessage().getString()));
		if (showSlider && isFocused() && isHovered()) {
			stack.drawCenteredString(font, Component.translatable("slider.btn.volume", (int) (sliderValue * 100)), getX() + (width / 2), getY() + 2, aquaText);
		} else {
			String msgTruncated = getMessage().getString();

			if (msgTruncated.contains(":")) {
				String[] s = msgTruncated.split(":");
				if (s.length == 2)
					msgTruncated = s[1] + ":" + s[0];
			}

			//make the text scroll horizontally if is too long and the button is being hovered
			if (this.isHovered() && !(btnToggleSound.isHovered() || btnPlaySound.isHovered()) && font.width(msgTruncated) > 205) {
				renderScrollingStringOverContents(stack.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR), Component.literal(msgTruncated).withColor(aquaText), 2);
			} else {
				msgTruncated = font.plainSubstrByWidth(msgTruncated, 205);
				//if is mufflind use green text, if hovering use aqua, otherwise use white!
				stack.drawString(font, msgTruncated, getX() + 2, getY() + 2, this.isHovered() ? aquaText : isMuffling ? greenText : whiteText, true); //title
			}
		}
	}

	@Override
	public void renderScrollingStringOverContents(@NonNull ActiveTextCollector textCollector, @NonNull Component component, int offset) {
		int x1 = this.getX() + offset;
		int x2 = this.getX() + this.getWidth() - offset;
		int y1 = this.getY() - offset;
		int y2 = this.getY() + this.getHeight();
		textCollector.acceptScrolling(component, x1, x1, x2, y1, y2);
	}

	//draws the "rainbow" gradient in the background
	private void drawGradient(GuiGraphics stack) {
		if (isMuffling) {
			stack.blit(RenderPipelines.GUI_TEXTURED, getIconsTextureID(), getX(), getY(), 0, 0, (int) (sliderValue * (width - 6)) + 5, height, 256, 256); //draw bg
		}
	}

	public void setVisible(boolean b) {
		this.visible = b;
		this.getBtnToggleSound().visible = b;
		this.getBtnPlaySound().visible = b;
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		this.getBtnToggleSound().setY(y + 1);
		this.getBtnPlaySound().setY(y + 1);
	}

	private void setBtnToggleSound(String sound) {
		int x = CommonConfig.get().leftButtons().get() ? getX() - 23 : getX() + getWidth();
		btnToggleSound = new ESMButton(x, getY() + 1, 132, 47, 11, b -> {
			if (screen.btnBlocks.isSelected()) {
				setMufflingBlocks(sound);
			} else if (screen.btnMods.isSelected()) {
				setMufflingMods(sound);
			} else {
				setMufflingSounds(sound);
			}

			((ESMButton) b).toggle();

			DataManager.saveData();
		}, isMuffling ? Component.translatable("slider.btn.muffler.unmuffle") : Component.translatable("slider.btn.muffler.muffle"));
	}

	private void setMufflingSounds(String sound) {
		if (isMuffling) {
			screen.removeSoundMuffled(sound);
			setFGColor(this, "white");
			if (screen.btnMuffled.isSelected()) {
				screen.updateButtons();
			}
		} else {
			setSliderValue(CommonConfig.get().defaultMuteVolume().get());
			screen.addSoundMuffled(sound, sliderValue);
			setFGColor(this, "green");
		}
	}

	private void setMufflingMods(String sound) {
		if (isMuffling) {
			screen.removeModsMuffled(sound.toLowerCase(Locale.ROOT));
			setFGColor(this, "white");
		} else {
			setSliderValue(CommonConfig.get().defaultMuteVolume().get());
			screen.addModsMuffled(sound.toLowerCase(Locale.ROOT), sliderValue);
			setFGColor(this, "green");
		}
	}

	private void setMufflingBlocks(String block) {
		CACHE_BLOCK_SOUNDS.get(block).forEach(this::setMufflingSounds);

		if (isMuffling) {
			screen.removeBlocksMuffled(block);
			setFGColor(this, "white");
		} else {
			screen.addBlocksMuffled(block);
			setFGColor(this, "green");
		}
	}

	public ESMButton getBtnToggleSound() {
		return btnToggleSound;
	}

	private void setBtnPlaySound(String sound) {
		//prevent it to work when the Mods tabs is selected or on "EVERYTHING"
		boolean shouldExist = !this.screen.btnMods.isSelected() && !sound.equals(EVERYTHING);

		btnPlaySound = new ESMPlay(btnToggleSound.getX() + 12, getY() + 1, sound, this, Component.translatable("slider.btn.play.play_sound"));
		btnPlaySound.setToggle(!shouldExist);
		btnPlaySound.canHover(shouldExist);
		btnPlaySound.active = shouldExist;
	}

	public ESMPlay getBtnPlaySound() {
		return btnPlaySound;
	}

	private void changeSliderValue(double mouseX) {
		setSliderValue((mouseX - (getX() + 4)) / (width - 8));
	}

	//from vanilla
	private void setSliderValue(double value) {
		double d0 = sliderValue;
		sliderValue = Mth.clamp(value, 0.0D, 0.9D);
		if (d0 != sliderValue) {
			func_230972_a_();
		}
		func_230979_b_();
		screen.replaceVolume(sound, sliderValue);
	}

	@Override
	protected void onDrag(MouseButtonEvent mouseButtonEvent, double d1, double d2) {
		changeSliderValue((float) mouseButtonEvent.x());
		super.onDrag(mouseButtonEvent, d1, d2);
	}

	@Override
	public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean success) {
		if (this.visible && isMuffling && mouseButtonEvent.button() == 0) {
			this.btnToggleSound.mouseClicked(mouseButtonEvent, success);
			this.btnPlaySound.mouseClicked(mouseButtonEvent, success);

			if (isHovered && isMuffling) {
				changeSliderValue((float) mouseButtonEvent.x());
				showSlider = true;
				setFocused(true);
			}
		}
		return super.mouseClicked(mouseButtonEvent, success);
	}

	@Override
	public boolean mouseReleased(@NotNull MouseButtonEvent mouseButtonEvent) {
		setFocused(false);
		return super.mouseReleased(mouseButtonEvent);
	}

	private void func_230979_b_() {
	}

	private void func_230972_a_() {
	}

	@Override
	public boolean isHovered() {
		return super.isHovered() && this.active;
	}

	private MutableComponent getText() {
		return this.getMessage().copy();
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput elementOutput) {
		elementOutput.add(NarratedElementType.TITLE, isMuffling ? Component.translatable("slider.btn.volume").toString() + (int) (sliderValue * 100) : this.sound);
	}
}