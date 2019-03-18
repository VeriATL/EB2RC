package fr.loria.mosel.rodin.eb2rc.core.datastructure;

import javax.xml.bind.annotation.XmlAttribute;

public class bActionObject {
	@XmlAttribute
	String content;
	
	public bActionObject(){
		
	}
	
	public bActionObject(String s){
		content = s;
	}
	
	public String getContent() {
		return content;
	}
}
