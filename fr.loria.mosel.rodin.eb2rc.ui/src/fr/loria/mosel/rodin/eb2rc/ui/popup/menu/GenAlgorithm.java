package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public class GenAlgorithm extends GenericAction implements IObjectActionDelegate{
	private Shell shell;
	
	
	public void run(IAction action) {
		PluginPreference pref = read();
		MessageDialog.openInformation(shell, "Info", pref.cv());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
}
