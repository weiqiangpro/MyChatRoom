package com.wq.ui.chatroom;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
		this.setTitle("致一聊天室");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 300, 430, 280);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JButton btnNewButton = new JButton("游客");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("游客");
				Login.this.dispose();
				ChatRoom  chat = new ChatRoom();
				chat.setVisible(true);
			}
		});
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setEnabled(true);
		btnNewButton.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		btnNewButton.setBounds(91, 166, 93, 39);
		contentPane.add(btnNewButton);
		
		JButton button = new JButton("登录");
		button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("登录");
					JOptionPane.showMessageDialog(null, "该功能未实现", "", JOptionPane.ERROR_MESSAGE);
				}
			});
		button.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		button.setBounds(231, 166, 93, 39);
		contentPane.add(button);
		
		JLabel label = new JLabel("用户名");
		label.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		label.setBounds(68, 43, 68, 30);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("密    码");
		label_1.setFont(new Font("微软雅黑", Font.PLAIN, 17));
		label_1.setBounds(68, 98, 68, 30);
		contentPane.add(label_1);

		JTextField textField = new JTextField();
		textField.setBounds(135, 46, 189, 30);
		contentPane.add(textField);
		textField.setColumns(10);

		JTextField textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(135, 101, 189, 30);
		contentPane.add(textField_1);
		this.setResizable(false);
	}
}
