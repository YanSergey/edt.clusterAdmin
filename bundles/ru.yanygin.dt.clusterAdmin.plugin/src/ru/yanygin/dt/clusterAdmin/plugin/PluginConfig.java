package ru.yanygin.dt.clusterAdmin.plugin;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PluginConfig {
	@SerializedName("servers")
	public 	Map<String, ServerConfig> servers = new HashMap<>();


	public class ServerConfig {
		
		@SerializedName("serverAddress")
		public String serverAddress;
		
		@SerializedName("rasPort")
		public String rasPort;
		
		@SerializedName("localRas")
		public String localRas;
		
		@SerializedName("localRasv8version")
		public String localRasv8version;
		
		@SerializedName("autoconnect")
		public String autoconnect;
		
	}
}


