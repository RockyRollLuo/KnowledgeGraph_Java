package models;

public class Main {

	public static void main(String[] args) {
		int n=100;
		double rate=0.01;
		double margin=1.0;
		int nbatches=100;
		int nepoch=10;         //迭代次数
		String method="bern";
		boolean L1_flag=true;
		
		
		TransE transE=new TransE(n, rate, margin, nbatches, nepoch, method, L1_flag);
		transE.runTrain();
		transE.runTest();
		
		
		
	}

}
