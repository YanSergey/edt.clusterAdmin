package ru.yanygin.dt.clusterAdmin.plugin;

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
//	public 	List<ServerConfig> servers = new ArrayList<>();

	public List<String> addNewServers(List<String> servers) {
		
		List<String> addedServers = new ArrayList<>();
		
		for (String serverName : servers) {
			if (!this.serversMap.containsKey(serverName)) {
				ServerConfig serverConfig = new ServerConfig(serverName);
				this.serversMap.put(serverName, serverConfig);
				
				addedServers.add(serverName);
			}
//			if (!this.serversMap.) {
//				TreeItem item = new TreeItem(serversTree, SWT.NONE);
//				item.setText(new String[] { server, Integer.toString(getRASPort(server)) });
//				item.setData("ServerName", getServerName(server));
//				item.setData("RASPort", getRASPort(server));
//				item.setImage(serverIcon);
//				
//				allServers.add(server);
//				addedServers = true;
			}

		//this.serversMap.put(key, value)
		return addedServers;
	}

	public class ServerConfig {
		
		@SerializedName("serverAddress")
		public String serverAddress;
		
		@SerializedName("agentPort")
		public int agentPort;
		
		@SerializedName("rasPort")
		public int rasPort;
		
		@SerializedName("localRas")
		public boolean localRas;
		
		@SerializedName("localRasV8version")
		public String localRasV8version;
		
		@SerializedName("autoconnect")
		public boolean autoconnect;

		public ServerConfig(String serverName) {
			this.serverAddress = getServerName(serverName);
			this.rasPort = getRASPort(serverName);
			this.agentPort = getAgentPort(serverName);
		}

		public String getServerName() {
			return serverAddress.concat(":").concat(Integer.toString(rasPort));
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
		
		private int getRASPort(String serverAddress) {
			int port;
			String[] ar = serverAddress.split(":");
			if (ar.length == 1) {
				port = 1545;
			} else {
				port = Integer.parseInt(ar[1].substring(0, ar[1].length()-1).concat("5"));
			}
			return port;
		}
		
		private int getAgentPort(String serverAddress) {
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


