package fr.loria.mosel.rodin.eb2rc.core.printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

import fr.loria.mosel.rodin.eb2rc.core.datastructure.bEvent;
import fr.loria.mosel.rodin.eb2rc.core.util.CoreConstants;
import fr.loria.mosel.rodin.eb2rc.core.util.SystemUtil;
import fr.loria.mosel.rodin.eb2rc.core.util.IOUtil;


public class TikzPrinter {

	/*
	 * Charset used in printing
	 * */
	private Map<String, String> charset;
	
	/*
	 * Initial bEvent object to print
	 * */
	private bEvent init;
	
	/*
	 * willbePrintedDecisions used in printing
	 * */
	private List<Integer> willbePrintedDecisions;

	/*
	 * Guards used in printing
	 * */
	private List<String> guardlist;
	
	public TikzPrinter(bEvent evt) {
		charset = SystemUtil.charset();
		init = evt;
	}
	
	/*
	 * Return charset for printing
	 * */
	private Map<String, String> charset() {
		return charset;
	}
	
	
	private void SetUp() {
		willbePrintedDecisions = new ArrayList<Integer>();
		guardlist = new ArrayList<String>();
	}
	
	
	public String toTikz(IPath proj) throws IOException {
		
		SetUp();
		
		String s = "";
		List<bEvent> l = new ArrayList<bEvent>();
		l.add(this.init);
		
		String nodes = bodiesNodes(l);	
		String lines = bodiesLines(l);	
		
		s += header();		
		s = catn(s, tidy(nodes));
		s = catn(s, tidy(lines));
		s += footer();

		
		IPath dir = proj.addTrailingSeparator()
				             .append(CoreConstants.FOLDER).addTrailingSeparator();
		String filename = CoreConstants.TIKZ + "." + CoreConstants.LATEXEXT;
		IOUtil.write(dir, filename, s);
		
		return s;
	}
	
	/*
	 * Print header of latex
	 * */
	private String header(){		
		String s = "";
		s = catn(s, "\\documentclass{article}");
		s = catn(s, "\\usepackage{tikz}");
		s = catn(s, "\\usepackage{amssymb}");
		s = catn(s, "\\usetikzlibrary{shapes,arrows,shadows.blur}");

		s = catn(s, "\\begin{document}");
		s = catn(s, "\\tikzstyle{decision} = [diamond, draw, fill=blue!20, text width=4.5em, text badly centered, node distance=3cm, inner sep=-2ex]");
		s = catn(s, "\\tikzstyle{block} = [rectangle, draw=blue!40!black!60, top color=white, bottom color=blue!50!black!20, very thick, shade, blur shadow, align=center, text width=5em, text centered, rounded corners, minimum height=4em]");
		s = catn(s, "\\tikzstyle{line} = [draw, -latex']");
		s = catn(s, "\\begin{tikzpicture}[node distance = 3cm, auto]");

		
		return s;
	}

	/*
	 * Print footer of latex
	 * */
	private String footer(){		
		String s = "\n";
		s = catn(s, "\\end{tikzpicture}");
		s = catn(s, "\\newpage");
		s = catn(s, conditions());
		s = catn(s, "\\end{document}");
				
		return s;
	}
	
	
	private String conditions() {
		String s = "";
		s = catn(s, "\\begin{itemize}");
		for(int i=0; i<guardlist.size(); i++) {
			s = catn(s, String.format("\\item[\\textbf{%s}] $%s$", CoreConstants.GLABEL+(i+1), tidy(guardlist.get(i))));
		}
		s = catn(s, "\\end{itemize}");
		return s;
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
	 * Print body of latex, focus on node gen
	 * */
	public String bodiesNodes(List<bEvent> evts){
		String s = "";	
		bEvent bro = null;
		
		List<bEvent> remainder = new ArrayList<bEvent>(evts);
		
		for(bEvent evt : evts) {
			
			int id = evt.hashCode();
			remainder.remove(evt);
					
			// print a block
			String acts = join(evt.actions(), " \\land ");
			if(evt.start().equals("")) {
				acts = String.format("\\node [block] (%s) { $ %s $};", id, acts);
			}else {
				if(bro != null) {
					int idbro = bro.hashCode();
					acts = String.format("\\node [block, right of=%s] (%s) { $ %s $};", idbro, id, acts);
				}else{
					acts = String.format("\\node [block, below of=%s] (%s) { $ %s $};", evt.start(), id, acts);
				}			
			}
		    s = catn(s, acts);
			
			// store decision after
		    if(!isWillbePrintedDecisions(remainder, evt.end())) {
		    	String str = String.format("\\node [decision, below of=%s] (%s) {$%s$};", id, evt.end(), evt.end());
				s = catn(s, str);
		    }else {
		    	willbePrintedDecisions.add(id);
		    }
		    
		    // recursively print sub evts
			s = cat(s, bodiesNodes(evt.nexts()));
						
			bro = evt;
		}
		

				
		return s;
	}
	
	/*
	 * Decide if any descendants of evt has decision named s
	 * */
	private boolean isWillbePrintedDecisions(List<bEvent> evts, String s) {
	    for(bEvent evt : evts) {
			if (evt.end().equals(s) || isWillbePrintedDecisions(evt.nexts(), s)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/*
	 * Print body of latex, focus on lines gen
	 * */
	public String bodiesLines(List<bEvent> evts){
		String s = "";	
		
		for(bEvent evt : evts) {
			
			int id = evt.hashCode();
			String guards = "";
			
			// line guards with evt
			if(!evt.start().equals("")) {
				if(evt.guards().size() > 0) {	
					guards = join(evt.guards(), " \\land ");
					guardlist.add(guards);
					String rep = CoreConstants.GLABEL + guardlist.size() ;
					guards = String.format("\\path [line] (%s) -- node [near end] {$%s$} (%s);", evt.start(), rep, id);
					s = catn(s, guards);
				}else {
					guards = String.format("\\path [line] (%s) -- node [near end] {$ $} (%s);", evt.start(), id);
					s = catn(s, guards);
				}
			}
			
			// line decision after with event
			if(willbePrintedDecisions.contains(id)) {
				String line = String.format("\\path [line] (%s) |- (%s);", id, evt.end());
				s = catn(s, line);
			}else {
				String line = String.format("\\path [line] (%s) -- (%s);", id, evt.end());
				s = catn(s, line);
			}
				
			// next events
			s = cat(s, bodiesLines(evt.nexts()));
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
