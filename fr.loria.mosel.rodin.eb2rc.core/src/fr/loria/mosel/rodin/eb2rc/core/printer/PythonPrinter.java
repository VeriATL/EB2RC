package fr.loria.mosel.rodin.eb2rc.core.printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.eclipse.core.runtime.IPath;

import fr.loria.mosel.rodin.eb2rc.core.datastructure.bEvent;
import fr.loria.mosel.rodin.eb2rc.core.util.CoreConstants;
import fr.loria.mosel.rodin.eb2rc.core.util.SystemUtil;
import fr.loria.mosel.rodin.eb2rc.core.util.IOUtil;


public class PythonPrinter {

	/*
	 * Charset used in printing
	 * */
	private Map<String, String> charset;
	
	/*
	 * Imported library dependencies
	 * */
	private List<String> imports;
	
	/*
	 * External Rodin project dependencies
	 * */
	private List<String> externals;
	
	/*
	 * Initial bEvent object to print
	 * */
	private bEvent init;
	
	public PythonPrinter(bEvent evt) {
		charset = SystemUtil.charset();
		init = evt;
		imports = null;
		externals = null;
	}
	
	/*
	 * Set Imported library dependencies
	 * */
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	
	/*
	 * Set External Rodin library dependencies
	 * */
	public void setExternals(List<String> externals) {
		this.externals = externals;
	}
	
	/*
	 * Return charset for printing
	 * */
	private Map<String, String> charset() {
		return charset;
	}
	
    /**
     * ?.
     *
     * @param ?
     *		   if null, skip print to file system location.
     * @return ?
     * @throws IOException, Exception 
     */
	public String toCode (IPath proj) throws IOException, Exception {
		String sig = init.machine().rodin().pref().sig();
		String[] parsedSig = signatureParser(sig);
		
		if(parsedSig.length!=3) {
			throw new Exception("Code Generation Failed. Please Check Configuration. \n"
					+ "Signature format: name(parameters;results)");
		}
		
		String fun = parsedSig[0];
		String pars = parsedSig[1];
		String rets = parsedSig[2];
		
		
		String s = "";
		List<bEvent> l = new ArrayList<bEvent>();
		l.add(this.init);
		
		// print imports
		if(this.imports!=null) {
			s += imports();
		}
		
		// print externals
		if(this.externals!=null) {
			s += externals();
		}
		
		String body = bodies(1, l);	
		s += header(fun, pars);		
		s += tidy(body);
		s += footer(rets);

		if(proj!=null) {
			IPath dir = proj.addTrailingSeparator()
					             .append(CoreConstants.FOLDER).addTrailingSeparator();
			String filename = fun + "." + CoreConstants.PYTHONEXT;
			IOUtil.write(dir, filename, s);
		}
		
		return s;
	}
	
	/*
	 * Print imports of python
	 * */
	private String imports() {
		String s = "";
		for(String i : imports) {
			s = catn(s, String.format("import %s;", i));
		}
		s = catn(s, "");
		
		return s;
	}

	/*
	 * Print externals rodin projects of python
	 * */
	private String externals() {
		String s = "";
		for(String ext: externals) {
			s = catn(s, ext);
		}
		
		return s;
	}
	
	/*
	 * Parse a function signature defined in the preference page
	 * */
	private String[] signatureParser(String sig) {
		sig = sig.trim();
		String pattern = "\\s+|\\(\\s*|\\;\\s*|\\)";
		String[] res = sig.split(pattern);
		return res;
	}

	/*
	 * Print header of python
	 * */
	private String header(String fun, String pars){		
		String s = "";
		s = catn(s, String.format("def %s(%s):", fun, pars));
		
		return s;
	}

	/*
	 * Print footer of python
	 * */
	private String footer(String rets){		
		String s = "\n";
		s = cat(s, lv(1));
		s = catn(s, String.format("return %s", rets));
				
		return s;
	}
	
	/*
	 * Pring n indentation
	 * */
	private String lv(int n) {
		String ind = "";
		
		for(int i=0;i<n;i++){
			ind += "\t";
		}
		
		return ind;
	}
	
