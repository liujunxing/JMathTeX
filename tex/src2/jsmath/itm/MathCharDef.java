package jsmath.itm;

/**
 * 模拟 \mathchardef 定义的数学符号的三元组 [class, family, apos].
 *
 * 按照 TeX.atom[] 数组的定义, clazz(分类) 对应的 atom type 为:
 *   0 -- 'ord'
 *   1 -- 'op'
 *   2 -- 'bin'
 *   3 -- 'rel'
 *   4 -- 'open'
 *   5 -- 'close'
 *   6 -- 'punct'
 *   7 -- 'ord' (特殊的, tex 将其变成 0, 用 \fam 定义的字体代替 f. )
 *   
 * 一个 mathchardef 可以表示为 4个十六进制数字. 如 0x037A
 */
public class MathCharDef {
	/** 所属分类, 取值范围 0-7 */
	public int clazz;
	
	/** 字体族, 取值范围 0-15 */
	public int family;
	
	/** 在字体中的位置, 取值范围 0-255 */
	public char apos;
	
	public MathCharDef() {
	
	}
	
	/**
	 * 构造新实例.
	 * @param clazz
	 * @param family
	 * @param apos
	 */
	public MathCharDef(int clazz, int family, char apos) {
		this.clazz = clazz;
		this.family = family;
		this.apos = apos;
	}
}
