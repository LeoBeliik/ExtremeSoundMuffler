package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.math.BlockPos;

public class DistanceCalculator {
    
    public static double distance(ISound sound, BlockPos pos) {
       return Math.sqrt( // d(P1, P2) = √(x2 - x1)² + (y2 - y1)² + (z2 - z1)²'
                Math.pow((sound.getX() - pos.getX()), 2)
                + Math.pow((sound.getY() - pos.getY()), 2)
                + Math.pow((sound.getZ() - pos.getZ()), 2)
        );
    }
}
