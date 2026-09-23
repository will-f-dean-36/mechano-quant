package com.will.cellseg;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import java.awt.Frame;
import java.awt.Window;

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

    static Window findThresholdWindow() {
        final Window[] windows = WindowManager.getAllNonImageWindows();
        if (windows == null) {
            return null;
        }
        for (Window window : windows) {
            if (window == null || !window.isVisible()) {
                continue;
            }
            final String title = window instanceof Frame ? ((Frame) window).getTitle() : "";
            if (title != null && title.toLowerCase().contains("threshold")) {
                return window;
            }
            final String className = window.getClass().getName().toLowerCase();
            if (className.contains("threshold")) {
                return window;
            }
        }
        return null;
    }
}
