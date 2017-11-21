package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Pair;
import beans.Triple;

public class PCRA {
	
	//默认构造方法读取FB15k目录下文件
	public PCRA() {
		this.entityFilePath=ConstVal.entity2idPath;
		this.relationFilePath=ConstVal.relation2idPath;
		this.trainFilePath=ConstVal.trainPath;
		run();
	}
	
	public PCRA(String entityFilePath,String relationFilePath,String trainFilePath) {
		this.entityFilePath=entityFilePath;
		this.relationFilePath=relationFilePath;
		this.trainFilePath=trainFilePath;
		run();
	}
	
	
	private String entityFilePath;
	private String relationFilePath;
	private String trainFilePath;
	
	
	private ArrayList<String> entityList=new ArrayList<String>();
	private ArrayList<String> relationList=new ArrayList<String>();
	private HashMap<String,String> relationToId=new HashMap<>(); //relation,id
	private HashMap<String,String> idToRelation=new HashMap<>(); //id,relation
	private ArrayList<Triple> tripleTrainList=new ArrayList<Triple>();
	
	private int pairNum;//产生关系实体对数
	
	//a
	//存(h,(r,t))和(t,(~r,h))
	private HashMap<String, HashMap<String,ArrayList<String>>> tripleMap=new HashMap<>();
	
	//ok
	//train中存在(h,t),relation或(t,h),~relation
	private HashMap<Pair, ArrayList<String>> pairToRelationMap=new HashMap<Pair, ArrayList<String>>(); 
	
	//h_e_p
	//实体(e1,e2)经过路径(r)或(r1 r2)的概率 
	private HashMap<Pair,HashMap<String,Double>> pairByPathPro=new HashMap<Pair,HashMap<String,Double>>();
	//只保留概率大于0.01的路径
	private HashMap<Pair,HashMap<String,Double>> pairByPathProFilter=new HashMap<Pair,HashMap<String,Double>>();
	
	//path_dict
	//某个关系路径的数量
	private HashMap<String,Double> pathDict=new HashMap<String,Double>();
	
	//path_r_dict
	//某个路径转成某个关系的数量 (path->r,数量)
	private HashMap<String,Double> pathToRelationDict=new HashMap<String,Double>();
	
	
	/**
	 * 运行此类
	 */
	private void run() {
		readEntity();
		readRelation();
		readTrainTriple();
		caculate();
	}
	
