package com.leobeliik.extremesoundmuffler.interfaces;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;

import java.util.Locale;
import java.util.Objects;

public interface IColorsGui {

    int whiteText = 0xffffff;
    int aquaText = 0x00ffff;
    int greenText = 0x00ff00;
    int darkBG = FastColor.ARGB32.color(255, 0, 0, 0);
    int brightBG = FastColor.ARGB32.color(200, 50, 50, 50);

    default Component setFGColor(MutableComponent message, String color) {
        return switch (color) {
            case ("white") -> message.withStyle(ChatFormatting.WHITE);
            case ("green") -> message.withStyle(ChatFormatting.GREEN);
            case ("aqua") -> message.withStyle(ChatFormatting.AQUA);
            default -> message.withStyle(message.getStyle());
        };
    }

    default String getFGColor(MutableComponent message) {
        return message.getStyle().getColor() != null ? message.getStyle().getColor().toString().toLowerCase(Locale.ROOT) : "null";
    }

}