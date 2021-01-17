package ru.yanygin.dt.cluster.admin.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.prefs.Preferences;

import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.InfobaseType;
import com._1c.g5.v8.dt.platform.services.model.Section;
import com._1c.g5.v8.dt.platform.services.model.ServerConnectionString;
import com._1c.g5.wiring.ServiceAccess;
import com._1c.g5.wiring.ServiceSupplier;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.yanygin.dt.cluster.admin.plugin.PluginConfig.ServerConfig;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.MenuItem;

//import ru.yanygin.dt.clusterAdmin.plugin.MyConfiguration;

public class ClusterViewer extends ViewPart {
	Image serverIcon;
	Image serverIconUp;
	Image serverIconDown;

	public ClusterViewer() {
		// TODO Auto-generated constructor stub
	}
	Tree serversTree;
	Composite mainForm;
	List<String> allServers = new ArrayList<>();
	PluginConfig pluginConfig = new PluginConfig();
	
	@Override
	public void createPartControl(Composite parent) {
		this.mainForm = parent;
		
//		Button btn = new Button(parent, 0);
//		btn.setText("Fill server list");
//		
//		btn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				fillServersList(parent);
//			}
//		});
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		// Toolbar
		ToolBar toolBar = new ToolBar(sashForm, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolBarItemFindNewServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemFindNewServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				findNewServers();
			}
		});
		toolBarItemFindNewServers.setText("Find new Servers");

		ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectToServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectToServers();
			}
		});
		toolBarItemConnectToServers.setText("Connect to servers");

		serversTree = new Tree(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		serversTree.setHeaderVisible(true);
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Cluster name");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
		columnPing.setText("RAS port");
		columnPing.setWidth(60);
		
		TreeColumn columnBase = new TreeColumn(serversTree, SWT.LEFT);
		columnBase.setText("Base name");
		columnBase.setWidth(200);
		
		Menu menu = new Menu(serversTree);
		serversTree.setMenu(menu);
		
		MenuItem menuItemEditServer = new MenuItem(menu, SWT.NONE);
		menuItemEditServer.setText("Edit Server");
		menuItemEditServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] item = serversTree.getSelection();
				if (item.length == 0)
					return;
				
				ServerConfig serverConfig = (ServerConfig) item[0].getData("ServerConfig");
				EditServerConnectionDialog connectionDialog;
//				try {
					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), serverConfig);
//				} catch (Exception e1) {
//					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
//					return;
//				}
				
				int dialogResult = connectionDialog.open();
				if (dialogResult != 0) {
					// перерисовать в дереве
//					return null;
				}

			}
		});
		
		MenuItem menuItemAddNewServer = new MenuItem(menu, SWT.NONE);
		menuItemAddNewServer.setText("Add new Server");
		menuItemAddNewServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				String newServerAddress = "newServerAddress";
