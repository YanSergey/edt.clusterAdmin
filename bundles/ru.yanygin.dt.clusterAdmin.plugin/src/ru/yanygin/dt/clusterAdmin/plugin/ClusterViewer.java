package ru.yanygin.dt.clusterAdmin.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.model.IConnectionString;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.InfobaseType;
import com._1c.g5.v8.dt.platform.services.model.Section;
import com._1c.g5.v8.dt.platform.services.model.ServerConnectionString;
import com._1c.g5.wiring.ServiceAccess;
import com._1c.g5.wiring.ServiceSupplier;

public class ClusterViewer extends ViewPart {

	public ClusterViewer() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent) {

		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		TreeColumn columnServer = new TreeColumn(tree, SWT.LEFT);
		columnServer.setText("Cluster name");
		columnServer.setWidth(200);
		
		TreeColumn columnPing = new TreeColumn(tree, SWT.CENTER);
		columnPing.setText("Ping");
		columnPing.setWidth(200);
		
		TreeColumn columnBase = new TreeColumn(tree, SWT.RIGHT);
		columnBase.setText("Base name");
		columnBase.setWidth(200);
		
		Button btn = new Button(parent, 0);
		btn.setText("Fill server list");
		
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Image serverIcon = getImage(parent.getDisplay(), "/icons/server_down_24.png");
			
				List<String> servers = searchServersFromInfobases();
		
				for (int i = 0; i < servers.size(); i++) {
					TreeItem item = new TreeItem(tree, SWT.NONE);
					item.setText(new String[] { servers.get(i) });
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
		});
		
		
		
		
		
		
		
		

		
	}

	private List<String> searchServersFromInfobases() {
		
		List<String> servers = new ArrayList<>();

		List<Section> infoBasesSection = getInfobaseManager().getAll(true);
		
		infoBasesSection.forEach(ib -> {
			if (ib instanceof InfobaseReference && ((InfobaseReference) ib).getInfobaseType() == InfobaseType.SERVER) {
				InfobaseReference ib1 = (InfobaseReference) ib;
				ServerConnectionString cs = (ServerConnectionString) ib1.getConnectionString();
				servers.add(cs.getServer());
			}
		});
		servers.sort(null);
		return servers;
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
	
}
