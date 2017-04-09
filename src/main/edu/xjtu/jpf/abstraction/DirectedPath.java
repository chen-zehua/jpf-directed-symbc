package edu.xjtu.jpf.abstraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * 
 * @author zehua
 * �����ʾ����·����Ϊһ�����нṹ�������б����������·���ϵ����з�֧
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
	 * ���ļ�directe-path.txt�ж�ȡ����·��
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
