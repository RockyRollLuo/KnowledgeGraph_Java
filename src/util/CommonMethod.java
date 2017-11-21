package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class CommonMethod {

	
	/*
	 * ----TransE------------------------------------------------------------------------
	 */
	
	public static double randn(double miu,double sigma,double min,double max) {
		double x,y,dScope;
		do{
			x=rand(min,max);
	        y=normal(x,miu,sigma);
	        dScope=rand(0.0,normal(miu,miu,sigma));
		}while(dScope>y);
		return x;
	}
	
	//在min到max之间的随机实数
	public static double rand(double min, double max)
	{
	    //return min+(max-min)*rand()/(RAND_MAX+1.0);
		return min+(max-min)*Math.random();
	}
	
	//在[0,x]之间的随机整数
	public static int rand_max(int x) {
		Random random=new Random();
		return random.nextInt(x);
	}
	
	//正太分布的概率密度函数(x,μ,σ)
	public static double normal(double x, double miu,double sigma)
	{
	    return (1.0/(sigma*Math.sqrt(2*Math.PI)))*Math.exp(-1*(x-miu)*(x-miu)/(2*sigma*sigma));
	}

	//转为单位向量
	public static void norm(ArrayList<Double> a) {
		double x=vec_len(a);
		if(x>1)
			for(int ii=0;ii<a.size();ii++)
				a.set(ii, a.get(ii)/x);
	}

	//向量的模，范数2
	public static double vec_len(ArrayList<Double> a) {
		double res=0.0;
		for(int i=0;i<a.size();i++) {
			res+=Math.pow(a.get(i), 2);
		}
		res=Math.sqrt(res);
		return res;
	}
	
	//将向量文件写入file
	public static void writeToFile(HashMap<String, ArrayList<Double>> a) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmm");
		String curTime=sdf.format(new Date());
		//将curTime加到名字后缀中
		
		System.out.println(curTime);
	}
	
	
	//深度复制一个Map<String,List>
	public static HashMap<String,ArrayList<Double>> deepClone(HashMap<String, ArrayList<Double>> map){
		HashMap<String, ArrayList<Double>> cloneMap=new HashMap<String,ArrayList<Double>>();
		
		for(String str:map.keySet()) {
			ArrayList<Double> list=new ArrayList<Double>();
			list=map.get(str);
			cloneMap.put(str, list);
		}
		return cloneMap;
	}
	
	
	/*
	 * ----PTransE------------------------------------------------------------------------
	 */
	
	public static double sigmod(double x) {
		return 1.0/(1+Math.exp(-x));
	}
}
