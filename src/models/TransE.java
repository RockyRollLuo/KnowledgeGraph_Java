package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import beans.Triple;
import util.CommonMethod;
import util.ConstVal;
import util.ReadData;

public class TransE {

	private int n;   //向量为度
	private double rate; //学习速率
	private double margin; //间距
	private int nbatches;  //batch数  100
 	private int nepoch;  //迭代次数 1000
	private String method; //bern 或 unif
	private boolean L1_flag; //是否使用范数1计算向量
	
	public TransE() {
		this.n=100;
		this.rate=0.01;
		this.margin=1.0;
		this.nbatches=100;
		this.nepoch=1000;
		this.method="bern";
		this.L1_flag=true;
	}
	public TransE(int n,double rate,double margin,int nbatches,int nepoch,String method,boolean L1_flag) {
		this.n=n;
		this.rate=rate;
		this.margin=margin;
		this.nbatches=nbatches;
		this.nepoch=nepoch;
		this.method=method;
		this.L1_flag=L1_flag;
	}
	
	
	//以下由readData获取
	public int entityNum;
	public int relationNum;
	public int tripleTrainNum; //训练集大小
	public int tripleTestNum; //测试集大小
	public ArrayList<String> relationList;
	public ArrayList<String> entityList;
	public ArrayList<Triple> tripleTrainList;
	public ArrayList<Triple> tripleTestList;
	public HashMap<String, ArrayList<String>> relationToHead; //reltion 对应的 头实体List
	public HashMap<String, ArrayList<String>> relationToTail;
	public HashMap<String, Double> relationToHeadNum;
	public HashMap<String, Double> relationToTailNum;
	
	//本类计算得来
	public HashMap<String,ArrayList<Double>> relationVec=new HashMap<String,ArrayList<Double>>();  //relation对应其vector
	public HashMap<String,ArrayList<Double>> entityVec=new HashMap<String,ArrayList<Double>>();     
	
	public HashMap<String,ArrayList<Double>> relationVecTmp=new HashMap<String,ArrayList<Double>>(); 
	public HashMap<String,ArrayList<Double>> entityVecTmp=new HashMap<String,ArrayList<Double>>();     
	
	/***
	 * --------------TransE_Train-----------------
	 */
	
	
	/***
	 * 取值和初始化
	 */
	public void runTrain() {
		
		System.out.println("==TransE.runTrain===================");
		//赋值
		ReadData rd=new ReadData(ConstVal.trainPath,ConstVal.testPath);
		this.entityNum=rd.getEntityNum();
		this.relationNum=rd.getRelationNum();
		this.tripleTrainNum=rd.getTripleTrainNum();
		this.relationList=rd.getRelationList();
		this.entityList=rd.getEntityList();
		this.tripleTrainList=rd.getTripleTrainList();
		this.tripleTestList=rd.getTripleTestList();
		this.relationToHead=rd.getRelationToHead();
		this.relationToTail=rd.getRelationToTail();
		this.relationToHeadNum=rd.getRelationToHeadNum();
		this.relationToTailNum=rd.getRelationToTailNum();
		
		//初始化关系和实体的向量
		for(String relation:relationList) {
			ArrayList<Double> list=new ArrayList<Double>();
			for(int ii=0;ii<n;ii++) {
				list.add(CommonMethod.randn(0,1.0/n,-6/Math.sqrt(n),6/Math.sqrt(n)));
			}
			relationVec.put(relation,list);
		}
		
		for(String entity:entityList) {
			ArrayList<Double> list=new ArrayList<Double>();
			for(int ii=0;ii<n;ii++) {
				list.add(CommonMethod.randn(0,1.0/n,-6/Math.sqrt(n),6/Math.sqrt(n)));
			}
			entityVec.put(entity,list);
		}
		
		bfgs();
	}
	
