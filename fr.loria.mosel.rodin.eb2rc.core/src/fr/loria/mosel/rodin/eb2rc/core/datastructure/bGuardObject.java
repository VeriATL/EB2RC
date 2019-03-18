package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import javax.xml.bind.annotation.XmlAttribute;

public class bGuardObject {
	@XmlAttribute
	String content;
	
	public bGuardObject(){
		
	}
	
	public bGuardObject(String s){
		content = s;
	}

	public String getContent() {
		return content;
	}
}
