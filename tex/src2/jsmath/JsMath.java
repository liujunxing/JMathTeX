package jsmath;

import java.util.LinkedList;
import java.util.List;

/**
 * 实现 jsMath.js 中 window.jsMath 类.
 *
 */
public class JsMath {
	/** 在编写此类的时候, jsMath 的版本是 3.6e */
	public static String version = "3.6e";
	
	/** 在 Translate.Asynchronous 中用于判断 jsMath 是否初始化过了的标志. */
	public boolean initialized = false;
	
	/** the document loading jsMath. (在 java 环境中没有此对象, 先暂时不用, 看实在需要则弄一个) */
	public Object document = null;
	
	/** the window of the of loading document */
	public Object window = null;
	
	/** 平台: 由调用者指定, 根据 browser 提供的信息决定. 可以取值为 "pc", "mac", "unix" 等. */
	public String platform = "pc";
	
	/** Font sizes for \tiny, \small, etc. (must match styles below) */
	public static int sizes[] = {50, 60, 70, 85, 100, 120, 144, 173, 207, 249};

	/** 宽度单位; 当前这些值是在 ie 下实测出来的, 可能在不同浏览器和环境下都有不同. */
	public static float em = 24.81481f;
	public static float h = 0.886567f;
	/**
	 * (已知)是普通文本的深度 d. 参见 h, hd
	 * (已知)在 HTML.Blank() 中使用此值. (为修复 msie 的 bug)
	 */
	public static float d = 0.120895f;
	public static float hd = 1.007423f;
	
	// The styles needed for the TeX fonts and other jsMath elements.
	// styles = { list<string, css> '.math' = { 'font-family' => 'serif' (map)
	//     css = list<StringPair>
	public static List<CssItem> styles = new LinkedList<CssItem>();
	
	// 用于初始化 styles 集合.
	static { 
		CssItem t;
		// unporcessed mathematics
		t = new CssItem(".math", 
				new StringPair("font-family", "serif"),
				new StringPair("font-style", "normal"),
				new StringPair("font-weight", "normal"));
		styles.add(t);
		
		// final typeset mathematics
		t = new CssItem(".typeset",
				new StringPair("font-family", "serif"),
				new StringPair("font-style",  "normal"),
				new StringPair("font-weight", "normal"),
				new StringPair("line-height", "normal"),
				new StringPair("text-indent", "0px"),
				new StringPair("white-space", "normal"));
		styles.add(t);

		// \hbox contents style
	    t = new CssItem(".typeset .normal",       
	    		new StringPair("font-family", "serif"),
	    		new StringPair("font-style",  "normal"),
	    		new StringPair("font-weight", "normal"));
		styles.add(t);

		// display mathematics
		t = new CssItem("div.typeset",
				new StringPair("text-align",  "center"),
				new StringPair("margin",      "1em 0px"));
		styles.add(t);
		
		// display mathematics
		t = new CssItem("div.typeset",            
				new StringPair("text-align",  "center"),
		        new StringPair("margin",      "1em 0px"));
		styles.add(t);
		
		// prevent outside CSS from setting these
	    t = new CssItem(".typeset span",          
	    		new StringPair("text-align",  "left"),
				new StringPair("border",      "0px"),
				new StringPair("margin",      "0px"),
				new StringPair("padding",     "0px"));
		styles.add(t);
		
		// links in image mode
	    t = new CssItem("a .typeset img, .typeset a img",   
	    		new StringPair("border", "0px"),
	    		new StringPair("border-bottom", "1px solid blue;"));
		styles.add(t);

		// Font sizes
		 	// tiny (\scriptscriptsize)
		styles.add(new CssItem(".typeset .size0", new StringPair("font-size", "50%"))); 
		styles.add(new CssItem(".typeset .size1", new StringPair("font-size", "60%")));  //       (50% of \large for consistency)
		styles.add(new CssItem(".typeset .size2", new StringPair("font-size", "70%")));  // scriptsize
		styles.add(new CssItem(".typeset .size3", new StringPair("font-size", "85%")));  // small (70% of \large for consistency)
		styles.add(new CssItem(".typeset .size4", new StringPair("font-size", "100%"))); // normalsize
		styles.add(new CssItem(".typeset .size5", new StringPair("font-size", "120%"))); // large
		styles.add(new CssItem(".typeset .size6", new StringPair("font-size", "144%"))); // Large
		styles.add(new CssItem(".typeset .size7", new StringPair("font-size", "173%"))); // LARGE
		styles.add(new CssItem(".typeset .size8", new StringPair("font-size", "207%"))); // huge
		styles.add(new CssItem(".typeset .size9", new StringPair("font-size", "249%"))); // Huge

		// TeX fonts
		styles.add(new CssItem(".typeset .cmr10",  new StringPair("font-family", "jsMath-cmr10, serif")));
		styles.add(new CssItem(".typeset .cmbx10", new StringPair("font-family", "jsMath-cmbx10, jsMath-cmr10")));
		styles.add(new CssItem(".typeset .cmti10", new StringPair("font-family", "jsMath-cmti10, jsMath-cmr10")));
		styles.add(new CssItem(".typeset .cmmi10", new StringPair("font-family", "jsMath-cmmi10")));
		styles.add(new CssItem(".typeset .cmsy10", new StringPair("font-family", "jsMath-cmsy10")));
		styles.add(new CssItem(".typeset .cmex10", new StringPair("font-family", "jsMath-cmex10")));
		
		
		styles.add(new CssItem(".typeset .textit", 
								new StringPair("font-family", "serif"), 
								new StringPair("font-style", "italic")));
		styles.add(new CssItem(".typeset .textbf", 
								new StringPair("font-family", "serif"),
								new StringPair("font-weight", "bold")));
	    
		styles.add(new CssItem(".typeset .link",
								new StringPair("text-decoration", "none")));  // links in mathematics

		// in-line error messages
	    t = new CssItem(".typeset .error",        
	    		new StringPair("font-size",        "90%"),
	    		new StringPair("font-style",       "italic"),
	    		new StringPair("background-color", "#FFFFCC"),
	    		new StringPair("padding",           "1px"),
	    		new StringPair("border",            "1px solid #CC0000"));
	    styles.add(t);

	    // internal use
		t = new CssItem(".typeset .blank",        
				new StringPair("display",  "inline-block"),
				new StringPair("overflow", "hidden"),
				new StringPair("border",   "0px none"),
				new StringPair("width",    "0px"),
				new StringPair("height",   "0px"));
		styles.add(t);

	    // internal use
		t = new CssItem(".typeset .spacer", 
				new StringPair("display", "inline-block"));
		styles.add(t);

	    // TODO: 还有一些,我们稍后再弄把....
	}

