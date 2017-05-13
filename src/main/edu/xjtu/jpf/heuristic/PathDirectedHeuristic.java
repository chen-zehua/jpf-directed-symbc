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
 *���ඨ����һ��·������������ʽ�������ԣ���������ָ��Ϊ����ʱ������ִ�й��̻���������·��ǰ��
 *
 */
public class PathDirectedHeuristic extends SimplePriorityHeuristic{
	//����·��
	private DirectedPath path;
	//��ǰ��ƥ���֧
	private Branch curBranch = null;
	
	/*
	 *���캯������ʼ���������ԡ�����·������ǰ��֧ 
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
	 * ���ڼ���״̬����ֵ�ķ������жϵ�ǰת̬�Ƿ��뵱����·���еĵ�ǰ��֧ƥ�䣬
	 * ƥ���״̬����ֵΪ0������ΪInteger.MAX_VALUE 
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
	 * ���Ǹ����еĸ÷�������Ҫ�޸�Ϊ�����ս�״̬��ȻҪѡ��һ��״̬��������ȻҪ�����е��ս�״̬����
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
	        	//��������´���
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
	 * ���Ǹ���ĸ÷�������Ϊ�Ը���÷���û�з���Ȩ�ޣ����빦����ͬ
	 */
	protected void backtrackToParent(){
	    backtrack();

	    depth--;
	    notifyStateBacktracked();  
	}
	
	/*
	 * ���Ǹ��෽��������Ӷ��ս�״̬���жϣ��ս�״̬û����״̬������Ҫ������״̬
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
	      //��Ϊ���ս�״̬Ҳ���������ȶ��У�����Ҳ����ս�״̬��ѡһ��״̬
	      while (!done && (parentState = getNextQueuedState()) != null) {
	        restoreState(parentState);
	        //�ж��Ƿ�Ϊ�ս�״̬
	        if (!isEndState())
	        	generateChildren();
	        else
	        	showPathCondition();
	      }
	    }
	    
	    notifySearchFinished();
	}
	
	/*
	 * ���Ǹ��෽�����޴����޸ģ�����Ϊ�ָ���ĳ��״̬
	 */
	public void restoreState(HeuristicState hState){
	    vm.restoreState(hState.getVMState());

	    // note we have to query the depth from the VM because the state is taken from the queue
	    // and we have no idea when it was entered there
	    depth = vm.getPathLength();
	    notifyStateRestored();
	}
	
	/*
	 * �жϵ�ǰ״̬�Ƿ����ƥ���֧ƥ�䣬��Ҫʹ��״̬�б����Transition���з��������trail
	 * ��ǰ��������ƥ���֧��ͬ����ƥ��ɹ�
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
