package com.leobeliik.extremesoundmuffler.utils;

import java.util.Random;

public enum Tips {
    disable("You can disable this tips in the config"),
    change_volume("You can change the volume of muffled sounds by dragging the slider"),
    inv_button("You can move the inventory button by pressing [WIP] while your inventory is open"),
    inv_button_disable("You can disable and enable the muffler button on your inventory screen in the config"),
    muffler_reskin("You can change the appearance of a muffler by right clicking it with any block in your hand"),
    muffler_screen("You can see all your mufflers by pressing the Mufflers list button"),
    muffler_rename("You can (and should) rename your Mufflers in an anvil"),
    play_sound("You can play any sound by pressing the corresponding Play sound button"),
    unmuffle("You can stop muffling all the selected sounds by pressing the Stop muffling sounds button, press it again to resume the muffling"),
    no_mufflers("You can dissable the Mufflers in the config"),
    sound_blacklist("You can blacklist sounds in the config"),
    left_buttons("You can change the side of the Muffler and Play buttons to the left in the config"),
    dark_theme("You can set the dark theme in the config");

    private String tip;

    Tips(String s) {
        tip = s;
    }

    public static String randomTip(){
        return Tips.values()[new Random().nextInt(Tips.values().length)].toString();
    }

    public String toString() {
        return "Tip: " + tip;
    }
}
