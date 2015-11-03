package loon.web.server.mini;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class ServerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private javax.swing.JLabel labMessage;
	private javax.swing.JButton btnOpenURL;
	private javax.swing.JButton btnServerClose;
	private javax.swing.JButton btnServerOpen;
	private javax.swing.JButton btnSelectDir;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField txtWebServerDir;
	private javax.swing.JTextField txtWebServerPort;

	private Server _server;

	private String _message = "";

	public ServerPanel(Server s, String m) {
		this._server = s;
		this._message = m;
		initComponents();
	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		txtWebServerDir = new javax.swing.JTextField();
		txtWebServerPort = new javax.swing.JTextField();
		btnSelectDir = new javax.swing.JButton();
		labMessage = new javax.swing.JLabel();
		btnServerOpen = new javax.swing.JButton();
		btnServerClose = new javax.swing.JButton();
		btnOpenURL = new javax.swing.JButton();

		setLayout(null);

		jLabel1.setText("Server Dir");
		add(jLabel1);
		jLabel1.setBounds(26, 60, 80, 15);

		jLabel2.setText("Server Port");
		add(jLabel2);
		jLabel2.setBounds(26, 22, 80, 15);
		add(txtWebServerDir);
		txtWebServerDir.setBounds(120, 60, 240, 21);

		add(txtWebServerPort);
		txtWebServerPort.setBounds(120, 20, 240, 21);
		txtWebServerDir.setText(_server.getConfig().getWebDir());
		txtWebServerDir.setEditable(false);

		btnSelectDir.setText("Update");
		add(btnSelectDir);
		btnSelectDir.setBounds(380, 60, 100, 25);
		btnSelectDir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser(new File(_server
						.getConfig().getWebDir()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(ServerPanel.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					txtWebServerDir.setText(file.getAbsolutePath());
					_server.getConfig().setWebDir(file.getAbsolutePath());
				}
			}

		});

		labMessage.setForeground(new java.awt.Color(255, 0, 0));
		labMessage.setText(String.format(_message, _server.getConfig()
				.getPort()));
		add(labMessage);
		labMessage.setBounds(25, 120, 460, 20);

		btnServerOpen.setText("Start");
		add(btnServerOpen);
		btnServerOpen.setBounds(380, 170, 100, 30);
		btnServerOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					_server.open();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				btnServerOpen.setEnabled(false);
				btnServerClose.setEnabled(true);
			}
		});

		btnServerClose.setText("Stop");
		add(btnServerClose);
		btnServerClose.setBounds(260, 170, 100, 30);
		btnServerClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					_server.stop();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				btnServerOpen.setEnabled(true);
				btnServerClose.setEnabled(false);
			}
		});

		txtWebServerPort.setText(_server.getConfig().getPort() + "");
		txtWebServerPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				input();
			}

			@Override
			public void keyTyped(KeyEvent e) {
				input();
			}

			private void input() {
				_server.getConfig().setPort(
						Integer.parseInt(txtWebServerPort.getText()));
				labMessage.setText(String.format(_message, _server.getConfig()
						.getPort()));
			}
		});
		btnServerOpen.setEnabled(true);
		btnServerClose.setEnabled(false);

		btnOpenURL.setText("OpenBrowse");
		btnOpenURL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Browse.openURL(String.format("http://127.0.0.1:%s", _server
						.getConfig().getPort()));
			}
		});
		add(btnOpenURL);
		btnOpenURL.setBounds(20, 170, 120, 30);
	}

}
