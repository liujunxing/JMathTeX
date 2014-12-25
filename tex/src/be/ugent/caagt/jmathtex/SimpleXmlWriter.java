package be.ugent.caagt.jmathtex;

/**
 * 简单的 XML 输出器. 
 * @author liujunxing
 *
 */
public class SimpleXmlWriter {
	private StringBuilder strbuf = new StringBuilder();
	
	public SimpleXmlWriter() {
		
	}
	
	public SimpleXmlWriter simpleHeader() {
		strbuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		return this;
	}
	
	/**
	 * 附加一个新行.
	 * @return
	 */
	public SimpleXmlWriter ln() {
		strbuf.append('\n');
		return this;
	}
	
	/**
	 * 附加一个空格.
	 * @return
	 */
	public SimpleXmlWriter blank() {
		strbuf.append(' ');
		return this;
	}
	
	/**
	 * 添加指定文本到 xml 输出缓冲中, 对 text 执行 XML escape 编码处理.
	 * @param text
	 * @return
	 */
	public SimpleXmlWriter append(String text) {
		strbuf.append(EscapeXml.escape(text));
		return this;
	}
	
	/**
	 * 添加文本到 xml 输出缓冲, 不对 text 执行 XML escape 编码处理.
	 * @param raw
	 * @return
	 */
	public SimpleXmlWriter appendRaw(String raw_text) {
		strbuf.append(raw_text);
		return this;
	}
	
	/**
	 * 添加一个 <elename>text</elename> 的简单 XML 节点.
	 *   如果 text 为 null, 则创建为 <elename /> 形式的.
	 * @param elename
	 * @param text
	 * @return
	 */
	public SimpleXmlWriter appendSimpleElement(String elename, String text) {
		if (text == null) {
			strbuf.append('<').append(elename).append(" />");
		} else {
			strbuf.append('<').append(elename).append('>')
				.append(EscapeXml.escape(text))
				.append("</").append(elename).append('>');
		}
		return this;
	}

	public SimpleXmlWriter attribute(String attrname, String val) {
		if (val == null) val = "";
		strbuf.append(attrname).append("=\"").append(escapeXml(val)).append("\"");
		return this;
	}
	
	public SimpleXmlWriter attribute(String attrname, boolean bval) {
		strbuf.append(attrname).append("=\"");
		if (bval) strbuf.append("true"); else strbuf.append("false");
		strbuf.append("\"");
		return this;
	}
	
	public SimpleXmlWriter attribute(String attrname, int i) {
		strbuf.append(attrname).append("=\"").append(Integer.toString(i)).append("\"");
		return this;
	}
	
	public SimpleXmlWriter attribute(String attrname, float f) {
		strbuf.append(attrname).append("=\"").append(Float.toString(f)).append("\"");
		return this;
	}
	
	/**
	 * 添加 <elename>
	 * @param elename
	 * @return
	 */
	public SimpleXmlWriter beginElement(String elename) {
		strbuf.append('<').append(elename).append('>');
		return this;
	}
	
	public SimpleXmlWriter endElement(String elename) {
		strbuf.append("</").append(elename).append('>');
		return this;
	}
	
	public static String escapeXml(String text) {
		return EscapeXml.escape(text);
	}

	public String toString() {
		return strbuf.toString();
	}
}
