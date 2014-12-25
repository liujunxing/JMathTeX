package jsmath.itm;

/**
 * 表示一个 TEX 字符的字段.
 *
 */
public class TeXCharField extends Field {
	/** 字体族编号, 范围在 0<=f<16. */
	public final String font;
	
	/** 位置, 范围在 0<=a<256. (原来用 final 修饰的, 结果外面还能改它...) */
	public char c;
	
	/**
	 * 构造一个 CharAtom 的新实例.
	 */
	public TeXCharField(String font, char c) {
		super.type = Field.FIELD_TYPE_TeX;
		this.font = font;
		this.c = c;
	}
}
