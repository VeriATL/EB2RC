package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class bRodinProject  {
	
	private bPreferencePage pref;
	private IRodinProject rodin;
	
	public bRodinProject(IProject proj, String cv, String sig, String start, String symbol) { 
		pref = new bPreferencePage(cv, sig, start, symbol);
		rodin = fromIProject(proj);
	}
	
	/* 
	 * Turns a normal IProject into a IRodinProject
	 */
	public IRodinProject fromIProject(IProject proj){		
		String name = proj.getProject().getName();
		return db().getRodinProject(name);
	}
	
	/* 
	 * Retrieve Rodin Database Object
	 */
	public IRodinDB db() {
		return RodinCore.getRodinDB();
	}
	
	/* 
	 * Retrieve default formula factory from Rodin
	 */
	public FormulaFactory factory () {
		return FormulaFactory.getDefault();
	}
	
	/* 
	 * Retrieve preference, see TODO: here for more detail
	 */
	public bPreferencePage preference() {
		return pref;
	}
	
	/* 
	 * Retrieve Rodin project instance
	 */
	public IRodinProject project() {
		return rodin;
	}

	/* 
	 * Retrieve all contexts of a rodin project
	 */
	public ArrayList<IContextRoot> getContexts() throws RodinDBException {	
		ArrayList<IContextRoot> contexts = new ArrayList<IContextRoot>();
		for (IRodinElement element : rodin.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element).getRoot();
				if (root instanceof IContextRoot) {
					contexts.add((IContextRoot) root);
				}
			}
		}
		return contexts;
	}
	
	/* 
	 * Retrieve all machines of a rodin project
	 */
	public ArrayList<IMachineRoot>  getMachines() throws RodinDBException {
		ArrayList<IMachineRoot> machines = new ArrayList<IMachineRoot>();
		for (IRodinElement element : rodin.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element).getRoot();
				if (root instanceof IMachineRoot) {
					machines.add((IMachineRoot) root);
				}
			}
		}
		return machines;
	}
	
	/* 
	 * Retrieve a context in a rodin project with given name
	 */
	public IContextRoot getContextByName(String n) throws RodinDBException
	{
		for(IContextRoot ctx : getContexts()){
			if(ctx.getComponentName().equals(n)){
				return ctx;
			}
		}
		return null;
	}
	
	/* 
	 * Retrieve a machine in a rodin project with given name
	 */
	public IMachineRoot getMachineByName(String n) throws RodinDBException
	{
		for(IMachineRoot mac : getMachines()){
			if(mac.getComponentName().equals(n)){
				return mac;
			}
		}
		return null;
	}
	
	/* 
	 * Control Flow Analysis on the Rodin Project based on control variables from preference page
	 */
	public void controlFlowAnalysis() {
		
	}
}
