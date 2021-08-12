/**
 * Copyright (C) 2021, Yanygin Sergey.
 */
package ru.yanygin.dt.cluster.admin.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ru.yanygin.clusterAdminLibrary.ClusterProvider;
import ru.yanygin.clusterAdminLibraryUI.ViewerArea;

public class ClusterViewer
    extends ViewPart
{

    public ClusterViewer()
    {
        // TODO Auto-generated constructor stub
    }

    Composite mainForm;

    ClusterProvider clusterProvider = new ClusterProvider();

    @Override
    public void createPartControl(Composite parent)
    {
        this.mainForm = parent;
//        ViewerArea container = new ViewerArea(this.mainForm, SWT.NONE, null, null, clusterProvider); // работает
//
//        ToolBar toolBar = this.getToolBarManager().createControl(parent);
//
//        SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
//
//        Composite composite = new Composite(parent, SWT.NONE);
//        RowLayout layout = new RowLayout(SWT.VERTICAL);
//        layout.wrap = true;
//        composite.setLayout(layout);
//
//        Menu mainMenu = new Menu(composite);
//        composite.setMenu(mainMenu);
//        mainMenu.setVisible(true);
//
//        ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
//        toolBar.setBounds(0, 0, 0, 23); // Для отладки

        ViewerArea container = new ViewerArea(this.mainForm, SWT.NONE, null, null, clusterProvider);

    }

    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub

    }
}