	/** */
	public CGlobal Global = CGlobal.instance();
	 
	/** */
	public CControls Controls = new CControls();
	
	/** */
	public CSetup Setup = new CSetup(this);
	
	/** */
	public CScript Script = new CScript();
	
	/** TeX 的字体信息 */
	public static CTeX TeX = new CTeX();
	
	/** Implement image-based fonts for fallback method */
	public static CImg Img = new CImg();
	
	/** Translate 对象 */
	public CTranslate Translate = new CTranslate(this);
	
	public static Browser Browser = new Browser();
	
	public static Typeset Typeset = new Typeset(null);
	
	public static Font Font = new Font();
	
	/** 根据 sizes[i] 相对应的 TeX 参数对象的数组. */
	public static TeXParam TeXparams[];
	
	/**
	 * 为方便, 设立的启动部分.
	 */
	public void Startup() {
		this.Global.Register();
		this.Loaded();
		this.Controls.GetCookie();
		this.Setup.Source();
		this.Global.Init();
		this.Script.Init();
		this.Setup.Fonts();
		this.Setup.Body(); // if (jsMath.document.body) 
		this.Setup.User("onload");  // 调用为 'onload' 注册的事件.
	}
	
	/**
	 * Get a jsMath DOM element
	 */
	public void Element(String name) {
		// TODO: ?? Element: function (name) {return jsMath.document.getElementById('jsMath_'+name)},
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 得到一个 HMTL 字符串的宽度/高度(以像素为单位...)
	 * Get the width and height (in pixels) of an HTML string
	 * @param s
	 */
	public void BBoxFor(Object s) {
		// 原来程序是使用一个 hidden 的 span 来得到该字符串的高度,宽度的. 
		// 那我们在服务器端该怎么办呢???
		// TODO:
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 由于我们不能模拟浏览器的 span 来计算该值, 因此我们尽量避免使用这类函数吧.
	 *   这意味着我们自己进行绝对定位计算?
	 * Get the width and height (in ems) of an HTML string.
     *   Check the cache first to see if we've already measured it.
	 * @param s
	 */
	public void EmBoxFor(Object s) {
		// TODO:
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * Initialize jsMath.  This determines the em size, and a variety
     *  of other parameters used throughout jsMath.
	 */
	public void Init() {
		if (this.Setup.inited != 1) {
			// 如果还未进行过初始化, 则进行初始化.
			if (this.Setup.inited == 0)
				this.Setup.Body();
			if (this.Setup.inited != 1) {
				// jsMath.Setup 没有正确初始化~
				throw new java.lang.RuntimeException("failed to setup properly.");
			}
		}
		
		// this.em = this.CurrentEm();
		// TODO: 后面还有一些计算 cache,bb,hh,d,ic 等, 我们暂时没有想到好办法来实现.
		
		this.Setup.TeXfonts();
		
		// float x_height = EmBoxFor("M")/2;
		// this.TeX.M_height = x_height*(26/14); ...
		// 在 js 中复制一份看起来没用? this.TeX.h = this.h; this.TeX.d = this.d; this.TeX.hd = this.hd;
		
		// 我们通过在 ie 下实测得到如下值, 这些值在别的浏览器, 甚至 ie 的其它版本, 不同字体等下可能都不同.
		// 我们暂时使用这些值:
		JsMath.em = 24.814814f;
		JsMath.h = 0.886567f; 
		JsMath.d = 0.120895f;
		JsMath.hd = 1.007462f;
		
		JsMath.Img.Scale();
		if (!this.initialized) {
			this.Setup.Sizes();
			JsMath.Img.UpdateFonts();
		}
		
		// TODO: this.p_height = ...
		
		this.initialized = true;
	}
	
	/**
	 * Get the x size and if it has changed, reinitialize the sizes
	 */
	public void ReInit() {
		// TODO:
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * Find the em size in effect at the current text location
	 */
	public float CurrentEm() {
		// 按照百度上面的说法, 一个 em 表示一种特殊字体的大写字母 M 的高度.
		// TODO: var em = this.BBoxFor('...width:27em...').w/27;
		float em = 24.814f; // 我们测试产生的一个数据, 先试验用.
		
		return em;
	}
	
	
	private boolean loaded = false;
	/**
	 *  Mark jsMath as loaded and copy any user-provided overrides
	 */
	public void Loaded() {
		// 原来代码使得定制者可以 override 一些属性/方法. 我们在 java 不用支持. 而是采用继承,对象
		// 等机制来实现定制.
		// 如 'Process', 'Macro' 等方法不让 override.
		
		// 最后设置已经加载标志.
		this.loaded = true;
	}
	
	// 我们希望少使用下面的这些方法. 因为 java 不支持 js 的那种添加属性/方法的方式...
	/**
     *  Manage JavaScript objects:
     *  
     *      Add:        add/replace items in an object (添加/替换一个对象中的内容)
     *      Insert:     add items to an object (添加项目到一个对象)
     *      Package:    add items to an object prototype (添加项目到一个对象的原型)
     */
	public void Add() {
		throw new java.lang.UnsupportedOperationException(); // 我们在 java 中不支持此功能.
	}
	
	public void Insert() {
		throw new java.lang.UnsupportedOperationException(); // 我们在 java 中不支持此功能.
	}
	
	public void Package() {
		throw new java.lang.UnsupportedOperationException(); // 我们在 java 中不支持此功能.
	}
	
	/**
	 * 在 HTML 页面的尾部调用此函数, 以异步地处理页面中的数学排版.
	 * Call this at the bottom of your HTML page to have the
     *  mathematics typeset asynchronously.  This lets the user
     *  start reading the mathematics while the rest of the page
     *  is being processed.
	 * @param document
	 */
	public void Process(Document document) {
		this.Setup.Body(); // 重复调用没有问题吧...?
		//this.Script.Push(this.Translate, "Asynchronous", document);
		this.Translate.Asynchronous(document);
	}
	
	/**
	 * 解析指定的 s 字符串, 
	 * @param s -- 要解析的 tex 字符串.
	 * @param font -- 缺省字体(索引)
	 * @param size -- 字大小(一般为 10 pt)
	 * @param style -- 显示形式(D or T)
	 * @return 返回 parser 对象, 完成解析的结果在该 parser 对象中.
	 */
	public Parser Parse(String s, Integer font, Integer size, String style) {
		Parser parser = new Parser(this, s, font, size, style);
		parser.Parse();
		return parser;
	}
}









