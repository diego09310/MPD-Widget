package widget;

public class MPDServer {
	private String name;
	private String ip;
	private int port;
	private String pass;
	
	public MPDServer(String name, String ip, int port, String pass) {
		this.setName(name);
		this.ip = ip;
		this.port = port;
		this.pass = pass;
	}
	
	public MPDServer() {
		this ("default", "localhost", 6600, null);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "name=" + name + ", ip=" + ip + ", port=" + port
				+ ", pass=" + pass;
	}
	public String toJson() {
		if(pass!=null)
			return ",\n	{\"name\": \""+ name + "\",\n	 \"ip\": \"" + ip + "\",\n	 \"port\": " + port + ",\n	 \"pass\": \"" + pass + "\"\n	}";
		else 
			return ",\n	{\"name\": \""+ name + "\",\n	 \"ip\": \"" + ip + "\",\n	 \"port\": " + port + ",\n	 \"pass\": -1\n	}";
	}
	
}
