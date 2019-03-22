package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.rodinp.core.RodinDBException;

import fr.loria.mosel.rodin.eb2rc.core.helper.CoreConstants;




public class bEvent {
	private bMachine machine;
	private IEvent event;
	private List<bEvent> nextEvents;
	private String start;
	private String end;
	private List<String> invariants;
	private List<String> guards;
	private List<String> actions;
	
	/*
	 * The exact name of this IEvent
	 * */
	private String name;
	
	/*
	 * The analyzed name of this event
	 * */
	private String callName;
	
	/*
	 * Indicate this event is visible or not
	 * 
	 * @seeAlso analyzeName()
	 * */
	private boolean isVisible; 
	
	/*
	 * Indicate this event's nature
	 * 
	 * @seeAlso analyzeName()
	 * @seeAlso bEventNature
	 * */
	private bEventNature nature;
	
	
	
	public bEvent(bMachine mac, IEvent e) throws CoreException{
		this.machine = mac;
		this.event = e;
		this.start = "";
		this.end = "";
		this.invariants = new ArrayList<String>();
		this.guards = new ArrayList<String>();
		this.actions = new ArrayList<String>();
		this.callName = "";
		this.name = "";
		this.isVisible = true;
		this.nature = bEventNature.NORMAL;
		
		analyzeName(e.getLabel());
		setCallName();
		setGuards();
		setActions();
		findStart();
		findEnd();
		findInvariants();
	}
	
	/*
	 * Set event's call name, use this method after analyzeName() method
	 * */
	private void setCallName() {
		if(this.isVisible) {
			if(this.nature == bEventNature.CALL || this.nature == bEventNature.REC) {
				String[] splits = this.name.split(this.machine.rodin().pref().symbol());
				this.callName = splits[1];
			}
		}	
	}

	/*
	 * Set event's guards, use this method after analyzeName() method
	 * */
	private void setGuards() throws RodinDBException {
		//TODO synthesis from refined events
		
		if(this.isVisible) {
			if(this.nature == bEventNature.REC) {
				String[] splits = this.name.split(this.machine.rodin().pref().symbol());
				String guard = splits[2];
				this.guards.add(guard);
			}else {
				for(IGuard guard: this.event.getGuards()) {
					this.guards.add(guard.getPredicateString());
				}
			}
		}	
	}
	
	/*
	 * Return event's guards
	 * 
	 * @see setGuards for how it is computed
	 * */
	public List<String> guards(){
		return this.guards;
	}
	
	/*
	 * Set event's actions, use this method after analyzeName() method
	 * */
	private void setActions() throws RodinDBException {
		//TODO synthesis from refined events
		
		if(this.isVisible) {
			if(this.nature == bEventNature.CALL || this.nature == bEventNature.REC) {
				String[] splits = this.name.split(this.machine.rodin().pref().symbol());
				String act = splits[1];
				this.actions.add(act);
			}else {
				for(IAction act: this.event.getActions()) {
					this.actions.add(act.getAssignmentString());
				}
			}
		}
	}
	
	/*
	 * Return event's actions
	 * 
	 * @see setActions for how it is computed
	 * */
	public List<String> actions(){	
		return this.actions;
	}
	
	/*
	 * Analyze event name for its nature, isVisible, guard
	 * */
	private void analyzeName (String evtName) {
		this.name = evtName;
		String evtNameUpper = evtName.toUpperCase();
		
		if(evtNameUpper.startsWith(CoreConstants.CALL_NATURE_EVT)) {
			this.nature = bEventNature.CALL;
			this.isVisible = true;
		} else if(evtNameUpper.startsWith(CoreConstants.REC_NATURE_EVT)){
			this.nature = bEventNature.REC;
			String[] splits = evtName.split(this.machine.rodin().pref().symbol());
			
			if(splits.length > 3 && splits[3].startsWith(CoreConstants.TRUE)) {
				this.isVisible = false;
			}else {
				this.isVisible = true;
			}		
		}else{
			this.nature = bEventNature.NORMAL;
			this.isVisible = true;
		}	
	}

	public boolean visibility() {
		return this.isVisible;
	}
	
