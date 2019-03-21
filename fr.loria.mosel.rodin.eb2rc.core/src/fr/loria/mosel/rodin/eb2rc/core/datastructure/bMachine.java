package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;

public class bMachine {
	
	private bRodinProject rodin;
	private IMachineRoot machine;
	
	
	public bMachine(bRodinProject bRodinProject, IMachineRoot machine){
		this.rodin = bRodinProject;
		this.machine = machine;
	}
	
	public bRodinProject rodin() {
		return rodin;
	}

	public IMachineRoot iMachine() {
		return machine;
	}
	
	public IInvariant[] invariants() throws RodinDBException {
		return machine.getInvariants();
	}
	
	public bEvent analyze(IEvent event) throws CoreException {	
		// contruction: invs, guards, acts
		bEvent bevt = new bEvent(this, event);
		
		
		// find next evts
		
		// foreach (evt : evts) {event.nextevts.add(analyze(evt))}
		
		
		return bevt;	
	}

	/*
	 * Find init event based on the control variable defined in the preference page.
	 * */
	public IEvent findInitEvent() throws RodinDBException, CoreException {
		for (IEvent evt : machine.getEvents()) {
			bEvent bevt = new bEvent(this, evt);
			
			for(IAction act : evt.getActions()) {
				bExpression bAssign = new bExpression(bevt, act.getAssignmentString());
				
				if(bAssign.isInitialControlAssignment()) {
					return evt;
				}			
		    }
		}
		
		return null;
	}
		
		
}
