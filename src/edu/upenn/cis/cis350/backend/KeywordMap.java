package edu.upenn.cis.cis350.backend;

public class KeywordMap {
	private String path;
	private String name;
	private String course_id;
	private Type t;
	
	public enum Type{
		COURSE, INSTRUCTOR;
	}
	
	public KeywordMap(String _path, String _name, String _course_id, Type _t){
		path = _path;
		name = _name;
		course_id = _course_id;
		t = _t;
	}
	
	public String getPath() { return path;	}
	public String getName() { return name; }
	public String getAlias() {return course_id; }
	public Type getType() { return t; }

}
