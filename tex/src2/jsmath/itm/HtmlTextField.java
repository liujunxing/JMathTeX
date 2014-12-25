package jsmath.itm;

/**
 * 直接在 Parse() 阶段转换为 HTML 文本的原子.
 *
 */
public class HtmlTextField extends Field {
	public String text;
	public String tclass;
	public float ascend;
	public float descend;
	
	/**
	 * 构造.
	 */
	public HtmlTextField(String text, String tclass) {
		super.type = Field.FIELD_TYPE_text;
		this.text = text;
		this.tclass = tclass;
	}
	
	public HtmlTextField(String text, String tclass, float a, float d) {
		super.type = Field.FIELD_TYPE_text;
		this.text = text;
		this.tclass = tclass;
		this.ascend = a;
		this.descend = d;
	}
}
