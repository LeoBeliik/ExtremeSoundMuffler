package com.leobeliik.extremesoundmuffler.utils;

import java.util.Random;

public enum Tips {
    disable("tip.disable"),
    change_volume("tip.change_volume"),
    inv_button("tip.inv_button"),
    inv_button_disable("tip.inv_button_disable"),
    play_sound("tip.play_sound"),
    unmuffle("tip.unmuffle"),
    no_anchors("tip.no_anchors"),
    sound_blacklist("tip.sound_blacklist"),
    left_buttons("tip.left_buttons"),
    dark_theme("tip.dark_theme"),
    use_anchors("tip.use_anchors"),
    set_anchors("tip.set_anchors"),
    modify_anchors("tip.modify_anchors"),
    modify_anchors_2("tip.modify_anchors_2"),
    reset_recent_sounds("tip.reset_recent_sounds");

    private String tip;

    Tips(String s) {
        tip = s;
    }

    public static String randomTip(){
        return Tips.values()[new Random().nextInt(Tips.values().length)].toString();
    }

    public String toString() {
        return tip;
    }
}
