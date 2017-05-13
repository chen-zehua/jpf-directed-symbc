package demo;

public class SimpleExample {
	public static void main(String[] args) {
		int result = func(2);
		System.out.println("[Result] : " + result);
	}
	
	public static int func(int x ){
		int a = -1;
		if (x > 3) {
			a = 0;
		}
		if (x >= 5) {
			a = 1;
		}
		return a;
	}
}
