package org.jhotdraw.action.edit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.jhotdraw.datatransfer.ClipboardUtil;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DuplicateActionTest {
    public DuplicateAction duplicateAction;
    public JComponent target;

    @Before
    public void setUp() {
        duplicateAction = new DuplicateAction();
        target = new JComponent() {
        };
    }

    @Test
    public void testCutAction() {
        // Test that the CutAction was constructed correctly
        assertNotNull(duplicateAction);
        assertTrue(duplicateAction instanceof AbstractSelectionAction);
    }

    @Test
    public void testActionPerformed() {
        // Test that the actionPerformed method works as expected
        duplicateAction.actionPerformed(new ActionEvent(target, 0, ""));

        Transferable transferable = ClipboardUtil.getClipboard().getContents(null);
        assertNotNull(transferable);
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
    }
}
