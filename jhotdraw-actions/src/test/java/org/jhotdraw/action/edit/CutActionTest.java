package org.jhotdraw.action.edit;



import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.jhotdraw.datatransfer.ClipboardUtil;

import static org.junit.Assert.*;
/*import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;*/
import org.junit.Before;
import org.junit.Test;

public class CutActionTest{
        public CutAction cutAction;
        public JComponent target;

        @Before
        public void setUp() {
            cutAction = new CutAction();
            target = new JComponent() {
            };
        }

        @Test
        public void testCutAction() {
            // Test that the CutAction was constructed correctly
            assertNotNull(cutAction);
            assertTrue(cutAction instanceof AbstractSelectionAction);
        }

        @Test
        public void testActionPerformed() {
            // Test that the actionPerformed method works as expected
            cutAction.actionPerformed(new ActionEvent(target, 0, ""));

            Transferable transferable = ClipboardUtil.getClipboard().getContents(null);
            assertNotNull(transferable);
            assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
        }
}
