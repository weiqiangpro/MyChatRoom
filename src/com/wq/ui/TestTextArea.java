package com.wq.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class TestTextArea {
    public static void main(String[] args) {
        final JFrame frame = new JFrame(TestTextArea.class.getName());
        frame.setDefaultCloseOperation(3);
        final KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0 );
        JTextArea messageTextArea = new JTextArea(3,2) {
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (ks.equals(enterKey)) return false;
                return super.processKeyBinding(ks, e, condition, pressed);
            }
        };
        messageTextArea.setEditable(true);
        messageTextArea.setFont(new Font("Helvetica", Font.PLAIN, 14));
        messageTextArea.setSelectedTextColor(Color.blue);
        messageTextArea.setSelectionColor(Color.lightGray);
        frame.getContentPane().add(messageTextArea);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}