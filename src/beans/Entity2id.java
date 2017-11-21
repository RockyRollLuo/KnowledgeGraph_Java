package beans;

public class Entity2id {
	private int id;
	private String entity;

	
	public Entity2id() {}
	public Entity2id(int id,String entity) {
		this.id=id;
		this.entity=entity;
	}
	public String toString() {
		return this.entity+" "+this.id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
}
