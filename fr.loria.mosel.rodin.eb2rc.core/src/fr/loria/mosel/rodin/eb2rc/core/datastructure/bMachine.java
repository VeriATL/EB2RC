package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.RodinDBException;

public class bMachine {
	
	private bRodinProject rodin;
	private IMachineRoot machine;
	
	/*
	 * Constructor 
	 * */
	public bMachine(bRodinProject bRodinProject, IMachineRoot machine){
		this.rodin = bRodinProject;
		this.machine = machine;
	}
	
	/*
	 * Return rodin project of this machine 
	 * @see bRodinProject
	 * */
	public bRodinProject rodin() {
		return rodin;
	}

	/*
	 * Return IMachine of this machine
	 * @see IMachine  
	 * */
	public IMachineRoot iMachine() {
		return machine;
	}
	
	/*
	 * Return IInvariants of this machine
	 * @see IInvariant 
	 * */
	public IInvariant[] invariants() throws RodinDBException {
		return machine.getInvariants();
	}
	
	/*
	 * Pre-analyze the given machine, turn its events into bEvent objects
	 * */
	public List<bEvent> preAnalyze() throws CoreException {	
		List<bEvent> bEvts = new ArrayList<bEvent>();
		
		for(IEvent evt : machine.getEvents()) {
			bEvent bevt = new bEvent(this, evt);
			bEvts.add(bevt);
		}
		
		return bEvts;
	}

	/*
	 * Analyze the given machine, recursively connect events based on their control variables
	 * */
	public void analyze(bEvent bEvt, List<bEvent> allbEvts) throws CoreException {	
		List<bEvent> nexts = bEvt.findNextEvents(allbEvts);
		bEvt.setNextEvents(nexts);
		
		for(bEvent next : nexts) {
			analyze(next, allbEvts);
		}
	}
	
	/*
	 * Find init event based on the control variable defined in the preference page.
	 * @seeAlso findInitEvent(ArrayList<bEvent>)
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
	
	/*
	 * Find init event based on the control variable defined in the preference page.
	 * @seeAlso findInitEvent()
	 * */
	public bEvent findInitEvent(List<bEvent> bEvts) throws RodinDBException, CoreException {
		String start = rodin().pref().start();
		
		for (bEvent evt : bEvts) {
			if(evt.start().equals(start)) {
				return evt;
			}
		}
		
		return null;
	}
		
}