	/*
	 * Concat two strings
	 * */
	private String cat(String s1, String s2) {
		return s1 + s2;
	}
	
	/*
	 * Concat two strings with line break
	 * */
	private String catn(String s1, String s2) {
		return s1 + s2 + charset().get("linebreak");
	}
	
	/*
	 * Concat two strings with line break
	 * */
	private String join(List<String> l, String op) {
		return String.join(op, l);
	}
	
	/*
	 * Print body of latex
	 * */
	public String bodies(int level, List<bEvent> evts){
		String s = "";	
		
		
		
		for(bEvent evt : evts) {
			int curLevel = level;	
			if(evt.guards().size() > 0) {
				s = cat(s, lv(curLevel));
				
				if(evts.indexOf(evt) == 0) {
					s = cat(s, "if ");
				}else {
					s = cat(s, "elif ");
				}
				
				s = cat(s, join(evt.guards(), " and "));
				s = catn(s, ": ");
				curLevel++;
			}
			
			for(String act : evt.actions()) {
				s = cat(s, lv(curLevel));
				act = String.format("%s", act);
				s = catn(s, act);
			}
			
			s = cat(s, bodies(curLevel, evt.nexts()));
			
			if(evt.guards().size() > 0) {
				curLevel--;
			}
				
		}
			
		return s;
	}
	
	/*
	 * Replace special chars in input with latex tags
	 * 
	 * TODO: 1. Comply with https://bit.ly/2JQA7Au
	 *       2. Unit testing needed here
	 * */
	private String tidy(String input) {	
		input = input.replace("\u2115", " 0 ");
		input = input.replace("\u2124", " 0 ");
		input = input.replace("\u2119", " \\mathbb{P} ");
	
		// FOL
		input = input.replace("\u0028", "(");
		input = input.replace("\u0029", ")");
		input = input.replace("\u21D4", " \\leqv ");
		input = input.replace("\u21D2", " \\limp ");
		input = input.replace("\u2227", " and ");
		input = input.replace("\u2228", " or ");
		input = input.replace("\u00AC", " \\lnot ");
		input = input.replace("\u22A4", " \\top ");
		input = input.replace("\u22A5", " \\perp ");
		input = input.replace("\u2200", " \\forall ");
		input = input.replace("\u2203", " \\exists ");
		input = input.replace("\u002C", " , ");
		input = input.replace("\u00B7", " \\qdot ");
		input = input.replace("\u003D", " == ");		
		input = input.replace("\u2260", " != ");		
		input = input.replace("\u003C", " < ");		
		input = input.replace("\u2264", " <= ");		
		input = input.replace("\u003E", " > ");		
		input = input.replace("\u2265", " >= ");		
		//input = input.replace("\u2208", " == ");		// "\in"
		input = input.replace("\u2209", " \\notin ");		
		input = input.replace("\u2282", " \\subset ");		
		input = input.replace("\u2284", " \\notsubset ");		
		input = input.replace("\u2286", " \\subseteq ");		
		input = input.replace("\u2288", " \\notsubseteq ");		
	
		// function/relation, 
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
		input = input.replace("\u21A6", " , ");
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
		input = input.replace("\u22a4", " True ");
		input = input.replace("\u22a5", " False ");
		input = input.replace("TRUE", " True ");
		input = input.replace("FALSE", " False ");
		input = input.replace("\u2223", " \\mid ");	
		input = input.replace("\u2254", " = ");	// ":="
		input = input.replace("\u003A\u2208", " = ");	// "::"
		input = input.replace("\u003A\u2223", " = ");	// ":|"
		input = input.replace("\u2025", " \\upto ");
		input = input.replace("\u002B", " + ");
		input = input.replace("\u2212", " - ");
		input = input.replace("\u2217", " * ");
		input = input.replace("\u00F7", " // ");
		input = input.replace("mod", " % ");
		input = input.replace("\u005E", " ^ ");	//problematic
		return input;
	}
}
