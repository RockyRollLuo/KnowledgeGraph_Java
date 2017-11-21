package beans;

import java.util.ArrayList;

public class Triple {
	private String head;
	private String tail;
	private String relation;
	
	public Triple(){}
	public Triple(String h,String t,String r) {
		this.head=h;
		this.tail=t;
		this.relation=r;
	}
	
	@Override
	public String toString() {
		return "("+this.head+" "+this.relation+" "+this.tail+")";
	}

	//重写equals方法，判断是两个Triple是否相等
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Triple) && ((this.toString()).equals(obj.toString()));
	}
	
	@Override
	public int hashCode() {
		return (this.head+this.relation+this.tail).hashCode();
	}
	
	
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getTail() {
		return tail;
	}
	public void setTail(String tail) {
		this.tail = tail;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}

	
	
	public static void main(String[] args) {
		ArrayList<Triple> list=new ArrayList<Triple>();
		
		Triple triple1=new Triple("h", "t", "r");
		Triple triple2=new Triple("h", "t", "r");
		
		list.add(triple1);
		
		if(triple1.equals(triple2)) {
			System.out.println("triple1 equals triple2");
		}else {
			System.out.println("triple1 not equals triple2");
		}
		
		if(list.contains(triple1)) {
			System.out.println("list contains triple1");
		}else {
			System.out.println("list not contains triple1");
		}
		
		if(list.contains(triple2)) {
			System.out.println("list contains triple2");
		}else {
			System.out.println("list not contains triple2");
		}
		
		if(list.contains(new Triple("h", "t", "r"))) {
			System.out.println("list contains (h,t,r)");
		}else {
			System.out.println("list not contains (h,t,r)");
		}
		
		for(Triple tri:list) {
			if(tri.equals(triple2)) {
				System.out.println("YES");
				break;
			}
		}
		
		
	}
	
}
