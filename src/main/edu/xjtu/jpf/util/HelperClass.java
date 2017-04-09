package edu.xjtu.jpf.util;

import gov.nasa.jpf.util.Left;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.Transition;

/**
 * 
 * @author zehua
 * 该类中主要放一些帮助方法
 *
 */
public class HelperClass {
	public static void printStateTrail(SystemState ss){
		//get last transition
		Transition trail = ss.getTrail();

		String lastLine = null;

		//mark a transition started
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		//iteratively process each step in the transition
		for (Step s : trail) 
		{
			String line = s.getLineString();
			//if this step has a corresponding SUT source line
			if (line != null) 
			{
				//if this step's corresponding SUT source line is the same as last one
				if (!line.equals(lastLine)) 
				{
					System.out.print("  ");
					System.out.print(Left.format(s.getLocationString(), 30));
					System.out.print(" : ");
					System.out.println(line.trim());
					lastLine = line;
				}
			}

		}
	}
	
	public static void printBranch(SystemState ss) {
		Transition trail = ss.getTrail();
		
		int count = 0;
		String lastLine = null;
		
		for(Step step : trail){
			
			if(count == 2){
				break;
			}
			
			String line = step.getLineString();
			
			if (line != null) {
				
				if (!line.equals(lastLine)) {
					if(count == 0){
						System.out.print("---From---");
						System.out.print(Left.format(step.getLocationString(), 30));
						System.out.print(" : ");
						System.out.println(line.trim());
						lastLine = line;
						count++;
					}else {
						System.out.print("----To----");
						System.out.print(Left.format(step.getLocationString(), 30));						
						System.out.print(" : ");
						System.out.println(line.trim());
						lastLine = line;
						count++;
					}
				}
			}
		}
	}
	
	public static void printTrail(Transition trail){
		
		String lastLine = null;

		//mark a transition started
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		//iteratively process each step in the transition
		for (Step s : trail) 
		{
			String line = s.getLineString();
			//if this step has a corresponding SUT source line
			if (line != null) 
			{
				//if this step's corresponding SUT source line is the same as last one
				if (!line.equals(lastLine)) 
				{
					System.out.print("  ");
					System.out.print(Left.format(s.getLocationString(), 30));
					System.out.print(" : ");
					System.out.println(line.trim());
					lastLine = line;
				}
			}

		}
	}
}
