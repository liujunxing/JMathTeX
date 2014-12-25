package jsmath;

/**
 * 模拟 jsMath.Translate. (Translate 是什么意思呢?)
 * 
 *   程序使用了异步执行模型, 那我们该如何处理呢???
 */
public class CTranslate implements ScriptCallable {
	/** container 对象 */
	private final JsMath jsMath;
	
	/** the list of math elements on the page */
	private Object[] element;
	
	/** set to 1 to cancel asynchronous processing */
	private int cancel  = 0;
	
	public CTranslate(JsMath jsMath) {
		this.jsMath = jsMath;
	}
	
	public void scriptCall(String method, Object data) {
		//if ("Asynchronous".equals(method))
		//	Asynchronous(data);
		throw new java.lang.UnsupportedOperationException();
	}
	
	private void test() {
		/*
		DelimInfo delim = new DelimInfo(0, 0x330, 0x330);
		Box.Delimiter(3.3333f, delim, "D", true);
		*/
	}
	
	/**
	 * 解析一个 tex 字符串, 转换为 HTML, 放在 document 对象中.
	 * Start the asynchronous processing of mathematics
	 * @param data
	 */
	public void Asynchronous(Document document) {
		if (jsMath.initialized == false) jsMath.Init();
		
		test();
		
		String tex = document.tex_string;
		
		// 这里对应 Translate.Parse() 函数中的核心处理代码.
		Parser parser = new Parser(jsMath, tex, null, null, "D");
		parser.Parse();
		parser.Atomize();
		String html = parser.Typeset();
		document.html = html;
		
		// DEBUG:
		System.out.println(html);
	}
}
