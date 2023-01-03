package org.jhotdraw.action;

import com.tngtech.jgiven.Stage;
import org.jhotdraw.action.edit.CutAction;
import org.jhotdraw.datatransfer.ClipboardUtil;
import org.junit.Test;

import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;

import static org.junit.Assert.*;


public class CutActionBDDTests extends Stage<CutActionBDDTests>{
    private CutAction cutAction = new CutAction();
    public JComponent target = new JComponent() {
    };

    
    public void whenACutIsMade() {
        cutAction.actionPerformed(new ActionEvent(target, 0, ""));

        ClipboardUtil.setClipboard(new Clipboard("test"));
//        Transferable transferable = ClipboardUtil.getClipboard().getContents("test");
        assertNotNull(ClipboardUtil.getClipboard());
        assertEquals(ClipboardUtil.getClipboard().getName(), "test");
    }
}
