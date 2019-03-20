package fr.loria.mosel.rodin.eb2rc.ui.popup.menu;

import fr.loria.mosel.rodin.eb2rc.core.datastructure.bRodinProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.rodinp.core.IRodinProject;


public class GenAlgorithm extends GenericAction implements IObjectActionDelegate{
	private Shell shell;
	private IProject selectedProject;
	

	
	public void run(IAction action) {
		
		bRodinProject rodin = new bRodinProject(selectedProject, cv(), sig(), start(), symbol());
		
		if(rodin.project() == null) {
			MessageDialog.openInformation(shell, "Info", "Not a Rodin Project. Code Generation Abort!");
		}else {
			rodin.controlFlowAnalysis();
			print(g)
		}
		

			

			
			IMachineRoot[] Machines = rodinHelper.getMachineRoots();
	

			// process machines Start
			for (IMachineRoot mR : Machines) {

				ArrayList<bEventObject> eos = new ArrayList<bEventObject>();
				
				env = mR.getSCMachineRoot().getTypeEnvironment(ff);
				String cv = conf.cv;
				
				//invPairs are of pairs of <label, inv>.
				HashMap<String, List<bInvObject>> invPairs = IMachineRootHelper.processInvs(mR,conf.cv);
				
				
				
				// process Events start
				for (IEvent evt : mR.getEvents()) {
					int type;
					List<String> grds = new ArrayList<String>();
					List<String> acts = new ArrayList<String>();
					
					ITypeEnvironment evtEnv = env.clone();
					
					
					bEvent tmpConcreteEvt = IMachineRootHelper.synthethizeEvent(mR, evt, ff, evtEnv, "");
					
					
					String cv_init = "";
					String cv_end = "";
					
					for(org.eventb.core.IAction act : evt.getActions())
					{
						boolean isControlAct = false;
						Assignment tmpAct = IRodinParsingHelper.typeCheckedParsingAssignment(act.getAssignmentString(), ff, env);

						if (tmpAct.getTag() == 6) {
							BecomesEqualTo temp = (BecomesEqualTo) tmpAct;

							for (int i = 0; i < temp.getAssignedIdentifiers().length; i++) {
								if (temp.getAssignedIdentifiers()[i].toString().equals(cv)) {
									cv_end = temp.getExpressions()[i].toString();
									isControlAct = true;
								}
							}
							if(!isControlAct)	
							{
								String tempStr = act.getAssignmentString();
								acts.add(tempStr);
							}
						}
						
						
					}
					
					for (bGuard grd : tmpConcreteEvt.getGuards()) {
						boolean isControlGrd=false;
						Predicate tmpPred = grd.getPred();
						if (tmpPred.getTag() == 101) {
							RelationalPredicate temp = (RelationalPredicate) tmpPred;
							if (temp.getLeft().toString().equals(cv)) {
								cv_init = temp.getRight().toString();
								isControlGrd = true;
							} else if (temp.getRight().toString().equals(cv)) {
								cv_init = temp.getLeft().toString();
								isControlGrd = true;
							}
						}
						if(!isControlGrd){
							String tempStr = grd.getPred().toString();
							grds.add(tempStr);
						}
						
					}
					
					if(evt.getLabel().toUpperCase().startsWith("CALL"))
					{
						// [123] it happens when an event named "call sth.."
						type = 2;
						String[] callNames = evt.getLabel().split(conf.splitSymbol);
						String tar = callNames[1];
						acts.clear();
						acts.add(StringHelper.EvtName2MethodCall(tar));
						
						grds.clear();
						if(!callNames[2].equals("NULL")){
							grds.add(callNames[2]);
						}
						
						
					}else if(evt.getLabel().toUpperCase().startsWith("REC"))
					{
						// a tuple of <type, callname, cond, selfdestructed>
						type = 3;
						String[] callNames = evt.getLabel().split(conf.splitSymbol);
						String tar = callNames[1];
						
						if(callNames.length>3){
							continue;
						}else{
							grds.clear();
							
							acts.clear();
							acts.add(StringHelper.EvtName2MethodCall(tar));
							
							if(!callNames[2].equals("NULL")){
								grds.add(callNames[2]);
							}
						}
						
					}else{
						type = 1;
					}
					
					
					
					bEventObject o = new bEventObject(
						tmpConcreteEvt.getName(),
						grds,
						acts,
						IMachineRootHelper.findCorInv(invPairs, cv_end),
						cv_init,
						cv_end,
						type
					);
					
					eos.add(o);
				}
				// process Events end
				
				
				for(bEventObject e: eos)
				{
					//List<bEventObject> restOfeos = (List<bEventObject>) eos.clone();
					//restOfeos.remove(e);
					if(!e.getCve().equals("")){
						e.setNextEvents(IMachineRootHelper.findNextEvents(eos, e.getCve()));
					}
						
				}
				
				
				// give the start label, should be in config.ini, the name given here
				// is only for testing.
				for(bEventObject e: eos)
				{
					if(e.getCve().equals(conf.iLabel) ){
						e.mSig = conf.mSig;
						String pContent = e.toString(0, true);
						pContent = StringHelper.toNormal(pContent);
						FileHelper.printToFile(pContent, mR.getComponentName(), rodinHelper.getProjFullPath(), "txt");
						isProcessed = true;
					}
				}
				
				
				//jaxbHelper.genXMItoFile(mo, mR.getComponentName());
				
			}
			// process machines End


			if(isProcessed){
				p += "finished. Generated TXT at:\n"+rodinHelper.getProjFullPath()+"/gen/";
			}else{
				p += MessageConstants.ERROR_CONFIG;
			}
			
			
			MessageDialog.openInformation(shell, "Info", p);

		} catch (RodinDBException e) {
			p += e.toString();
			MessageDialog.openInformation(shell, "Info", p);
		} catch (Exception e) {
			if(e instanceof IndexOutOfBoundsException){
				p += "\nPossible violation of naming convention for EVENT, try different split symbol in config file.";
				MessageDialog.openInformation(shell, "Info", p);
			}else{
				p += e.toString();
				MessageDialog.openInformation(shell, "Info", p);
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
