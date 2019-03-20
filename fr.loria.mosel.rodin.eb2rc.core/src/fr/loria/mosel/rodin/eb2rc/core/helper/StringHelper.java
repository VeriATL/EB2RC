package fr.loria.mosel.rodin.eb2rc.core.helper;

public class StringHelper {
	// translate Event name to an action, the action is a method call
	// [123] use regular expression to simplfy this
	public static String EvtName2MethodCall(String label) {
		String res = "";
		
		String[] r = label.split(";");
		String rhs = r[0]+")";
		String lhs = r[1].replace(")", "");
		res = lhs + "\u2254" + rhs;
		return res;
	}
	
	
	
	public static String toNormal(String input){
		input = input.trim();
		
		input = input.replace("\u2115", " NAT ");
		input = input.replace("\u2124", " INT ");
		input = input.replace("\u2119", " POW ");
		
		// pure predicate calculas
		input = input.replace("\u0028", "(");
		input = input.replace("\u0029", ")");
		input = input.replace("\u21D4", "#EQV");
		input = input.replace("\u21D2", "#IMP");
		input = input.replace("\u2227", " & ");
		input = input.replace("\u2228", " or ");
		input = input.replace("\u00AC", " not ");
		input = input.replace("\u22A4", " TRUE ");
		input = input.replace("\u22A5", " FALSE ");
		input = input.replace("\u2200", " forall ");
		input = input.replace("\u2203", " exists ");
		input = input.replace("\u002C", " , ");
		input = input.replace("\u00B7", " :: ");
		

		// expression	
		input = input.replace("\u003D", "#EQEQ");		
		input = input.replace("\u2260", " != ");		
		input = input.replace("\u003C", " < ");		
		input = input.replace("\u2264", " <= ");		
		input = input.replace("\u003E", " > ");		
		input = input.replace("\u2265", " >= ");		
		input = input.replace("\u2208", " IN ");		
		input = input.replace("\u2209", " NOTIN ");		
		input = input.replace("\u2282", " <<: ");		
		input = input.replace("\u2284", " /<<: ");		
		input = input.replace("\u2286", " <: ");		
		input = input.replace("\u2288", " /<: ");		
	
		// function/relation
		input = input.replace("\u2194", " <-> ");
		input = input.replace("\uE100", " <<-> ");
		input = input.replace("\uE101", " <->> ");
		input = input.replace("\uE102", " <<->> ");
		input = input.replace("\u21F8", " +-> ");
		input = input.replace("\u2192", " --> ");
		input = input.replace("\u2914", " >+> ");
		input = input.replace("\u21A3", " >-> ");
		input = input.replace("\u2900", " +>> ");
		input = input.replace("\u21A0", " ->> ");
		input = input.replace("\u2916", " >->> ");
		
		
		// set manipulation
		input = input.replace("\u007B", " { ");
		input = input.replace("\u007D", " } ");
		input = input.replace("\u21A6", " |-> ");
		input = input.replace("\u2205", " {} ");
		input = input.replace("\u2229", " /\\ ");
		input = input.replace("\u222A", " \\/ ");
		input = input.replace("\u2216", " \\ ");
		input = input.replace("\u00D7", " ** ");
		

		  
		// function/relation manipulation
		input = input.replace("\u005B", " [ ");
		input = input.replace("\u005D", " ] ");
		input = input.replace("\u21A6", " |-> ");
		input = input.replace("\uE103", " <+ ");
		input = input.replace("\u2218", " circ ");
		//input = input.replace("\u003B", " ; ");
		input = input.replace("\u2297", " >< ");
		input = input.replace("\u2225", " || ");
		input = input.replace("\u223C", " ~ ");
		input = input.replace("\u25C1", " <| ");
		input = input.replace("\u2A64", " <<| ");
		input = input.replace("\u25B7", " |> ");
		input = input.replace("\u2A65", " |>> ");
		

		
		
		// misc
		input = input.replace("\u03BB", " lambda ");
		input = input.replace("\u22C2", " INTER ");
		input = input.replace("\u22C3", " UNION ");
		input = input.replace("\u2223", " | ");
		
		input = input.replace("\u2254", " := ");

		input = input.replace("\u2025", " .. ");
		input = input.replace("\u002B", " + ");
		input = input.replace("\u2212", " - ");
		input = input.replace("\u2217", " * ");
		input = input.replace("\u00F7", " div ");
		input = input.replace("\u005E", " ^ ");

		
		// bring it back
		input = input.replace("#EQEQ", " = ");
		input = input.replace("#EQV", " <==> ");
		input = input.replace("#IMP", " ==> ");
		input = input.replace("#DOTARROW", "->");
		return input;
	}
	
