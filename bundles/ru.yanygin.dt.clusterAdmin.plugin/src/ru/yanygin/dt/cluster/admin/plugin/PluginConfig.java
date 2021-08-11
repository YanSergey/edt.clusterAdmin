package ru.yanygin.dt.cluster.admin.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import com.google.gson.annotations.SerializedName;

public class PluginConfig {
	@SerializedName("servers")
	public 	Map<String, ServerConfig> serversMap = new HashMap<>();

	public List<String> addNewServers(List<String> servers) {

		List<String> addedServers = new ArrayList<>();

		for (String serverName : servers) {
			if (!this.serversMap.containsKey(serverName)) {
				ServerConfig serverConfig = new ServerConfig(serverName);
				this.serversMap.put(serverName, serverConfig);

				addedServers.add(serverName);
			}
		}

		return addedServers;
	}

	public class ServerConfig {
		
		@SerializedName("serverAddress")
		public String serverAddress;
		
		@SerializedName("managerPort")
		public int managerPort;
		
		@SerializedName("remoteRasPort")
		public int remoteRasPort;
		
		@SerializedName("useLocalRas")
		public boolean useLocalRas;
		
		@SerializedName("localRasPort")
		public int localRasPort;
		
		@SerializedName("localRasV8version")
		public String localRasV8version;
		
		@SerializedName("autoconnect")
		public boolean autoconnect;

		public ServerConfig(String serverName) {
			this.serverAddress = getServerName(serverName);
			this.managerPort = getManagerPort(serverName);
			this.remoteRasPort = getRemoteRASPort(serverName);
			this.useLocalRas = false;
			this.localRasPort = 0;
			this.localRasV8version = "";
			this.autoconnect = false;
		}

		public String getServerName() {
			return serverAddress.concat(":").concat(Integer.toString(remoteRasPort));
		}

		public String getManagerPortAsString() {
			return Integer.toString(managerPort);
		}

		public String getRemoteRasPortAsString() {
			return Integer.toString(remoteRasPort);
		}

		public String getLocalRasPortAsString() {
			return Integer.toString(localRasPort);
		}
		
		public void setNewServerProperties(String serverAddress,
											int managerPort,
											int remoteRasPort,
											boolean useLocalRas,
											int localRasPort,
											String localRasV8version,
											boolean autoconnect) {
			this.serverAddress = serverAddress;
			this.managerPort = managerPort;
			this.remoteRasPort = remoteRasPort;
			this.useLocalRas = useLocalRas;
			this.localRasPort = localRasPort;
			this.localRasV8version = localRasV8version;
			this.autoconnect = autoconnect;
		}
		
		private String getServerName(String serverAddress) {
			String serverName;
			String[] ar = serverAddress.split(":");
			if (ar.length > 0) {
				serverName = ar[0];
			} else {
				serverName = "localhost";
			}
			
			return serverName;
		}
		
		private int getRemoteRASPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1545;
			} else {
				port = Integer.parseInt(ar[1].substring(0, ar[1].length()-1).concat("5"));
			}
			return port;
		}
		
		private int getManagerPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1541;
			} else {
				port = Integer.parseInt(ar[1]);
			}
			return port;
		}


	}
}


