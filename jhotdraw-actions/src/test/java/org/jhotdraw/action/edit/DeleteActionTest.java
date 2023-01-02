package org.jhotdraw.action.edit;

import org.jhotdraw.datatransfer.ClipboardUtil;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeleteActionTest {
    public DeleteAction deleteAction;
    public JComponent target;

    @Before
    public void setUp() {
        deleteAction = new DeleteAction();
        target = new JComponent() {
        };
    }

    @Test
    public void testCutAction() {
        // Test that the CutAction was constructed correctly
        assertNotNull(deleteAction);
        assertTrue(deleteAction instanceof AbstractAction);
    }

    @Test
    public void testActionPerformed() {
        // Test that the actionPerformed method works as expected
        deleteAction.actionPerformed(new ActionEvent(target, 0, ""));

        Transferable transferable = ClipboardUtil.getClipboard().getContents(null);
        assertNotNull(transferable);
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
    }
}
