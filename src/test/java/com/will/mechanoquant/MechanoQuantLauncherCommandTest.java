package com.will.mechanoquant;

import ij.ImagePlus;
import ij.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.scijava.command.CommandService;

import static org.junit.Assert.*;

public class MechanoQuantLauncherCommandTest {
    private List<Object[]> launch(int choice) throws Exception {
        final List<Object[]> calls = new ArrayList<>();
        final CommandService service = (CommandService) Proxy.newProxyInstance(
                CommandService.class.getClassLoader(), new Class<?>[]{CommandService.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("run")) calls.add(args);
                    return null;
                });
        final MechanoQuantLauncherCommand launcher = new MechanoQuantLauncherCommand() {
            @Override
            int chooseWorkflow() { return choice; }
        };
        final Field field = MechanoQuantLauncherCommand.class.getDeclaredField("commandService");
        field.setAccessible(true);
        field.set(launcher, service);
        launcher.run();
        return calls;
    }

    @Test
    public void currentImageDispatchesWithTheActiveImage() throws Exception {
        final ij.ImageStack stack = new ij.ImageStack(2, 2);
        stack.addSlice(new ij.process.ByteProcessor(2, 2));
        stack.addSlice(new ij.process.ByteProcessor(2, 2));
        final ImagePlus image = new ImagePlus("stack", stack);
        final ImagePlus previous = WindowManager.getCurrentImage();
        WindowManager.setTempCurrentImage(image);
        try {
            final List<Object[]> calls = launch(0);
            assertEquals(1, calls.size());
            assertEquals(MechanoQuantCommand.class, calls.get(0)[0]);
            assertEquals(false, calls.get(0)[1]);
            assertArrayEquals(new Object[]{"imp", image}, (Object[]) calls.get(0)[2]);
        } finally {
            WindowManager.setTempCurrentImage(previous);
        }
    }

    @Test
    public void selectFilesDispatchesWithoutRequiringAnOpenImage() throws Exception {
        final List<Object[]> calls = launch(1);
        assertEquals(1, calls.size());
        assertEquals(MechanoQuantBatchCommand.class, calls.get(0)[0]);
        assertEquals(false, calls.get(0)[1]);
        assertEquals(0, ((Object[]) calls.get(0)[2]).length);
    }

    @Test
    public void cancelAndWindowCloseDoNotDispatch() throws Exception {
        assertTrue(launch(2).isEmpty());
        assertTrue(launch(-1).isEmpty());
    }
}
