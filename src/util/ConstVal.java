package util;

import java.io.File;

public class ConstVal {
	private static String rootPath=new File(System.getProperty("user.dir")).getPath();
	
	private static String FB15k=rootPath+"\\FB15k\\";
	private static String PCRA=rootPath+"\\PCRA\\";
	
	//文件路径
	public final static String entity2idPath=FB15k+"entity2id.txt";
	public final static String relation2idPath=FB15k+"relation2id.txt";
	public final static String trainPath=FB15k+"train.txt";
	public final static String validPath=FB15k+"valid.txt";
	public final static String testPath=FB15k+"test.txt";
	
	//PCRA文件路径
	public final static String trainPraFilepath=PCRA+"train_pra.txt";
	public final static String testPraFilepath=PCRA+"test_pra.txt";
	public final static String confidenceFilepath=PCRA+"confidence.txt";
	public final static String pathFilepath=PCRA+"path.txt";
	
	
	public final static int MaxInt=Integer.MAX_VALUE;
}
