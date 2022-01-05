package com.leobeliik.extremesoundmuffler.interfaces;

import com.leobeliik.extremesoundmuffler.gui.buttons.MuffledSlider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;

import java.util.Locale;

public interface IColorsGui {

    int whiteText = 0xffffff;
    int aquaText = 0x00ffff;
    int greenText = 0x00ff00;
    int darkBG = FastColor.ARGB32.color(255, 0, 0, 0);
    int brightBG = FastColor.ARGB32.color(200, 50, 50, 50);

    default void setFGColor(AbstractWidget button, String color) {
        MutableComponent message = button.getMessage().copy();
        switch (color) {
            case ("white") -> button.setMessage(message.withStyle(ChatFormatting.WHITE));
            case ("green") -> button.setMessage(message.withStyle(ChatFormatting.GREEN));
            case ("aqua") -> button.setMessage(message.withStyle(ChatFormatting.AQUA));
            default -> button.setMessage(message.withStyle(message.getStyle()));
        }
    }

    default Boolean getFGColor(MutableComponent message, String color) {
        return message.getStyle().getColor() != null && message.getStyle().getColor().toString().toLowerCase(Locale.ROOT).equals(color);
    }

}