package org.jhotdraw.action.edit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.jhotdraw.datatransfer.ClipboardUtil;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class CopyActionTest {
    private CopyAction copyAction;
    private JComponent target;

    @Before
    public void setUp() {
        copyAction = new CopyAction();
        target = new JComponent() {
        };
    }

    @Test
    public void testCutAction() {
        // Test that the CutAction was constructed correctly
        assertNotNull(copyAction);
        assertTrue(copyAction instanceof AbstractSelectionAction);
    }

    @Test
    public void testActionPerformed() {
        // Test that the actionPerformed method works as expected
        copyAction.actionPerformed(new ActionEvent(target, 0, ""));

        Transferable transferable = ClipboardUtil.getClipboard().getContents(null);
        assertNotNull(transferable);
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
    }
}
