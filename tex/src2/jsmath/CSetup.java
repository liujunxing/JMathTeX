package jsmath;

import java.util.HashMap;
import java.util.Map;

/**
 * Miscellaneous setup and initialization.
 *
 */
public class CSetup {
	/** 包含此对象的全局 jsmath 对象. */
	private final JsMath jsMath;
	
	// array of files already loaded
	private int loaded;
	
	/** 在 Body() 函数中设置为 1 */
	public int inited = -1;
	
	public CSetup(JsMath jsMath) {
		this.jsMath = jsMath;
	}
	
	/**
	 * Insert a DIV at the top of the page with given ID,
     *  attributes, and style settings.
     *  
     * 现在我们没有浏览器了, 所以插入 DIV 的函数也就不需要存在了?
     * 
	 * @param id
	 * @param styles
	 * @param parent
	 */
	public void DIV(Object id, Object styles, Object parent) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Find the root URL for the jsMath files (so we can load
     *  the other .js and .gif files)
	 */
	public void Source() {
		// TODO: 计算 Img.root, blank 等属性. 如 jsMath.root + 'fonts/';
		// 这个 img 属性估计是用于存放字体对应图片的目录. 我们可以根据 server 配置上.
	}

	/**
	 * 初始化所有字体(font) 的 encoding 字段.
	 * Initialize the encodings for all fonts.
	 */
	public void Fonts() {
		String[] fam = JsMath.TeX.getFam(); // 字体名的数组.
		for (int i = 0; i < fam.length; ++i) {
			String name = fam[i];
			if (name != null && name.length() > 0) {
				this.EncodeFont(name);
			}
		}
	}
	
	/**
	 * Initialize a font's encoding array
	 * @param name - 字体名, 取值为 cmr10, cmmi10 等名字. 参见 FamNameMap.
	 */
	private void EncodeFont(String name) {
		// var font = jsMath.TeX[name];
		FontInfo font = JsMath.TeX.getFontByName(name);
		if (font == null) return; // 可能暂时没有给出该字体...
		
		if (font.get(0).c != null) return;
		for (int k = 0; k < 128; ++k) {
			FontCharInfo data = font.get(k); // 原语句: var data=font[k]; 
			// 语义 font[k] = data[3]; 表示将 font[k] 变更为一个 object, 里面一般有 {ic, krn, lig}
			// 语义: font[k].w = data[0]; 表示 font[k] 对应的 object 设置属性 w(宽度)
			// 同样设置 object 的 h(高度), d(深度), c 属性.
			data.c = CTeX.encoding[k]; // 我们在这里只设置 encoding 就好.
		}
	}
	
	/**
	 * Do the initialization that requires the <body> to be in place.
	 */
	public void Body() {
		// TODO: 原来这里有不少初始化代码, 我们暂时不知道其语义, 先不做这里.
		this.inited = -1;
		
		// TODO: JsMath.Setup.Hidden(); this.inited = -2;
		// TODO: 更多... JsMath.Browser.Init(); this.inited = -3;
		
		Font.Check(); // 原来是 Script.Push() 到一个序列中的, 导致顺序很难理解... 我们先放这里直接调用?
		
		// 但设置 inited 标志.
		this.inited = 1;
	}
	
	/**
	 * 页面作者(程序员)能重载这个函数, 使得在 jsMath 初始化的不同阶段执行不同的操作... (框架结构?)
	 * Web page author can override the entries to the UserEvent hash 
     *  functions that will be run at various times during jsMath's setup
     *  process.
	 * @param when
	 */
	public void User(String when) {
		// TODO: 事件注册和调用机制...  jsMath.Setup.UserEvent[]
	}
	
	/**
	 * Init all the TeX fonts.
	 */
	public void TeXfonts() {
		String[] fam = JsMath.TeX.getFam();
		for (int i = 0; i < fam.length; ++i) {
			if (fam[i] != null)
				this.TeXfont(fam[i]);
		}
	}
	