	/**
	 * 读实体存入entityList
	 */
	private void readEntity() {
		long startTime=System.currentTimeMillis();

		File file=new File(entityFilePath);
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
				if(!entityList.contains(entity)) 
					entityList.add(entity);
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

		long endTime=System.currentTimeMillis();
		System.out.format("----Entity Total:%d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/**
	 * 读关系存入relationList,并存入反向关系
	 */
	private void readRelation() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(relationFilePath);
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
				if(!relationList.contains(relation)) 
					relationList.add(relation);
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

		//增加反向关系
//		CopyOnWriteArrayList<String> relationListTmp=new CopyOnWriteArrayList<>();
		//注意，不能边对list做修改边迭代
		ArrayList<String> relationListTmp=new ArrayList<>();
		relationListTmp.addAll(relationList);
		for(String r:relationListTmp) {
			relationList.add("~"+r);
		}
		
		for(int i=0;i<relationList.size();i++) {
			idToRelation.put(i+"", relationList.get(i));
			relationToId.put(relationList.get(i), i+"");
		}
			
		long endTime=System.currentTimeMillis();
		System.out.format("----Relation Total:%d Cost time:%dms \n",2*line,endTime-startTime);
	}

	/**
	 * 读训练三元组
	 * 存入TripleList (h,t,r)
	 * 存入tripleMap (h,(r,(t)))  (t,(~r,(h)))
	 * 存入pairToRelationMap ((h,t),r)  ((t,h),~r)
	 */
	private void readTrainTriple() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(trainFilePath);
		BufferedReader reader=null;
		int line=0;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString=null;
			String head,tail,relation,relationid1,relationid2;
			String[] triple=null;
			while((tempString=reader.readLine())!=null) {
				triple=tempString.split("	");
				head=triple[0];
				tail=triple[1];
				relation=triple[2];
				relationid1=relationToId.get(relation); //正向关系
				relationid2=relationToId.get("~"+relation); //反向关系
				tripleTrainList.add(new Triple(head,tail,relationid1));
				
				//tripleMap
				tripleMapAdd(head, relationid1, tail);
				tripleMapAdd(tail, relationid2, head);
				
				//pairToRelationMap
				pairToRelationMapAdd(new Pair(head,tail), relationid1);
				pairToRelationMapAdd(new Pair(tail,head), relationid2);
				
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

		long endTime=System.currentTimeMillis();
		System.out.format("----TrainTriple Total:%d Cost time:%dms \n",line,endTime-startTime);
	}
	
	/**
	 * 计算一步路径和两步路径的概率
	 */
	private void caculate() {
		long startTime=System.currentTimeMillis();
		System.out.println();
		
		for(String e1:tripleMap.keySet()) {
			//一步路径: r1
			for(String r1:tripleMap.get(e1).keySet()) {
				ArrayList<String> e2_list=tripleMap.get(e1).get(r1);
				for(String e2:e2_list) {
					Pair pair=new Pair(e1,e2);
					pathDictAdd(r1);//增加路径r1
					if(pairToRelationMap.containsKey(pair))
						for(String r:pairToRelationMap.get(pair)) 
							pathToRelationDictAdd(r1+"->"+r);  //一步路径  r1可表示r
					pairByPathProAdd(pair, r1, 1.0/e2_list.size()); //e1与e2由一步路径r1联通的概率为1/e2_list.size
				}
			}
			//两步路径 :r1 r2
			for(String r1:tripleMap.get(e1).keySet()) {
				ArrayList<String> e2_list=tripleMap.get(e1).get(r1);
				for(String e2:e2_list) {
					if(tripleMap.containsKey(e2)) {
						for(String r2:tripleMap.get(e2).keySet()) {
							ArrayList<String> e3_list=tripleMap.get(e2).get(r2);
							for(String e3:e3_list) {
								Pair pair=new Pair(e1,e3);
								pathDictAdd(r1+" "+r2);  //两步路径  r1 r2
								if(pairToRelationMap.containsKey(pair))
									for(String r:pairToRelationMap.get(pair))
										pathToRelationDictAdd(r1+" "+r2+"->"+r); //r1 r2->r
								pairByPathProAdd(pair, r1+" "+r2,pairByPathPro.get(new Pair(e1,e2)).get(r1)/e3_list.size());//e1到e3的概率
							}
						}
					}
				}
			}
			//三步路径:r1 r2 r3
			//略去
			
			
			//略去概率小于0.01的路径
			for(String e2:tripleMap.keySet()) {
				Pair pair=new Pair(e1,e2);
				if(pairByPathPro.containsKey(pair)) {
					double sum=0.0;
					HashMap<String, Double> pathPro=new HashMap<>();
					
					for(String path:pairByPathPro.get(pair).keySet())
						sum+=pairByPathPro.get(pair).get(path);
					
					for(String path:pairByPathPro.get(pair).keySet()) {
						double pro=pairByPathPro.get(pair).get(path)/sum;   //此路径的概率占此pair所有路径概率和超过0.01
						if(pro>0.01)
							pathPro.put(path,pro);
					}
					
					pairByPathProFilter.put(pair, pathPro);
				}
			}
		}
		
		pairNum=pairByPathPro.size();
		
		long endTime=System.currentTimeMillis();
		System.out.format("----PairNum Total:%d  Cost time:%dms \n",pairNum,endTime-startTime);
	}
	
	/**
	 * 向tripleMap中新增一条
	 * @param e1
	 * @param r
	 * @param e2
	 */
	private void tripleMapAdd(String e1,String r,String e2) {
		HashMap<String, ArrayList<String>> map=(tripleMap.get(e1)==null)?(new HashMap<>()):(tripleMap.get(e1));
		ArrayList<String> list=(map.get(r)==null)?(new ArrayList<String>()):(map.get(r));
		list.add(e2);
		map.put(r, list);
		tripleMap.put(e1, map);
	}
	
	/**
	 * 向pairToRelationMap中添一条
	 * @param pair
	 * @param r
	 */
	private void pairToRelationMapAdd(Pair pair,String r) {
		ArrayList<String> list=(pairToRelationMap.get(pair)==null)?(new ArrayList<String>()):(pairToRelationMap.get(pair));
		list.add(r);
		pairToRelationMap.put(pair, list);
	}
	
	/**
	 * 向pathDict中添加一路径
	 * @param path
	 */
	private void pathDictAdd(String path) {
		if(!pathDict.containsKey(path)) {
			pathDict.put(path, 1.0);
		}
		Double num=pathDict.get(path)+1;
		pathDict.put(path,num);
	}
	
	/**
	 * 向pathToRelationDict中添加一路径
	 * @param str:path->r
	 */
	private void pathToRelationDictAdd(String str) {
		if(!pathToRelationDict.containsKey(str)) {
			pathToRelationDict.put(str, 1.0);
		}
		pathToRelationDict.put(str, pathToRelationDict.get(str)+1);
	}
	
	/**
	 * 向pairByPathPro中增加一条
	 * @param pair
	 * @param path
	 * @param pro
	 */
	private void pairByPathProAdd(Pair pair,String path,Double pro) {
		HashMap<String, Double> map=(pairByPathPro.get(pair)==null)?(new HashMap<>()):(pairByPathPro.get(pair));
		Double orignPro=(map.get(path)==null)?(0.0):map.get(path);
		map.put(path, orignPro+pro);
		pairByPathPro.put(pair, map);
	}
	
	
	
	
	//getter & setter
	public String getEntityFilePath() {
		return entityFilePath;
	}

	public void setEntityFilePath(String entityFilePath) {
		this.entityFilePath = entityFilePath;
	}

	public String getRelationFilePath() {
		return relationFilePath;
	}

	public void setRelationFilePath(String relationFilePath) {
		this.relationFilePath = relationFilePath;
	}

	public String getTrainFilePath() {
		return trainFilePath;
	}

	public void setTrainFilePath(String trainFilePath) {
		this.trainFilePath = trainFilePath;
	}

	public ArrayList<String> getEntityList() {
		return entityList;
	}

	public void setEntityList(ArrayList<String> entityList) {
		this.entityList = entityList;
	}

	public ArrayList<String> getRelationList() {
		return relationList;
	}

	public void setRelationList(ArrayList<String> relationList) {
		this.relationList = relationList;
	}

	public ArrayList<Triple> getTripleTrainList() {
		return tripleTrainList;
	}

	public void setTripleTrainList(ArrayList<Triple> tripleTrainList) {
		this.tripleTrainList = tripleTrainList;
	}

	public int getPairNum() {
		return pairNum;
	}

	public void setPairNum(int pairNum) {
		this.pairNum = pairNum;
	}

	public HashMap<String, HashMap<String, ArrayList<String>>> getTripleMap() {
		return tripleMap;
	}

	public void setTripleMap(HashMap<String, HashMap<String, ArrayList<String>>> tripleMap) {
		this.tripleMap = tripleMap;
	}

	public HashMap<Pair, ArrayList<String>> getPairToRelationMap() {
		return pairToRelationMap;
	}

	public void setPairToRelationMap(HashMap<Pair, ArrayList<String>> pairToRelationMap) {
		this.pairToRelationMap = pairToRelationMap;
	}

	public HashMap<Pair, HashMap<String, Double>> getPairByPathPro() {
		return pairByPathPro;
	}

	public void setPairByPathPro(HashMap<Pair, HashMap<String, Double>> pairByPathPro) {
		this.pairByPathPro = pairByPathPro;
	}

	public HashMap<Pair, HashMap<String, Double>> getPairByPathProFilter() {
		return pairByPathProFilter;
	}

	public void setPairByPathProFilter(HashMap<Pair, HashMap<String, Double>> pairByPathProFilter) {
		this.pairByPathProFilter = pairByPathProFilter;
	}

	public HashMap<String, Double> getPathDict() {
		return pathDict;
	}

	public void setPathDict(HashMap<String, Double> pathDict) {
		this.pathDict = pathDict;
	}

	public HashMap<String, Double> getPathToRelationDict() {
		return pathToRelationDict;
	}

	public void setPathToRelationDict(HashMap<String, Double> pathToRelationDict) {
		this.pathToRelationDict = pathToRelationDict;
	}
	
	
	
	public static void main(String[] args) {
		PCRA pcra=new PCRA();
		
	}
}
