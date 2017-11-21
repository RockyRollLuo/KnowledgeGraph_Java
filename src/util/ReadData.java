package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import beans.Triple;

public class ReadData {
	/***
	 * 只读triple构造方法
	 * @param triplePath
	 */
	public ReadData(String tripleTrainPath,String tripleTestPath) {
		this.tripleTrainPath=tripleTrainPath;
		this.tripleTestPath=tripleTestPath;
		System.out.println("---Reading Triple waiting……");
		readTrainTriple();
		readTestTriple();
		calculate();
		System.out.println("---DONE---\n");
	}
	
	/***
	 * 读四种文件构造方法
	 * @param tripleFilePath
	 * @param entityFilePath
	 * @param relationFilePath
	 */
	public ReadData(String trainFilePath,String testFilePath,String entityFilePath,String relationFilePath) {
		this.entityPath=entityFilePath;
		this.relationPath=relationFilePath;
		this.tripleTrainPath=trainFilePath;
		this.tripleTestPath=testFilePath;
		System.out.println("---Reading entity and relation waiting……");
		readEntity();
		readRelation();
		System.out.println("---Reading triple waiting……");
		readTrainTriple();
		readTestTriple();
		calculate();
		System.out.println("---DONE---\n");
	}
	
	private String entityPath;
	private String relationPath;
	private String tripleTrainPath;
	private String tripleTestPath;
	
	private int entityNum;
	private int relationNum;
	private int tripleTrainNum;
	private int tripleTestNum;
	
	private ArrayList<String> relationList=new ArrayList<String>();
	private ArrayList<String> entityList=new ArrayList<String>();
	private ArrayList<Triple> tripleTrainList=new ArrayList<Triple>();
	private ArrayList<Triple> tripleTestList=new ArrayList<Triple>();
	
	private HashMap<String, ArrayList<String>> relationToHead=new HashMap<String,ArrayList<String>>(); //reltion 对应的 头实体List
	private HashMap<String, ArrayList<String>> relationToTail=new HashMap<String,ArrayList<String>>();
	
