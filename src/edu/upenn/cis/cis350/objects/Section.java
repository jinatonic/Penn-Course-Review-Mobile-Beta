package edu.upenn.cis.cis350.objects;

public class Section {
	private String alias;
	private String id;
	private String path;
	private String name;
	private String sectionNum;

	public Section(String _alias, String _id, String _path, String _name, String _sectionNum) {
		this.alias = _alias;
		this.id = _id;
		this.path = _path;
		this.name = _name;
		this.sectionNum = _sectionNum;
	}

	public String getAlias() { return alias; }
	public String getID() { return id; }
	public String getPath() { return path; }
	public String getName() { return name; }
	public String getSectionNum() { return sectionNum; }
}

