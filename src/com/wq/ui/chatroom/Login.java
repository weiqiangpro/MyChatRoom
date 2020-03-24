package com.wq.ui.chatroom;

import com.wq.ui.MyLable.ButtonJLabel;
import com.wq.ui.MyLable.EnterAndExitLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new Login().setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		setUndecorated(true);
		this.setTitle("致一聊天室");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 350, 430, 280);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel labe = new JLabel("欢迎来到致一聊天室");
		labe.setBounds(100,20,230,20);
		labe.setFont(new Font("微软雅黑", Font.PLAIN, 23));
		labe.setForeground(Color.WHITE);
		contentPane.add(labe);
		JButton btnNewButton = new JButton("退出");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Login.this.dispose();

			}
		});
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setEnabled(true);
		btnNewButton.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		btnNewButton.setBounds(91, 216, 93, 39);
		contentPane.add(btnNewButton);
		
		ButtonJLabel button = new ButtonJLabel("登录");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Login.this.dispose();
				ChatRoom  chat = new ChatRoom();
				chat.setVisible(true);
				//Login.this.setExtendedState(JFrame.ICONIFIED);
			}
		});
		button.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		button.setBounds(231, 216, 93, 39);
		contentPane.add(button);
		
		JLabel label = new JLabel("用户名");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		label.setBounds(68, 93, 68, 30);

		contentPane.add(label);
		
		JLabel label_1 = new JLabel("密    码");
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		label_1.setBounds(68, 148, 68, 30);
		contentPane.add(label_1);

		JTextField textField = new JTextField();
		textField.setBounds(135, 96, 189, 30);
		contentPane.add(textField);
		textField.setColumns(10);

		JTextField textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(135, 151, 189, 30);
		contentPane.add(textField_1);
		ImageIcon imageIcon = new ImageIcon("/home/wq/IdeaProjects/MyChat/out/production/chat/3.png");
		JLabel jLabel = new JLabel(imageIcon);
		jLabel.setBounds(0, 0, 430, 280);
		contentPane.add(jLabel);
		this.setResizable(false);
	}
}
