package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;

public interface GameObject {
    /**
     * Update our state by this many milliseconds.
     */
    void stepMs(long deltaMs);

    void drawOn(Canvas canvas);
}
