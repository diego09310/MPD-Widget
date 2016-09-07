package widget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bff.javampd.MPD;
import org.bff.javampd.Player;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDResponseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class W3 {

	private JPanel contentPane;
	static MPD mpd = null;
	// private static final String filePath = "servers.json";
	private static final String filePath = "/home/diego/servers.json";

	static MPDServer mpd_server_def = new MPDServer();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// MPDServer[] mpd_server_list = loadServers(filePath);
		ArrayList<MPDServer> mpd_server_list = loadServers(filePath);
		// mpd = connect(mpd_server_def, mpd); //Default (y meter en comboBox)
		try {
			new W3(mpd_server_list);
		} catch (MPDPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 * 
	 * @param mpd_server_list
	 * 
	 * @throws MPDPlayerException
	 */
	// public W3(final MPDServer[] mpd_server_list) throws MPDPlayerException {
	public W3(final ArrayList<MPDServer> mpd_server_list)
			throws MPDPlayerException {
		JFrame mainFrame = new JFrame("MPD Widget");

		// Dimensiones
		int totalHeight = 110;
		int totalWidth = 270;

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(0, 0, totalWidth, totalHeight);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		// contentPane.setBackground(Color.WHITE);
		mainFrame.setContentPane(contentPane);

		mainFrame.getContentPane().setLayout(null);

		JLabel lblSongtitle;
		try {
			lblSongtitle = new JLabel(mpd.getPlayer().getCurrentSong()
					.getTitle());
		} catch (Exception e) {
			lblSongtitle = new JLabel("");
		}
		lblSongtitle.setBounds(0, 0, totalWidth, 18);
		contentPane.add(lblSongtitle);

		JButton btnRewind = new JButton("\u25C0\u25C0");
		btnRewind.setBounds(0, 19, totalWidth / 3, 40);
		contentPane.add(btnRewind);
		btnRewind.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					mpd.getPlayer().playPrev();
				} catch (MPDPlayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		JButton btnPlayPause = new JButton("\u25B6");
		btnPlayPause.setBounds(totalWidth / 3, 19, totalWidth / 3, 40);
		contentPane.add(btnPlayPause);
		btnPlayPause.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					if (mpd.isConnected()) {
						if (mpd.getPlayer().getStatus() == Player.Status.STATUS_PLAYING)
							mpd.getPlayer().pause();
						else
							mpd.getPlayer().play();
					}
				} catch (MPDPlayerException e) {
					e.printStackTrace();
				}

			}
		});

		JButton btnForward = new JButton("\u25B6\u25B6");
		btnForward.setBounds(totalWidth * 2 / 3, 19, totalWidth / 3, 40);
		contentPane.add(btnForward);
		btnForward.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					mpd.getPlayer().playNext();
				} catch (MPDPlayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		final JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(0, 60, 2 * totalWidth / 3, 18);
		// comboBox.setBackground(Color.WHITE);
		contentPane.add(comboBox);
		// for (int i = 0; i < mpd_server_list.length; i++) {
		for (int i = 0; i < mpd_server_list.size(); i++) {
			// comboBox.addItem(mpd_server_list[i].getName());
			comboBox.addItem(mpd_server_list.get(i).getName());
		}
		// mpd = connect(mpd_server_list[0], mpd);
		mpd = connect(mpd_server_list.get(0), mpd);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (mpd.isConnected()) { // Quitar y dejar catch?
						mpd.close();
					}

					// if (comboBox.getSelectedIndex() != 0) // Habilitar esto
					// cuando se meta uno vacío en la lista
					mpd = connect(
							// mpd_server_list[comboBox.getSelectedIndex()],
							mpd_server_list.get(comboBox.getSelectedIndex()),
							mpd);

				} catch (MPDResponseException e) {
					// TODO Auto-generated catch block
					System.out.println("Que no se conecta al server, vaya");
					// mpd.toString());
				}
			}
		});

		JButton btnAdd = new JButton("Añadir");
		btnAdd.setBounds(2 * totalWidth / 3, 60, totalWidth / 3, 18);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// menuNewServer();
				JTextField name = new JTextField(10);
				JTextField ip = new JTextField(10);
				JTextField port = new JTextField(10);
				JTextField pass = new JTextField(10);

				JPanel pan = new JPanel();
				pan.add(new JLabel("Name: "));
				pan.add(name);
				pan.add(new JLabel("IP: "));
				pan.add(ip);
				pan.add(new JLabel("Port: "));
				pan.add(port);
				pan.add(new JLabel("Password: "));
				pan.add(pass);

				int res = JOptionPane.showConfirmDialog(null, pan, null,
						JOptionPane.OK_CANCEL_OPTION);
				if (res == 0) {
					try {
						MPDServer newServer;
						if (pass.getText() == "") 
							newServer = new MPDServer(name.getText(), ip
								.getText(), Integer.parseInt(port.getText()),
								pass.getText());
						
						else 
							newServer = new MPDServer(name.getText(), ip
									.getText(), Integer.parseInt(port.getText()),
									null);
						

						saveNewServer(newServer, comboBox);
						mpd_server_list.add(newServer);
						System.out.println("Pass: " + newServer.getPass());
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"Se ha introducido un valor de puerto erróneo");
					}
				}
			}
		});

		mainFrame.setVisible(true);

		while (true) {
			refresh(lblSongtitle, btnPlayPause);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static MPD connect(MPDServer mpd_server, MPD mpd) {
		try {
			mpd = new MPD.Builder().server(mpd_server.getIp())
					.port(mpd_server.getPort()).password(mpd_server.getPass())
					.build();
		} catch (MPDConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mpd;

	}

	private static ArrayList<MPDServer> loadServers(String filePath) {
		try {
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray servers = (JSONArray) jsonObject.get("servers");
			ArrayList<MPDServer> mpd_server_list = new ArrayList<MPDServer>();

			// MPDServer[] mpd_server_list = new MPDServer[servers.size()];
			for (int i = 0; i < servers.size(); i++) {
				if (((JSONObject) jsonParser.parse(servers.get(i).toString()))
						.get("pass").getClass().equals("".getClass())) {
					// mpd_server_list[i] = new MPDServer(
					mpd_server_list.add(new MPDServer(((JSONObject) jsonParser
							.parse(servers.get(i).toString())).get("name")
							.toString(), ((JSONObject) jsonParser.parse(servers
							.get(i).toString())).get("ip").toString(),
							Integer.parseInt(((JSONObject) jsonParser
									.parse(servers.get(i).toString())).get(
									"port").toString()),
							((JSONObject) jsonParser.parse(servers.get(i)
									.toString())).get("pass").toString()));
				} else {
					// mpd_server_list[i] = new MPDServer(
					mpd_server_list.add(new MPDServer(((JSONObject) jsonParser
							.parse(servers.get(i).toString())).get("name")
							.toString(), ((JSONObject) jsonParser.parse(servers
							.get(i).toString())).get("ip").toString(),
							Integer.parseInt(((JSONObject) jsonParser
									.parse(servers.get(i).toString())).get(
									"port").toString()), null));
				}
			}
			return mpd_server_list;

			// String name1 = jsonObject.getJSONObject("name");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out
					.println("No se encontró el fichero de servidores. ¿Desea crear uno?");

			// MPDServer[] mpd_server_list = { new MPDServer("", "", 0, "") };
			ArrayList<MPDServer> mpd_server_list = new ArrayList<MPDServer>();
			mpd_server_list.add(new MPDServer("", "", 0, ""));
			return mpd_server_list;
			// e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void refresh(JLabel lblSongtitle, JButton btnPlayPause)
			throws MPDPlayerException {
		try {
			lblSongtitle.setText(mpd.getPlayer().getCurrentSong().getTitle());
			if (mpd.getPlayer().getStatus() == Player.Status.STATUS_PLAYING)
				btnPlayPause.setText("\u2590 \u258c");
			else
				btnPlayPause.setText("\u25B6");
		} catch (Exception e) {
			lblSongtitle.setText("No se pudo conectar con el servidor");
		}
	}

	private void saveNewServer(MPDServer mpdServer, JComboBox<String> comboBox) {
		comboBox.addItem(mpdServer.getName());
		System.out.println(mpdServer.toJson());
	}

}