	private HashMap<String, Double> relationToHeadNum=new HashMap<String,Double>();
	private HashMap<String, Double> relationToTailNum=new HashMap<String,Double>();
	
	
	/***
	 * 读实体文件
	 */
	private void readEntity() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(this.entityPath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			String entity;
			String[] triple=null;
			while((tempString=reader.readLine())!=null) {
				triple=tempString.split("	");
				entity=triple[0];
				if(!entityList.contains(entity)) entityList.add(entity);
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}catch(NullPointerException e1){
			System.out.println("wrong line:"+line);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		entityNum=entityList.size();
		

		long endTime=System.currentTimeMillis();
		System.out.format("----Entity Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/***
	 * 读关系文件
	 */
	private void readRelation() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(this.relationPath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			String relation;
			String[] triple=null;
			while((tempString=reader.readLine())!=null) {
				triple=tempString.split("	");
				relation=triple[0];
				if(!relationList.contains(relation)) relationList.add(relation);
				
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}catch(NullPointerException e1){
			System.out.println("wrong line:"+line);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		relationNum=relationList.size();
		
		long endTime=System.currentTimeMillis();
		System.out.format("----Relation Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/***
	 * 读训练集三元组
	 */
	private void readTrainTriple() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(this.tripleTrainPath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			String head,tail,relation;
			String[] triple=null;
			while((tempString=reader.readLine())!=null) {
				triple=tempString.split("	");
				head=triple[0];
				tail=triple[1];
				relation=triple[2];
				
				tripleTrainList.add(new Triple(head,tail,relation));
				
				if(!relationList.contains(relation)) relationList.add(relation);
				if(!entityList.contains(head)) entityList.add(head);
				if(!entityList.contains(tail)) entityList.add(tail);
				
				if(relationToHead.containsKey(relation)) {
					ArrayList<String> headList=relationToHead.get(relation);
					headList.add(head);
					relationToHead.put(relation, headList);
				}else {
					ArrayList<String> headList=new ArrayList<String>();
					headList.add(head);
					relationToHead.put(relation, headList);
				}
				
				if(relationToTail.containsKey(relation)) {
					ArrayList<String> tailList=relationToTail.get(relation);
					tailList.add(tail);
					relationToTail.put(relation, tailList);
				}else {
					ArrayList<String> tailList=new ArrayList<String>();
					tailList.add(tail);
					relationToTail.put(relation, tailList);
				}
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}catch(NullPointerException e1){
			System.out.println("wrong line:"+line);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		relationNum=relationList.size();
		entityNum=entityList.size();
		tripleTrainNum=line;
		
		long endTime=System.currentTimeMillis();
		System.out.format("----TrainTriple Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/***
	 * 读测试集三元组
	 */
	private void readTestTriple() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(this.tripleTestPath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			String head,tail,relation;
			String[] triple=null;
			while((tempString=reader.readLine())!=null) {
				triple=tempString.split("	");
				head=triple[0];
				tail=triple[1];
				relation=triple[2];
				
				tripleTestList.add(new Triple(head,tail,relation));
				
//				if(!relationList.contains(relation)) relationList.add(relation);
//				if(!entityList.contains(head)) entityList.add(head);
//				if(!entityList.contains(tail)) entityList.add(tail);
//				
//				if(relationToHead.containsKey(relation)) {
//					ArrayList<String> headList=relationToHead.get(relation);
//					headList.add(head);
//					relationToHead.put(relation, headList);
//				}else {
//					ArrayList<String> headList=new ArrayList<String>();
//					headList.add(head);
//					relationToHead.put(relation, headList);
//				}
//				
//				if(relationToTail.containsKey(relation)) {
//					ArrayList<String> tailList=relationToTail.get(relation);
//					tailList.add(tail);
//					relationToTail.put(relation, tailList);
//				}else {
//					ArrayList<String> tailList=new ArrayList<String>();
//					tailList.add(tail);
//					relationToTail.put(relation, tailList);
//				}
					
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}catch(NullPointerException e1){
			System.out.println("wrong line:"+line);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
//		relationNum=relationList.size();
//		entityNum=entityList.size();
		tripleTestNum=line;
		
		long endTime=System.currentTimeMillis();
		System.out.format("----TestTriple Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/***
	 * 计算每个关系的的每个实体的数量占这个关系所有实体数量的比例
	 */
	private void calculate() {
		for(String relation:relationToHead.keySet()) {
			ArrayList<String> listHead=relationToHead.get(relation);
			HashSet<String> setHead=new HashSet<String>();
			setHead.addAll(listHead);
			double sum1=0.0,sum2=0.0;
			sum1=setHead.size();
			sum2=listHead.size();
			relationToHeadNum.put(relation, sum2/sum1);
		}
		for(String relation:relationToTail.keySet()) {
			ArrayList<String> listTail=relationToTail.get(relation);
			HashSet<String> setTail=new HashSet<String>();
			setTail.addAll(listTail);
			double sum3=0.0,sum4=0.0;
			sum3=setTail.size();
			sum4=listTail.size();
			relationToTailNum.put(relation, sum4/sum3);
		}
		
		System.out.println("entityNum:"+entityNum);
		System.out.println("relationNum:"+relationNum);
		System.out.println("tripleTrainNum:"+tripleTrainNum);
		System.out.println("tripleTestNum:"+tripleTestNum);
		System.out.println("relationToHead.size:"+relationToHead.size());
		System.out.println("relationToTail.size:"+relationToTail.size());
	}
	
	/**
	 * getter setter
	 */
	public int getEntityNum() {
		return entityNum;
	}

	public void setEntityNum(int entityNum) {
		this.entityNum = entityNum;
	}

	public int getRelationNum() {
		return relationNum;
	}

	public void setRelationNum(int relationNum) {
		this.relationNum = relationNum;
	}

	public int getTripleTrainNum() {
		return tripleTrainNum;
	}

	public void setTripleTrainNum(int tripleTrainNum) {
		this.tripleTrainNum = tripleTrainNum;
	}
	
	public int getTripleTestNum() {
		return tripleTestNum;
	}
	
	public void setTripleTestNum(int tripleTestNum) {
		this.tripleTestNum=tripleTestNum;
	}

	public ArrayList<String> getRelationList() {
		return relationList;
	}

	public void setRelationList(ArrayList<String> relationList) {
		this.relationList = relationList;
	}

	public ArrayList<String> getEntityList() {
		return entityList;
	}

	public void setEntityList(ArrayList<String> entityList) {
		this.entityList = entityList;
	}

	public ArrayList<Triple> getTripleTrainList() {
		return tripleTrainList;
	}

	public ArrayList<Triple> getTripleTestList(){
		return tripleTestList;
	}
	
	public void setTripleTrainList(ArrayList<Triple> tripleTrainList) {
		this.tripleTrainList = tripleTrainList;
	}

	public HashMap<String, ArrayList<String>> getRelationToHead() {
		return relationToHead;
	}

	public void setRelationToHead(HashMap<String, ArrayList<String>> relationToHead) {
		this.relationToHead = relationToHead;
	}

	public HashMap<String, ArrayList<String>> getRelationToTail() {
		return relationToTail;
	}

	public void setRelationToTail(HashMap<String, ArrayList<String>> relationToTail) {
		this.relationToTail = relationToTail;
	}

	public HashMap<String, Double> getRelationToHeadNum() {
		return relationToHeadNum;
	}

	public void setRelationToHeadNum(HashMap<String, Double> relationToHeadNum) {
		this.relationToHeadNum = relationToHeadNum;
	}

	public HashMap<String, Double> getRelationToTailNum() {
		return relationToTailNum;
	}

	public void setRelationToTailNum(HashMap<String, Double> relationToTailNum) {
		this.relationToTailNum = relationToTailNum;
	}
	/*
	 * 测试方法
	 */
	public static void main(String[] args) {
		ReadData rData=new ReadData(ConstVal.trainPath,ConstVal.testPath);
		
		ReadData rData2=new ReadData(ConstVal.trainPath,ConstVal.testPath,ConstVal.entity2idPath,ConstVal.relation2idPath);
		HashMap<String, Double> relationToHeadNum=rData2.relationToHeadNum;
		System.out.println("test relation:"+relationToHeadNum.containsValue(2.0));
		
	}

}
