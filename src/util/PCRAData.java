package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/***
 * PCRA对训练文件的处理
 *
 * 读取confidence 和train_pra
 */
public class PCRAData {
	
	public PCRAData() {
		
		readConfidence();
	}
	
	private void readTrainPra() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(ConstVal.trainPraFilepath);
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
		System.out.format("----Entity Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	private void readTestPra() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(ConstVal.testPraFilepath);
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
		System.out.format("----Entity Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	private void readPath() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(ConstVal.pathFilepath);
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
		System.out.format("----Entity Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
	
	private void readConfidence() {
		long startTime=System.currentTimeMillis();
		
		File file=new File(ConstVal.confidenceFilepath);
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
		System.out.format("----Entity Total:%-8d Cost time:%dms \n",line,endTime-startTime);
	}
}
