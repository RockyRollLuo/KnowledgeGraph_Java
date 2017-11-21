package models;

import java.util.ArrayList;
import java.util.HashMap;

import beans.Pair;
import beans.Triple;
import util.CommonMethod;
import util.ConstVal;
import util.PCRA;
import util.ReadData;

public class PTransE {
	private int n;   //向量为度
	private double rate; //学习速率
	private double margin; //间距
	private int nbatches;  //batch数  100
 	private int nepoch;  //迭代次数 1000
	private String method; //bern 或 unif
	private boolean L1_flag; //是否使用范数1计算向量
	
	public PTransE() {
		this.n=100;
		this.rate=0.01;
		this.margin=1.0;
		this.nbatches=100;
		this.nepoch=1000;
		this.method="bern";
		this.L1_flag=true;
	}
	public PTransE(int n,double rate,double margin,int nbatches,int nepoch,String method,boolean L1_flag) {
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
	
	//以下由PCRA获取
	public HashMap<String, HashMap<String,ArrayList<String>>> tripleMap=new HashMap<>();
	public HashMap<Pair, ArrayList<String>> pairToRelationMap=new HashMap<Pair, ArrayList<String>>(); 
	public HashMap<Pair,HashMap<String,Double>> pairByPathPro=new HashMap<Pair,HashMap<String,Double>>();
	public HashMap<Pair,HashMap<String,Double>> pairByPathProFilter=new HashMap<Pair,HashMap<String,Double>>();
	public HashMap<String,Double> pathDict=new HashMap<String,Double>();
	public HashMap<String,Double> pathToRelationDict=new HashMap<String,Double>();
	public HashMap<String,String> pathToRelationMap=new HashMap<String,String>();
	
	//本类计算得来
	public HashMap<String,ArrayList<Double>> relationVec=new HashMap<String,ArrayList<Double>>();  //relation对应其vector
	public HashMap<String,ArrayList<Double>> entityVec=new HashMap<String,ArrayList<Double>>();     
	
	public HashMap<String,ArrayList<Double>> relationVecTmp=new HashMap<String,ArrayList<Double>>(); 
	public HashMap<String,ArrayList<Double>> entityVecTmp=new HashMap<String,ArrayList<Double>>();     
	
	/***
	 * --------------PTransE_Train-----------------
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
		
		PCRA pcra=new PCRA();
		this.tripleMap=pcra.getTripleMap();
		this.pairToRelationMap=pcra.getPairToRelationMap();
		this.pairByPathPro=pcra.getPairByPathPro();
		this.pairByPathProFilter=pcra.getPairByPathProFilter();
		this.pathDict=pcra.getPathDict();
		this.pathToRelationDict=pcra.getPathToRelationDict();
		
		
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
				relationVecTmp=relationVec;
				entityVecTmp=entityVec;
				
				for(int k=0;k<batchsize;k++) {
					int i=CommonMethod.rand_max(tripleTrainNum);
					Triple triple=tripleTrainList.get(i);//随机一个triple
					String h=triple.getHead();
					String r=triple.getRelation(); //随机一个训练集中的r
					String t=triple.getTail();
					
					int j=CommonMethod.rand_max(entityNum);   
					String e=entityList.get(j);               //随机一个实体                
					
					double randTmp=Math.random()*100; //[0,100]
					
					if(randTmp<25) {//换尾
						while(e.equals(t)) { //换一个尾结点，使训练集中不存在此三元组，负例
							j=CommonMethod.rand_max(entityNum);
							e=entityList.get(j);
						}
						res=train(h,t,r,h,e,r,res);   //(h,t,r) (h,t',r)   t'=e
					}else if(randTmp<50){//换头
						while(e.equals(h)) {//换一个头结点，使训练集中不存在此三元组，负例
							j=CommonMethod.rand_max(entityNum);
							e=entityList.get(j);
						}
						res=train(h,t,r,e,t,r,res);    //(h,t,r) (h',t,r)  h'=e
					} else {//换关系
						int r_neg_id=CommonMethod.rand_max(relationNum);
						String r_neg=relationList.get(r_neg_id);
						while(r_neg.equals(r)) {
							r_neg_id=CommonMethod.rand_max(relationNum);
							r_neg=relationList.get(r_neg_id);
						}
						res=train(h,t,r,h,t,r_neg,res);
					}
					
					//路径
					Pair pair=new Pair(h,t);
					if(pairByPathPro.containsKey(pair)) {
						int r_neg_id=CommonMethod.rand_max(relationNum);
						String r_neg=relationList.get(r_neg_id);
						while(r_neg.equals(r)) {
							r_neg_id=CommonMethod.rand_max(relationNum);
							r_neg=relationList.get(r_neg_id);
						}
						
						for(String path:pairByPathPro.get(pair).keySet()) {
							HashMap<String, Double> rel_path=pairByPathPro.get(pair); //连接此(h,t)所有路径

							double pro=pairByPathPro.get(pair).get(path);
							double pro_path=1.0/pairByPathPro.get(pair).size();//pro_path为路径path表示r的概率 
							pro_path=0.99*pro_path+0.01;
							
							res=train_path(r,r_neg,rel_path,2*margin,pro*pro_path,res);
						}
					}
					
					CommonMethod.norm(relationVecTmp.get(r));
					CommonMethod.norm(entityVecTmp.get(e));
					CommonMethod.norm(entityVecTmp.get(triple.getHead()));
					CommonMethod.norm(entityVecTmp.get(triple.getTail()));
				}
				
				relationVec=relationVecTmp;
				entityVec=entityVecTmp;
			}
			System.out.format("----epoch:%-5d  res:%10.4f \n",epoch,res);
			
		}
		
		System.out.println("---DONE---");
	}
	
	/***
	 * loss=正例+margin-负例
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
	 * |t-h-r| L1/L2 
	 */
	private double calcuSum(String h,String t,String r) {
		double sum=0.0;
		for(int ii=0;ii<n;ii++) {
			double tmp=entityVec.get(t).get(ii)-entityVec.get(h).get(ii)-relationVec.get(r).get(ii);
		if(L1_flag)
			sum+=Math.abs(tmp);
		else 
			sum+=Math.pow(tmp, 2);
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
	
	public double train_path(String r,String r_neg,HashMap<String, Double> rel_path,double margin,double x,double res) {
		double sum1=calcuPath(r,rel_path);
		double sum2=calcuPath(r_neg,rel_path);
		double lambda=1;
		if(sum1+margin>sum2) {
			res+=x*lambda*(margin+sum1-sum2);
			gradientPath(r,rel_path,-x*lambda);
			gradientPath(r_neg,rel_path,x*lambda);
		}
		return res;
	}
	
	public double calcuPath(String r,HashMap<String, Double> rel_path) {
		double sum=0.0;
		for(int ii=0;ii<n;ii++) {
			double tmp=relationVec.get(r).get(ii);
			for(String path:rel_path.keySet()) {
				tmp-=relationVec.get(pathToRelationMap.get(path)).get(ii);
			}
			
			if(L1_flag)
				sum+=Math.abs(tmp);
			else
				sum+=Math.pow(tmp, 2);
		}
		return sum;
	}
	
	public void gradientPath(String r,HashMap<String, Double> rel_path,double belta) {
		for(int ii=0;ii<n;ii++) {
			double x=relationVec.get(r).get(ii);
			for(String path:rel_path.keySet())
				x-=relationVec.get(pathToRelationMap.get(path)).get(ii);
			if(L1_flag) {
				if(x>0) x=1;
				else x=-1;
			}
			(relationVecTmp.get(r)).set(ii,relationVecTmp.get(r).get(ii)+belta*rate*x);
			for(String path:rel_path.keySet())
				relationVecTmp.get(pathToRelationMap.get(path)).set(ii, relationVecTmp.get(pathToRelationMap.get(path)).get(ii)-belta*rate*x);
		}
	}
	
	
	
	/*
	 *--------------------test----------------------------------------- 
	 */
	double lsum=0 ,lsum_filter= 0;
	double rsum = 0,rsum_filter=0;
	double mid_sum = 0,mid_sum_filter=0;
	double lp_n=0,lp_n_filter = 0;
	double rp_n=0,rp_n_filter = 0;
	double mid_p_n=0,mid_p_n_filter = 0;
	HashMap<Integer,Double> lsum_r,lsum_filter_r;
	HashMap<Integer,Double> rsum_r,rsum_filter_r;
	HashMap<Integer,Double> mid_sum_r,mid_sum_filter_r;
	HashMap<Integer,Double> lp_n_r,lp_n_filter_r;
	HashMap<Integer,Double> rp_n_r,rp_n_filter_r;
	HashMap<Integer,Double> mid_p_n_r,mid_p_n_filter_r;
	HashMap<String,Integer> rel_num;
	
	double l_one2one=0,r_one2one=0,one2one_num=0; //一对一
    double l_n2one=0,r_n2one=0,n2one_num=0;       //多对一
    double l_one2n=0,r_one2n=0,one2n_num=0;       //一对多
    double l_n2n=0,r_n2n=0,n2n_num=0;             //多对多
    
    
	public void runTest() {
		
		for(Triple triple:tripleTestList) {
			String h=triple.getHead();
			String t=triple.getTail();
			String r=triple.getRelation();
			
			int relnum=(rel_num.get(r)==null)?0:rel_num.get(r);
			rel_num.put(r, relnum+1);
			
			
		}
		
		
	}
	
}
