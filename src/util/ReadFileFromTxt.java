package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Entity2id;
import beans.Relation2id;
import beans.Triple;

public class ReadFileFromTxt {
	
	public static HashMap<String, String> left_entity;  //由relation获得左entity 
	public static HashMap<String, String> right_entity; //由relation获得右entity
	
	/***
	 * 逐行读取文件并打印
	 * @param filePath
	 */
	public static void readFileByPath(String filePath) {
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				System.out.format("%-8d  %s \n",line,tempString);
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%d ms: (%s) \n",line,endTime-startTime,filePath);
	}
	
	/***
	 * 逐行读入实体文件构成对象存入list
	 * @param filePath
	 * @return
	 */
	public static  ArrayList<Entity2id> getEntityObjectList(String filePath){
		ArrayList<Entity2id> list=new ArrayList<Entity2id>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(new Entity2id(Integer.parseInt(tempString.split("	")[1]), tempString.split("	")[0]));
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%d ms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	/***
	 * 逐行读入实体存入list
	 * @param filePath
	 * @return
	 */
	public static  ArrayList<String> getEntityList(String filePath){
		ArrayList<String> list=new ArrayList<String>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(tempString.split("	")[0]);
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%d ms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	/***
	 * 逐行读入关系文件构成对象存入list
	 * @param filePath
	 * @return
	 */
	public static ArrayList<Relation2id> getRelationObjectList(String filePath){
		ArrayList<Relation2id> list=new ArrayList<Relation2id>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(new Relation2id(Integer.parseInt(tempString.split("	")[1]), tempString.split("	")[0]));
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%d ms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	/***
	 * 逐行读入关系存入list
	 * @param filePath
	 * @return
	 */
	public static ArrayList<String> getRelationList(String filePath){
		ArrayList<String> list=new ArrayList<String>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(tempString.split("	")[0]);
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%d ms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	/***
	 * 逐行读入三元组文件构成对象存入list
	 * @param filePath
	 * @return
	 */
	public static ArrayList<Triple> getTripleObjectList(String filePath){
		ArrayList<Triple> list=new ArrayList<Triple>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(new Triple(tempString.split("	")[0],tempString.split("	")[1],tempString.split("	")[2]));
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%dms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	/***
	 * 逐行读入三元组文件构成存入Map
	 * @param filePath
	 * @return
	 */
	public static ArrayList<Triple> getTrainHTRMap(String filePath){
		ArrayList<Triple> list=new ArrayList<Triple>();
		long startTime=System.currentTimeMillis();
		File file=new File(filePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			while((tempString=reader.readLine())!=null) {
				list.add(new Triple(tempString.split("	")[0],tempString.split("	")[1],tempString.split("	")[2]));
			    line++;
			}
			reader.close();
		}catch (IOException e) {
			System.out.println(e);
		}finally {
			if(reader!=null)
				try {
					reader.close();
				}catch (IOException e2) {
				}
		}
		long endTime=System.currentTimeMillis();
		System.out.format("---Total:%-8d Cost time:%dms: (%s) \n",line,endTime-startTime,filePath);
		return list; 
	}
	
	
	
	/**
	 * 测试函数功能
	 * @param args
	 */
	public static void main(String[] args) {
		
//		readFileByPath(ConstVal.testPath);
		
		ArrayList<Entity2id> list=getEntityObjectList(ConstVal.entity2idPath);
//		System.out.println(list.get(7));//list的index即为id号
		
		ArrayList<Relation2id> list2=getRelationObjectList(ConstVal.relation2idPath);
//		System.out.println(tripleList.get(19));
		
		ArrayList<Triple> tripleList1=getTripleObjectList(ConstVal.testPath);
		ArrayList<Triple> tripleList2=getTripleObjectList(ConstVal.trainPath);
		ArrayList<Triple> tripleList3=getTripleObjectList(ConstVal.validPath);
		
		
	}

}