	// 调用入 TeXfont() 函数的可能的 fontName 列表如下:
	// "cmr10", "cmmi10", "cmsy10", "cmex10", "cmti10", "", "cmbx10", ""
	
	private static class tfinfo {
		public float w;
		public float h;
		public float w_strut;
		public float h_strut;
	}
	// 数组 float[] 分别对应 w, h, 加上 strut w, strut h
	private static Map<String, tfinfo> tfembfor = _init_embfor();
	
	// 我们手工为 ie9 下 EmBoxFor() 这些字体计算出返回值, 作为暂时的实现.
	private static Map<String, tfinfo> _init_embfor() {
		Map<String, tfinfo> m = new HashMap<String, tfinfo>();
		// cmr10:  w=0.725373, h=1.007463, STRUT: w=0.765672, h=1.128358
		// cmmi10: w=0.725373, h=1.007463, 
		// cmsy10: w=0.725373, h=1.007463,
		// cmex10: w=0.725373, h=1.007463,
		// cmti10: w=0.725373, h=1.007463,
		// 这些数值一致, 是因为前端没装字体, 或映射到相同的字体吗??
		// cmbx10: w=0.725373, h=1.007463, 由于前面的值一样, 后面 STRUT 的值也不会不同.

		tfinfo b = new tfinfo(); 
		b.w = 0.725373f; b.h = 1.007463f; b.w_strut = 0.765672f; b.h_strut = 1.128358f;
		
		m.put("cmr10", b);
		m.put("cmmi10", b); // 现在值都一样, 以后不一样?? 怎样计算出来?
		m.put("cmsy10", b);
		m.put("cmex10", b);
		m.put("cmti10", b);
		m.put("cmbx10", b);
		
		return m;
	}
	
	/**
	 * 为指定的 TeX 字体计算出缺省高度和深度, 并设置其 skewchar.
	 * Look up the default height and depth for a TeX font
     *  and set the skewchar
	 * @param fontName - 字体名, 如 'cmr10'
	 */
	private void TeXfont(String fontName) {
		FontInfo font = JsMath.TeX.getFontByName(fontName);
		if (font == null) return;
		
		// 计算机理: 先计算字符 A 的高度, 然后用 A 加上一个同高的 img(但是以 baseline 进行对齐),
		//   于是 A<IMG> 的总高度就 = A的总高度 + 深度.
		// 
		
		// 由于我们无法使用 EmBoxFor() 系列函数, 这里采用的临时方法是预先计算出来, 放在 tfembfor 中.
		tfinfo b = tfembfor.get(fontName);
		if (b == null) throw new InternalError(); // 没有该字体的信息? 这一定是一个内部不匹配错误.

		// font.hd = jsMath.EmBoxFor("<span class=name>font[65].c</span>").h; ?
		font.hd = b.h; // 一个字符的总高度.
		font.d = b.h_strut - b.h;  // 字符的深度 (descent)
		font.h = font.hd - font.d; // 字符基线以上高度 (ascent)
		
		
		// font.skewchar = 0177; for cmmi10
		if ("cmmi10".equals(fontName))
			font.skewchar = 0177;
		else if ("cmsy10".equals(fontName))
			font.skewchar = 060;
		// 我们预先算出??
	}
	
	/**
	 * 为不同的字体尺寸计算字体参数. (我们先延续原 js 的方式.)
	 * Compute font parameters for various sizes
	 */
	public void Sizes() {
		JsMath.TeXparams = new TeXParam[JsMath.sizes.length];
		int j;
		for (j = 0; j < JsMath.sizes.length; ++j) {
			int factor = JsMath.sizes[j];
			JsMath.TeXparams[j] = new TeXParam(factor);  // 按照比例 factor 构造 TeX 参数对象.
		}
	}
	
	// 我们也许根本不需要.
	// 事件注册模式: HashMap<String eventName, EventHandler handler? or Chain> UserEvent;
}


