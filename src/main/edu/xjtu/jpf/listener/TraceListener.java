package edu.xjtu.jpf.listener;

import edu.xjtu.jpf.util.HelperClass;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.Transition;

public class TraceListener extends PropertyListenerAdapter{
	
	/*
	 * 搜索结束输出执行轨迹，JPF的执行轨迹保存在vm的Path中
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
