package ru.yanygin.dt.cluster.admin.plugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Activator extends AbstractUIPlugin {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "ru.yanygin.dt.clusterAdmin.plugin"; //$NON-NLS-1$
	
	// The shared instance
	private static Activator plugin;
	
	private static final String PLUGIN_PREFIX = "[Cluster Admin] "; //$NON-NLS-1$
	private Injector injector;
	private BundleContext bundleContext;
	
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
		this.bundleContext = context;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	
	protected BundleContext getContext() {
		return bundleContext;
	}
	
	public static Activator getDefault() {
		return plugin;
	}
	
	public synchronized Injector getInjector() {
		if (injector == null)
			injector = createInjector();
		
		return injector;
	}
	
	private Injector createInjector() {
		try {
			return Guice.createInjector(new ExternalDependenciesModule(this));
		} catch (Exception e) {
			log(createErrorStatus("Failed to create injector for " //$NON-NLS-1$
					+ getBundle().getSymbolicName(), e));
			throw new RuntimeException("Failed to create injector for " //$NON-NLS-1$
					+ getBundle().getSymbolicName(), e);
		}
	}
	
	public static IStatus createInfoStatus(String message) {
		return new Status(IStatus.INFO, PLUGIN_ID, 0, PLUGIN_PREFIX.concat(message), (Throwable) null);
	}
	
	public static IStatus createCancelStatus(String message) {
		return new Status(IStatus.CANCEL, PLUGIN_ID, 0, message, (Throwable) null);
	}
	
	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, PLUGIN_PREFIX.concat(message), throwable);
	}
	
	public static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, PLUGIN_PREFIX.concat(message), (Throwable) null);
	}
	
	public static IStatus createErrorStatus(Throwable throwable) {
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, PLUGIN_PREFIX.concat(throwable.getLocalizedMessage()),
				throwable);
	}
	
	public static void log(IStatus status) {
		plugin.getLog().log(status);
	}
}
