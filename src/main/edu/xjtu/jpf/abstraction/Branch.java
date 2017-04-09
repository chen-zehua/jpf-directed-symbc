package edu.xjtu.jpf.abstraction;

/**
 * 
 * @author zehua
 * 该类表是一个分支，包含一条分支语句，和一条跳转的目标语句
 *
 */
public class Branch {
	
	private SourceLine branchLine;
	
	private SourceLine target;
	
	public Branch(){
		
	}
	
	public Branch(String b){
		String[] fragments = b.split("@");
		branchLine = new SourceLine(fragments[0]);
		target = new SourceLine(fragments[1]);
	}
	
	public Branch(SourceLine b, SourceLine t){
		branchLine = b;
		target = t;
	}
	
	public void setBranchLine(SourceLine branchLine) {
		this.branchLine = branchLine;
	}
	
	public SourceLine getBranchLine() {
		return branchLine;
	}
	
	public void setTarget(SourceLine target) {
		this.target = target;
	}
	
	public SourceLine getTarget() {
		return target;
	}
}
