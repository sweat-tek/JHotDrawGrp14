package org.jhotdraw.action.edit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.jhotdraw.datatransfer.ClipboardUtil;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PasteActionTest {
    public PasteAction pasteAction;
    public JComponent target;

    @Before
    public void setUp() {
        pasteAction = new PasteAction();
        target = new JComponent() {
        };
    }

    @Test
    public void testCutAction() {
        // Test that the CutAction was constructed correctly
        assertNotNull(pasteAction);
        assertTrue(pasteAction instanceof AbstractSelectionAction);
    }

    @Test
    public void testActionPerformed() {
        // Test that the actionPerformed method works as expected
        pasteAction.actionPerformed(new ActionEvent(target, 0, ""));

        Transferable transferable = ClipboardUtil.getClipboard().getContents(null);
        assertNotNull(transferable);
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
    }
}
