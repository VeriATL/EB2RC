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
		
		s += header();		
		s += bodies(0, l);		
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
		return catn("\\begin{algorithm}", "");
	}

	/*
	 * Print footer of latex
	 * */
	private String footer(){
		return catn("", "\\end{algorithm}");
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
					s = cat(s, "\\If{$");
				}else {
					s = cat(s, "\\ElseIf{$");
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
				s = catn(s, act);
			}
			
			s = cat(s, bodies(tLevel, evt.nexts()));
			
			if(evt.guards().size() > 0) {
				s = cat(s, lv(level));
				s = catn (s, "}");
			}
		}
			
		return s;
	}
}
