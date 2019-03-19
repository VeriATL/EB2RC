package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.loria.mosel.rodin.eb2rc.ui.preference.EB2RCConstants;

public class GenericAction {

	public PluginPreference read() {
		ScopedPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "fr.loria.mosel.eventb.eb2rc.config.ui");
						
		String cv = store.getString(EB2RCConstants.CV);
		String sig = store.getString(EB2RCConstants.SIG);
		String start = store.getString(EB2RCConstants.START);
		String symbol = store.getString(EB2RCConstants.SYMBOL);
		
		return new PluginPreference(cv, sig, start, symbol);
	}
}
