package edu.upenn.cis.cis350.objects;

public class Instructor {
	private String id;
	private String name;
	private String path;
	
	public Instructor(String _id, String _name, String _path) {
		this.id = _id;
		this.name = _name;
		this.path = _path;
	}
	
	public String getID() {	return id; }
	public String getName() { return name; }
	public String getPath() { return path; }
}

