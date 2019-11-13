package fr.loria.mosel.rodin.eb2rc.core.datastructure;


public class bPreferencePage {

	private String cv;
	private String sig;
	private String start;
	private String symbol;
	
	public bPreferencePage(String cv, String sig, String start, String symbol) {
		this.cv = cv;
		this.sig = sig;
		this.start = start;
		this.symbol = symbol;
	}
	
	// read control variable name from store
	public String cv() {
		return cv;
	}

	// read signature name from store
	public String sig() {
		return sig;
	}

	// read start symbol from store
	public String start() {
		return start;
	}

	// read split symbol from store
	public String symbol() {
		return symbol;
	}
}
