package com.wq.ui.MyLable;

import com.wq.ui.utils.Constant;

import javax.swing.*;
import java.awt.*;

public class UserJLabel extends EnterAndExitLabel {


	private static final long serialVersionUID = 1L;

	public UserJLabel(String text, int num) {
        super(text);
		this.setOpaque(true);
		this.setForeground(Color.BLACK);
		this.setBackground(Color.WHITE);
		this.setBounds(0, num*Constant.USER_BUTTON_HEIGHT, 139, 50);
    }


}
