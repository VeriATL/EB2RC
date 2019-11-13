package fr.loria.mosel.rodin.eb2rc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import fr.loria.mosel.rodin.eb2rc.core.datastructure.bEvent;
import fr.loria.mosel.rodin.eb2rc.core.datastructure.bRodinProject;
import fr.loria.mosel.rodin.eb2rc.core.printer.PythonPrinter;

public final class RodinUtil {

    private RodinUtil() {
        throw new IllegalStateException("This class should not be initialized");
    }
    
    /**
     * Returns the absolute path of the platform-based {@code uri}.
     *
     * @param ?
     *
     * @return ?
     */
    public static ArrayList<String> imports(String s) {
    	
    	String[] items = s.split("\n\r");
    	ArrayList<String> rtn = new ArrayList<String>();
    	for(String item : items) {
    		String[] item_split = item.split("\\|");
    		if(item_split[0].toLowerCase().startsWith("py")) {
    			rtn.add(item_split[1]);
    		}
    	}
    	
    	return rtn;
    }
    
    /**
     * Returns the absolute path of the platform-based {@code uri}.
     *
     * @param ?
     *
     * @return ?
     */
    public static Map<String,String> externals(String s) {
    	
    	String[] items = s.split("\n\r");
    	HashMap<String,String> rtn = new HashMap<String,String>();
    	for(String item : items) {
    		String[] item_split = item.split("\\|", 3);
    		if(item_split[0].toLowerCase().startsWith("rodin")) {
    			rtn.put(item_split[1],item_split[2]);
    		}
    	}
    	
    	return rtn;
    }
    
    /**
     * Returns the absolute path of the platform-based {@code uri}.
     *
     * @param ?
     *
     * @return ?
     * @throws Exception 
     */
    public static String rcode(String proj, String config) throws Exception {
    	String[] cs = config.split("\\|");
    	
		if(cs.length!=4) {
			throw new Exception(String.format("Code Generation Failed ___%s___. Please Check Configuration. \n"
					+ "Signature format: name(parameters;results)", proj));
		}
		
		String cv = cs[0];
		String sig = cs[1];
		String start = cs[2];
		String symbol = cs[3];
		
		IProject task = findRodinProj(proj);
		
		bRodinProject rodin = new bRodinProject(task, cv, sig, start, symbol);
		
		if(rodin.project() == null) {
			throw new Exception(String.format("___%s___ not a Rodin Project. Code Generation Abort!", proj));
		}else {
			try {
				bEvent init = rodin.controlFlowAnalysis();
				
				if(init != null) {
					PythonPrinter py = new PythonPrinter(init);								
					return py.toCode(null);
				}else {
					throw new Exception(String.format("at ___%s___ : Control Flow Analysis Failed. Please Check Machine.", proj));
				}
				
			} catch (Exception e) {
				throw new Exception(String.format("at ___%s___ : error %s", proj, e.toString()));			
			}
		}
		
    }

	private static IProject findRodinProj(String proj) throws Exception {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		try {
			final IRodinDB rodinDB = RodinCore.valueOf(workspace.getRoot());
			final IRodinProject rodinProject = rodinDB.getRodinProject(proj);
			final IProject project = rodinProject.getProject();
			return project;
		} catch (Exception e) {
			throw new Exception(String.format("Cannot find project ___%s___  in workspace.", proj));         
		}
	}
    
    
    
    
}
