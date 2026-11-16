package com.will.mechanoquant;

import ij.plugin.PlugIn;
import net.imagej.legacy.IJ1Helper;
import org.scijava.command.CommandService;

/** Single classic Fiji menu entry, using the running ImageJ context. */
public class MechanoQuantLauncher_IJ1 implements PlugIn {
    @Override
    public void run(String arg) {
        IJ1Helper.getLegacyContext().service(CommandService.class)
                .run(MechanoQuantLauncherCommand.class, false);
    }
}
