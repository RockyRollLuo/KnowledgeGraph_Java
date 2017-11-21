package beans;

import java.util.HashMap;

public class Pair {
	private String first;
	private String second;
	
	public Pair() {};
	public Pair(String first,String second) {
		this.first=first;
		this.second=second;
	}
	
	@Override
	public String toString() {
		return first+" "+second;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Pair) && (obj.toString()).equals(this.toString());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	
	
	public static void main(String[] args) {
		
		HashMap<Pair, Double> map=new HashMap<>();
		
		Pair pair1=new Pair("e1","e2");
		Pair pair2=new Pair("e1","e2");
		Pair pair3=new Pair("e2","e1");
		
		map.put(pair1, 0.1);
		
		if(map.containsKey(new Pair("e1","e2"))) {
			System.out.println("已经包含pair2了");
		}else {
			System.out.println("不包含pair2");
		}
		
		System.out.println((new Pair("1","3")).toString());
	}
}
