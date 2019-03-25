package fr.loria.mosel.rodin.eb2rc.core.util;

import ie.nuim.cs.eventb.ASTextension.bAction;
import ie.nuim.cs.eventb.ASTextension.bEvent;
import ie.nuim.cs.eventb.ASTextension.bGuard;
import ie.nuim.cs.eventb.ASTextension.bLocalVariable;
import ie.nuim.cs.eventb.ASTextension.bParameter;
import ie.nuim.cs.eventb.ASTextension.bVariable;
import ie.nuim.cs.eventb.datastructure.bEventObject;
import ie.nuim.cs.eventb.datastructure.bInvObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.rodinp.core.RodinDBException;

public class IMachineRootHelper {

	// find event by label name
	public static ArrayList<bEvent> findNextEvents(ArrayList<bEvent> eos, String nextEvtName) {
		ArrayList<bEvent> rtn = new ArrayList<bEvent>();
		for(bEvent e: eos)
		{
			if(e.getCvi().equals(nextEvtName)){
				rtn.add(e);
			}
		}
		
		return rtn;
	}
	
	/**
	 * process invariant with the format of c=label ==> invs.
	 * add pair <label,invs> to the return result
	 * @throws Exception 
	 * */
	public static HashMap<String, List<bInvObject>> processInvs(IMachineRoot mR, String cv) throws Exception {
		FormulaFactory ff = FormulaFactory.getDefault(); 
		ITypeEnvironment env = mR.getSCMachineRoot().getTypeEnvironment(ff);
		
		HashMap<String, List<bInvObject>> res = new HashMap<String, List<bInvObject>>();
		
		for(IInvariant inv : mR.getInvariants()){
			Predicate p = IRodinParsingHelper.typeCheckedParsing(inv.getPredicateString(), ff, env);
			
			if(p.getTag() == 251){	// this is logical implication
				BinaryPredicate tmpPred = (BinaryPredicate)p;
				if(tmpPred.getLeft().getTag() == 101){	// left part of impl is equiv(=).
					RelationalPredicate tmpPredLeft = (RelationalPredicate) tmpPred.getLeft();
					String tmpPredLeftLeftString = tmpPredLeft.getLeft().toString();
					String tmpPredLeftRightString = tmpPredLeft.getRight().toString();
					
					
					String tempStr = tmpPred.getRight().toString();
					tempStr = "("+ tempStr +")";
					
					bInvObject o = new bInvObject(tempStr, inv.getLabel());
					if(tmpPredLeftLeftString.equals(cv)){
						
						if(res.containsKey(tmpPredLeftRightString)){						
							res.get(tmpPredLeftRightString).add(o);
						}else{
							List<bInvObject> newList = new ArrayList<bInvObject>();
							newList.add(o);
							res.put(tmpPredLeftRightString, newList);
						}
						
					}else if(tmpPredLeftRightString.equals(cv)){				
						if(res.containsKey(tmpPredLeftLeftString)){
							res.get(tmpPredLeftLeftString).add(o);
						}else{
							List<bInvObject> newList = new ArrayList<bInvObject>();
							newList.add(o);
							res.put(tmpPredLeftLeftString, newList);
						}
						
					}
				}
			}
			
			
		}
		return res;
	}

	
	public static bInvObject[] findCorInv(HashMap<String, List<bInvObject>> invPair, String label){
		if(invPair.containsKey(label) && invPair.get(label)!=null){
			return invPair.get(label).toArray(new bInvObject[invPair.get(label).size()]);
		}else{
			return null;
		}
	}
	
	public static bEvent synthethizeEvent(IMachineRoot mR, IEvent evt, FormulaFactory ff, ITypeEnvironment env, String prefix) throws RodinDBException, Exception {
		List<bParameter> params = new ArrayList<bParameter>();
		List<bAction> acts = new ArrayList<bAction>();
		List<bLocalVariable> localDecl = new ArrayList<bLocalVariable>();
		List<bGuard> guards = new ArrayList<bGuard>();

		List<bVariable> modifies = new ArrayList<bVariable>();
		

		ITypeEnvironment evtEnv = env.clone();
		
		
		
		 
		
		bEvent tmpEvt = null;
		
		if(evt.isExtended()){
			ArrayList<String> refinesClause = new ArrayList<String>();
			for ( IRefinesEvent absEvt : evt.getRefinesClauses()){
				refinesClause.add(absEvt.getAbstractEventLabel());
			}
			
			
			for (IRefinesMachine absMachine : mR.getRefinesClauses()) {
				for (IEvent absEvt : absMachine.getAbstractSCMachineRoot().getMachineRoot().getEvents()) {
					if (refinesClause.contains(absEvt.getLabel()) || absEvt.getLabel().equals(evt.getLabel())) {
						if(prefix.equals("")){
							prefix = mR.getComponentName() + "_" + evt.getLabel() + "_";
						}
						prefix = prefix.replaceAll(" ", "").replaceAll("\t", "");
						tmpEvt = synthethizeEvent(absMachine.getAbstractSCMachineRoot().getMachineRoot(), absEvt, ff, evtEnv, prefix);
					}
				}
			}
		}
		
		for (IGuard grd : evt.getGuards()) {
			Predicate pred = IRodinParsingHelper.typeCheckedParsing(grd.getPredicateString(), ff, evtEnv);
			String grdName = "";
			if(prefix.equals("")){
				grdName = mR.getComponentName() + "_" + evt.getLabel() + "_" + grd.getLabel();
				
			}else{
				grdName = prefix + grd.getLabel();
			}
			grdName = grdName.replaceAll(" ", "").replaceAll("\t", "");
			// register free identifiers for this pred to the type
			// env
			for (FreeIdentifier id : pred.getFreeIdentifiers()) {
				//[2013.06.15.000]
				if (evtEnv.getType(id.getName()) == null && id.getType()!=null)
					evtEnv.add(id);
			}

			guards.add(new bGuard(grdName, pred, IRodinParsingHelper.calculateFreeIdentifier(pred, evtEnv)));

		}
		
		// add parameters
		for (IParameter param : evt.getParameters()) {
			params.add(new bParameter(param.getIdentifierString(),
					evtEnv.getType(param.getIdentifierString())));
		}

		// process actions
		for (org.eventb.core.IAction act : evt.getActions()) {
			Assignment assign = IRodinParsingHelper.typeCheckedParsingAssignment(act.getAssignmentString(), ff, evtEnv);

			for (FreeIdentifier modifyId : assign.getAssignedIdentifiers()) {
				modifies.add(new bVariable(modifyId.getName(), null));
			}

			if(assign instanceof BecomesSuchThat){
				BecomesSuchThat tmpAssign = (BecomesSuchThat) assign;
				for(BoundIdentDecl id : tmpAssign.getPrimedIdents()){
					localDecl.add(new bLocalVariable(id.getName(), id.getType()));
				}
			}
			
			//according to the defination of GRD PO, concrete acts are not part of it.
			//acts.add(new bAction(act.getLabel(), assign));
			
		}

		//p+=pog.getDebugString();
		bEvent rtnEvt = new bEvent(evt.getLabel().replaceAll(" ", "").replaceAll("\t", ""), 
								params.toArray(new bParameter[params.size()]),
								guards.toArray(new bGuard[guards.size()]),
								acts.toArray(new bAction[acts.size()]),
								null,
								null, 
								modifies.toArray(new bVariable[modifies.size()]),
								ff,
								localDecl.toArray(new bLocalVariable[localDecl.size()]));
		rtnEvt.union(tmpEvt);
		
		return rtnEvt;
	}



}
