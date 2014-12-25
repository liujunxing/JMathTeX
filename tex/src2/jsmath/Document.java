package jsmath;

/**
 * 模拟 browser 的 document, 以简化 jsmath -> javamath 的转化测试工作.
 *
 */
public class Document {
	/** 要解析的 tex 字符串 */
	public String tex_string;
	
	/** 产生的 html 结果 */
	public String html;
	
	public Document(String tex) {
		this.tex_string = tex;
	}
}
