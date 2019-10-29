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


public class LatexPrinter {

	/*
	 * Charset used in printing
	 * */
	private Map<String, String> charset;
	
	/*
	 * Initial bEvent object to print
	 * */
	private bEvent init;
	
	public LatexPrinter(bEvent evt) {
		charset = SystemUtil.charset();
		init = evt;
	}
	
	/*
	 * Return charset for printing
	 * */
	private Map<String, String> charset() {
		return charset;
	}
	
	public String toLatex (IPath proj) throws IOException {
		String s = "";
		List<bEvent> l = new ArrayList<bEvent>();
		l.add(this.init);
		
		String body = bodies(0, l);	
		s += header();		
		s += tidy(body);
		s += footer();

		
		IPath dir = proj.addTrailingSeparator()
				             .append(CoreConstants.FOLDER).addTrailingSeparator();
		String filename = CoreConstants.LATEX + "." + CoreConstants.LATEXEXT;
		IOUtil.write(dir, filename, s);
		
		return s;
	}
	
	/*
	 * Print header of latex
	 * */
	private String header(){		
		String s = "";
		s = catn(s, "\\documentclass{article}");
		s = catn(s, "\\usepackage{algorithmic}");
		s = catn(s, "\\usepackage{algorithm}");
		s = catn(s, "\\usepackage{amssymb}");	
		s = catn(s, "\\begin{document}");
		s = catn(s, "\\begin{algorithm}");
		s = catn(s, String.format("\\caption{Algorithm: %s}", init.machine().rodin().pref().sig()));
		s = catn(s, "\\label{alg}");
		s = catn(s, "\\begin{algorithmic}[1]");	// [1] for generate line number
		
		return s;
	}

	/*
	 * Print footer of latex
	 * */
	private String footer(){		
		String s = "\n";
		s = catn(s, "\\end{algorithmic}");
		s = catn(s, "\\end{algorithm}");
		s = catn(s, "\\end{document}");
				
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
			int tLevel = level;
			if(evt.guards().size() > 0) {
				s = cat(s, lv(tLevel));
				
				if(evts.indexOf(evt) == 0) {
					s = cat(s, "\\IF{$");
				}else {
					s = cat(s, "\\ELSIF{$");
				}
				
				s = cat(s, join(evt.guards(), " \\land "));
				s = cat(s, "$}");
				s = catn(s, "{");
				
				tLevel++;
			}
			
			for(String act : evt.actions()) {
				if(evt.guards().size() > 0) { 
					s = cat(s, lv(tLevel));
				}
				act = String.format("\\STATE $ %s $", act);
				act = tidy(act);
				s = catn(s, act);
			}
			
			s = cat(s, bodies(tLevel, evt.nexts()));
			
			if(evt.guards().size() > 0) {			
				s = cat(s, lv(level));
				s = catn (s, "}");
						
				if(evts.indexOf(evt) == evts.size()-1) {
					s = cat(s, lv(level));
					s = catn(s, "\\ENDIF");
				}
			}
		}
			
		return s;
	}
	
	/*
	 * Replace special chars in input with latex tags
	 * 
	 * TODO: 1. Comply with https://bit.ly/2FseP9j (bmath.lex)
	 *       2. Unit testing needed here
	 * */
	private String tidy(String input) {
		input = input.trim();
		
		input = input.replace("\u2115", " \\mathbb{N} ");
		input = input.replace("\u2124", " \\mathbb{Z} ");
		input = input.replace("\u2119", " \\mathbb{P} ");
	
		// FOL
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
		input = input.replace("\u2254", " \\gets ");	// ":="
		input = input.replace("\u2025", " \\upto ");
		input = input.replace("\u002B", " + ");
		input = input.replace("\u2212", " - ");
		input = input.replace("\u2217", " * ");
		input = input.replace("\u00F7", " \\div ");
		input = input.replace("\u005E", " \\expn ");	//problematic
		return input.trim();
	}
}