	/***
	 * 优化算法
	 */
	private void bfgs() {
		int batchsize=tripleTrainNum/nbatches;
		
		double res=0.0; //loss function value
		for(int epoch=0;epoch<nepoch;epoch++) {
			res=0;
			for(int batch=0;batch<nbatches;batch++) {
//				relationVecTmp=CommonMethod.deepClone(relationVec);
//				entityVecTmp=CommonMethod.deepClone(entityVec);
				relationVecTmp=relationVec;
				entityVecTmp=entityVec;
				
				for(int k=0;k<batchsize;k++) {
					int i=CommonMethod.rand_max(tripleTrainNum);
					Triple triple=tripleTrainList.get(i);//随机一个triple
					String r=triple.getRelation(); //随机一个训练集中的r
					
					int j=CommonMethod.rand_max(entityNum);   
					String e=entityList.get(j);               //随机一个实体                
					
					double pr=1000*relationToTailNum.get(r)/(relationToTailNum.get(r)+relationToHeadNum.get(r));
					if(method.equals("unif")) pr=500;
					
					if(CommonMethod.rand_max(ConstVal.MaxInt)%1000<pr) {
						while(e.equals(triple.getTail())) { //换一个尾结点，使训练集中不存在此三元组，负例
							j=CommonMethod.rand_max(entityNum);
							e=entityList.get(j);
						}
						res=train(triple.getHead(),triple.getTail(),r,triple.getHead(),e,r,res);   //(h,t,r) (h,t',r)   t'=e
					}else {
						while(e.equals(triple.getHead())) {//换一个头结点，使训练集中不存在此三元组，负例
							j=CommonMethod.rand_max(entityNum);
							e=entityList.get(j);
						}
						res=train(triple.getHead(),triple.getTail(),r,e,triple.getTail(),r,res);    //(h,t,r) (h',t,r)  h'=e
					} 
					CommonMethod.norm(relationVecTmp.get(r));
					CommonMethod.norm(entityVecTmp.get(e));
					CommonMethod.norm(entityVecTmp.get(triple.getHead()));
					CommonMethod.norm(entityVecTmp.get(triple.getTail()));
					
					
				}
				
				
//				relationVec=CommonMethod.deepClone(relationVecTmp);
//				entityVec=CommonMethod.deepClone(entityVecTmp);
				relationVec=relationVecTmp;
				entityVec=entityVecTmp;
			}
			System.out.format("----epoch:%-5d  res:%10.4f \n",epoch,res);
			
		}
		
		System.out.println("---DONE---");
	}
	
	/***
	 * 正例+margin>负例
	 * (h1,t1,r1)与(h2,t2,r2)
	 * 一个正例，一个反例 
	 */
	private double train(String h1,String t1,String r1,String h2,String t2,String r2,double res) {
		double sum1=calcuSum(h1,t1,r1);
		double sum2=calcuSum(h2,t2,r2);
		if(sum1+margin>sum2) {
			res+=(margin+sum1-sum2);
			gradient(h1,t1,r1,h2,t2,r2);
		}
		return res;
	}
	
	/***
	 * |h-r-t| L1/L2 
	 */
	private double calcuSum(String h,String t,String r) {
		double sum=0.0;
		if(L1_flag) {
			for(int ii=0;ii<n;ii++)
				sum+=Math.abs(entityVec.get(t).get(ii)-entityVec.get(h).get(ii)-relationVec.get(r).get(ii));
		}else {
			for(int ii=0;ii<n;ii++)
				sum+=Math.pow(entityVec.get(t).get(ii)-entityVec.get(h).get(ii)-relationVec.get(r).get(ii), 2);
		}
		return sum;
	}
	
	/***
	 * 梯度下降
	 */
	private void gradient(String h1,String t1,String r1,String h2,String t2,String r2) {
		for(int ii=0;ii<n;ii++) {
			//x=2(t1-h1-r1)
			double x=2*(entityVec.get(t1).get(ii)-entityVec.get(h1).get(ii)-relationVec.get(r1).get(ii));
			if(L1_flag) {
				if(x>0)
					x=1;
				else 
					x=-1;
			}
			(relationVecTmp.get(r1)).set(ii, (relationVecTmp.get(r1)).get(ii)-(-1*rate*x));   
			(entityVecTmp.get(h1)).set(ii, (entityVecTmp.get(h1)).get(ii)-(-1*rate*x));
			(entityVecTmp.get(t1)).set(ii, (entityVecTmp.get(t1)).get(ii)+(-1*rate*x));

			x=2*(entityVec.get(t2).get(ii)-entityVec.get(h2).get(ii)-relationVec.get(r2).get(ii));
			if(L1_flag) {
				if(x>0)
					x=1;
				else 
					x=-1;
			}
			(relationVecTmp.get(r2)).set(ii, (relationVecTmp.get(r2)).get(ii)-(1*rate*x));   
			(entityVecTmp.get(h2)).set(ii, (entityVecTmp.get(h2)).get(ii)-(1*rate*x));
			(entityVecTmp.get(t2)).set(ii, (entityVecTmp.get(t2)).get(ii)+(1*rate*x));
		}
	}
	
	
	
