package beans;

public class Relation2id {
	private int id;
	private String relation;
	
	
	public Relation2id(){}
	public Relation2id(int id,String relation) {
		this.id=id;
		this.relation=relation;
	}
	public String toString() {
		return this.relation+" "+this.id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}

}
