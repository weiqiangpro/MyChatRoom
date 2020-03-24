package com.wq.ui.MyLable;

import com.wq.ui.utils.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class EnterAndExitLabel extends JLabel {
    protected EnterAndExitLabel(String text) {
        super(text);
    }

    protected void addEnterAndExit() {
        this.setForeground(Color.BLACK);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        this.setCursor(Constant.HAND);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                EnterAndExitLabel.this.setBackground(Color.gray);

            }
            @Override
            public void mouseExited(MouseEvent e) {
                EnterAndExitLabel.this.setBackground(Color.WHITE);
            }
        });
    }
}
