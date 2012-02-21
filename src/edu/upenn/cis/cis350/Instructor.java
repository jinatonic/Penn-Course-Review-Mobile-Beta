package edu.upenn.cis.cis350;

public class Instructor {
	private String id;
	private String name;
	private String path;
	
	public Instructor(String _id, String _name, String _path){
		id = _id;
		name = _name;
		path = _path;
		
	}
	
	public String getID() {	return id;	}
	public String getName() {return name; }
	public String getPath() {return path;}
}

