# MechanoQuant

![Version](https://img.shields.io/badge/version-0.6.0-green)

MechanoQuant is a Fiji/ImageJ plugin for segmenting cells in RICM images and measuring the resulting regions in source or paired images. It supports individual images, stacks, and batch workflows, with optional interactive threshold and ROI review.

The plugin supports:
- measuring accepted ROIs in the source image or in a paired image, such as a fluorescence image of the same region
- running on a single open image, an open stack, or many files in batch mode
- several batch organization schemes, including separate RICM and fluorescence files, multi-series containers, and multi-channel images

Open `Plugins > CPMM > MechanoQuant...` and choose `Current image` to process the active image or stack, or `Select file(s)` to open the file/batch workflow. `Cancel` closes the launcher without processing.

Repository: https://github.com/will-f-dean-36/mechano-quant

Planned documentation/wiki: https://sites.imagej.net/mechano-quant (not live yet)

## Status

Early release (`0.x`), under active development.

## Installation

### Requirements

- Fiji / ImageJ

### Installation From the ImageJ Update Site

### Update site steps

1. In Fiji, choose `Help > Update...`.
2. Click `Manage update sites`.
3. Click `Add update site`.
4. Enter:
   - Name: `CPMM`
   - URL: `https://sites.imagej.net/CPMM/`
5. Check the new `CPMM` update site.
6. Click `Close`.
7. Click `Apply changes`.
8. Restart Fiji.

After installation, Fiji can receive plugin updates through the normal updater.

The plugin should appear under:

```text
Plugins > CPMM > MechanoQuant...
```

### Installation From a JAR

### Install steps

1. Obtain the plugin JAR.
2. Copy the JAR into Fiji’s `plugins/` directory.
3. Restart Fiji.
4. The plugin should appear under:

```text
Plugins > CPMM > MechanoQuant...
```

If the plugin does not appear:
- confirm the JAR is in the Fiji `plugins/` folder
- restart Fiji completely
- check `Help > Refresh Menus`

## Building From Source

### Requirements

- JDK 8 or newer
- Maven

### Build

From the repository root:

```bash
mvn clean package
```

The built JAR will be written to:

```text
target/Mechano_Quant-<version>.jar
```

Install that JAR into Fiji as described above.

## What the Plugin Does

The segmentation pipeline is:

1. Duplicate the segmentation image
2. Apply the selected edge detector
3. Normalize the gradient image to `[0, 1]`
4. Threshold the gradient image
5. Convert to mask
6. Fill holes / close gaps
7. Optionally run watershed to separate touching cells
8. Generate ROIs from 4-connected mask components
9. Optionally exclude border-touching objects
10. Optionally review ROIs interactively
11. Measure accepted ROIs on the chosen measurement image

Outputs can include:
- mask image
- labels image
- label overlay image
- ROI ZIP
- measurements CSV
- segmentation parameters CSV

## Data Requirements

### Single-image mode

Menu:

```text
Plugins > CPMM > MechanoQuant...
```

Choose `Current image` in the launcher.

Input requirements:
- the currently active Fiji image is used
- it can be:
  - a single 2D RICM image
  - a simple stack where each slice is treated as an independent RICM image

For stack mode:
- the plugin will ask whether to process all slices
- each slice is treated as a separate segmentation target
- masks, labels, label overlays, measurements, and parameter rows are aggregated across slices

In single-image mode, accepted ROIs are measured on the active image itself. After the run, you can use the saved ROI ZIP or the ROIs in Fiji's ROI Manager for any additional manual measurements.

To automatically measure accepted ROIs on a separate paired image, such as a fluorescence image of the same region, use batch mode.

### Batch mode

Menu:

```text
Plugins > CPMM > MechanoQuant...
```

Choose `Select file(s)` in the launcher.

Batch mode supports five input modes.

#### RICM file list

Use this when:
- you have a list of standalone RICM files
- each file is a single segmentation target
- no fluorescence file is involved

Measurements:
- accepted ROIs are measured on the same RICM image

#### RICM container

Use this when:
- you have one multi-series container file
- each series is an independent RICM image
- no fluorescence file is involved

Measurements:
- accepted ROIs are measured on the same RICM series

#### Two container files

Use this when:
- one container file holds RICM series
- another container file holds fluorescence series
- series are paired by series index

Example:
- RICM series 1 pairs with fluorescence series 1
- RICM series 2 pairs with fluorescence series 2

#### Two file lists

Use this when:
- one file list contains RICM images
- one file list contains fluorescence images
- files are paired by selection order

Example:
- first selected RICM file pairs with first selected fluorescence file
- second selected RICM file pairs with second selected fluorescence file

#### Multi-channel file list (segment one channel, measure others)

Use this when:
- one file contains both RICM and fluorescence channels
- one channel is used for segmentation
- all channels except the selected RICM channel are used for measurement
- files must contain at least two channels

Only the RICM segmentation channel must be specified.

## Single-image Dialog

The single-image dialog has two tabs:

- `General`
- `Advanced`

### General tab

#### Segmentation

- `Auto-threshold method`
  - Fiji/ImageJ auto-threshold method applied to the normalized edge image unless a manual threshold is selected during review.
- `Object polarity`
  - `Dark`: cells are darker than background
  - `Light`: cells are lighter than background
- `Edge method`
  - edge detector used before thresholding
  - options: Sobel, Prewitt, Scharr, Laplacian, or None
- `Min cell area (px)`
  - minimum accepted object area in pixels
- `Exclude border-touching cells`
  - `Yes`: remove any ROI touching the image border
  - `No`: keep border-touching ROIs
- `Attempt to separate touching cells (watershed)`
  - `Yes`: run ImageJ watershed before ROI generation
  - `No`: skip watershed

#### Save

- `Save results`: enables saving to disk after the run
- `Output directory`: destination folder for saved outputs

Select the individual output types in the Advanced tab's Save panel.

The dialog also includes `Restore Defaults`, which resets processing, review, measurement, and save settings to factory defaults while leaving selected files and output directories alone.

### Advanced tab

The advanced tab groups review, display, measurement, and save options into sections.

#### Review

- `Threshold Review`
  - `Yes`: stop after edge detection and before thresholding
  - `No`: run without threshold stop points
- `ROI Review`
  - `Yes`: show detected ROIs for manual review before final acceptance
  - `No`: accept detected ROIs directly

If threshold review is enabled, the stop-point dialog supports:
- `Continue`
- `Continue to end`
- `Skip`
- `Abort`
- `Remember threshold`

If ROI review is enabled, the stop-point dialog supports:
- `Split ROIs`
  - draw a line across a single ROI, then click `Split`
- `Merge ROIs`
  - draw a line with endpoints inside two ROIs, then click `Merge`
- `Delete ROIs`
  - draw through ROIs or place point markers inside them, then click `Delete`
- `Undo`
  - undo the most recent split, merge, delete, reset, or manual ROI Manager edit
- `Redo`
  - reapply the most recently undone ROI edit
- `Reset`
  - restore the ROI list to the ROIs originally proposed for this review step
- `Redo thresholding`
  - discard ROI review edits and return to threshold review for the same image or slice
- `Continue`
  - accept the current ROI list and continue processing
- `Continue to end`
  - accept the current ROI list and continue without showing more ROI review stops in this run
- `Skip`
  - skip the current image or slice without saving results for it
- `Abort`
  - stop the run

When splitting ROIs:
- if the drawn line overlaps multiple ROIs, the ROI containing the most sampled line points is split
- split fragments smaller than `Min cell area (px)` are discarded

For stacks, those review steps are applied slice-by-slice.

#### Display

- `Clear ROI Manager first` (on by default)
  - clear the ROI Manager before adding final accepted ROIs
- `Show ROI overlay on source image` (on by default)
  - apply accepted ROIs to the source image via ROI Manager
- `Show mask image` (off by default)
  - show the final binary mask image
- `Show labels image` (off by default)
  - show the final label image
- `Show label overlay` (off by default)
  - show a label overlay image
- `Show intermediate images` (off by default)
  - show intermediate processing steps from the segmentation pipeline

#### Measurements

Available measurement toggles:
- Area
- Mean
- Min/Max
- Std Dev
- Perimeter
- Centroid
- Bounding rectangle
- Feret's diameter
- Shape descriptors
- Integrated density

These are standard ImageJ measurements performed on the accepted ROIs.

#### Save

- `Save ROIs (ZIP)` (on by default)
- `Save measurements (CSV)` (on by default)
- `Save segmentation parameters (CSV)` (on by default)
- `Save mask image` (off by default)
- `Save labels image` (off by default)
- `Save label overlay` (off by default)

When `Save results` is off in General, the individual save toggles are disabled.

Labels use the Rainbow RGB LUT; the LUT selector is currently hidden.

## Batch Dialog

The batch dialog has three tabs:

- `Inputs`
- `General`
- `Advanced`

### Inputs tab

Controls vary by input mode.

Common behavior:
- selected files are shown in lists
- file selection uses `JFileChooser`
- the last-used input directory is remembered

Mode-specific inputs:

- RICM file list
  - `RICM files`
- RICM container
  - `RICM container`
- Two container files
  - `RICM container`
  - `Fluorescence container`
- Two file lists
  - `RICM files`
  - `Fluorescence files`
- Multi-channel file list
  - `Combined multi-channel files`
  - `RICM channel`

### General tab

#### Segmentation

Same meaning as single-image mode:
- `Auto-threshold method`
- `Object polarity`
- `Edge method`
- `Min cell area (px)`
- `Exclude border-touching cells`
- `Attempt to separate touching cells (watershed)`

#### Save

- `Output directory`: destination folder for saved outputs

Batch mode always saves results, so it has no master save checkbox. Select the individual output types in the Advanced tab's Save panel.

The dialog also includes `Restore Defaults`, which resets processing, review, measurement, and save settings to factory defaults while preserving selected input files and the output directory.

### Advanced tab

The advanced tab groups review, measurement, and save options into sections.

#### Review

- `Threshold Review`
  - `Yes`: stop once per unique RICM segmentation source
  - `No`: no threshold stop points
- `ROI Review`
  - `Yes`: review ROIs once per unique RICM segmentation source
  - `No`: no ROI review

Behavior notes:
- `Continue to end` for threshold review only disables future threshold review stops
- `Continue to end` for ROI review only disables future ROI review stops
- these two review streams are independent
- batch ROI review uses the same split, merge, delete, undo, redo, reset, and redo-thresholding tools as single-image mode
- `Redo thresholding` during ROI review discards ROI edits for that review pass, returns to threshold review for the same RICM source, reruns segmentation, and then shows ROI review again

#### Measurements

Same measurement toggles as single-image mode.

For batch mode:
- in RICM file list and RICM container inputs, measurements use the RICM image/series itself
- in paired container/file list inputs, measurements use the paired fluorescence images
- in multi-channel file list inputs, measurements use all channels except the selected RICM channel

#### Save

- `Save ROIs (ZIP)` (on by default)
- `Save measurements (CSV)` (on by default)
- `Save segmentation parameters (CSV)` (on by default)
- `Save mask image` (off by default)
- `Save labels image` (off by default)
- `Save label overlay` (off by default)

## Saved Outputs

### Measurements CSV

Contains the selected ImageJ measurements for accepted ROIs.

In single-image stack mode:
- a `Slice` column is included

In batch mode:
- one or more measurement CSV files may be written depending on the measurement channels and timepoints

### Segmentation parameters CSV

Batch mode writes:

```text
segmentation_parameters.csv
```

Single-image mode writes:

```text
<base>_segmentation_parameters.csv
```

The parameter CSV records the segmentation settings actually used for each processed image or slice, including:
- source file / source path
- series or slice index
- segmentation channel
- threshold mode (`auto` or `manual`)
- threshold method
- object polarity
- manual threshold min/max when applicable
- edge method
- minimum area
- border-touch exclusion
- ROI review action
- accepted ROI count

## Defaults

Factory defaults include:
- threshold review on
- ROI review on
- watershed off
- dark-object segmentation
- Sobel edge detection
- minimum cell area of 500 px
- all measurement toggles on

Settings chosen in the custom dialogs are remembered between runs using Fiji preferences. `Restore Defaults` changes the visible dialog controls back to factory defaults; those values are saved as preferences only if the run is started from that dialog.

## Notes

- Batch file selections themselves are not persisted as defaults.
- This is still a `0.x` project, so UI details and defaults may continue to evolve.

## License

MechanoQuant is distributed under the [BSD 2-Clause License](LICENSE).
Copyright (c) 2026 William F Dean.
