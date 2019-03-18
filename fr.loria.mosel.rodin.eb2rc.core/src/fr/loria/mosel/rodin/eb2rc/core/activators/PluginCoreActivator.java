package fr.loria.mosel.rodin.eb2rc.core.activators;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PluginCoreActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.loria.mosel.eventb.eb2rc.core"; //$NON-NLS-1$
		
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		PluginCoreActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		PluginCoreActivator.context = null;
	}

}
