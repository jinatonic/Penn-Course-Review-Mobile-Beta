package edu.upenn.cis.cis350;

public class Section {
	private String[] aliases;
	private String id;
	private String path;
	private String name;
	private String sectionNum;

	public Section(String[] _aliases, String _id, String _path, String _name, String _sectionNum) {
		aliases = _aliases;
		id = _id;
		path = _path;
		name = _name;
		sectionNum = _sectionNum;
	}

	public String[] getAliases() { return aliases;}
	public String getID() { return id; }
	public String getPath() { return path; }
	public String getName() { return name; }
	public String getSectionNum() { return sectionNum; }
}

