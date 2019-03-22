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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.rodinp.core.RodinDBException;




public class bEvent {
	private bMachine machine;
	private IEvent event;
	private List<bEvent> nextEvents;
	private String start;
	private String end;
	private List<String> invariants;
	
	public bEvent(bMachine mac, IEvent e) throws CoreException{
		this.machine = mac;
		this.event = e;
		this.start = "";
		this.end = "";
		
		findStart();
		findEnd();
		findInvariants();
	}
	
	public bMachine machine() {
		return this.machine;
	}
	
	public IGuard[] guards() throws RodinDBException {
		return this.event.getGuards();
	}
	
	public IAction[] actions() throws RodinDBException {
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
		for(IAction action : actions()) {
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
		for(IGuard guard : guards()) {
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
			if(pred.getTag() == 251){	
				BinaryPredicate imply = (BinaryPredicate)pred;
				
				// case of equivalence, i.e. " = ".
				if(imply.getLeft().getTag() == 101){	
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
			if(bEvt.start().equals(end())) {
				nextEvts.add(bEvt);
			}
		}
		
		return nextEvts;
	}
	
}
