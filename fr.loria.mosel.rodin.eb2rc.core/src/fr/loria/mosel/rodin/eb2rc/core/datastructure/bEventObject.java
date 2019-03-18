package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.ArrayList;
import java.util.List;




public class bEventObject {

	private bGuardObject[] grds;
	private bActionObject[] acts;
	private bInvObject[] invs;
	
	private String cvi;
	private String cve;
	private int type;
	private String name;
	
	
	
	private List<bEventObject> nextEvts;
	
	public void setNextEvts(List<bEventObject> nextEvts) {
		this.nextEvts = nextEvts;
	}

	public List<bEventObject> getNextEvts() {
		return nextEvts;
	}

	public String mSig;
	
	public bEventObject(){
		this.grds = null;
		this.acts = null;
		this.cvi = "";
		this.cve = "";
		this.type = 0;
		this.name = "";
	}
	
	public bEventObject(String name, List<String> guards, List<String> actions, bInvObject[] invs, String cv_init,
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
	
	public void setNextEvents(List<bEventObject> eos){
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
		for(bEventObject evt: nextEvts)
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
		for(bEventObject evt: nextEvts)
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
