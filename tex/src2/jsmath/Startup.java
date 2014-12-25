package jsmath;

/**
 * 启动 jsmath 并执行操作.
 *
 */
public class Startup {
	JsMath jsMath;
	
	private void test1() {
		// Test HTML.Em() function.
		String astr = HTML.Em(3.1415926f);
		System.out.println("astr = " + astr);
		
		// Test HTML.Spacer() function.
		astr = HTML.Spacer(2.718f);
		System.out.println("astr = " + astr);
		
		// Test HTML.Blank() function.
		astr = HTML.Blank(3.142f, 1.618f, 0.386f, false);
		System.out.println("astr = " + astr);
		
		// Test HTML.Rule() function.
	}
	
	private void doMain(String tex) {
		// 构造新实例.
		jsMath = new JsMath();
		jsMath.Startup();
		
		// 执行页面处理.
		Document document = new Document(tex);
		jsMath.Process(document);
		
	}
	
	public static void main(String arg) {
		Startup s = new Startup(); 
		s.doMain(arg);
	}
}
