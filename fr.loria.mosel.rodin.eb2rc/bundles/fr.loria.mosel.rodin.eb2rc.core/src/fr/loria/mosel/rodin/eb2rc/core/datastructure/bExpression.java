package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

public class bExpression{
	private String content;
	private bEvent event;
	
	public bExpression(bEvent evt, String c) {
		this.content = c;
		this.event = evt;
	}

	/*
	 * Parse assignment content, according to type environment of this machine
	 * */
	public Assignment parseAssignment () throws CoreException {
		IParseResult result;
		FormulaFactory ff = event.machine().iMachine().getFormulaFactory();
		result = ff.parseAssignment(content, null);
		Assignment pred = result.getParsedAssignment();
		ITypeEnvironment env = event.machine().iMachine().getSCMachineRoot().getTypeEnvironment();
		
		pred.typeCheck(env);
		pred.getSyntaxTree();	//required for instantiate quantifier (i.e., rewrite the boundIdentifier to boundIdDecl)
		
		return pred;
	}
	
	/*
	 * Determine whether an assignment assignees to control variable (defined in preference page) 
	 * */
	public boolean isControlAssignment() throws CoreException {
		Assignment assign = parseAssignment();
		String cv = event.machine().rodin().pref().cv();
		
		if (assign.getTag() == Formula.BECOMES_EQUAL_TO) {
			BecomesEqualTo bet = (BecomesEqualTo) assign;

			for (int i = 0; i < bet.getAssignedIdentifiers().length; i++) {
				if (bet.getAssignedIdentifiers()[i].toString().equals(cv)) {
					return true; 
				}
			}
		}	
		return false;	
	}
	
	/*
	 * Determine whether an assignment assigns to control variable to be the start (defined in preference page) 
	 * */
	public boolean isInitialControlAssignment() throws CoreException {
		Assignment assign = parseAssignment();
		String cv = event.machine().rodin().pref().cv();
		String start = event.machine().rodin().pref().start();
		
		if (assign.getTag() == Formula.BECOMES_EQUAL_TO) {
			BecomesEqualTo bet = (BecomesEqualTo) assign;

			for (int i = 0; i < bet.getAssignedIdentifiers().length; i++) {
				if (bet.getAssignedIdentifiers()[i].toString().equals(cv) 
				 && bet.getExpressions()[i].toString().equals(start)) {
					return true; 
				}
			}
		}	
		return false;	
	}
	
	/*
	 * Parse assignment content, according to type environment of this machine
	 * */
	public Predicate parsePredicate () throws CoreException {
		IParseResult result;
		FormulaFactory ff = event.machine().iMachine().getFormulaFactory();
		result = ff.parsePredicate(content, null);
		Predicate pred = result.getParsedPredicate();
		ITypeEnvironment env = event.machine().iMachine().getSCMachineRoot().getTypeEnvironment();
		
		pred.typeCheck(env);
		pred.getSyntaxTree();	//required for instantiate quantifier (i.e., rewrite the boundIdentifier to boundIdDecl)
		
		return pred;
	}
	
	/*
	 * Determine whether a guard is control variable (defined in preference page) 
	 * */
	public boolean isControlPredicate() throws CoreException {
		Predicate pred = parsePredicate();
		String cv = event.machine().rodin().pref().cv();
		
		if (pred.getTag() == Formula.EQUAL) {
			RelationalPredicate relPred = (RelationalPredicate) pred;
			if (relPred.getLeft().toString().equals(cv)) {
				return true;
			} else if (relPred.getRight().toString().equals(cv)) {
				return true;
			}
		}
		
		return false;
	}
	
}
