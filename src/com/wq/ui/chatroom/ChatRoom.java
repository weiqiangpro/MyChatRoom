package com.wq.ui.chatroom;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.wq.ui.MyLable.ButtonJLabel;
import com.wq.ui.MyLable.UserJLabel;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoom extends JFrame {

	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		new ChatRoom().setVisible(true);

	}

	public ChatRoom() {
		this.setTitle("致一聊天室");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 869, 670);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.18);
		splitPane.setDividerSize(0);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.8);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setDividerSize(0);
		splitPane.setRightComponent(splitPane_1);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setResizeWeight(0.7);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setRightComponent(splitPane_2);
		splitPane_2.setDividerSize(0);

		JPanel panel_2 = new JPanel();
		splitPane_2.setLeftComponent(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);

		JPanel panel_3 = new JPanel();
		splitPane_2.setRightComponent(panel_3);
		panel_3.setLayout(null);

		ButtonJLabel send = new ButtonJLabel("    发送");
		send.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				System.out.println("发送");
			}
		});
		send.setBounds(594, 0, 69, 34);
		panel_3.add(send);
		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_3.setResizeWeight(0.93);
		splitPane_1.setLeftComponent(splitPane_3);
		splitPane_3.setDividerSize(0);
		JPanel panel = new JPanel();
		splitPane_3.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane_4 = new JSplitPane();
		splitPane_4.setResizeWeight(0.03);
		splitPane_4.setDividerSize(0);
		splitPane_4.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel.add(splitPane_4, BorderLayout.CENTER);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_4.setRightComponent(scrollPane_1);

		JTextArea txtrSdfdsfs = new JTextArea();
		txtrSdfdsfs.setText("sdfdsfs");
		txtrSdfdsfs.setEditable(false);
		scrollPane_1.setViewportView(txtrSdfdsfs);

		JLabel lblNewLabel_3 = new JLabel("New label");
		splitPane_4.setLeftComponent(lblNewLabel_3);

		JPanel panel_1 = new JPanel();
		splitPane_3.setRightComponent(panel_1);
		panel_1.setLayout(null);

		ButtonJLabel	history = new ButtonJLabel("  历史纪录");
		history.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("历史纪录");
			}
		});
		history.setBounds(599, 0, 72, 33);
		panel_1.add(history);

		JPanel panel_4 = new JPanel();
		splitPane.setLeftComponent(panel_4);
		panel_4.setLayout(null);

		UserJLabel lblNewLabel = new UserJLabel("群聊",0);
		panel_4.add(lblNewLabel);
		UserJLabel user_1 = new UserJLabel("用户一",1);
		panel_4.add(user_1);
		UserJLabel user_2 = new UserJLabel("用户二",2);
		panel_4.add(user_2);
		this.setResizable(false);
	}
	

}
