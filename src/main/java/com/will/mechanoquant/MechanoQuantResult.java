package com.will.mechanoquant;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;

/** Outputs from a segmentation run. */
public final class MechanoQuantResult {
    public final ImagePlus mask;
    public final ImagePlus labels;
    public final int roiCount;
    public final RoiManager roiManager;
    public final ResultsTable resultsTable;

    public MechanoQuantResult(ImagePlus mask, ImagePlus labels, int roiCount, RoiManager roiManager, ResultsTable resultsTable) {
        this.mask = mask;
        this.labels = labels;
        this.roiCount = roiCount;
        this.roiManager = roiManager;
        this.resultsTable = resultsTable;
    }
}
