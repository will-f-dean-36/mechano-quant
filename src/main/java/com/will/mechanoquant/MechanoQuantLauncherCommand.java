package com.will.mechanoquant;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/** Selects a workflow; processing and settings remain in the underlying commands. */
@Plugin(type = Command.class, name = "MechanoQuant Launcher")
public class MechanoQuantLauncherCommand implements Command {

    @Parameter
    private CommandService commandService;

    @Override
    public void run() {
        final int choice = chooseWorkflow();
        if (choice == 0) {
            final ImagePlus image = WindowManager.getCurrentImage();
            if (image == null) {
                IJ.noImage();
                return;
            }
            commandService.run(MechanoQuantCommand.class, false, "imp", image);
        } else if (choice == 1) {
            commandService.run(MechanoQuantBatchCommand.class, false);
        }
        // Cancel and closing the dialog both leave without starting a workflow.
    }

    int chooseWorkflow() {
        final int[] choice = {JOptionPane.CLOSED_OPTION};
        final Runnable show = () -> {
            final String[] options = {"Current image", "Select file(s)", "Cancel"};
            final JOptionPane pane = new JOptionPane("Choose a workflow",
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
                    options, options[0]);
            final JDialog dialog = pane.createDialog(IJ.getInstance(), "MechanoQuant");
            try {
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                for (int i = 0; i < options.length; i++) {
                    if (options[i].equals(pane.getValue())) {
                        choice[0] = i;
                        break;
                    }
                }
            } finally {
                dialog.dispose();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            show.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(show);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return JOptionPane.CLOSED_OPTION;
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to show MechanoQuant launcher.", e.getCause());
            }
        }
        return choice[0];
    }
}
