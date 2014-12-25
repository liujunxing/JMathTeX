package jsmath;

import jsmath.itm.MathCharDef;

/**
 * 封装 delimiter 的信息.
 * 每个 \delimiter<number> 中的数字有 7 个十六进制数字. 
 * 例如: \langle{\delimiter"426830A}. 则其中 4=class open, 表示是开符号.
 *      small 变体部分为 268; large 变体部分为 30A.
 * 在 jsMath 中使用 '\\langle':[4,2,0x68,3,0x0A] 来表示, 推断为 4=类别(clazz), 2,0x68 为small 变体部分
 *   3,0x0A 为 large 变体部分.
 * 
 * @author liujunxing
 *
 */
public class DelimInfo {
	/** 此定界符的类别. 取值为 0-7. */
	public final int clazz;
	
	/** 此定界符的 small 变体部分. 取值为 3 个十六进制数字, 如 0x268. */
	public final int small;
	
	/** 此定界符的 large 变体部分. 取值为 3 个十六进制数字, 如 0x30A. */
	public final int large;
	
	/** 方便调试用, 看此字符代表什么. */
	public String dbg_str;
	
	/**
	 * 使用指定参数构造 DelimInfo 的新实例.
	 * @param clazz
	 */
	public DelimInfo(int clazz, int small, int large) {
		this.clazz = clazz;
		this.small = small;
		this.large = large;
	}
	
	/** 得到定界符的类别. */
	public int getClazz() {
		return this.clazz;
	}
	
	public int getSmall() {
		return this.small;
	}
	
	public int getLarge() {
		return this.large;
	}

	/**
	 * 为兼容 js中访问方式. 
	 * 在js中定义的 delim 为 [0,2,0x68,3,0x0A], 则访问 delim[0]=0, delim[1]=2, 
	 *   delim[2]=0x68, 依次类推.
	 * @param j
	 * @return
	 */
	public int indx(int j) {
		if (j == 0) 
			return this.clazz;
		else if (j == 1)
			return this.small >> 8;
		else if (j == 2)
			return this.small & 0xFF;
		else if (j == 3)
			return this.large >> 8;
		else if (j == 4)
			return this.large & 0xFF;
		else
			throw new IndexOutOfBoundsException();
	}
	
	/**
	 * 取 clazz+small 部分构成一个 MathCharDef.
	 * @return
	 */
	public MathCharDef toMathChar() {
		return new MathCharDef(clazz, small / 256, (char)(small % 256));
	}
}
