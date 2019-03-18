package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import java.util.List;



public class bMachineObject {


	private bEventObject[] evts;
	private String name;
	
	public bMachineObject(){
		this.evts = null;
		this.name = "TBC";
	}
	
	public bMachineObject(List<bEventObject> evts, String n) {
		this.evts = evts.toArray(new bEventObject[evts.size()]);
		this.name = n;
	}

}
