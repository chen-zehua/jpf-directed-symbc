package edu.xjtu.jpf.listener;

import edu.xjtu.jpf.util.HelperClass;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.Transition;

public class TraceListener extends PropertyListenerAdapter{
	
	/*
	 * �����������ִ�й켣��JPF��ִ�й켣������vm��Path��
	 */
	@Override
	public void searchFinished(Search search) {
		// TODO Auto-generated method stub
		Path path = search.getVM().getPath();
		
		for (Transition t : path){
			HelperClass.printTrail(t);
		}
	}
}
