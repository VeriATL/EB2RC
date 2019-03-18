package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import javax.xml.bind.annotation.XmlAttribute;

public class bInvObject {
	@XmlAttribute
	String content;
	@XmlAttribute
	String name;
	
	public bInvObject(){
		
	}
	
	public bInvObject(String s, String n){
		content = s;
		name = n;
	}

	public String getContent() {
		return content;
	}
	
	public String getName() {
		return name;
	}
}
