package ru.yanygin.dt.clusterAdmin.plugin;

import java.util.ArrayList;
import java.util.List;
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

import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.InfobaseType;
import com._1c.g5.v8.dt.platform.services.model.Section;
import com._1c.g5.v8.dt.platform.services.model.ServerConnectionString;
import com._1c.g5.wiring.ServiceAccess;
import com._1c.g5.wiring.ServiceSupplier;
import com._1c.v8.ibis.admin.client.AgentAdminConnectorFactory;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.custom.SashForm;

public class ClusterViewer extends ViewPart {

	public ClusterViewer() {
		// TODO Auto-generated constructor stub
	}
	Tree serversTree;
	Composite mainForm;
	
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
		
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		
		// Toolbar
		ToolBar toolBar = new ToolBar(sashForm, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolBarItemFillServersList = new ToolItem(toolBar, SWT.NONE);
		toolBarItemFillServersList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillServersList();
			}
		});
		toolBarItemFillServersList.setText("Fill server list");

		ToolItem toolBarItemConnectToServers = new ToolItem(toolBar, SWT.NONE);
		toolBarItemConnectToServers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectToServers();
			}
		});
		toolBarItemConnectToServers.setText("Connect to servers");

		serversTree = new Tree(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		serversTree.setHeaderVisible(true);
		TreeColumn columnServer = new TreeColumn(serversTree, SWT.LEFT);
		columnServer.setText("Cluster name");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(serversTree, SWT.CENTER);
		columnPing.setText("RAS port");
		columnPing.setWidth(50);
		
		TreeColumn columnBase = new TreeColumn(serversTree, SWT.RIGHT);
		columnBase.setText("Base name");
		columnBase.setWidth(200);
		
		Menu menu = new Menu(serversTree);
		serversTree.setMenu(menu);
		sashForm.setWeights(new int[] {1, 4});
		
		
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

	private void fillServersList() {
		Image serverIcon = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");

		List<String> servers = searchServersFromInfobases();

		for (String server : servers) {
		//for (int i = 0; i < servers.size(); i++) {
			//String server = servers.get(i);
			
			TreeItem item = new TreeItem(serversTree, SWT.NONE);
			item.setText(new String[] { server, Integer.toString(getRASPort(server)) });
			item.setData("ServerName", getServerName(server));
			item.setData("RASPort", getRASPort(server));
			item.setImage(serverIcon);
			
			
			
//			for (int j = 0; j < 4; j++) {
//				TreeItem subItem = new TreeItem(item, SWT.NONE);
//				subItem.setText(new String[] { "subitem " + j, "jklmnop", "qrs" });
//				subItem.setImage(getDefaultImage());
//				for (int k = 0; k < 4; k++) {
//					TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
//					subsubItem.setText(new String[] { "subsubitem " + k, "tuv", "wxyz" });
//					subsubItem.setImage(getTitleImage());
//				}
//			}
		}
	}

	private List<String> searchServersFromInfobases() {
		
		List<String> servers = new ArrayList<>();

		List<Section> infoBasesSection = getInfobaseManager().getAll(true);
		
		infoBasesSection.forEach(ib -> {
			if (ib instanceof InfobaseReference && ((InfobaseReference) ib).getInfobaseType() == InfobaseType.SERVER) {
				InfobaseReference ib1 = (InfobaseReference) ib;
				ServerConnectionString cs = (ServerConnectionString) ib1.getConnectionString();
				String newServer = cs.getServer();
				if (!servers.contains(newServer)) {
					servers.add(newServer);					
				}
			}
		});
		servers.sort(null);
		return servers;
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
		Image serverIconUp = getImage(mainForm.getDisplay(), "/icons/server_up_24.png");
		Image serverIconDown = getImage(mainForm.getDisplay(), "/icons/server_down_24.png");
			
		AgentAdminConnectorFactory factory = new AgentAdminConnectorFactory();
		AgentAdminUtil conn = new AgentAdminUtil(factory);
		
		TreeItem[] servers = serversTree.getItems();
		for (TreeItem server : servers) {
			String serverAdress = (String) server.getData("ServerName");
			int serverPort = (int) server.getData("RASPort");
			
			try {
				conn.connect(serverAdress, serverPort, 20);
				
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
}
