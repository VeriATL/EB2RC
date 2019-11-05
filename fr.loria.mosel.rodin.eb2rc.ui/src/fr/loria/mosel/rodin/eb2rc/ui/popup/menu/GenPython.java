package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import fr.loria.mosel.rodin.eb2rc.core.datastructure.bEvent;
import fr.loria.mosel.rodin.eb2rc.core.datastructure.bRodinProject;
import fr.loria.mosel.rodin.eb2rc.core.printer.LatexPrinter;
import fr.loria.mosel.rodin.eb2rc.core.printer.PythonPrinter;

public class GenPython extends GenericAction implements IObjectActionDelegate{

	private Shell shell;
	private IProject selectedProject;
	

	
	public void run(IAction action) {
		bRodinProject rodin = new bRodinProject(selectedProject, cv(), sig(), start(), symbol());
		
		if(rodin.project() == null) {
			MessageDialog.openInformation(shell, "Info", "Not a Rodin Project. Code Generation Abort!");
		}else {
			try {
				bEvent init = rodin.controlFlowAnalysis();
				
				if(init != null) {
					PythonPrinter py = new PythonPrinter(init);
					py.toCode(selectedProject.getLocation());
					
					String msg = String.format("Python Gen Finished. \n\n Code gen at %s/code/",
		                    selectedProject.getLocation().toPortableString());
			
					MessageDialog.openInformation(shell, "Info", msg);
				}else {
					MessageDialog.openInformation(shell, "Info", "Control Flow Analysis Failed. Please Check Machine.");
				}
				
			} catch (Exception e) {
				MessageDialog.openInformation(shell, "Info", e.toString());
			}
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {    
	        Object element = ((IStructuredSelection)selection).getFirstElement();    
        
	        if (element instanceof IProject) {    
	        	selectedProject = ((IProject) element);    
	        }  
	    }
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
}
