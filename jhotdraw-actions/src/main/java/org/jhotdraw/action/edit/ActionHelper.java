package org.jhotdraw.action.edit;

import javax.swing.*;
import java.awt.*;

public class ActionHelper {
    public ActionHelper(){

    }
    public JComponent TargetCheck(JComponent target) {
        JComponent c = target;
        /*if (c == null && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getPermanentFocusOwner() instanceof JComponent)) {
            c = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
        }*/


        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        if (c == null && (keyboardFocusManager.getPermanentFocusOwner() instanceof JComponent)) {
            c = (JComponent) keyboardFocusManager.getPermanentFocusOwner();
        }


        return c;
    }
}
