package fr.loria.mosel.rodin.eb2rc.core.util;

import java.util.HashMap;
import java.util.Map;

public class SystemUtil {

	/*
	 * Return os name 
	 * */
	private static String os() {
		String name = System.getProperty("os.name").toLowerCase();

		if (name.contains("win")) {
			name = "win";
		} else if (name.contains("mac")) {
			name = "osx";
		} else {
			name = "linux";
		}

		return name;
	}
	
	/*
	 * Return os architecture
	 * */
	private static String arch() {
		String arch = System.getProperty("os.arch").toLowerCase();

		if (arch.contains("64")) {
			arch = "64";
		} else {
			arch = "32";
		}

		return arch;
	}
	
	/*
	 * Return charset, based on the current operation system
	 * */
	public static Map<String, String> charset() {
		String os = os();
		Map<String, String> charset = new HashMap<String, String>();
		
		charset.put("linebreak", "\n");
		
		if(os.contains("win")) {
			
		} else {
			
		}
			
		return charset;		
	}
}
