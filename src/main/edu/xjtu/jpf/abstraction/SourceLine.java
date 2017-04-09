package edu.xjtu.jpf.abstraction;

/**
 * 
 * @author zehua
 * 该类表示静态代码中的一行源码
 *
 */
public class SourceLine {
	//源码所在类
	private String className;
	//源码所在方法
	private String methodName;
	//源码所在包
	private String packageName;
	//源码所在文件
	private String fileName;
	//源码行号
	private int lineNumber;
	
	public SourceLine(){
		
	}
	
	public SourceLine(String location){
		String[] fragments = location.split(":");
		packageName = fragments[0];
		fileName = fragments[1];
		lineNumber = Integer.parseInt(fragments[2]);
	}
	
	public SourceLine(String pName, String fName, int lineNum){
		packageName = pName;
		fileName = fName;
		lineNumber = lineNum;
	}
	
	public SourceLine(String cName, String mName, 
			String pName, String fName, int lineNum){
		className = cName;
		methodName = mName;
		packageName = pName;
		fileName = fName;
		lineNumber = lineNum;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String toString(){
		return className + "." + methodName + ":" + lineNumber;		
	}
	
	public String getLocationString(){
		return packageName + "/" + fileName + ":" + lineNumber;
	}
}
