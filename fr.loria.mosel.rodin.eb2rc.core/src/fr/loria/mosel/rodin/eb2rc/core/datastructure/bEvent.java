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

import fr.loria.mosel.rodin.eb2rc.core.helper.IRodinParsingHelper;
import fr.loria.mosel.rodin.eb2rc.core.helper.bInvObject;




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
		
		findStart();
		findEnd();
		findInvariants();
	}
	
	public bMachine machine() {
		return machine;
	}
	
	public IGuard[] guards() throws RodinDBException {
		return this.event.getGuards();
	}
	
	public IAction[] actions() throws RodinDBException {
		return this.event.getActions();
	}
	
	public String start() {
		return start;
	}
	
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
	


	

	private int type;

	
	
	
	
	
	public void setNextEvts(List<bEvent> nextEvts) {
		this.nextEvts = nextEvts;
	}

	public List<bEvent> getNextEvts() {
		return nextEvts;
	}

	public String mSig;
	

	
	public bEvent(String name, List<String> guards, List<String> actions, bInvObject[] invs, String cv_init,
			String cv_end, int type) {
		this.name = name;

		this.cvi = cv_init;
		this.cve = cv_end;
		this.type = type;
		this.invs = invs;
		
		List<bActionObject> tmpActs = new ArrayList<bActionObject>();
		List<bGuardObject> tmpGrds = new ArrayList<bGuardObject>();
		
		for(String act : actions){
			tmpActs.add(new bActionObject(act));
		}
		
		for(String grd : guards){
			tmpGrds.add(new bGuardObject(grd));
		}
		
		this.grds = tmpGrds.toArray(new bGuardObject[tmpGrds.size()]);
		this.acts = tmpActs.toArray(new bActionObject[tmpActs.size()]);
	}


	public String getName(){
		return name;
	}
	
	public String getCvi() {
		return cvi;
	}

	public String getCve() {
		return cve;
	}
	
	public void setNextEvents(List<bEvent> eos){
		this.nextEvts = eos;
	}
	
	
	public String toDot()
	{
		String res="";
		
		if(this.acts!=null && this.acts.length>0){
			res += "\""+this.name+"\""+"[shape=record,label=\"{";
			int i = 0;
			for(bActionObject a: this.acts){
				res += a.getContent();
				if(i != this.acts.length - 1){
					res += " | ";
				}
				i++;
			}
			
			res +=	"}\"]";
			res += ";\n";
		}
		
			
		if(this.invs!=null && this.invs.length>0){
			res += "\""+this.cve+"\""+"[tooltip=\"";
			int i = 0;
			for(bInvObject inv: this.invs){
				res += inv.getName()+": "+inv.getContent();
				if(i != this.invs.length - 1){
					res += " &#013; ";	// [CONECTIVE] print line break
				}
				i++;
			}
			
			res +=	"\"]";
			res += ";\n";
		}				
				
		if(this.cvi!=null && this.cvi!=""){
			res += this.cvi+"#DOTARROW";
			res += "\n";
		}
		
		if(this.acts!=null && this.acts.length > 0){
			
			res += "\""+this.name+"\"";
			if(this.grds!=null && this.grds.length > 0){
				res += "[ label=\"";
				int i = 0;
				for(bGuardObject grd : this.grds){
					res += grd.getContent();
					if(i != this.grds.length - 1){
						res += " & ";  // [CONECTIVE] print AND
					}
					i++;
				}
				
				res += " \"]";
			}
			res += ";";
			res += "\""+this.name+"\"#DOTARROW";
			res += this.cve;
			res += ";";
		}else{
			res += this.cve;
			if(this.grds!=null && this.grds.length > 0){
				res += "[ label=\"";
				int i = 0;
				for(bGuardObject grd : this.grds){
					res += grd.getContent();
					if(i != this.grds.length - 1){
						res += " & "; // [CONECTIVE] print AND
					}
					i++;
				}
				
				res += " \"]";
			}
			res += ";";
		}
		res += "\n";
		return res;
		
	}
	
	
	
	
	
	// Notice: In here, the '&' is used to conjunct grds and invs
	// @param level control indention
	// @param isFirst control how to print each nextEvent, the first nextEvent print if
	//     			  the rest print else if
	public String toString(int level, boolean isFirst)
	{
		String rtn = "";
		
		
		if(mSig != null)
		{
			rtn+=mSig+"{\n";
			level++;
		}
		
		int oldLevel = level;
		
		if(this.grds.length>0)
		{
			if(isFirst){for(int i=0;i<level;i++){rtn+="\t";}}
				
			if(!isFirst){
				for(int i=0;i<level;i++){rtn+="\t";}
				rtn += "else ";
			}
			rtn += "if(";
			
			int counter=0;
			for(bGuardObject grd : grds)
			{
				rtn += grd.getContent();
				if(counter!=grds.length-1)
				{
					rtn+=" & "; // [CONECTIVE] print AND
				}
				counter++;
			}
			
			rtn += ")";
			rtn += "{\n";
			level++;
		}
		
		for(bActionObject act : acts)
		{
			for(int i=0;i<level;i++){rtn+="\t";}
			rtn += act.getContent()+ ";\n";
		}
		
		//print assertion, and remove line break which is only used in call graph.
		String invString = "";
		if(this.invs!=null){
			for(int i=0;i<level;i++){rtn+="\t";}
			int counter=0;
			for(bInvObject inv : this.invs){
				invString += inv.getContent();
				if(counter!=invs.length-1)
				{
					invString+=" & "; // [CONECTIVE] print AND
				}
				counter++;
			}
			if(!invString.equals("")){
				rtn += "/* assert "+ invString + "*/   \n";
			}
			
		}
		
		
		
		int count = 1;
		for(bEvent evt: nextEvts)
		{
			if(count==1){
				rtn += evt.toString(level, true);
			}else{
				rtn += evt.toString(level, false);
			}
			
			count++;
		}
		
		if(this.grds.length>0)
		{
			for(int i=0;i<oldLevel;i++){rtn+="\t";}
			rtn += "}\n";
		}
		
		if(mSig != null)
		{
			rtn+="}";
		}
		return rtn;
	}
	
	
	public String toLatex(int level, boolean isFirst)
	{
		String rtn="";
		
		rtn += PrintHead();
		
		rtn += PrintBody(level, isFirst);
		
		rtn += PrintTail();
		return rtn;
	}
	
	public String PrintHead(){
		return "\\begin{algorithm}";
	}

	public String PrintBody(int level, boolean isFirst){
		String rtn = "";	
		
		if(mSig != null)
		{
			rtn += "\n";
			level++;
		}
		
		int oldLevel = level;
		
		// start the guard
		if(this.grds.length>0)
		{
			if(isFirst){for(int i=0;i<level;i++){rtn+="\t";}}
				
			if(!isFirst){
				for(int i=0;i<level;i++){rtn+="\t";}
				rtn += "\\ElseIf{$";
			}else{
				rtn += "\\If{$";
			}
			
			int counter=0;
			for(bGuardObject grd : grds)
			{
				rtn += grd.getContent();
				if(counter!=grds.length-1)
				{
					rtn+=" \\land "; // [CONECTIVE] print AND
				}
				counter++;
			}
			
			rtn += "$}";
			rtn += "{\n";
			level++;
		}
		
		for(bActionObject act : acts)
		{
			for(int i=0;i<level;i++){rtn+="\t";}
			rtn += "$"+act.getContent()+ "$\\;\n";
		}

		
		int count = 1;
		for(bEvent evt: nextEvts)
		{
			if(count==1){
				rtn += evt.PrintBody(level, true);
			}else{
				rtn += evt.PrintBody(level, false);
			}
			
			count++;
		}
		
		// close the guard
		if(this.grds.length>0)
		{
			for(int i=0;i<oldLevel;i++){rtn+="\t";}
			rtn += "}\n";
		}
		
		
		return rtn;
	}
	
	public String PrintTail(){
		String rtn = "\n\\label{-to-put-in-}\n"; 
		rtn += "\\caption{Algorithm "+mSig+" }\n";
		rtn += "\\end{algorithm}\n";
		return rtn;
	}
	
}
