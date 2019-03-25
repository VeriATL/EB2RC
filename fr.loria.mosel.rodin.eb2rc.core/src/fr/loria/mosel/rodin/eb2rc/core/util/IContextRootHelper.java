package fr.loria.mosel.rodin.eb2rc.core.util;

import ie.nuim.cs.eventb.ASTextension.bAxiom;

import java.util.ArrayList;

import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.rodinp.core.RodinDBException;

public class IContextRootHelper {


	// Find the constant with [spec] in its comment, they are our target.
	public static ArrayList<IConstant> findSpecConstant(IContextRoot ctx) throws RodinDBException{
		
		ArrayList<IConstant> rtn = new ArrayList<IConstant>();
		IConstant[] tmpConsts = ctx.getConstants();
		
		for(IConstant c : tmpConsts){
			if(c.hasComment() && c.getComment().contains("[SPEC]")){
				rtn.add(c);
			}
		}
		
		return rtn;
	}
	
	public static ArrayList<String> getMethodSignatures(IContextRoot ctx, ArrayList<IConstant> tmpConstList) throws RodinDBException{
		final FormulaFactory ff = FormulaFactory.getDefault();
		ITypeEnvironment env = ctx.getSCContextRoot().getTypeEnvironment(ff);
		
		ArrayList<String> rtn = new ArrayList<String>();
		
		for(IConstant c : tmpConstList){
			Type t = env.getType(c.getIdentifierString());
			String fName = c.getIdentifierString();
			rtn.add(toFunctionSignature(fName, t));
			
		}
		
		return rtn;
		
	}
	
	
	private static String toFunctionSignature(String n, Type t){
		if(t instanceof PowerSetType){
			
			String rtnType = getArgsFromType(t.getTarget());
			String rtn = "function "+n+"(";
			rtn += getArgsFromType(t.getSource());
			
			rtn+="): "+rtnType;
			return rtn;
		}else{
			return t.toString()+"is illegal type to translate into a function signature.";
		}
	}
	
	private static String getArgsFromType(Type t){
		String rtn = "";
		
		if(t instanceof PowerSetType){
			if(t.getSource() != null){
				rtn += getArgsFromType(t.getSource());
			}
			
			if(t.getTarget()!= null){
				rtn += getArgsFromType(t.getTarget());
			}
		}else if (t instanceof IntegerType){
			rtn += "int ";
		}else if (t instanceof BooleanType){
			rtn += "bool ";
		}else if (t instanceof ProductType){
			ProductType tmp = (ProductType) t;
			
			if(tmp.getLeft() != null){
				rtn += getArgsFromType(tmp.getLeft());
			}
			
			if(tmp.getRight()!= null){
				rtn += getArgsFromType(tmp.getRight());
			}
			
			
		}
		
		return rtn;
	}
	
	
	
	public static ArrayList<String> getSpecByConstant(IContextRoot ctx, ArrayList<IConstant> tmpConstList) throws RodinDBException{
		final FormulaFactory ff = FormulaFactory.getDefault();
		ITypeEnvironment env = ctx.getSCContextRoot().getTypeEnvironment(ff);
		
		ArrayList<String> rtn = new ArrayList<String>();
		ArrayList<String> consts = new ArrayList<String>();
		
		for(IConstant c : tmpConstList){
			consts.add(c.getIdentifierString());
		}
		
		
		for (IAxiom axm : ctx.getAxioms()) {
			
			Predicate pred = typeCheckedParsing(axm.getPredicateString(), ff, env);

			for (FreeIdentifier id : pred.getFreeIdentifiers()) {
				if(consts.contains(id.getName()) && axm.hasComment() && axm.getComment().contains("[SPEC]")){
					rtn.add(pred.toString());
					break;
				}
			}

			
		}
		return rtn;
		
		
		
	}
	
	private static Predicate typeCheckedParsing(String s, FormulaFactory ff, ITypeEnvironment env) {
		IParseResult result;
		result = ff.parsePredicate(s, LanguageVersion.LATEST, null);
		
		Predicate pred = result.getParsedPredicate();
		pred.typeCheck(env);
		pred.getSyntaxTree();	//only for instantiate quantifier, i.e., rewrite the boundIdentifier to boundIdDecl
		
		return pred;
		
		
	}
}
