package edu.xjtu.jpf.abstraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * 
 * @author zehua
 * 该类表示引导路径，为一个队列结构，队列中保存的是这条路径上的所有分支
 *
 */
public class DirectedPath {
	
	private LinkedList<Branch> path;
	
	public DirectedPath(){
		path = new LinkedList<Branch>();
		init();
	}
	
	public void add(Branch b) {
		path.add(b);
	}
	
	public Branch remove() {
		return path.removeFirst();
	}
	
	/*
	 * 从文件directe-path.txt中读取引导路径
	 */
	private void init(){
		File file = new File("resource/directed-path.txt");
		if (file.exists()) {

			try (BufferedReader br = new BufferedReader(new FileReader(file));) {
				String branch = null;
				while((branch = br.readLine()) != null){
					path.add(new Branch(branch));
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public boolean isEmpty(){
		return path.isEmpty();
	}
	
}
