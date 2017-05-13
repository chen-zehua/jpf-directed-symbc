package edu.xjtu.jpf.heuristic;

import java.util.ArrayList;

import edu.xjtu.jpf.abstraction.Branch;
import edu.xjtu.jpf.abstraction.DirectedPath;
import edu.xjtu.jpf.util.HelperClass;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.heuristic.HeuristicState;
import gov.nasa.jpf.search.heuristic.SimplePriorityHeuristic;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.Transition;
import gov.nasa.jpf.vm.VM;
/**
 * 
 * @author zehua
 *该类定义了一种路径引导的启发式搜索策略，将搜索类指定为该类时，符号执行过程会依据引导路径前进
 *
 */
public class PathDirectedHeuristic extends SimplePriorityHeuristic{
	//引导路径
	private DirectedPath path;
	//当前待匹配分支
	private Branch curBranch = null;
	
	/*
	 *构造函数，初始化搜索策略、引导路径及当前分支 
	 */
	public PathDirectedHeuristic(Config config, VM vm) {
		super(config, vm);
		// TODO Auto-generated constructor stub
		path = new DirectedPath();
		if (!path.isEmpty()) {
			curBranch = path.remove();
		}
	}

	/*
	 * 用于计算状态优先值的方法，判断当前转态是否与当引导路径中的当前分支匹配，
	 * 匹配的状态优先值为0，否则为Integer.MAX_VALUE 
	 */
	@Override
	protected int computeHeuristicValue() {
		// TODO Auto-generated method stub
		int priority = 0;
		if (curBranch != null && isMatchedBranch()){
			priority = 0;			
			if (!path.isEmpty()){
				curBranch = path.remove();
			}else{
				curBranch = null;
			}
		}else{
			priority = Integer.MAX_VALUE;
		}
		return priority;
	}
	
	/*
	 * 覆盖父类中的该方法，主要修改为：在终结状态仍然要选择一个状态，所以仍然要将所有的终结状态排序
	 */
	@Override
	protected boolean generateChildren() {
		// TODO Auto-generated method stub
	    childStates = new ArrayList<HeuristicState>();
	    
	    while (!done) {
	      
	      if (!forward()) {
	        notifyStateProcessed();
	        return true;
	      }

	      depth++;
	      notifyStateAdvanced();

	      if (currentError != null){
	        notifyPropertyViolated();
	        if (hasPropertyTermination()) {
	          return false;
	        }
	        
	        // note that we don't store the error state anymore, which means we
	        // might encounter it along different paths. However, this is probably
	        // what we want for search.multiple_errors.
	        
	      } else {
	      
	        if (!isEndState() && !isIgnoredState()) {
	          boolean isNewState = isNewState();

	          if (isNewState && depth >= depthLimit) {
	            // we can't do this before we actually generated the VM child state
	            // since we don't want to report DEPTH_CONSTRAINTs for parents
	            // that have only visited or end state children
	            notifySearchConstraintHit("depth limit reached: " + depthLimit);

	          } else if (isNewState || isPathSensitive) {

	            if (isQueueLimitReached()) {
	              notifySearchConstraintHit("queue limit reached: " + getQueueSize());
	            }
	          
	            HeuristicState newHState = queueCurrentState();            
	            if (newHState != null) { 
	              childStates.add(newHState);
	              notifyStateStored();
	            }
	          }
	        
	        } else {
	          // end state or ignored transition
	        	//仅添加以下代码
	            HeuristicState newHState = queueCurrentState();            
	            if (newHState != null) { 
	              childStates.add(newHState);
	              notifyStateStored();
	            }
	        }
	      }
	      
	      backtrackToParent();
	    }
	    
	    return false;
	}
	
	/*
	 * 覆盖父类的该方法，因为对父类该方法没有访问权限，代码功能相同
	 */
	protected void backtrackToParent(){
	    backtrack();

	    depth--;
	    notifyStateBacktracked();  
	}
	
	/*
	 * 覆盖父类方法，仅添加对终结状态的判断，终结状态没有子状态，不需要生成子状态
	 */
	@Override
	public void search() {
		// TODO Auto-generated method stub
	    queueCurrentState();
	    notifyStateStored();
	    
	    // kind of stupid, but we need to get it out of the queue, and we
	    // don't have to restore it since it's the first one
	    parentState = getNextQueuedState();
	    
	    done = false;
	    notifySearchStarted();
	    
	    if (!hasPropertyTermination()) {
	      generateChildren();
	      //因为对终结状态也创建了优先队列，所以也会从终结状态中选一个状态
	      while (!done && (parentState = getNextQueuedState()) != null) {
	        restoreState(parentState);
	        //判断是否为终结状态
	        if (!isEndState())
	        	generateChildren();
	        else
	        	showPathCondition();
	      }
	    }
	    
	    notifySearchFinished();
	}
	
	/*
	 * 覆盖父类方法，无代码修改，功能为恢复至某个状态
	 */
	public void restoreState(HeuristicState hState){
	    vm.restoreState(hState.getVMState());

	    // note we have to query the depth from the VM because the state is taken from the queue
	    // and we have no idea when it was entered there
	    depth = vm.getPathLength();
	    notifyStateRestored();
	}
	
	/*
	 * 判断当前状态是否与待匹配分支匹配，主要使用状态中保存的Transition进行分析，如果trail
	 * 的前两条语句待匹配分支相同，则匹配成功
	 */
	public boolean isMatchedBranch(){
		SystemState systemState = vm.getSystemState();
		Transition trail = systemState.getTrail();
		String lastLine = null;
		int count = 0;
		
		for (Step s : trail) {
			
			String line = s.getLineString();
			if(line != null){
				
				if(!line.equals(lastLine)){
					if(count == 0){
						if (s.getLocationString().equals(
								curBranch.getBranchLine().getLocationString())){
							count++;
							lastLine = line;
							continue;
						}
						return false;
					}
					
					if (count == 1){
						if (s.getLocationString().equals(
								curBranch.getTarget().getLocationString())){
							return true;
						}
						return false;
					}					
					
				}
			}
		}
		return false;
	}
	
	public void showPathCondition() {
		System.out.println("++++++++++++++++++++ Boundary ++++++++++++++++++++++");
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		PCChoiceGenerator prevPcGen;		
		if (cg instanceof PCChoiceGenerator) {
			prevPcGen = (PCChoiceGenerator) cg;
		} else {
			prevPcGen = (PCChoiceGenerator) cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
		}
		
		PathCondition pc = null;
		ArrayList<String> unsatCore = null;
		if (prevPcGen != null) {
			pc = prevPcGen.getCurrentPC();
			pc.solve();
			unsatCore = pc.unsatCore;
		}
		
		if (pc != null) {
			Constraint constraint = pc.header;
			Transition trail;
			while (constraint != null ) {
				System.out.println("==================Constraint====================");
				System.out.println(constraint);
				System.out.println(constraint.hashCode());
				System.out.println("==================Transition====================");
				trail = constraint.getTrail();
				if (trail != null) {
					HelperClass.printTrail(trail);					
				}
				constraint = constraint.and;
			}
			
			if (unsatCore != null) {
				System.out.println("[The Unsatisfiable Core]:");
				for (String s : unsatCore) {
					System.out.println(s);
				}
			}
			System.out.println("++++++++++++++++++++ Boundary ++++++++++++++++++++++");
		}
	}
}