	/***
	 * --------------TransE_Test-----------------
	 */
	
	public double lsum=0.0,rsum=0.0;
	public double lsum_filter=0.0,rsum_filter=0.0;
	public double lp_n=0,lp_n_filter;
	public double rp_n=0,rp_n_filter;
	public HashMap<String, Double> lsum_r=new HashMap<String,Double>();
	public HashMap<String, Double> lsum_filter_r=new HashMap<String,Double>();
	public HashMap<String, Double> rsum_r=new HashMap<String,Double>();
	public HashMap<String, Double> rsum_filter_r=new HashMap<String,Double>();
	public HashMap<String, Double> lp_n_r=new HashMap<String,Double>();
	public HashMap<String, Double> lp_n_filter_r=new HashMap<String,Double>();
	public HashMap<String, Double> rp_n_r=new HashMap<String,Double>();
	public HashMap<String, Double> rp_n_filter_r=new HashMap<String,Double>();
	
	public void runTest() {
		System.out.println("==TransE.runTest===================");
		
		for(Triple triple:tripleTestList) {
			String head=triple.getHead();
			String tail=triple.getTail();
			String relation=triple.getRelation();
			double tmp=calcuSum(head, tail, relation); //范数L1

			HashMap<String, Double> map=new HashMap<String, Double>();
			//实体集中每一个实体替换此三元组的头计算范数
			for(String h:entityList) {
				double sum=calcuSum(h, tail, relation);
				map.put(h, sum);
			}
			//map按value降序
			List<Map.Entry<String, Double>> list=new ArrayList<Map.Entry<String,Double>>(map.entrySet());
			Collections.sort(list,new Comparator<Map.Entry<String, Double>>() {
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			int filter=0;
			int ttt=0;
			
			int index=0;
			for(String h:map.keySet()) {
				index++;
				Triple triple2=new Triple(h, tail, relation);
				
				if(tripleTrainList.contains(triple2)) //训练集中包含此三元组
					ttt++;
				if(!tripleTrainList.contains(triple2))
					filter++;
				
				if(h.equals(head)) {
					lsum+=map.size()-index;
					lsum_filter+=filter+1;
					
					lsum_r.put(relation, lsum_r.get(relation)+map.size()-index);
					lsum_filter_r.put(relation, lsum_filter_r.get(relation)+filter+1);
					
					if(map.size()-index<10) {
						lp_n+=1;
						lp_n_r.put(relation, lp_n_r.get(relation)+1);
					}
					if(filter<10) {
						lp_n_filter+=1;
						lp_n_filter_r.put(relation, lp_n_filter_r.get(relation)+1);
					}
					break;
				}
			}
			
			
			map=null;
			map=new HashMap<String, Double>();
			for(String t:entityList) {
				double sum=calcuSum(head, t, relation);
				map.put(t, sum);
			}
			//map按value降序
			List<Map.Entry<String, Double>> list2=new ArrayList<Map.Entry<String,Double>>(map.entrySet());
			Collections.sort(list2,new Comparator<Map.Entry<String, Double>>() {
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			filter=0;
			ttt=0;
			
			index=0;
			for(String t:map.keySet()) {
				index++;
				Triple triple2=new Triple(head, t, relation);
				
				if(tripleTrainList.contains(triple2)) 
					ttt++;
				if(!tripleTrainList.contains(triple2))
					filter++;
				
				if(t.equals(tail)) {
					rsum+=map.size()-index;
					rsum_filter+=filter+1;
					
					rsum_r.put(relation, rsum_r.get(relation)+map.size()-index);
					rsum_filter_r.put(relation, rsum_filter_r.get(relation)+filter+1);
					
					if(map.size()-index<10) {
						rp_n+=1;
						rp_n_r.put(relation, rp_n_r.get(relation)+1);
					}
					if(filter<10) {
						rp_n_filter+=1;
						rp_n_filter_r.put(relation, rp_n_filter_r.get(relation)+1);
					}
					break;
				}
			}
		}
		
		System.out.format("left :%f %f %f %f \n",lsum/tripleTestNum,lp_n/tripleTestNum,lsum_filter/tripleTestNum,lp_n_filter/tripleTestNum);
		System.out.format("right:%f %f %f %f \n",rsum/tripleTestNum,rp_n/tripleTestNum,rsum_filter/tripleTestNum,rp_n_filter/tripleTestNum);
		
	}
}
