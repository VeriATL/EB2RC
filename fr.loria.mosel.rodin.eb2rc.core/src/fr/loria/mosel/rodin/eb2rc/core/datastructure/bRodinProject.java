package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
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
	public bPreferencePage pref() {
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
	public ArrayList<IContextRoot> contexts() throws RodinDBException {	
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
	public ArrayList<IMachineRoot>  machines() throws RodinDBException {
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
	public IContextRoot context(String n) throws RodinDBException
	{
		for(IContextRoot ctx : contexts()){
			if(ctx.getComponentName().equals(n)){
				return ctx;
			}
		}
		return null;
	}
	
	/* 
	 * Retrieve a machine in a rodin project with given name
	 */
	public IMachineRoot machine(String n) throws RodinDBException
	{
		for(IMachineRoot mac : machines()){
			if(mac.getComponentName().equals(n)){
				return mac;
			}
		}
		return null;
	}
	
	/* 
	 * Retrieve a machine in a rodin project that is not the ancestor of any other machine.
	 * We consider such machine as the machine of last refinement.
	 * @return null if there is more than one of such machine, or non-existing of it.
	 */
	public IMachineRoot lastRefinedMachine() throws RodinDBException
	{
		ArrayList<IMachineRoot> macs = machines();
		ArrayList<IMachineRoot> result = new ArrayList<IMachineRoot>();
		
		for(IMachineRoot m1 : macs){	
			String n1 = m1.getComponentName();
			result.add(m1);
			
			for(IMachineRoot m2 : macs){
				String n2 = m2.getComponentName();
				IRefinesMachine[] r2 = m2.getRefinesClauses();
				
				for(IRefinesMachine rm : r2) {
					String rmn = rm.getAbstractMachineRoot().getComponentName();
				
					// if the machine being the refined machine of any other machine, it is not the last refinement
					if(!n1.equals(n2) && n1.equals(rmn)) {
						result.remove(m1);
					}
				}			
			}
		}
		
		if(result.size() == 1) {
			return result.get(0);
		}else {
			return null;
		}

	}
	
	/* 
	 * Control Flow Analysis on the Rodin Project based on control variables from preference page
	 */
	public void controlFlowAnalysis() throws CoreException {
		IMachineRoot iRefinedMachine = lastRefinedMachine();
		
		bMachine ibMachine= new bMachine(this, iRefinedMachine);
		IEvent initEvent = ibMachine.findInitEvent();
		
		ibMachine.analyze(initEvent);
		
	}
	
	

	
	
}