	public static String sugaring(String input){
		input = input.trim();
		
		input = input.replace("\u2115", " \\nat ");
		input = input.replace("\u2124", " \\intg ");
		input = input.replace("\u2119", " \\pow ");

		
		// pure predicate calculus
		input = input.replace("\u0028", "(");
		input = input.replace("\u0029", ")");
		input = input.replace("\u21D4", " \\leqv ");
		input = input.replace("\u21D2", " \\limp ");
		input = input.replace("\u2227", " \\land ");
		input = input.replace("\u2228", " \\lor ");
		input = input.replace("\u00AC", " \\lnot ");
		input = input.replace("\u22A4", " \\top ");
		input = input.replace("\u22A5", " \\perp ");
		input = input.replace("\u2200", " \\forall ");
		input = input.replace("\u2203", " \\exists ");
		input = input.replace("\u002C", " , ");
		input = input.replace("\u00B7", " \\qdot ");
		

		// expression	
		input = input.replace("\u003D", " = ");		
		input = input.replace("\u2260", " \\neq ");		
		input = input.replace("\u003C", " < ");		
		input = input.replace("\u2264", " \\leq ");		
		input = input.replace("\u003E", " > ");		
		input = input.replace("\u2265", " \\geq ");		
		input = input.replace("\u2208", " \\in ");		
		input = input.replace("\u2209", " \\notin ");		
		input = input.replace("\u2282", " \\subset ");		
		input = input.replace("\u2284", " \\notsubset ");		
		input = input.replace("\u2286", " \\subseteq ");		
		input = input.replace("\u2288", " \\notsubseteq ");		
	
		// function/relation, 
		// [IMPORTANT] ObjectiveZ package notation
		input = input.replace("\u2194", " \\rel ");
		input = input.replace("\uE100", " \\trel ");
		input = input.replace("\uE101", " \\srel ");
		input = input.replace("\uE102", " \\strel ");
		input = input.replace("\u21F8", " \\pfun ");
		input = input.replace("\u2192", " \\tfun ");
		input = input.replace("\u2914", " \\pinj ");
		input = input.replace("\u21A3", " \\tinj ");
		input = input.replace("\u2900", " \\psur ");
		input = input.replace("\u21A0", " \\tsur ");
		input = input.replace("\u2916", " \\bij ");
		
		
		// set manipulation
		input = input.replace("\u007B", " { ");
		input = input.replace("\u007D", " } ");
		input = input.replace("\u21A6", " \\mapsto ");
		input = input.replace("\u2205", " \\emptyset ");
		input = input.replace("\u2229", " \\cap ");
		input = input.replace("\u222A", " \\cup ");
		input = input.replace("\u2216", " \\setminus ");
		input = input.replace("\u00D7", " \\cprod ");
		

		  
		// function/relation manipulation
		input = input.replace("\u005B", " [ ");
		input = input.replace("\u005D", " ] ");
		input = input.replace("\u21A6", " \\mapsto ");
		input = input.replace("\uE103", " \\ovl ");
		input = input.replace("\u2218", " \\bcomp ");
		//input = input.replace("\u003B", " \\fcomp ");
		input = input.replace("\u2297", " \\dprod ");
		input = input.replace("\u2225", " \\pprod ");
		input = input.replace("\u223C", " \\conv ");
		input = input.replace("\u25C1", " \\domres ");
		input = input.replace("\u2A64", " \\domsub ");
		input = input.replace("\u25B7", " \\ranres ");
		input = input.replace("\u2A65", " \\ransub ");
		

		
		
		// misc
		input = input.replace("\u03BB", " \\lambda ");
		input = input.replace("\u22C2", " \\Inter ");
		input = input.replace("\u22C3", " \\Union ");
		input = input.replace("\u2223", " \\mid ");
		
		input = input.replace("\u2254", " := ");
		

		input = input.replace("\u2025", " \\upto ");
		input = input.replace("\u002B", " + ");
		input = input.replace("\u2212", " - ");
		input = input.replace("\u2217", " * ");
		input = input.replace("\u00F7", " \\div ");
		input = input.replace("\u005E", " \\expn ");	//problematic
		return input;
	}
	

	
	
}
