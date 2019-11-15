package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.loria.mosel.rodin.eb2rc.ui.preference.EB2RCConstants;

public class GenericAction {

	// Returns preference page store
	public ScopedPreferenceStore read() {
		ScopedPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "fr.loria.mosel.eventb.eb2rc.config.ui");
		
		return store;
	}
	
	/* accessors to preference store start */
	
	// read control variable name from store
	public String cv() {
		return read().getString(EB2RCConstants.CV);
	}

	// read signature name from store
	public String sig() {
		return read().getString(EB2RCConstants.SIG);
	}

	// read start symbol from store
	public String start() {
		return read().getString(EB2RCConstants.START);
	}

	// read split symbol from store
	public String symbol() {
		return read().getString(EB2RCConstants.SYMBOL);
	}

	/* accessors to preference store end */


	
}
