package com.will.mechanoquant.batch;

import java.io.File;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class InputResolverTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private PairedUnit resolve(int channels, int ricmChannel) throws Exception {
        final File file = folder.newFile();
        final InputResolver.MetadataProvider metadata = new InputResolver.MetadataProvider() {
            public int getSeriesCount(File source) { return 1; }
            public SeriesMetadata getSeriesMetadata(File source, int series) {
                return new SeriesMetadata(8, 8, channels, 2);
            }
        };
        return InputResolver.resolve(InputMode.SAME_FILE_CHANNELS, null, null, null, null,
                new File[]{file}, ricmChannel, true, metadata).get(0);
    }

    @Test public void measuresEveryChannelExceptRicm() throws Exception {
        final PairedUnit pair = resolve(4, 3);
        assertEquals(2, pair.getSegUnit().getSegChannelIndex());
        assertArrayEquals(new int[]{0, 1, 3}, pair.getMeasUnit().getMeasChannelIndices());
        final List<FrameSpec> frames = MeasurementPlan.planFrames(pair.getMeasUnit(),
                new SeriesMetadata(8, 8, 4, 2));
        assertEquals(6, frames.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsFilesWithNoOtherChannels() throws Exception {
        resolve(1, 1);
    }

    @Test public void measuresRemainingChannelInTwoChannelFile() throws Exception {
        assertArrayEquals(new int[]{1}, resolve(2, 1).getMeasUnit().getMeasChannelIndices());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRicmChannelOutsideFile() throws Exception {
        resolve(2, 3);
    }
}