	public String name() {
		return this.name;
	}
	
	public bMachine machine() {
		return this.machine;
	}
	
	public IGuard[] iGuards() throws RodinDBException {
		return this.event.getGuards();
	}
	
	public IAction[] iActions() throws RodinDBException {
		return this.event.getActions();
	}
	
	/*
	 * Return start value of the control variable, find in the guard of IEvent
	 * @see findStart()
	 * */
	public String start() {
		return start;
	}
	
	/*
	 * Return end value of the control variable, find in the action of IEvent
	 * @see findEnd()
	 * */
	public String end() {
		return end;
	}
	
	/*
	 * Find in the event actions for the value of control variable
	 * */
	public void findEnd() throws CoreException {
		for(IAction action : iActions()) {
			bExpression bAction = new bExpression(this, action.getAssignmentString());		
			Assignment assign = bAction.parseAssignment();
			String cv = this.machine().rodin().pref().cv();
			
			if (assign.getTag() == 6) {
				BecomesEqualTo bet = (BecomesEqualTo) assign;

				for (int i = 0; i < bet.getAssignedIdentifiers().length; i++) {
					if (bet.getAssignedIdentifiers()[i].toString().equals(cv)) {
						end = bet.getExpressions()[i].toString(); 
					}
				}
			}	
		}
	}
	
	/*
	 * Find in the event guards for the value of control variable
	 * */
	public void findStart() throws CoreException {
		for(IGuard guard : iGuards()) {
			bExpression bGuard = new bExpression(this, guard.getPredicateString());		
			Predicate pred = bGuard.parsePredicate();
			String cv = this.machine().rodin().pref().cv();
			
			if (pred.getTag() == 101) {
				RelationalPredicate relPred = (RelationalPredicate) pred;
				if (relPred.getLeft().toString().equals(cv)) {
					start = relPred.getRight().toString();
				} else if (relPred.getRight().toString().equals(cv)) {
					start = relPred.getLeft().toString();
				}
			}		
		}
	}
	
	/*
	 * Find in the event invariants for the value of control variable
	 * */
	public void findInvariants() throws CoreException {
		String cv = machine.rodin().pref().cv();
		
		for(IInvariant inv : machine.invariants()) {
			bExpression bInvariant = new bExpression(this, inv.getPredicateString());
			Predicate pred = bInvariant.parsePredicate();
			
			// case of logical implication
			if(pred.getTag() == Formula.LIMP){	
				BinaryPredicate imply = (BinaryPredicate)pred;
				
				// case of equivalence, i.e. " = ".
				if(imply.getLeft().getTag() == Formula.EQUAL){	
					RelationalPredicate equal = (RelationalPredicate) imply.getLeft();
					String equal_lhs = equal.getLeft().toString();
					String equal_rhs = equal.getRight().toString();
					
					// case of "cv = ?" or "? = cv"
					if(equal_lhs.equals(end) && equal_rhs.equals(cv)
					|| equal_lhs.equals(cv) && equal_rhs.equals(end)) {
						invariants.add(imply.getRight().toStringFullyParenthesized());									
					}
				}
			}	
		}
	}

	/*
	 * Return a set of bEvents, who are next to this bEvent
	 * @seeAlso findNextEvents(List<bEvent>)
	 * */
	public List<bEvent> nexts() {
		return this.nextEvents;
	}
	
	/*
	 * Set next bEvents of this bEvent
	 * @seeAlso nexts()
	 * */
	public void setNextEvents(List<bEvent> nextEvts) {
		this.nextEvents = nextEvts;
	}
	
	/*
	 * Find in the given list of bEvents for which their start() is equal to the end() of this bEvent
	 * @seeAlso start()
	 * @seeAlso end()
	 * */
	public List<bEvent> findNextEvents(List<bEvent> allbEvts){
		List<bEvent> nextEvts = new ArrayList<bEvent>();
		
		for(bEvent bEvt : allbEvts) {
			if(bEvt.start().equals(this.end()) && bEvt.visibility()) {
				nextEvts.add(bEvt);
			}
		}
		
		return nextEvts;
	}
	
}
