package com.will.cellseg;

import ij.ImagePlus;
import ij.gui.ImageWindow;

/** Shared placement helpers for review image windows. */
final class ReviewWindowUtils {
    private ReviewWindowUtils() {
    }

    static void showImageWindowCentered(ImagePlus image) {
        if (image == null || image.getWindow() != null) {
            return;
        }
        ImageWindow.centerNextImage();
        image.show();
    }
}
