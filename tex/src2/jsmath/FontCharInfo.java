package jsmath;

import jsmath.itm.BoxSize;

/**
 * 一个字体中的字符信息.
 *
 */
public class FontCharInfo {
	/** 此字符 */
	public int char_code = 0;
	
	/** 宽度. 宽度, 高度, 深度 通过 js.EmBoxFor() 计算出来. */
	public float width = 0.0f;
	
	/** 高度 */
	public float height = 0.0f;
	
	/** 深度 */
	public float depth = 0.0f;
	
	/** 倾斜校正 */
	public float ic = 0.0f;
	
	/** 字符间距调整 */
	public KernInfo[] krn = null;
	
	/** 连字信息 */
	public LigInfo[] lig = null;
	
	/** 对应 encoding[char_code] 的值. 在 Setup.EncodeFont() 中设置. */
	public String c = null;
	
	/** Img 使用的, 我们暂时不了解其具体含义 */
	public CharImg img = null; 
	
	// 在 TeXIMG() 函数中设置, 可能是 css类或...?
	public String tclass = null;
	
	// 部分字体的字符有这些属性... 在 Box.TeX 等函数中使用.
	public Float w = null;
	public Float h = null;
	// 从 emboxfor() 计算出来的 h,d 不是该字符图形真的 h,d 值, 这里使用 a,d 分别表示真正的 h,d 值.
	/** 部分字符设置有 非null 的 a (ascend) 值, 其在函数 Box.simple_Text() 中似乎用于计算 box.h 的值. */
	public Float a = null; 
	// 部分字符设置有, 应该是指 descent.
	public Float d = null;
	public Integer n = null;
	
	// 表示 box.h, box.d; 我们在 s2.html 中预先计算出来.
	public float bh = 0;
	public float bd = 0;

	/** 如果这个字符是一个组合字符, 这个字段描述如何组合的信息. */
	public DelimChar delim = null;
	
	/**
	 * 普通构造.
	 */
	public FontCharInfo() {
		
	}
	
	/**
	 * 使用指定的字符代码 cc, 宽度 width, 高度 height 构造 FontCharInfo 新实例.
	 *   对应 js 中为 [0.625, 0.683] 这种情况.
	 * @param cc
	 * @param w
	 * @param h
	 */
	/*public FontCharInfo(int cc, float w, float h) {
		this(cc, w, h, 0, 0);
	}*/
	
	/**
	 * 使用指定的字符代码 cc, 宽度 width, 高度 height, 深度 depth 构造 FontCharInfo 新实例.
	 * @param cc
	 * @param w
	 * @param h
	 * @param d
	 */
	/* public FontCharInfo(int cc, float w, float h, float d) {
		this(cc, w, h, 0, 0);
	} */
	
	/**
	 * 使用指定的字符代码 cc, 宽度 width, 高度 height, 深度 depth, 倾斜校正 ic 构造 FontCharInfo 新实例.
	 * @param cc
	 * @param width
	 * @param height
	 * @param depth
	 * @param ic
	 */
	/*public FontCharInfo(int cc, float w, float h, float d, float ic) {
		this.char_code = cc;
		this.w = w;
		this.h = h;
		this.ic = ic;
	}*/

	// 构造函数, 当 k=null, l=null 的版本.
	public FontCharInfo(int cc, float width, float height, float depth, float ic, String c, String tclass) {
		this(cc, width, height, depth, ic, c, tclass, null, null);
	}
	
	// 调用示例: c = new FontCharInfo(cc_begin + 65, 0.7254f, 0.8866f, 0.1209f, 0f, "A", "normal", k, l); 
	/** 使用指定 */
	public FontCharInfo(int cc, float width, float height, float depth, float ic, String c, String tclass, KernInfo[] krn, LigInfo[] lig) {
		this.char_code = cc;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.bh = height; // 缺省=h
		this.bd = depth; // 缺省=d
		this.ic = ic;
		this.c = c;
		this.tclass = tclass;
		this.krn = krn;
		this.lig = lig;
	}
	
	/**
	 * 模拟出 EmBoxFor() 返回的值.
	 * @return
	 */
	public BoxSize emBoxFor() {
		// 这里高度+深度构成总的高度.
		return new BoxSize(width, height+depth);
	}

	/**
	 * 得到 this.w, 如果有的话; 否则返回 this.width.
	 * @return
	 */
	public float getCalcW() {
		if (this.w == null)
			return this.width;
		return this.w;
	}
}
