package widget;

import java.awt.BorderLayout;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDResponseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MPDWidget {

	static MPD mpd = null;
	private static final String filePath = "servers.json";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		List<MPDServer> mpdServers = loadServers();
		try {
			new MPDWidget(mpdServers);
		} catch (MPDPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 * 
	 * @param mpdServers
	 * 
	 * @throws MPDPlayerException
	 */
	public MPDWidget(final List<MPDServer> mpdServers)
			throws MPDPlayerException {
		JFrame mainFrame = new JFrame("MPD Widget");

		// Dimensiones
		int totalHeight = 110;
		int totalWidth = 270;

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(0, 0, totalWidth, totalHeight);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		// contentPane.setBackground(Color.WHITE);
		mainFrame.setContentPane(contentPane);

		mainFrame.getContentPane().setLayout(null);

		JLabel lblSongtitle;
		try {
			lblSongtitle = new JLabel(mpd.getMPDPlayer().getCurrentSong()
					.getTitle());
		} catch (Exception e) {
			lblSongtitle = new JLabel("");
		}
		lblSongtitle.setBounds(0, 0, totalWidth, 18);
		contentPane.add(lblSongtitle);

		JButton btnRewind = new JButton("\u25C0\u25C0");
		btnRewind.setBounds(0, 19, totalWidth / 3, 40);
		contentPane.add(btnRewind);
		btnRewind.addActionListener(evt -> {
			try {
				mpd.getMPDPlayer().playPrev();
			} catch (MPDPlayerException | MPDConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		JButton btnPlayPause = new JButton("\u25B6");
		btnPlayPause.setBounds(totalWidth / 3, 19, totalWidth / 3, 40);
		contentPane.add(btnPlayPause);
		btnPlayPause.addActionListener(evt -> {
			try {
				if (mpd.isConnected()) {
					if (mpd.getMPDPlayer().getStatus() == MPDPlayer.PlayerStatus.STATUS_PLAYING)
						mpd.getMPDPlayer().pause();
					else
						mpd.getMPDPlayer().play();
				}
			} catch (MPDResponseException | MPDConnectionException e) {
				e.printStackTrace();
			}

		});

		JButton btnForward = new JButton("\u25B6\u25B6");
		btnForward.setBounds(totalWidth * 2 / 3, 19, totalWidth / 3, 40);
		contentPane.add(btnForward);
		btnForward.addActionListener(evt -> {
			try {
				mpd.getMPDPlayer().playNext();
			} catch (MPDPlayerException | MPDConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		final JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBounds(0, 60, 2 * totalWidth / 3, 18);
		contentPane.add(comboBox);
		for (MPDServer mpdServer : mpdServers) {
			comboBox.addItem(mpdServer.getName());
		}
		mpd = connect(mpdServers.get(0), mpd);
		comboBox.addActionListener(arg0 -> {
			try {
				if (mpd.isConnected()) { // Quitar y dejar catch?
					mpd.close();
				}

				mpd = connect(
						mpdServers.get(comboBox.getSelectedIndex()),
						mpd);

			} catch (MPDResponseException | MPDConnectionException e) {
				// TODO Auto-generated catch block
				System.out.println("Que no se conecta al server, vaya");
			}
		});

		JButton btnAdd = new JButton("Añadir");
		btnAdd.setBounds(2 * totalWidth / 3, 60, totalWidth / 3, 18);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(evt -> {
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
					if (pass.getText().isEmpty())
						newServer = new MPDServer(name.getText(), ip
							.getText(), Integer.parseInt(port.getText()),
							pass.getText());

					else
						newServer = new MPDServer(name.getText(), ip
								.getText(), Integer.parseInt(port.getText()),
								null);


					saveNewServer(newServer, comboBox);
					mpdServers.add(newServer);
					System.out.println("Pass: " + newServer.getPass());
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Se ha introducido un valor de puerto erróneo");
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

	private static MPD connect(MPDServer mpdServer, MPD mpd) {
		try {
			mpd = new MPD(mpdServer.getIp(), mpdServer.getPort(), mpdServer.getPass());
		} catch (MPDConnectionException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mpd;

	}

	private static List<MPDServer> loadServers() {
		try {
			FileReader reader = new FileReader(MPDWidget.filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray servers = (JSONArray) jsonObject.get("servers");
			List<MPDServer> mpdServers = new ArrayList<>();

			for (Object server : servers) {
				if (((JSONObject) jsonParser.parse(server.toString()))
						.get("pass").getClass().equals("".getClass())) {
					mpdServers.add(new MPDServer(((JSONObject) jsonParser
							.parse(server.toString())).get("name")
							.toString(), ((JSONObject) jsonParser.parse(server.toString())).get("ip").toString(),
							Integer.parseInt(((JSONObject) jsonParser
									.parse(server.toString())).get(
									"port").toString()),
							((JSONObject) jsonParser.parse(server
									.toString())).get("pass").toString()));
				} else {
					mpdServers.add(new MPDServer(((JSONObject) jsonParser
							.parse(server.toString())).get("name")
							.toString(), ((JSONObject) jsonParser.parse(server.toString())).get("ip").toString(),
							Integer.parseInt(((JSONObject) jsonParser
									.parse(server.toString())).get(
									"port").toString()), null));
				}
			}
			return mpdServers;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out
					.println("No se encontró el fichero de servidores. ¿Desea crear uno?");
			List<MPDServer> mpdServers = new ArrayList<>();
			mpdServers.add(new MPDServer("", "", 0, ""));
			return mpdServers;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void refresh(JLabel lblSongtitle, JButton btnPlayPause) {
		try {
			lblSongtitle.setText(mpd.getMPDPlayer().getCurrentSong().getTitle());
			if (mpd.getMPDPlayer().getStatus() == MPDPlayer.PlayerStatus.STATUS_PLAYING)
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