//				ServerConfig serverConfig = new ServerConfig(newServerAddress);
//				EditServerConnectionDialog connectionDialog;
////				try {
//					connectionDialog = new EditServerConnectionDialog(mainForm.getDisplay().getActiveShell(), serverConfig);
////				} catch (Exception e1) {
////					Activator.log(Activator.createErrorStatus(e1.getLocalizedMessage(), e1));
////					return;
////				}
//				
//				int dialogResult = connectionDialog.open();
//				if (dialogResult == 0) {
//					serverConfig = null;
//				}
//				else {
//					// добавить в дерево
//					pluginConfig.serversMap.put(serverConfig.getServerName(), serverConfig);
//					
//					List<String> addedServers = new ArrayList<>();
//					addedServers.add(serverConfig.getServerName());
//					fillServersList(addedServers);
////					return null;
//				}
			}
		});
		
		MenuItem menuItemDeleteServer = new MenuItem(menu, SWT.NONE);
		menuItemDeleteServer.setText("Delete Server");
		menuItemDeleteServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		MenuItem menuItemAutoconnectEnable = new MenuItem(menu, SWT.CHECK);
		menuItemAutoconnectEnable.setText("Autoconnect");
		menuItemAutoconnectEnable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		MenuItem mntmNewRadiobutton = new MenuItem(menu, SWT.RADIO);
		mntmNewRadiobutton.setText("New RadioButton");
		
		MenuItem mntmNewRadiobutton_1 = new MenuItem(menu, SWT.RADIO);
		mntmNewRadiobutton_1.setText("New RadioButton");
		sashForm.setWeights(new int[] {1, 4});
		
		initIcon();
		readSavedKnownServers();
		fillServersList();

	}

	private void initIcon() {
		serverIcon = getImage(mainForm.getDisplay(), "/icons/server_24.png");
		serverIconUp = getImage(mainForm.getDisplay(), "/icons/server_up_24.png");
		serverIconDown = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");
	}
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	protected IInfobaseManager getInfobaseManager() {
		ServiceSupplier<IInfobaseManager> infobaseManagerSupplier = ServiceAccess.supplier(IInfobaseManager.class,
				Activator.getDefault());
		IInfobaseManager infobaseManager = infobaseManagerSupplier.get();
		infobaseManagerSupplier.close();
		return infobaseManager;
	}
	
	private Image getImage(Device device, String name) {
		return new Image(device, this.getClass().getResourceAsStream(name));
	}

	private void findNewServers() {
//		Boolean addedServers = false;

		List<String> foundServers = searchServersFromInfobasesList();

		List<String> newServers = pluginConfig.addNewServers(foundServers);
		
		fillServersList(newServers);
		
//		for (String server : newServers) {
//			
//			if (!allServers.contains(server)) {
//				TreeItem item = new TreeItem(serversTree, SWT.NONE);
//				item.setText(new String[] { server, Integer.toString(getRASPort(server)) });
//				item.setData("ServerName", getServerName(server));
//				item.setData("RASPort", getRASPort(server));
//				item.setImage(serverIcon);
//				
//				allServers.add(server);
//				addedServers = true;
//			}
//			
//			
//			
////			for (int j = 0; j < 4; j++) {
////				TreeItem subItem = new TreeItem(item, SWT.NONE);
////				subItem.setText(new String[] { "subitem " + j, "jklmnop", "qrs" });
////				subItem.setImage(getDefaultImage());
////				for (int k = 0; k < 4; k++) {
////					TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
////					subsubItem.setText(new String[] { "subsubitem " + k, "tuv", "wxyz" });
////					subsubItem.setImage(getTitleImage());
////				}
////			}
//		}

		if (!newServers.isEmpty()) {
			saveKnownServers();
		}
	}

	private List<String> searchServersFromInfobasesList() {

		List<String> foundServers = new ArrayList<>();

		List<Section> infoBasesList = getInfobaseManager().getAll(true);

		infoBasesList.forEach(ib -> {
			if (ib instanceof InfobaseReference && ((InfobaseReference) ib).getInfobaseType() == InfobaseType.SERVER) {
				InfobaseReference ib1 = (InfobaseReference) ib;
				ServerConnectionString cs = (ServerConnectionString) ib1.getConnectionString();
				String newServer = cs.getServer();
				if (!foundServers.contains(newServer)) {
					foundServers.add(newServer);
				}
			}
		});
		foundServers.sort(null);
		return foundServers;
	}

	private void fillServersList() {
//		if (pluginConfig == null || pluginConfig.serversMap.isEmpty()) {
		if (pluginConfig.serversMap.isEmpty()) {
			return;
		}
		pluginConfig.serversMap.forEach((server, config) -> {
			addServerItemInServersTree(config);
		});

//			for (ServerConfig server : pluginConfig.servers) {
//				TreeItem item = new TreeItem(serversTree, SWT.NONE);
//				item.setText(new String[] { server.serverAddress, Integer.toString(server.rasPort) });
//				item.setData("ServerName", server.getServerName());
//				item.setData("RASPort", server.rasPort);
//				item.setImage(serverIcon);
//				item.setChecked(true);
//				
//			}
//			for (String server : pluginConfig.servers) {
//	
//				TreeItem item = new TreeItem(serversTree, SWT.NONE);
//				item.setText(new String[] { server, Integer.toString(getRASPort(server)) });
//				item.setData("ServerName", getServerName(server));
//				item.setData("RASPort", getRASPort(server));
//				item.setImage(serverIcon);
//				item.setChecked(true);
//			}

	}

	private void fillServersList(List<String> newServers) {
		for (String newServer : newServers) {
			addServerItemInServersTree(pluginConfig.serversMap.get(newServer));
		}
	}

	private void addServerItemInServersTree(ServerConfig config) {
		TreeItem item = new TreeItem(serversTree, SWT.NONE);
		item.setText(new String[] { config.serverAddress, config.getRemoteRasPortAsString() });
		item.setData("ServerName", config.getServerName()); // del
		item.setData("RASPort", config.remoteRasPort); // del
		item.setData("ServerConfig", config);
		item.setImage(serverIcon);
		item.setChecked(false);
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
		int rasPort;
		String[] ar = serverAddress.split(":");
		if (ar.length == 1) {
			rasPort = 1545;
		} else {
			rasPort = Integer.parseInt(ar[1].substring(0, ar[1].length()-1).concat("5"));
		}
		
		return rasPort;
	}

	private void connectToServers() {
			
		AgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
		AgentAdminUtil conn = new AgentAdminUtil(factory);
		
		TreeItem[] servers = serversTree.getItems();
		for (TreeItem server : servers) {
			String serverAddress = (String) server.getData("ServerName");
			int rasPort = (int) server.getData("RASPort");
			
			try {
				conn.connect(serverAddress, rasPort, 20);
				
//				List<IClusterInfo> clusterInfoList = conn.getClusterInfoList();
//				IClusterInfo cluster = clusterInfoList.get(0);
//				UUID clusterId = clusterInfoList.get(0).getClusterId();
//
//
//				//auth
//				conn.authenticateCluster(clusterId, "", "");
//				
//				List<IInfoBaseInfoShort> infobases = conn.getInfoBaseShortInfos(clusterId);
//				IInfoBaseInfo infoBaseInfo = conn.getInfoBaseInfo(clusterId, infobases.get(1).getInfoBaseId());
//				String descr = infoBaseInfo.getDescr();
//				
////				infoBaseInfo.
//
//				UUID infobaseId = infoBaseInfo.getInfoBaseId();
//				conn.terminateAllSessionsOfInfobase(clusterId, infobaseId);
//				
//				conn.terminateAllSessions(clusterId);
				
				server.setImage(serverIconUp);
				conn.disconnect();
			}
			catch (Exception e) {
				server.setImage(serverIconDown);
//				return;
			}
//			finally {
//			}
			
		}

	}
	
	private void readSavedKnownServers() {
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		String knownServers = store.getString("ServersList");
//		if (!knownServers.isBlank()) {
//			allServers = Arrays.asList(knownServers.split(","));
//		}
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String knownServersPath = store.getString("KnownServersPath");
		File configFileName;
		JsonReader jsonReader;
		if (knownServersPath.isBlank()) {
			configFileName = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(".metadata")
					.append("edtclusteradmin.config").toFile();
		} else {
			configFileName = new File(knownServersPath);
		}

		try {
			jsonReader = new JsonReader(
					new InputStreamReader(new FileInputStream(configFileName), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		pluginConfig = new Gson().fromJson(jsonReader, PluginConfig.class);

		if (pluginConfig == null) {
			pluginConfig = new PluginConfig();
		}
	}

	private void saveKnownServers() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		String knownServersPath = store.getString("KnownServersPath");
		File configFileName;
		if (knownServersPath.isBlank()) {
			configFileName = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(".metadata")
					.append("edtclusteradmin.config").toFile();
			store.setValue("KnownServersPath", configFileName.getPath());
		} else {
			configFileName = new File(knownServersPath);
		}

		JsonWriter jsonWriter;
		try {
			jsonWriter = new JsonWriter(
					new OutputStreamWriter(new FileOutputStream(configFileName), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		new Gson().toJson(pluginConfig, pluginConfig.getClass(), jsonWriter);
		
		try {
			jsonWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		allServers = Arrays.asList(knownServersPath.split(","));
	
		
	}
}
