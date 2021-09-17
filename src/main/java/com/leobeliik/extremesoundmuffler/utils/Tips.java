package com.leobeliik.extremesoundmuffler.utils;

import java.util.Random;

public enum Tips {
    disable("You can disable this tips in the config"),
    change_volume("You can change the volume of muffled sounds by dragging the slider"),
    inv_button("You can move the inventory button by holding the RMB over the button and draging it around"),
    inv_button_disable("You can disable and enable the muffler button on your inventory screen in the config"),
    play_sound("You can play any sound by pressing the corresponding Play sound button"),
    unmuffle("You can stop muffling all the selected sounds by pressing the Stop muffling sounds button, press it again to resume the muffling"),
    no_anchors("You can dissable the Anchors in the config"),
    sound_blacklist("You can blacklist sounds in the config"),
    left_buttons("You can change the side of the Muffler and Play buttons to the left in the config"),
    dark_theme("You can set the dark theme in the config"),
    use_anchors("Anchors (the top 10 buttons) are used to muffle sounds in a selected area"),
    set_anchors("Click any of the Anchor buttons (the numbered ones) and then click the marker like button (in the right) to set the Anchor position"),
    modify_anchors("Once setted the Anchors, you can click the marker button to move the position at any time."),
    modify_anchors_2("You can change the name and range of the Anchors once you setted the position");

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
