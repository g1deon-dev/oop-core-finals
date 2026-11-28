package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.view.LoginFrame;
import javax.swing.SwingUtilities;

public class OopCoreFinals {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
