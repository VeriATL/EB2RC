package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

public class PluginPreference {
	
	
	private String cv;	// control label name
	private String sig;	// signature of developed algorithm
	private String start;// constant of initial control variable assigned to
	private String symbol; // split symbol when analysis event's name
	
	
	public PluginPreference(String cv, String sig, String start, String symbol){
		this.cv = cv;
		this.sig = sig;
		this.start = start;
		this.symbol = symbol;
	}


	public String cv() {
		return cv;
	}


	public void setCv(String cv) {
		this.cv = cv;
	}


	public String sig() {
		return sig;
	}


	public void setSig(String sig) {
		this.sig = sig;
	}


	public String start() {
		return start;
	}


	public void setStart(String start) {
		this.start = start;
	}


	public String symbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
