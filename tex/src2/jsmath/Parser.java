package jsmath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsmath.itm.*;

/** 
 * 表示类 jsMath.Parser
 * 
 * 使用:
 *   var parse = new jsMath.Parser(s, font, size, style);
 *   parse.Parse();
 *   return parse;
 *   
 * 我们的需求:
 *   1. 在任何创建 mItem, atom 的地方都标记上, 以方便地找到所创建的所有类型的 mItem.
 *     搜索 TAG:MITEM 希望能找到所有 mItem 的创建.
 *   2. 全面的理解 mItem 的所有 itemType. (从 Parse() 函数入手?)
 */
public class Parser {
	private final JsMath jsMath;
	
	// 在 tex 中使用的特殊字符.
	/** 转义符 */
	public static final char CH_CMD = '\\';
	/** 组开始 */
	public static final char CH_OPEN = '{';
	/** 组结束 */
	public static final char CH_CLOSE = '}';
	
	/** 用于封装 matrix 系列命令的参数:
	 * 所有命令及其参数列表如下:
	 *                 a[0]   a[1]     a[2]      a[3]        a[4]    a[5]
	 *   命令    参数   左定界符 右定界符  ?列对齐    ?rspacing    ?       style
	 *  matrix      无
	 *  array       无
	 *  pmatrix        (       )       [c]
	 *  cases          \{      .       [l,l]      null        2
	 *  eqalign        无      无       [r,l]     [5/18]       3       D
	 *  displaylines   无      无       [c]       null         3       D
	 *  eqalignno      无      无       [r,l,r]   [5/8,3]      3       D
	 *  leqalignno     无      无       [r,l,r]   [5/8,3]      3       D
	 */
	private static class MatrixCSParam {
		public String left_delim = null;   // [0] 左定界符
		public String right_delim = null;  // [1] 右定界符
		public String[] align = null;      // [2] 对齐方式?
		public float[] cspacing = null;    // [3] r-spacing?
		public Float vspace = null;          // [4] ??
		public String style = null;        // [5] 显示样式.
		public MatrixCSParam() { } /* 完全都是缺省值 */
		public MatrixCSParam(String left, String right, String[] align) {
			this(left, right, null, null, null);
		}
		public MatrixCSParam(String left, String right, String[] align, float[] rspacing, Float vspace) {
			this(left, right, null, null, null, null);
		}
		public MatrixCSParam(String left, String right, String[] align, float[] rspacing, Float vspace, String style) {
			this.left_delim = left; this.right_delim = right; this.align = align;
			this.cspacing = rspacing; this.vspace = vspace; this.style = style;
		}
		// 得到 delim[2] (matrix_cs_param[2]) 也即 String[] align 参数. 如果 align==null, 则返回 defval.
		public String[] get_2(String[] defval) {
			if (this.align == null) return defval;
			return this.align;
		}
		public float[] get_3(float[] defval) {
			if (this.cspacing == null) return defval;
			return this.cspacing;
		}
		public Object get_4(Object defval) {
			if (this.vspace == null) return defval;
			return this.vspace;
		}
		public String get_5(String defval) {
			if (this.style == null) return defval;
			return this.style;
		}
	}
	
	/**
	 * 一个 mathchar 以三元组 [class, family, a_pos] 来定义, 我们简写其为 cfa. 
	 * mathchar 在 js 中定义为: mathchar: { '!': [5, 0, 0x21] ... }
	 * 所以我们(暂时)在 java 中以 map<char, MathCharDef> 来表示.
	 */
	public static final Map<Character, MathCharDef> mathchar = _init_mathchar();
	private static final Map<Character, MathCharDef> _init_mathchar() {
		Map<Character, MathCharDef> m = new HashMap<Character, MathCharDef>();
		
		//   char                  c  f  a (class, family, apos) 
		m.put('!', new MathCharDef(5, 0, (char)0x21));
	    m.put('(', new MathCharDef(4, 0, (char)0x28));
	    m.put(')', new MathCharDef(5, 0, (char)0x29));
	    m.put('*', new MathCharDef(2, 2, (char)0x03)); // \ast
	    m.put('+', new MathCharDef(2, 0, (char)0x2B));
	    m.put(',', new MathCharDef(6, 1, (char)0x3B));
	    m.put('-', new MathCharDef(2, 2, (char)0x00));
	    m.put('.', new MathCharDef(0, 1, (char)0x3A));
	    m.put('/', new MathCharDef(0, 1, (char)0x3D));
	    m.put(':', new MathCharDef(3, 0, (char)0x3A));
	    m.put(';', new MathCharDef(6, 0, (char)0x3B));
	    m.put('<', new MathCharDef(3, 1, (char)0x3C));
	    m.put('=', new MathCharDef(3, 0, (char)0x3D));
	    m.put('>', new MathCharDef(3, 1, (char)0x3E));
	    m.put('?', new MathCharDef(5, 0, (char)0x3F));
	    m.put('[', new MathCharDef(4, 0, (char)0x5B));
	    m.put(']', new MathCharDef(5, 0, (char)0x5D));
	//  '{': [4,2,0x66],
	//  '}': [5,2,0x67],
	    m.put('|', new MathCharDef(0, 2, (char)0x6A));
	          
		return m;
	}
	
	/**
	 * 宏 macros 映射的宏的基类. 需要能执行指定命令名的宏.
	 */
	public static abstract class MacroFunction {
		/**
		 * 执行此 macro 的逻辑. 这里设计的此 MacroFunction 的实例是不保留 Parser 对象引用及其
		 *   每次执行的状态, 为保存执行状态, 派生实现可以自己构造新的对象以传递该状态. 
		 *   (也可以请求 Parser 保留状态)
		 * @param parser
		 * @param cmd_name
		 */
		public abstract void doMacro(Parser parser, String cmd_name);
	}
	
	/**
	 * 宏定义表. plain tex 的基本宏. 值 Object[] 的第一项是函数的名字, 后面的项是该函数的参数.
	 * 
	 * 在使用普通函数, 还是实现某个接口的子类上, 我们还是选择用子类. 虽然麻烦一些, 但能够让程序
	 *   更清晰, 容易调试修改... (一个艰难的决定?)
	 */
	private static final Map<String, MacroFunction> macros = _init_macros();
	private static final Map<String, MacroFunction> _init_macros() {
		Map<String, MacroFunction> m = new HashMap<String, MacroFunction>();
		
		// 函数 HandleStyle() -- 设置当前 mlist 的显示样式, 以及添加一个 ChangeStyle 的 mItem.
		m.put("displaystyle", new StyleCSHandler("D"));
		m.put("textstyle", new StyleCSHandler("T"));
		m.put("scriptstyle", new StyleCSHandler("S"));
		m.put("scriptscriptstyle", new StyleCSHandler("SS"));
		
		// 函数 HandleFont() -- 设置当前 mlist 的字体(索引).
		m.put("rm", new FontCSHandler(0));
		m.put("mit", new FontCSHandler(1));
		m.put("oldstyle", new FontCSHandler(1));
		m.put("cal", new FontCSHandler(2));
		m.put("it", new FontCSHandler(4));
		m.put("bf", new FontCSHandler(6));
		
		// 函数 Extension() -- jsMath 中使用, 我们暂不实现.
		m.put("font", MacroNotImpl.instance);
		
		// 实现 \left 命令, 得到 delimiter, 然后调用 mlist.Open() 开始开符号.
		m.put("left", new LeftCSHandler()); // 测试用例: \left( 1 \over 1-x^2 \right)
		// 实现 \right 命令, 得到对应的匹配的 \left, 调用 mlist.Close() 结束闭符号.
		m.put("right", new RightCSHandler());
		
		// 函数 NamedOp(), TODO: 更多 NamedOp 项目.
		///m.put("arcsin", new NamedOp(false));
		///m.put("arccos", new NamedOp(false));
		// ....
		
		// 函数 HandleAtom(): 得到参数构造为类型为 vcenter/overline 等的原子.
		// 例子: \overline{x+y} 表示在表达式 x+y 上面添加一条线.
		m.put("vcenter", new AtomCSHandler());
		m.put("overline", new AtomCSHandler());
		m.put("underline", new AtomCSHandler());
		
		// 函数 HandleOver(), 处理一般分数命令. 分数中记录/更新 data.overI, .overF 字段.
		m.put("over", new OverCSHandler()); // \over 等价于 \overwithdelims.. (没有 delim 的 \overwithdelims 命令)
		m.put("overwithdelims", new OverCSHandler()); // 语法: \overwithdelims<delim1><delim2>
		m.put("atop", new OverCSHandler()); // x \atop y
		m.put("atopwithdelims", new OverCSHandler()); // \atopwithdelims<delim1><delim2>
		m.put("above", new OverCSHandler()); // x \above y
		m.put("abovewithdelims", new OverCSHandler()); // \abovewithdelims<delim1><delim2><dimen>
			// 关于 \brace, \brack, \choose 命令, 可参见如下网页:
			// http://www.combinatorics.net/weblib/commands/command.html
		m.put("brace", new OverCSHandler("\\{", "\\}")); 	// \{, \} 作为定界符.
		m.put("brack", new OverCSHandler("[", "]")); 		// [, ] 作为定界符
		m.put("choose", new OverCSHandler("(", ")")); 		// (, ) 作为定界符
		
		// TODO: Extension() 以后研究. 
		
		// TODO: 函数 HandleLap(), \llap (left overlap), \rlap (right overlap) 
		/*m.put("llap", new LapCSHandler());
		m.put("rlap", new LapCSHandler());
		m.put("ulap", new LapCSHandler());
		m.put("dlap", new LapCSHandler()); */
		
		// 函数 RaiseLower() 处理 \raise, \lower 命令. 例子: \raise<dimen><box>, \lower<dimen><box>
		m.put("raise", new RaiseLowerCSHandler());
		m.put("lower", new RaiseLowerCSHandler());
		
		// 函数 MoveLeftRight() 处理 \moveleft, \moveright 命令.
		m.put("moveleft", new MoveLeftRightCSHandler());
		m.put("moveright", new MoveLeftRightCSHandler());
		
		// 函数 Frac() 处理 \frac 命令.
		m.put("frac", new FracCSHandler());
		// 函数 Root() 处理 \root 命令.
		m.put("root", new RootCSHandler());
		// 函数 Sqrt() 处理 \sqrt 命令.
		m.put("sqrt", new SqrtCSHandler());
		
		// TeX substitution macros
		m.put("hbar", new SubstCSHandler("\\hbarchar\\kern-.5em h"));
		m.put("ne",   new SubstCSHandler("\\not="));
		m.put("mathrm", new SubstCSHandler("{\\rm #1}", 1));
		  // TODO: 更多...
		
		
		
		// 函数 Spacer()
		m.put(",", new SpacerCSHandler(1f/6f));
		m.put(":", new SpacerCSHandler(1f/6f));  // for LaTeX
		m.put(">", new SpacerCSHandler(2f/9f));
		m.put(";", new SpacerCSHandler(5f/18f));
		m.put("!", new SpacerCSHandler(-1f/6f));
		m.put("enspace", new SpacerCSHandler(1f/2f));
		m.put("quad", new SpacerCSHandler(1f));
		m.put("qquad", new SpacerCSHandler(2f));
		m.put("thinspace", new SpacerCSHandler(1f/6f));
		m.put("negthinspace", new SpacerCSHandler(-1f/6f));
		
		// 函数 HandleSize(): 
		m.put("tiny", new SizeCSHandler(0));
		m.put("Tiny", new SizeCSHandler(1));	// non-standard
		m.put("scriptsize", new SizeCSHandler(2));
		m.put("small", new SizeCSHandler(3));
		m.put("normalsmall", new SizeCSHandler(4));
		m.put("large", new SizeCSHandler(5));
		m.put("Large", new SizeCSHandler(6));
		m.put("LARGE", new SizeCSHandler(7));
		m.put("huge",  new SizeCSHandler(8));
		m.put("Huge",  new SizeCSHandler(9));
		
		// 加重音. (7,0,0x13) 是 class,font,char 的三元组吗?
		m.put("acute",    new MathAccentCSHandler(7, 0, 0x13));
		m.put("grave",    new MathAccentCSHandler(7, 0, 0x12));
		m.put("ddot",     new MathAccentCSHandler(7, 0, 0x7F));
		m.put("tilde",    new MathAccentCSHandler(7, 0, 0x7E));
		m.put("bar",      new MathAccentCSHandler(7, 0, 0x16));
		m.put("breve",    new MathAccentCSHandler(7, 0, 0x15));
		m.put("check",    new MathAccentCSHandler(7, 0, 0x14));
		m.put("hat",      new MathAccentCSHandler(7, 0, 0x5E));
		m.put("vec",      new MathAccentCSHandler(0, 1, 0x7E));
		m.put("dot",      new MathAccentCSHandler(7, 0, 0x5F));
		m.put("widetilde",new MathAccentCSHandler(0, 3, 0x65));
		m.put("widehat",  new MathAccentCSHandler(0, 3, 0x62));
		
		// 矩阵相关的处理. (包括 matrix, array, pmatrix, cases, eqalign, displaylines, cr, ...)
		m.put("matrix",   new MatrixCSHandler());
		m.put("array",    new MatrixCSHandler());
		   //  pmatrix: ['Matrix','(',')','c']
		m.put("pmatrix",  new MatrixCSHandler(new MatrixCSParam("(", ")", new String[]{"c"})));
			// cases: ['Matrix','\\{','.',['l','l'],null,2],
		m.put("cases",    new MatrixCSHandler(new MatrixCSParam("\\{", ".", new String[]{"l", "l"}, null, 2f)));
		m.put("cr",       new RowCSHandler());
		m.put("\\",       new RowCSHandler());
		m.put("newline",  new RowCSHandler());
		
		
		return m;
	}
	
	
	/**
	 * 定界符映射表. 根据 delim 字符串查找到 DelimInfo. 
	 * (参见 TeXbook 附录B)
	 */
	private static final Map<String, DelimInfo> delimiter = _init_delims();
	private static final Map<String, DelimInfo> _init_delims() {
		Map<String, DelimInfo> m = new HashMap<String, DelimInfo>();
		
		// <delim> 用来定义 f 族中的 `小字符' a, 和 g 族中的 `大字符' b;   small=f.a, large=g.b
		//   name             clazz small  large
		m.put("(", new DelimInfo(0, 0x028, 0x300)); // 这些在 plain.tex 中以 \delcode 进行的定义.
		m.put(")", new DelimInfo(0, 0x029, 0x301));
		m.put("[", new DelimInfo(0, 0x05B, 0x302));
		m.put("]", new DelimInfo(0, 0x05D, 0x303));
		m.put("<", new DelimInfo(0, 0x268, 0x30A));
		m.put(">", new DelimInfo(0, 0x269, 0x30B));
		m.put("\\lt", new DelimInfo(0, 0x268, 0x30A));  // 因为HTML 中<> 字符被占用. extra since < and > are
		m.put("\\gt", new DelimInfo(0, 0x269, 0x30B));  //  hard to get in HTML
		m.put("/", new DelimInfo(0, 0x02F, 0x30E));
		m.put("|", new DelimInfo(0, 0x26A, 0x30C));
		m.put(".", new DelimInfo(0, 0x000, 0x000));     // 这里 `.' 表示 null-delimiter.(空白)
		m.put("\\", new DelimInfo(0, 0x26E, 0x30F));
		
		// 下面是 plain.tex 以类似于 \def\lmoustache{\delimiter"1234567} 的形式定义的...
		m.put("\\lmoustache", new DelimInfo(4, 0x37A, 0x340)); // top from (, bottom from )
		m.put("\\rmoustache", new DelimInfo(5, 0x37B, 0x341)); // top from ), bottom from (
		m.put("\\lgroup", new DelimInfo(4, 0x628, 0x33A));  // extensible ( with sharper tips
		m.put("\\rgroup", new DelimInfo(5, 0x629, 0x33B));  // extensible ) with sharper tips
		m.put("\\arrowvert", new DelimInfo(0, 0x26A, 0x33C));  // arrow without arrowheads
		m.put("\\Arrowvert", new DelimInfo(0, 0x26B, 0x33D));  // double arrow without arrowheads
	//  '\\bracevert':      [0,7,0x7C,3,0x3E],  // the vertical bar that extends braces
	    m.put("\\bracevert", new DelimInfo(0, 0x26A, 0x33E));  // we don't load tt, so use | instead
	    m.put("\\Vert", new DelimInfo(0, 0x26B, 0x30D));
	    m.put("\\|", new DelimInfo(0, 0x26B, 0x30D));
	    m.put("\\vert", new DelimInfo(0, 0x26A, 0x30C));
	    m.put("\\uparrow", new DelimInfo(3, 0x222, 0x378));
	    m.put("\\downarrow", new DelimInfo(3, 0x223, 0x379));
	    m.put("\\updownarrow", new DelimInfo(3, 0x26C, 0x33F));
	    m.put("\\Uparrow", new DelimInfo(3, 0x22A, 0x37E));
	    m.put("\\Downarrow", new DelimInfo(3, 0x22B, 0x37F));
	    m.put("\\Updownarrow", new DelimInfo(3, 0x26D, 0x377));
	    m.put("\\backslash", new DelimInfo(0, 0x26E, 0x30F));  // for double coset G\backslash H
	    m.put("\\rangle", new DelimInfo(5, 0x269, 0x30B));
	    m.put("\\langle", new DelimInfo(4, 0x268, 0x30A));
	    m.put("\\rbrace", new DelimInfo(5, 0x267, 0x309));
	    m.put("\\lbrace", new DelimInfo(4, 0x266, 0x308)); // plain.tex 中定义为 \lbrace=\delimiter"4266308
	    m.put("\\}", new DelimInfo(5, 0x267, 0x309));
	    m.put("\\{", new DelimInfo(4, 0x266, 0x308));
	    m.put("\\rceil", new DelimInfo(5, 0x265, 0x307));
	    m.put("\\lceil", new DelimInfo(4, 0x264, 0x306));
	    m.put("\\rfloor", new DelimInfo(5, 0x263, 0x305));
	    m.put("\\lfloor", new DelimInfo(4, 0x262, 0x304));
	    m.put("\\lbrack", new DelimInfo(0, 0x05B, 0x302));
	    m.put("\\rbrack", new DelimInfo(0, 0x05D, 0x303));
		
		return m;
	}
	
	/**
	 * 表示 \mathchardef 表 (参见 texbook 附录B)
	 */
	private static final Map<String, MathCharDef> mathchardef = _init_mathchardef();
	private static final Map<String, MathCharDef> _init_mathchardef() {
		Map<String, MathCharDef> m = new HashMap<String, MathCharDef>();
		
		//    \cs                        c  f  a
		// brace parts 括号部分.
		m.put("braceld", new MathCharDef(0, 3, (char)0x7A));
		m.put("bracerd", new MathCharDef(0, 3, (char)0x7B));
		m.put("bracelu", new MathCharDef(0, 3, (char)0x7C));
	    m.put("braceru", new MathCharDef(0, 3, (char)0x7D));
	    
		// Greek letters 希腊字母. αβγδεζηθικλμνξοπρστυφχψω
		m.put("alpha", 		new MathCharDef(0, 1, (char)0x0B)); // α
	    m.put("beta",       new MathCharDef(0, 1, (char)0x0C));
	    m.put("gamma",      new MathCharDef(0, 1, (char)0x0D));
	    m.put("delta",      new MathCharDef(0, 1, (char)0x0E));
	    m.put("epsilon",    new MathCharDef(0, 1, (char)0x0F)); // ε
	    m.put("zeta",       new MathCharDef(0, 1, (char)0x10)); // ζ
	  
	    m.put("eta",          new MathCharDef(0, 1, (char)0x11));
	    m.put("theta",        new MathCharDef(0, 1, (char)0x12));
	    m.put("iota",         new MathCharDef(0, 1, (char)0x13));
	    m.put("kappa",        new MathCharDef(0, 1, (char)0x14));
	    m.put("lambda",       new MathCharDef(0, 1, (char)0x15));
	    m.put("mu",           new MathCharDef(0, 1, (char)0x16));
	    m.put("nu",           new MathCharDef(0, 1, (char)0x17));
	    m.put("xi",           new MathCharDef(0, 1, (char)0x18));
	    m.put("pi",           new MathCharDef(0, 1, (char)0x19));
	    m.put("rho",          new MathCharDef(0, 1, (char)0x1A));
	    m.put("sigma",        new MathCharDef(0, 1, (char)0x1B));
	    m.put("tau",          new MathCharDef(0, 1, (char)0x1C));
	    m.put("upsilon",      new MathCharDef(0, 1, (char)0x1D));
	    m.put("phi",          new MathCharDef(0, 1, (char)0x1E));
	    m.put("chi",          new MathCharDef(0, 1, (char)0x1F));
	    m.put("psi",          new MathCharDef(0, 1, (char)0x20));
	    m.put("omega",        new MathCharDef(0, 1, (char)0x21)); // ω
	    m.put("varepsilon",   new MathCharDef(0, 1, (char)0x22));
	    m.put("vartheta",     new MathCharDef(0, 1, (char)0x23));
	    m.put("varpi",        new MathCharDef(0, 1, (char)0x24));
	    m.put("varrho",       new MathCharDef(0, 1, (char)0x25));
	    m.put("varsigma",     new MathCharDef(0, 1, (char)0x26));
	    m.put("varphi",       new MathCharDef(0, 1, (char)0x27));
	    
	    	// 大写希腊字母.
	    m.put("Gamma",        new MathCharDef(7, 0, (char)0x00));
	    m.put("Delta",        new MathCharDef(7, 0, (char)0x01));
	    m.put("Theta",        new MathCharDef(7, 0, (char)0x02));
	    m.put("Lambda",       new MathCharDef(7, 0, (char)0x03));
	    m.put("Xi",           new MathCharDef(7, 0, (char)0x04));
	    m.put("Pi",           new MathCharDef(7, 0, (char)0x05));
	    m.put("Sigma",        new MathCharDef(7, 0, (char)0x06));
	    m.put("Upsilon",      new MathCharDef(7, 0, (char)0x07));
	    m.put("Phi",          new MathCharDef(7, 0, (char)0x08));
	    m.put("Psi",          new MathCharDef(7, 0, (char)0x09));
	    m.put("Omega",        new MathCharDef(7, 0, (char)0x0A)); // Ω
	   	
	    // Ord symbols                         c=0
	    m.put("aleph",        new MathCharDef(0, 2, (char)0x40));
	    m.put("imath",        new MathCharDef(0, 1, (char)0x7B));
	    m.put("jmath",        new MathCharDef(0, 1, (char)0x7C));
	    m.put("ell",          new MathCharDef(0, 1, (char)0x60));
	    m.put("wp",           new MathCharDef(0, 1, (char)0x7D));
	    m.put("Re",           new MathCharDef(0, 2, (char)0x3C));
	    m.put("Im",           new MathCharDef(0, 2, (char)0x3D));
	    m.put("partial",      new MathCharDef(0, 1, (char)0x40));
	    m.put("infty",        new MathCharDef(0, 2, (char)0x31));
	    m.put("prime",        new MathCharDef(0, 2, (char)0x30));
	    m.put("emptyset",     new MathCharDef(0, 2, (char)0x3B));
	    m.put("nabla",        new MathCharDef(0, 2, (char)0x72));
	    m.put("surd",         new MathCharDef(1, 2, (char)0x70));
	    m.put("top",          new MathCharDef(0, 2, (char)0x3E));
	    m.put("bot",          new MathCharDef(0, 2, (char)0x3F));
	    m.put("triangle",     new MathCharDef(0, 2, (char)0x34));
	    m.put("forall",       new MathCharDef(0, 2, (char)0x38));
	    m.put("exists",       new MathCharDef(0, 2, (char)0x39));
	    m.put("neg",          new MathCharDef(0, 2, (char)0x3A));
	    m.put("lnot",         new MathCharDef(0, 2, (char)0x3A));
	    m.put("flat",         new MathCharDef(0, 1, (char)0x5B));
	    m.put("natural",      new MathCharDef(0, 1, (char)0x5C));
	    m.put("sharp",        new MathCharDef(0, 1, (char)0x5D));
	    m.put("clubsuit",     new MathCharDef(0, 2, (char)0x7C));
	    m.put("diamondsuit",  new MathCharDef(0, 2, (char)0x7D));
	    m.put("heartsuit",    new MathCharDef(0, 2, (char)0x7E));
	    m.put("spadesuit",    new MathCharDef(0, 2, (char)0x7F));

	  // big ops -- 巨算符                  c=1
	    m.put("coprod",      new MathCharDef(1, 3, (char)0x60));
	    m.put("bigvee",      new MathCharDef(1, 3, (char)0x57));
	    m.put("bigwedge",    new MathCharDef(1, 3, (char)0x56));
	    m.put("biguplus",    new MathCharDef(1, 3, (char)0x55));
	    m.put("bigcap",      new MathCharDef(1, 3, (char)0x54));
	    m.put("bigcup",      new MathCharDef(1, 3, (char)0x53));
	    m.put("intop",       new MathCharDef(1, 3, (char)0x52)); 
	    m.put("prod",        new MathCharDef(1, 3, (char)0x51));
	    m.put("sum",         new MathCharDef(1, 3, (char)0x50));
	    m.put("bigotimes",   new MathCharDef(1, 3, (char)0x4E));
	    m.put("bigoplus",    new MathCharDef(1, 3, (char)0x4C));
	    m.put("bigodot",     new MathCharDef(1, 3, (char)0x4A));
	    m.put("ointop",      new MathCharDef(1, 3, (char)0x48));
	    m.put("bigsqcup",    new MathCharDef(1, 3, (char)0x46));
	    m.put("smallint",    new MathCharDef(1, 2, (char)0x73));

	  // binary operations -- 二元运算符              c=2
	    m.put("triangleleft",      new MathCharDef(2, 1, (char)0x2F));
	    m.put("triangleright",     new MathCharDef(2, 1, (char)0x2E));
	    m.put("bigtriangleup",     new MathCharDef(2, 2, (char)0x34));
	    m.put("bigtriangledown",   new MathCharDef(2, 2, (char)0x35));
	    m.put("wedge",       new MathCharDef(2, 2, (char)0x5E));
	    m.put("land",        new MathCharDef(2, 2, (char)0x5E));
	    m.put("vee",         new MathCharDef(2, 2, (char)0x5F));
	    m.put("lor",         new MathCharDef(2, 2, (char)0x5F));
	    m.put("cap",         new MathCharDef(2, 2, (char)0x5C));
	    m.put("cup",         new MathCharDef(2, 2, (char)0x5B));
	    m.put("ddagger",     new MathCharDef(2, 2, (char)0x7A));
	    m.put("dagger",      new MathCharDef(2, 2, (char)0x79));
	    m.put("sqcap",       new MathCharDef(2, 2, (char)0x75));
	    m.put("sqcup",       new MathCharDef(2, 2, (char)0x74));
	    m.put("uplus",       new MathCharDef(2, 2, (char)0x5D));
	    m.put("amalg",       new MathCharDef(2, 2, (char)0x71));
	    m.put("diamond",     new MathCharDef(2, 2, (char)0x05));
	    m.put("bullet",      new MathCharDef(2, 2, (char)0x0F));
	    m.put("wr",          new MathCharDef(2, 2, (char)0x6F));
	    m.put("div",         new MathCharDef(2, 2, (char)0x04));
	    m.put("odot",        new MathCharDef(2, 2, (char)0x0C));
	    m.put("oslash",      new MathCharDef(2, 2, (char)0x0B));
	    m.put("otimes",      new MathCharDef(2, 2, (char)0x0A));
	    m.put("ominus",      new MathCharDef(2, 2, (char)0x09));
	    m.put("oplus",       new MathCharDef(2, 2, (char)0x08));
	    m.put("mp",          new MathCharDef(2, 2, (char)0x07));
	    m.put("pm",          new MathCharDef(2, 2, (char)0x06));
	    m.put("circ",        new MathCharDef(2, 2, (char)0x0E));
	    m.put("bigcirc",     new MathCharDef(2, 2, (char)0x0D));
	    m.put("setminus",    new MathCharDef(2, 2, (char)0x6E)); // for set difference A\setminus B
	    m.put("cdot",        new MathCharDef(2, 2, (char)0x01));
	    m.put("ast",         new MathCharDef(2, 2, (char)0x03));
	    m.put("times",       new MathCharDef(2, 2, (char)0x02));
	    m.put("star",        new MathCharDef(2, 1, (char)0x3F));

	  // Relations -- 关系运算符.             c=3
	    m.put("propto",      new MathCharDef(3, 2, (char)0x2F));
	    m.put("sqsubseteq",  new MathCharDef(3, 2, (char)0x76));
	    m.put("sqsupseteq",  new MathCharDef(3, 2, (char)0x77));
	    m.put("parallel",    new MathCharDef(3, 2, (char)0x6B));
	    m.put("mid",         new MathCharDef(3, 2, (char)0x6A));
	    m.put("dashv",       new MathCharDef(3, 2, (char)0x61));
	    m.put("vdash",       new MathCharDef(3, 2, (char)0x60));
	    m.put("leq",         new MathCharDef(3, 2, (char)0x14));
	    m.put("le",          new MathCharDef(3, 2, (char)0x14));
	    m.put("geq",         new MathCharDef(3, 2, (char)0x15));
	    m.put("ge",          new MathCharDef(3, 2, (char)0x15));
	    m.put("lt",          new MathCharDef(3, 1, (char)0x3C));  // extra since < and > are hard
	    m.put("gt",          new MathCharDef(3, 1, (char)0x3E));  //   to get in HTML
	    m.put("succ",        new MathCharDef(3, 2, (char)0x1F));
	    m.put("prec",        new MathCharDef(3, 2, (char)0x1E));
	    m.put("approx",      new MathCharDef(3, 2, (char)0x19));
	    m.put("succeq",      new MathCharDef(3, 2, (char)0x17));
	    m.put("preceq",      new MathCharDef(3, 2, (char)0x16));
	    m.put("supset",      new MathCharDef(3, 2, (char)0x1B));
	    m.put("subset",      new MathCharDef(3, 2, (char)0x1A));
	    m.put("supseteq",    new MathCharDef(3, 2, (char)0x13));
	    m.put("subseteq",    new MathCharDef(3, 2, (char)0x12));
	    m.put("in",          new MathCharDef(3, 2, (char)0x32));
	    m.put("ni",          new MathCharDef(3, 2, (char)0x33));
	    m.put("owns",        new MathCharDef(3, 2, (char)0x33));
	    m.put("gg",          new MathCharDef(3, 2, (char)0x1D));
	    m.put("ll",          new MathCharDef(3, 2, (char)0x1C));
	    m.put("not",         new MathCharDef(3, 2, (char)0x36));
	    m.put("sim",         new MathCharDef(3, 2, (char)0x18));
	    m.put("simeq",       new MathCharDef(3, 2, (char)0x27));
	    m.put("perp",        new MathCharDef(3, 2, (char)0x3F));
	    m.put("equiv",       new MathCharDef(3, 2, (char)0x11));
	    m.put("asymp",       new MathCharDef(3, 2, (char)0x10));
	    m.put("smile",       new MathCharDef(3, 1, (char)0x5E));
	    m.put("frown",       new MathCharDef(3, 1, (char)0x5F));

	  // Arrows
	    m.put("Leftrightarrow",   new MathCharDef(3,2, (char)0x2C));
	    m.put("Leftarrow",        new MathCharDef(3,2, (char)0x28));
	    m.put("Rightarrow",       new MathCharDef(3,2, (char)0x29));
	    m.put("leftrightarrow",   new MathCharDef(3,2, (char)0x24));
	    m.put("leftarrow",        new MathCharDef(3,2, (char)0x20));
	    m.put("gets",             new MathCharDef(3,2, (char)0x20));
	    m.put("rightarrow",       new MathCharDef(3,2, (char)0x21));
	    m.put("to",               new MathCharDef(3,2, (char)0x21));
	    m.put("mapstochar",       new MathCharDef(3,2, (char)0x37));
	    m.put("leftharpoonup",    new MathCharDef(3,1, (char)0x28));
	    m.put("leftharpoondown",  new MathCharDef(3,1, (char)0x29));
	    m.put("rightharpoonup",   new MathCharDef(3,1, (char)0x2A));
	    m.put("rightharpoondown", new MathCharDef(3,1, (char)0x2B));
	    m.put("nearrow",          new MathCharDef(3,2, (char)0x25));
	    m.put("searrow",          new MathCharDef(3,2, (char)0x26));
	    m.put("nwarrow",          new MathCharDef(3,2, (char)0x2D));
	    m.put("swarrow",          new MathCharDef(3,2, (char)0x2E));

	    m.put("minuschar",  	  new MathCharDef(3,2, (char)0x00)); // for longmapsto
	    m.put("hbarchar",         new MathCharDef(0,0, (char)0x16)); // for \hbar
	    m.put("lhook",            new MathCharDef(3,1, (char)0x2C));
	    m.put("rhook",            new MathCharDef(3,1, (char)0x2D));

	    m.put("ldotp",            new MathCharDef(6,1, (char)0x3A)); // ldot as a punctuation mark
	    m.put("cdotp",            new MathCharDef(6,2, (char)0x01)); // cdot as a punctuation mark
	    m.put("colon",            new MathCharDef(6,0, (char)0x3A)); // colon as a punctuation mark

	    m.put("#",                new MathCharDef(7,0, (char)0x23));
	    m.put("$",                new MathCharDef(7,0, (char)0x24));
	    m.put("%",                new MathCharDef(7,0, (char)0x25));
	    m.put("&",                new MathCharDef(7,0, (char)0x26));

		return m;
	}
	
	
	
	/** 要解析的字符串 */
	private String string = "";
	
	/** 是用于扫描 string 的位置索引 */
	private int i = 0;
	
	/** 解析结果放在此对象中. */
	private MList mlist;
	
	/** Typeset() 函数中使用. 作为 MList.Typeset() 函数返回结果 */
	private Box typeset;
	
	/** Matrix 系列命令的 cs 名, 如 "matrix", "cases", "pmatrix" 等 */
	private String matrix = null; 
	/** Matrix 系列命令使用, 类型有待研究 */
	private MatrixRow row = null;
	/** Matrix 系列命令使用, 类型有待研究 */
	private MatrixTable table = null;
	/** Matrix 系列命令使用, 推测是保留每行的 dimen 尺寸信息. */
	private List<Dimen> rspacing = null;
	
	/**
	 * 构造 Parser.
	 * @param s - 要解析的字符串.
	 * @param font
	 * @param size -- 尺寸的索引(对 JsMath.sizes 数组的索引), 取值为 null 表示缺省索引=4.
	 * @param style -- 显示样式, 如 "D", "T" 等.
	 */
	public Parser(JsMath jsMath, String s, Integer font, Integer size, String style) {
		this.jsMath = jsMath;
		this.string = s;
		this.i = 0;
		this.mlist = new MList(null, font, size, style);
		
	}
	
	/**
	 * 解析一个 TeX 数学字符串, 处理 tex 宏等.
	 * Parse a TeX math string, handling macros, etc.
	 */
	public void Parse() {
		char c;
		// 在解析过程中, this.i, this.string 都可能在内部调用的函数中发生变化, 因此这里要小心处理.
		while (this.i < this.string.length()) {
			// 得到该位置的字符, 根据不同的字符选择不同的处理函数.
			c = this.string.charAt(this.i++);

			// 根据不同字符进行处理:
			if (mathchar.containsKey(c)) // 数学符号及标点, 如 '+', '*' 等符号.
				this.HandleMathCode(String.valueOf(c), mathchar.get(c));
			else if (isLetter(c)) // 字母.
				this.HandleVariable(c);
			else if (isNumber(c)) // 数字.
				this.HandleNumber(c);
			
			// 原 js 使用判断 this.special[c], 然后进入特定函数处理, 我们简化为一组 if 判断...
			else if (c == '\\') // 转义符, 处理 \cs 命令
				this.HandleCS(c);
			else if (c == ' ') // 空格.
				this.Space(c);
			else if (c == '{')
				this.HandleOpen(c);
			else if (c == '}')
				this.HandleClose(c);
			else if (c == '~') // 连字符.
				this.Tilde(c);
			else if (c == '^') // 上标.
				this.HandleSuperscript(c);
			else if (c == '_') // 下标.
				this.HandleSubscript(c);
			else if (c == '\'') // 单引号
				this.Prime(c);
			else if (c == '%') // tex 注释
				this.HandleComment(c);
			else if (c == '&') // 表格,矩阵
				this.HandleEntry("&");
			else if (c == '#') // 报错, 不能直接用参数字符
				this.Hash(c);
			
			// 其它不支持的字符当普通字符看待.
			else
				this.HandleOther(c);
			
			// TODO: 更多字符类型...
		}
		
		// 全部字符串解析完成, 处理未封闭的 开符号. 其依据是 openI != null.
		if (this.mlist.data.openI != null) {
			BoundaryItem open = (BoundaryItem)this.mlist.Get(this.mlist.data.openI);
			if (open.left == null) {
				// left == null 表示使用 {} 作为定界符.
				throw new RuntimeException("Missing close brace");
			} else {
				// left 值非空, 则表示有左定界符(是一个 \cs, 或指定的定界符字符).
				throw new RuntimeException("Missing " + "\\this.cmd " + "right");
			}
		}
		
		if (this.mlist.data.overI != null) {
			// 完成 \over 系列命令.
			this.mlist.Over(); 
		}
	}

	/**
	 * 执行附录G 中的处理.
	 */
	public void Atomize() {
		MList.Init data = this.mlist.init;
		this.mlist.Atomize(data.style, data.size);
	}
	
	/**
	 * 产生最终的 HTML.
	 * @return
	 */
	public String Typeset() {
		MList.Init data = this.mlist.init;
		Box box = this.typeset = this.mlist.Typeset(data.style, data.size);
		// 略: if (this.error) ...
		if ("null".equals(box.format)) return "";  // 不用产生任何输出.
		
		Box box2 = box.Styled();
		box2.Remeasured(); // 无法实现的: 重新计算大小.
		
		boolean isSmall = false; // box 的内容比 box 本身小?
		boolean isBig = false; // box 的大小比标准文本大?
		if (box.bh > box.h && box.bh > JsMath.h + 0.001)
			isSmall = true;
		if (box.bd > box.d && box.bd > JsMath.d + 0.001)
			isSmall = true;
		if (box.h > JsMath.h || box.d > JsMath.d) 
			isBig = true;
		
		String html = box.html;
		if (isSmall) { // hide the extra size
			// 原js 有多种判断处理, 暂时略. jsMath.Browser.allowAbsolute
			if (JsMath.Browser.allowAbsolute) {
				float y = (box.bh > jsMath.h +0.001f ? jsMath.h - box.bh : 0f);
				html = HTML.Absolute(html, box.w, JsMath.h, 0, y);
			}  // TODO: 几个 else if 先忽略了.
			
			isBig = true;
		}
		
		if (isBig) {
			// add height and depth to the line
			//  (force a little extra to separate lines if needed)
			html += HTML.Blank(0f, box.h+0.05f, box.d+0.05f, false);
		}
		
		return "<nobr><span class=\"scale\">" + html + "</span></nobr>";
	}
	
	// === 处理各种字符 ======
	
	/**
	 * 处理数学字符, 在 tex 中以 \mathchar, \mathchardef 定义的.
	 * @param name - 该数学字符, 如 '+'
	 * @param code - 该数学字符对应的 cfa 数据.
	 */
	private void HandleMathCode(String name, MathCharDef code) {
		// 例子项: '+': [2, 0, 0x2B],  class=code[0]=2, family=code[1]=0, apos=code[2]=0x2B;
	    this.HandleTeXchar(code.clazz, code.family, code.apos);
	}
	
	/** 从 tex 字体中添加指定的字符. 加载该字体...
	 * Add a specific character from a TeX font (use the current
     *  font if the type is 7 (variable) or the font is not specified)
     *  Load the font if it is not already loaded.
     * @param type - 类型(char class), 指该字符所属的类别, 范围在 0-7.
     * @param font - 字体族索引(family), 范围在 0-15
     * @param code - 字符编码(apos). 合起来是 cfa 三元组.
     */	
	private void HandleTeXchar(int type, int font, char code) {
		// 如果 type==7, 表示使用当前字体.
		if (type == 7 && this.mlist.data.font != null) {
			font = this.mlist.data.font;
		}
		
		// 得到该字体的名字.
		String font_name = CTeX.fam[font];
		FontInfo fi = JsMath.TeX.getFontByName(font_name);
		if (fi == null) {
			// 该字体不存在. 了解这些语义.
			// TODO: jsMath.TeX[font] = []; 
			// TODO: this.Extension(null, [jsMath.Font.URL(font)]);
			throw new java.lang.UnsupportedOperationException();
		}
		else {
			// 添加一个 item(内含一个atom)到当前 mlist.
			// 类型为: "ord", "op", "bin", "rel", "open", "close", "punct", "ord"
			String item_type = CTeX.atom[type]; // mItem 的内容 class
			
			// 为指定字体下指定字符创建 AtomItem, 其 nuc 是原子 {type="TeX",...}. TAG:MITEM, TAG:ATOM
			AtomItem item = MItem.TeXAtom(item_type, code, font_name);
			this.mlist.Add(item);
		}
	}

	/**
	 * 添加 tex 变量(字母)字符或数字(斜体)字符.
	 * Add a TeX variable character or number
	 * @param c -- 该字母字符.
	 */
	private void HandleVariable(char c) {
		// clazz=7 表示使用当前字体族, family=1 表示 cmmi 数学斜体.
		// 对于字母, 数字, 直接使用其字符编码做为在字体族中的位置.
		this.HandleTeXchar(7, 1, c);
	}
	
	/**
	 * 添加数字 0-9
	 * @param c
	 */
	private void HandleNumber(char c) {
		// clazz=7 表示使用当前字体族, family=0 表示 cmr 罗马体.
		this.HandleTeXchar(7, 0, c);
	}
	
	// === special[] 中各种字符的处理 ================================================
	
	/**
	 * 处理特殊字符 ~ .
	 * @param c
	 */
	private void Tilde(char c) {
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 处理特殊字符 ^, 给前面的原子添加上标.
	 *  Add a superscript to the preceeding atom
	 * @param c
	 */
	private void HandleSuperscript(char c) {
		// 得到当前 mlist 的最后一项, 其可能是一个 atom 项, 也可能不是.
		MItem base = this.mlist.Last();
		
		// 语义: overI 表示 \over 的位置. 如果 overI == mlist.Length() 表示最后一项是 \over 命令.
		// 举例为  x \over ^ 2 (在 ^ 前面是 \over 命令)
		if (this.mlist.data.overI != null 
				&& this.mlist.data.overI.intValue() == this.mlist.Length()) {
			base = null; // 不能以 \over 为原子, 所以将创建一个空的原子项当上标的核.
		}
		
		// 如果最后没有 atom, 或..., 总之不能给它加上下标. 则创建各个字段为 empty 的 ord 原子.
		if (canAddScript(base) == false) {
			// 原 js: this.mlist.Add(jsMath.mItem.Atom('ord', {type:null})) 
			//   我们可能没有正确理解和实现其语义? 应创建一种 EmptyAtom 类?
			// TAG:MITEM
			Field nuc = new EmptyField();
			base = new AtomItem(MItem.TYPE_ord, nuc);
			this.mlist.Add(base);
		}
		
		// ?? 我们必须使用该类型, 才有 atom.sup, sub 属性. 或者规定一个 AtomItem 的基类, 其它从其派生.?
		AtomItem atom = (AtomItem)base;
		
		// 如果前一个原子已经有了 sup(上标)
		if (atom.sup != null) {
			// TODO: isPrime 特殊进行处理
			//if (base.sup.isPrime) ...
			
			// 否则报告错误: else
			{
				throw new RuntimeException("Double exponent: use braces to clarify");
			}
		}
		
		// 设置 sup 字段为后面的 <math field>, 进入函数 ProcessScriptArg()
		atom.sup = this.ProcessScriptArg("superscript");
	}
	
	// HandleSuperscript() 中使用的判定, 单独拿出来, 好分析理解其语义.
	// 判断能否给 base 项目添加上下标. 如果不能, 则外面调用者会创建一个空的 atom 项目.
	private boolean canAddScript(MItem base) {
		if (base == null) // base 为空, 则无法给其加上下标. 
			return false;
		String type = base.type;
		
		// 问题: 'frac' 没有见到创建出该类型的 mItem 的地方?? 也许是 'over' ?? 或其代码不一致?
		if (base.atom == false  // 如果不含原子, 则无法加上下标.
				&& "box".equals(type) == false  // "box" 类型的无法加上下标?
				&& "frac".equals(type) == false) // "frac" 类型的无法加上下标?
			return false;
		
		return true;
	}
	
	/**
	 * 处理特殊字符 _, 给前面的原子添加下标.
	 * @param c
	 */
	private void HandleSubscript(char c) {
		MItem base = this.mlist.Last(); // 得到前一个 mItem. (给其添加上下标)
		
		// 这里同函数 HandleSuperscript()
		if (this.mlist.data.overI != null 
				&& this.mlist.data.overI.intValue() == this.mlist.Length()) {
			base = null; // 不能当 \over 为原子, 所以将创建空原子. 
		}
		
		// 如果最后没有 atom, 或...
		if (canAddScript(base) == false) {
			// 原 js: this.mlist.Add(jsMath.mItem.Atom('ord', {type:null})) 
			//   我们可能没有正确理解和实现其语义?
			// TAG:MITEM
			base = new AtomItem(MItem.TYPE_ord, (Field)null);
			this.mlist.Add(base);
		}
		
		// 
		AtomItem base2 = (AtomItem)base;
		// 
		if (base2.sub != null) {
			throw new RuntimeException("Double subscripts: use braces to clarify");
		}
		
		// 添加到下标字段. 
		base2.sub = this.ProcessScriptArg("subscript");
	}
	
	/**
	 * 数学模式下忽略空格, 所以空处理即可.
	 * @param c
	 */
	private void Space(char c) {
		// 空处理.
	}
	
	/**
	 * 处理 ' 字符, 将其转换为相应的上标.
	 * @param c
	 */
	private void Prime(char c) {
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 处理 % 字符, 注释.
	 * @param c
	 */
	private void HandleComment(char c) {
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 处理 & 字符, 表格,矩阵分隔符. 尝试添加一个矩阵项(类似于 <td> 标记) 到表格行(row)数据.
	 *   (使用在当前 mlist 中的数据, 然后清空 mlist)
	 * (现在已知)在两个地方有调用, 参数略有差异. 
	 *   1. 在解析字符 '&' 的时候, 此时参数 name="&"
	 *   2. 在 HandleRow() 中调用, 此时 name 是cs命令名, 如 \cr, \cases 等.
	 */
	private void HandleEntry(String name) {
		if (this.matrix == null) {
			throw new RuntimeException(name + " can only appear in a matrix or array");
		}
		
		// 这里的语义应该是, 遇到 &, \cr 等命令时, 结束当前 \over, open/close 对.
		if (this.mlist.data.openI != null) {
			// 推测 测试用例: \cases {  \left( x  \cr   y }
			throw new UnsupportedOperationException("TODO: 给出测试用例");
		}
		if (this.mlist.data.overI != null) {
			this.mlist.Over(); 
		}
		
		MList.Data data = this.mlist.data;
		this.mlist.Atomize(data.style, data.size); // 对现有 mlist 内容进行原子处理(?本质) ?从解析中间态->内部数据存储方式?
		Box box = this.mlist.Typeset(data.style, data.size); // 排版为盒子(三步曲:解析,原子化,排版)
		box.entry = data.entry; data.entry = null; // 清空 data.entry, 为解析下一个 entry 做准备.
		if (box.entry == null) { box.entry = new Object(); /* TODO: js: ={} */ }
		this.row.add(box);  // 添加到当前行末尾.
		this.mlist = new MList(null, null, data.size, data.style);
	}
	
	/**
	 * 处理 # 字符.
	 * @param c
	 */
	private void Hash(char c) {
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
	 * 处理 \ 转义符, 即 \cs 命令.
	 * @param c
	 */
	private void HandleCS(char c) {
		// 1. 首先读取 cmd
		String cmd = this.GetCommand(); // if (this.error) 这里采用异常方式处理.
		
		// 看该 cmd 是否在 macros 映射中有. 里面有大量的命令...(我们先选择部分研究及实现)
		// this.macros[cmd] -- 原来 jsMath 采用表格记录下要处理的宏的一些信息, 我们在 java 中
		//   采用稍微变通的方法...来解决...
		if (macros.containsKey(cmd)) {
			callMacroFunc(cmd);
			return;
		}
		
		// 数学符号定义
		if (mathchardef.containsKey(cmd)) {
			MathCharDef _t = mathchardef.get(cmd);
			this.HandleMathCode(cmd, _t);
			return;
		}
		
		// (?单个)定界符.
		if (delimiter.containsKey(CH_CMD + cmd)) {
			DelimInfo delim = delimiter.get(CH_CMD + cmd);
			MathCharDef _t2 = delim.toMathChar();
			this.HandleMathCode(cmd, _t2);
			return;
		}
		
		throw new RuntimeException("Syntax error.");
	}
	
	/**
	 * 处理 { 组开始.
	 * @param c
	 */
	private void HandleOpen(char c) {
		this.mlist.Open(null);
	}
	
	/**
	 * 处理 } 组结束.
	 * @param c
	 */
	private void HandleClose(char c) {
		MList.Data data = this.mlist.data;
		if (data.openI == null) {
			throw new RuntimeException("Extra close brace.");
		}
		
		BoundaryItem open = this.mlist.getOpenItem(); // 肯定应该有, 如果 data.openI 非空的话.
		if (open == null || open.left == null) {
			// 遇到字符 '}' 结束组. 也即调用 Close(right_delimiter=null);
			this.mlist.Close(null);
		}
		else {
			throw new RuntimeException("Extra close brace or missing " + CH_CMD + "right");
		}
	}
	
	/**
	 * 处理其它字符, 当做普通字符处理.
	 * @param c
	 */
	private void HandleOther(char c) {
		// jsMath.mItem.TextAtom('ord', c, 'normal') -- 我们用 HtmlTextAtom, GeneralAtomItem 取代.
		Field nuc = new HtmlTextField(String.valueOf(c), "normal");
		AtomItem item = new AtomItem(MItem.TYPE_ord, nuc);
		this.mlist.Add(item);
	}
	

	// ==== macros 的实现 ======================================================
	
	// === macros[] 映射的宏的处理 ======================================================
	
	// 现在参数中比较混乱的一个地方是, 有的 cs 带有 \, 有的不带... 有什么规律吗?
	
	/**
	 * 表示一个尚未实现的, 仅占位用的宏项目.
	 */
	private static class MacroNotImpl extends MacroFunction {
		public static final MacroNotImpl instance = new MacroNotImpl();
		private MacroNotImpl() {}
		public void doMacro(Parser parser, String cmd_name) {
			throw new UnsupportedOperationException("cmd " + cmd_name + " not impl.");
		}
	}
	
	/**
	 * 添加 style 变更 item, 见附录G 规则 26.
	 *  Add a style change (e.g., \displaystyle, etc)
	 */
	private static class StyleCSHandler extends MacroFunction {
		private final String style;
		public StyleCSHandler(String style) { 
			this.style = style; 
		}
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleStyle(style);
		}
	}
	
	/** 从内部类 HandleStyle.doMacro() 调用.  */
	private void HandleStyle(String style) {
		// 1. 设置 mlist 的当前 'display style'
		this.mlist.setDisplayStyle(style); // this.mlist.data.style = style;
		
		// 2. 添加一个 change-style 的 mitem 到当前 mlist
		StyleItem mitem = new StyleItem(style);
		this.mlist.Add(mitem);
	}
	
	/**
	 * 处理 ',', ':', '>' ';', '!' 等产生 'space' 项目的命令.
	 */
	private static class SpacerCSHandler extends MacroFunction {
		float w;
		public SpacerCSHandler(float w) { this.w = w; }
		public void doMacro(Parser parser, String cmd_name) {
			parser.Spacer(cmd_name, w);
		}
	}
	/** 在水平方向上添加指定量的间距(space), 如命令 '\quad' 
	 * Add a fxied amount of horizontal space */
	private void Spacer(String cmd, float w) {
		SpaceItem sitem = MItem.Space(w);
		this.mlist.Add(sitem);
	}
	
	/**
	 * 处理 \tiny, \small 等命令.
	 */
	private static class SizeCSHandler extends MacroFunction {
		int size;
		public SizeCSHandler(int size) { this.size = size; }
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleSize(size);
		}
	}
	
	private void HandleSize(int size) {
		// 同时设置 data 的大小
		this.mlist.data.size = size;
		
		// 创建 size mitem.
		MItem item = new SizeItem(size);
		this.mlist.Add(item);
	}
	
	// 加数学重音.
	private static class MathAccentCSHandler extends MacroFunction {
		public int[] accent = new int[3];
		public MathAccentCSHandler(int a0, int a1, int a2) {
			this.accent[0] = a0;
			this.accent[1] = a1;
			this.accent[2] = a2;
		}
		public void doMacro(Parser parser, String cmd_name) {
			parser.MathAccent(cmd_name, this.accent);
		}
	}
	private void MathAccent(String name, int[/*3*/] accent) {
		//
		Field c = this.ProcessArg(CH_CMD + name);
		AtomItem atom = MItem.Atom("accent", c);
		atom.accent = accent; // int[3]
		this.mlist.Add(atom);
	}
	
	// 和矩阵有关的 \cases 等命令的处理.
	//   JS 中 cases: ['Matrix', '\\{', '.', ['l','l'], null, 2],
	//   初步推测, 参数为 左定界符, 右定界符, 列对齐数组, ?间距, ?列数, ?
	private static class MatrixCSHandler extends MacroFunction {
		public MatrixCSParam mparam;
		public MatrixCSHandler() { this.mparam = new MatrixCSParam(); }
		public MatrixCSHandler(MatrixCSParam mparam) { this.mparam = mparam; }
		public void doMacro(Parser parser, String cmd_name) {
			parser.Matrix(mparam, cmd_name);
		}
	}
	// Create an array or matrix.
	// 测试用例1: \cases {x \cr y}
	private void Matrix(MatrixCSParam delim, String name) {
		MList.Data data = this.mlist.data;
		String arg = this.GetArgument(CH_CMD+name, false);
		// 初始化一个新的 parser, 然后解析参数中的内容(矩阵的内容)
		// 这个新 parser, 由于设置了 .matrix 属性非空, 从而知道是要解析一个表格内部. 在 arg 后面加上了 \\ 命令, 其能够终结最后一个 entry
		Parser parse = new Parser(jsMath, arg + CH_CMD + "\\", null, data.size, delim.get_5("T"));
		parse.matrix = name;
		parse.row = new MatrixRow();
		parse.table = new MatrixTable();
		parse.rspacing = new ArrayList<Dimen>();
		parse.Parse(); /* if(parse.error) error(), return;*/
		
		parse.HandleRow(name, true); // 确保最后的行正确的记录了...终结最后一个 row.
		
		// 这里使用了 delim[2,3,4] 等参数, 所以我们需要暂时将其封装起来...
		// 原型 Layout(size, table, align, cspacing, rspacing, vspace, useStrut, addWidth)
		Dimen[] rspacing = new Dimen[0];
		if (parse.rspacing != null && parse.rspacing.size() > 0) 
			parse.rspacing.toArray(rspacing);
		SafeList<String> delim_align = arrayToList(delim.align);
		SafeList<Float> delim_cspacing = arrayToList(delim.cspacing);
		SafeList<Dimen> list_rspacing = arrayToList(rspacing);
		Box box = Box.Layout(data.size, parse.table, delim_align, delim_cspacing, 
				list_rspacing, delim.vspace, null, null);
		
		// add parentheses, if needed.
		if (delim.left_delim != null && delim.right_delim != null) {
			float H = box.h + box.d - jsMath.hd/4f; // 内容高度
			Box left = Box.Delimiter(H, this.delimiter.get(delim.left_delim), "T", false);
			Box right = Box.Delimiter(H, this.delimiter.get(delim.right_delim), "T", false);
			// 排版: 左定界符+内容+右定界符
			List<TypeObject> boxes = MList.MakeTOList(left, box, right);
			box = Box.SetList(boxes, data.style, data.size);
		}
		
		// 为什么这里区分了 "inner", "ord" 类型呢??
		String type = delim.left_delim != null ? "inner" : "ord";
		AtomItem atom = MItem.Atom(type, box);
		this.mlist.Add(atom);
	}
	
	public static SafeList<Dimen> arrayToList(Dimen[] arr) {
		SafeList<Dimen> list = new SafeList<Dimen>(Dimen.ZERO_D);
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; ++i)
				list.add(arr[i]);
		}
		return list;
	}
	public static SafeList<String> arrayToList(String[] arr) {
		SafeList<String> list = new SafeList<String>("");
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; ++i)
				list.add(arr[i]);
		}
		return list;
	}
	public static SafeList<Float> arrayToList(float[] arr) {
		SafeList<Float> list = new SafeList<Float>(new Float(0f));
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; ++i)
				list.add(arr[i]);
		}
		return list;
	}
	
	private static class RowCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleRow(cmd_name, false); // 缺省未给出 last 参数, 则应该为 false.
		}
	}
	
	/**
	 * 当遇到 \cr, 或 \\ 命令时, 增加一个 row 到表格 table 中.
	 * When we see a \cr or \\, try to add a row to the table
	 * @param name -- matrix 的名字
	 * @param last -- 推测是标志, 为 true 表示最后一行结束.
	 */
	private void HandleRow(String name, boolean last) {
		Dimen dimen = null;
		if (this.matrix == null) {
			throw new RuntimeException(CH_CMD + name + " can only appear in a matrix or array");
		}
		if (name.equals("\\")) {
			String dimen_str = this.GetBrackets(CH_CMD+name);
			if (dimen_str != null && dimen_str.length() > 0) {
				dimen = this.ParseDimen(dimen_str, CH_CMD+name, 0, 1);
			}
		}
		this.HandleEntry(name); // 处理当前 entry (如果是 & 字符,则分隔多个 entry; 如果是 \cr, 则是最后一个 entry)
		if (!last || this.row.size() > 1 /* TODO: || this.row[0].format != 'null' */) {
			// 从这里可以看到, 一个 table 有多个 row, 一个 row 有多个 box(item)
			this.table.add(this.row);
		}
		if (dimen != null) {
			setListElem(this.rspacing, this.table.size(), dimen);
		}
		// 也许应该将 row 封装为某个类从 ArrayList<Row> 派生? 这样看起来简单得多. 
		this.row = new MatrixRow();
	}
	
	/** 给指定数组的指定位置设置一个元素. 如果数组元素数量不够则填充够. */
	private static <T> void setListElem(List<T> list, int index, T element) {
		if (index > list.size()) {
			// list 中元素数量不够, 需要添加一些.
			for (int i = list.size(); i < index; ++i)
				list.add(null);
			assert(list.size() == index);
		}
		list.set(index, element);
	}
	
	/**
	 * 设置当前字体 (如处理 \rm 等命令)
	 *
	 */
	private static class FontCSHandler extends MacroFunction {
		private int font_index;
		public FontCSHandler(int font_index) { this.font_index = font_index; }
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleFont(font_index);
		}
	}
	
	/** 从内部类 HandleFont.doMacro() 调用. */
	private void HandleFont(int font_index) {
		this.mlist.setFont(font_index);
	}
	
	/** 处理 \left 命令. */
	private static class LeftCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleLeft("\\left");
		}
	}

	// @param cmd_name -- 例如为 '\left'
	private void HandleLeft(String cmd_name) {
		// 读取 left 定界符(delimiter)
		DelimInfo left = this.GetDelimiter(cmd_name);
		
		// 原来 js: if (this.error) return;
		this.mlist.Open(left); // 开始开符号.
	}
	
	/** 处理 \right 命令. */
	private static class RightCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleRight("\\right");
		}
	}
	
	private void HandleRight(String cmd_name) {
		// 读取 right 定界符
		DelimInfo right = this.GetDelimiter(cmd_name);
		
		// 得到当前`层次'的 left 开定界符.
		BoundaryItem open = this.mlist.getOpenItem();
		if (open == null)
			// 没有对应的开定界符.
			throw new RuntimeException("Missing " + cmd_name + " left");
		else if (open.left == null)
			// 有定界符 '{', 但是不匹配 cmd_name.
			throw new RuntimeException("Extra open brace, unmatched " + cmd_name + " left");
		
		// 闭符号.
		this.mlist.Close(right);
	}
	
	/** 
	 * 处理如 \arcsin 等命令.
	 * 创建出 TextItem, 添加到 mList 中. (推测, 一个 TextItem 被原始的翻译为 HTML)  
	 */
	private static class NamedOpCSHandler extends MacroFunction {
		public Boolean limits;
		public String text;
		public NamedOpCSHandler() {
			limits = null;
			text = null;
		}
		public NamedOpCSHandler(boolean limits) {
			this.limits = limits;
			this.text = null;
		}
		public NamedOpCSHandler(String text) {
			this.limits = null;
			this.text = text;
		}
		public void doMacro(Parser parser, String cmd_name) {
			parser.NamedOp(cmd_name, limits, text);
		}
	}
	
	private void NamedOp(String cmd_name, Boolean limits, String text) {
		// 大写字母 ABCDEFGHIJKLMNOPQRSTUVWXYZ, 的 ascend=1, descend=0
		// 小写字母 abcdefghijklmnopqrstuvwxyz 分几种:
		//   bdfhijklt 都有额外的 ascend. (上边出来一些)
		//   gjpqy 有额外的 descent. (下边出来一些)
		// var a = (name.match(/[^acegm-su-z]/)) ? 1: 0;
		// var d = (name.match(/[gjpqy]/)) ? .2: 0;
		// 我们怎么办呢? java 中这样写很麻烦吧......
		float a = calcAscend(cmd_name);
		float d = calcDescend(cmd_name);

		// 如果给出了 text 则使用其作为输出的 html 文本.
		String name = (text != null) ? text : cmd_name;
		
		// 创建为 TextAtom...
		String f = CTeX.fam[0];
		Field atom = new HtmlTextField(name, f, a, d);
		AtomItem box = new AtomItem(MItem.TYPE_op, atom);
		if (limits != null)
			box.limits = limits;

		this.mlist.Add(box);
	}
	
	/**
	 * 函数 HandleAtom(), 处理 \vcenter, \overline, \\underline 命令.
	 * 添加类型为 vcenter/overline/underline 的 atom. 
	 * 例子:
	 *    \overline : ['HandleAtom', 'overline']
	 *    mathord   : ['HandleAtom', 'ord']
	 *  Adds the argument as a specific type of atom (for commands like
     *    \overline, etc.)  
	 */
	private static class AtomCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleAtom(cmd_name, cmd_name);
		}
	}
	
	// item_type 取值范围: [\vcenter, \overline, \\underline]
	private void HandleAtom(String cmd_name, String item_type) {
		// 读取并解析此命令的参数, 这里语义是该命令有一个参数(只有一个, 必须有一个). 返回应为 atom
		// (可能是 mlist 内容的 atom, 或一个单独的 atom)
		Field arg = this.ProcessArg(CH_CMD + cmd_name);
		
		// 使用返回的原子构造一般原子项目. TAG:MITEM
		AtomItem item = new AtomItem(item_type, arg);
		this.mlist.Add(item);
	}
	
	/**
	 * 函数 HandleOver(), 处理 \over, \overwithdelims, ... 等命令.
	 *
	 */
	private static class OverCSHandler extends MacroFunction {
		private String left_delim = null;
		private String right_delim = null;
		public OverCSHandler() {}
		public OverCSHandler(String left_delim, String right_delim) {
			this.left_delim = left_delim;
			this.right_delim = right_delim;
		}
		public void doMacro(Parser parser, String cmd_name) {
			parser.HandleOver(cmd_name, left_delim, right_delim);
		}
	}
	
	/**
	 * HandleOver() 有两种使用方式:
	 *    -- 无参数, left_delim=right_delim=null.
	 *   '\{', '\}' -- 给出左右定界符字符串参数, 分别为 left_delim, right_delim
	 * 
	 * @param cmd_name
	 */
	private void HandleOver(String cmd_name, String left_delim, String right_delim) {
		MList.Data data = this.mlist.data;
		
		// data.overI 用于记录前一个 \over 的位置的. 如 'x \over y' 则记录为 \over 的索引.
		if (data.overI != null) {
			// tex 不支持 x \over y \over z 这种语法.
			throw new RuntimeException("Ambiguous use of " + CH_CMD + cmd_name);
		}
		
		// data.overI 字段记录下位置(\over 分隔的前后两部分的后面部分开始位置)
		data.overI = this.mlist.Length();
		// data.overF 字段记录是哪一种 \over 命令.
		data.overF = new MList.OverProp(cmd_name); // js 中为 {name: name}, 后面还给里面添加属性.
		
		// 如果给出了左右定界符, 例如: brace: [HandleOver, \{, \}]
		if (left_delim != null || right_delim != null) {
			// 记录到 overF 中. 测试用例 {x \brack y}
			data.overF.left_delim = Parser.delimiter.get(left_delim);
			data.overF.right_delim = Parser.delimiter.get(right_delim);
		}
		else if (cmd_name.endsWith("withdelims")) {
			// \overwithdelims, \atopwithdelims, \abovewithdelims 几个命令符合此条件:
			data.overF.left_delim = this.GetDelimiter(CH_CMD + cmd_name);
			data.overF.right_delim = this.GetDelimiter(CH_CMD + cmd_name);
		}
		else {
			// 否则缺省就没有左右定界符.
			data.overF.left_delim = null;
			data.overF.right_delim = null;
		}
		
		// \above* 命令带有分隔的线, 其它缺省没有.
		if (cmd_name.startsWith("above")) {
			// TODO: 实现 GetDimen()
			data.overF.thickness = this.GetDimen(CH_CMD + cmd_name, 1);
		}
		else {
			data.overF.thickness = null;
		}
	}
	
	/**
	 * 函数 HandleLap() 处理 \llap, \rlap, \\ulap, \\dlap 命令.
	 *
	 */
	private static class LapCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			// 暂未实现.
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * 函数 RaiseLower() 处理 \raise, \lower 命令.
	 *
	 */
	private static class RaiseLowerCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.RaiseLower(cmd_name);
		}
	}
	
	/**
	 * 命令 \raise 语法: \raise <dimen><arg> (\lower 语法同)
	 * 例子: \lower.5ex, \raise-.5ex
	 * @param cmd_name
	 */
	private void RaiseLower(String cmd_name) {
		// 1. 得到 dimen 参数.
		Dimen h = this.GetDimen(cmd_name, 1);
		
		// 读取 \raise, \lower 的参数: 可能是 单个字符, {} 或 \cs.
		Field box = this.ProcessScriptArg(CH_CMD + cmd_name); // 这里又要求给出 CH_CMD, 所以有点乱.
		
		// 如果是 \lower 命令, 则对 h 的值取负的.
		if ("lower".equals(cmd_name))
			h = h.toNeg();
		// 创建 itemType="raise" 的 mItem. 原js: new jsMath.mItem('raise',{nuc: box, raise: h})
		// 因为内部有一个原子的核, 所以需要用 GeneralAtomItem (或其派生类)
		RaiseLowerItem item = new RaiseLowerItem(box, h);
		this.mlist.Add(item);
	}
	
	/**
	 * 函数 MoveLeftRight() 处理 \moveleft, \moveright 命令.
	 *
	 */
	private static class MoveLeftRightCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.MoveLeftRight(cmd_name);
		}
	}
	
	/** 实现 \moveleft, \moveright 等命令. */
	private void MoveLeftRight(String cmd_name) {
		// 读取此命令的 dimen 参数.
		Dimen x = this.GetDimen(cmd_name, 1);
		Field box = this.ProcessScriptArg(CH_CMD+cmd_name);
		
		// 计算平移量, 如果 moveleft 则对 x 取负.
		if ("moveleft".equals(cmd_name))
			x = x.toNeg();

		// 添加空白 x (?为何这么做呢)
		this.mlist.Add(new SpaceItem(x));
		
		// 中间内容当做 "ord" 普通内容项目.
		AtomItem item = new AtomItem("ord", box); 
		this.mlist.Add(item);
		
		// 再添加负的间距 x 的项.
		this.mlist.Add(new SpaceItem(x.toNeg()));
	}
	
	/**
	 * 处理 \frac 命令. 
	 */
	private static class FracCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.Frac(cmd_name);
		}
	}
	
	/* 分数语法: \frac{分子}{分母}, 构造为 FractionItem. */
	private void Frac(String name) {
		// 获得参数: 分子
		Field num = this.ProcessArg(name); // name 参数在 js 中为 CMD+name
		
		// 获得参数: 分母
		Field den = this.ProcessArg(name);
		
		// 构造为分数项目. 添加到 mlist.
		MItem item = new FractionItem("over", num, den);
		this.mlist.Add(item);
	}
	
	/**
	 * 处理 \root 命令.
	 */
	private static class RootCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 处理 \sqrt 命令.
	 */
	private static class SqrtCSHandler extends MacroFunction {
		public void doMacro(Parser parser, String cmd_name) {
			parser.Sqrt(cmd_name);
		}
	}
	
	/**
	 * 命令 \sqrt[次数]{表达式} 的处理.
	 * @param name -- 取值为 "sqrt", 或 "radical"?
	 */
	private void Sqrt(String name) {
		// 读取 [] 中的可选参数. (根式次数)
		String n = this.GetBrackets(name);
		
		// 读取 {} 中的表达式参数.
		Field arg = this.ProcessArg(name);
		
		// 构造 "radical" 类型的 atom.
		RadicalItem box = new RadicalItem(arg);
		// Object box = jsMath.mItem.Atom("radical", arg);
		
		// 然后再构造根次... 添加为 box 的 root 属性...
		if (n != null && n.length() > 0) {
			box.root = this.Process(n);
		}
		
		this.mlist.Add(box); // 添加到 mlist 里面.
	}
	
	private static class SubstCSHandler extends MacroFunction {
		public String sstr; // 替换为该字符串.
		public int para_num = 0;
		public SubstCSHandler(String sstr) { this.sstr = sstr; }
		public SubstCSHandler(String sstr, int para_num) { this.sstr = sstr; this.para_num = para_num; }
		public void doMacro(Parser parser, String cmd_name) {
			parser.Subst(cmd_name, sstr, para_num);
		}
	}
	/** 
	 * 从类 SubstMacro 调用, 实现 TeX substitution macros. (jsmath 中对应函数为 Parser.Macro() )
	 * Implements macros like those created by \def.  The named control
     *  sequence is replaced by the string given as the first data value(sstr参数).
     *  If there is a second data value(para_num 参数), this specifies how many arguments
     *  the macro uses, and in this case, those arguments are substituted
     *  for #1, #2, etc. within the replacement string.
     *  
     *  See the jsMath.Macro() command below for more details.
     *  The "newcommand" extension implements \newcommand and \def
     *  and are loaded automatically if needed.
     *  
     *  带参数的定义例子: pmod => {sstr='\\kern 18mu ({\\rm mod}\\,\\,#1)', para_num=1}
	 */
	private void Subst(String name, String sstr, int para_num) {
		String text = sstr;
		if (para_num > 0) {
			String[] args = new String[para_num];
			for (int i = 0; i < para_num; ++i) {
				String arg = this.GetArgument("\\"+name, false);
				args[i] = arg;
			}
			text = this.SubstituteArgs(args, text);
		}
		// 替换到原字符串... 这样我们无法保留原始的字符串, 多不好...
		this.string = this.AddArgs(text, this.string.substring(this.i));
		this.i = 0; // 设置扫描指针位置.
	}
	
	// Replace macro paramters with their values. 被 SubstImpl() 调用.
	private String SubstituteArgs(String[] args, String str) {
		StringBuilder text = new StringBuilder();
		String newstring = "";
		char c;
		int i = 0;
		while (i < str.length()) { 
			c = str.charAt(i++);
			if (c == CH_CMD) {
				text.append(c).append(str.charAt(i++));
			} else if (c == '#') { // 参数字符.
				c = str.charAt(i++); // 应该是数字 1-9, 或 '#'
				if (c == '#') { // ## 表示 #
					text.append('#');
				} else {
					int cc = (c >= '1' && c <= '9') ? (c - '0') : 100;
					if (cc > args.length) {
						throw new RuntimeException("Illegal macro parameter reference");
					}
					newstring = this.AddArgs(this.AddArgs(newstring, text.toString()), args[cc-1]);
					text.setLength(0);
				}
			} else {
				text.append(c);
			}
		}
		
		return this.AddArgs(newstring, text.toString());
	}
	
	/**
	 * Make sure that macros are followed by a space if their names
     *  could accidentally be continued into the following text.
	 * @param s1
	 * @param s2
	 * @return
	 */
	private String AddArgs(String s1, String s2) {
		// 这里有一个复杂的判定, 我们暂时不好移植...先不要.
		if (false /*s2.match(/^[a-z]/i) && s1.match(/(^|[^\\])(\\\\)*\\[a-z]+$/i)*/) {
			s1 += " "; // 其实不加也可能正好是所需的?
		}
		return s1+s2;
	}
	
	// === 辅助函数 ====================================================================

	/**
	 * 解析一个子字符串, 生成对应的 mlist 返回.
	 * Parse a substring to get its mList, and return it.
     *  Check that no errors occured.
     * 子字符串, 当前是要解析的 tex 字符串中 {} 组符号包围的一个子串, 作为参数, 或一个独立单元.
     * @return 按照程序逻辑, 这里返回的类型为 atom.
	 */
	private Field Process(String arg) {
		// 得到当前 data (状态信息)
		MList.Data data = this.mlist.data;
		
		// 递归调用 Parse(), 解析 arg 字符串.
		Parser parser = jsMath.Parse(arg, data.font, data.size, data.style);
		
		// TeXbook p.291 说明: TeX 使用结果 mList 作为新的 "ord" 原子的核.
		//   如果结果 mList 只是一个单独的 acc (重音?)原子, 则该原子自身被使用...
		// TeX uses the resulting math list as the nucleus of a new Ord
		// atom that is appended to the current list.
		// If the resulting math list is a single Acc
		// atom, however (i.e., an accented quantity), that atom itself is appended.
		MList mlist = parser.mlist;
		if (mlist.Length() == 1) {
			MItem item = mlist.Last(); // 也即唯一的一个.
			// 这里的判定很复杂, 语义上怎么说明呢?
			if (single_acc_atom(item)) {
				return (Field)(((AtomItem)item).nuc);
			}
		}
		
		// 返回一个对象 {type: 'mlist', mlist: mlist} 整个列表当做原子的核.
		MListField atom = new MListField(mlist);
		return atom;
	}
	
	/** 提供给函数 Process() 使用. */
	private static boolean single_acc_atom(MItem item) {
		if (item.atom == false) return false;
		AtomItem a_item = (AtomItem)item;
		// 必须是 "ord" 类型
		if ("ord".equals(a_item.type) == false) return false;
		if (a_item.nuc == null) return false;
		if (a_item.sub != null) return false;
		if (a_item.sup != null) return false;
		
		if ("text".equals(a_item.nuc.type) || "TeX".equals(a_item.nuc.type))
			return true;
		return false;
	}
	
	/**
	 * 从当前扫描位置读取一个 \cs 命令.
	 * @return 返回读取的这个命令字符串.
	 */
	private String GetCommand() {
		// 在 js 中使用了正则表达式 /^([a-z]+|.) ?/i 来作为匹配. 那我们怎么做呢?
		// 而 jmathtex 中的代码也比较奇异, 看来我们自己写一个比较好?
		// 入口条件 this.i 指向 this.string 的当前字符是 cs 的第一个字符.
		if (this.i >= this.string.length())
			throw new java.lang.RuntimeException("在读取命令的时候, 超出了最后一个字符.");
		
		// 如果第一个字符不是字母, 则该字符即是命令. 如 \$ 的情况.
		char ch = this.string.charAt(this.i++);
		if (isLetter(ch) == false) {
			// 将这个字符作为 cs 命令. 跳过后面可能的空格.
			while (this.i < this.string.length()) {
				char ch2 = this.string.charAt(this.i);
				if (isSpace(ch2) == false) // 非空白了, 则跳出.
					break;
				++this.i; // 忽略/吃掉此空白.
			}
			
			return String.valueOf(ch);
		}
		
		// 读取后面可能的多个字母.
		int start_i = this.i - 1;
		while (this.i < this.string.length()) {
			char ch2 = this.string.charAt(this.i);
			if (isLetter(ch2) == false)
				break;
			++this.i; // 继续找下一个字母.
		}
		
		// 得到命令. 注意此时 this.i 指向 cs 最后一个字符之后.
		String cs = this.string.substring(start_i, this.i);
		
		// 跳过后面可能的空白.
		while (this.i < this.string.length()) {
			char ch2 = this.string.charAt(this.i);
			if (isSpace(ch2) == false) // 非空白了, 则跳出.
				break;
			++this.i; // 忽略/吃掉此空白.
		}
		
		return cs; // 返回命令.
	}
	
	/**
	 * 读取并返回一个 tex 的参数(其要么是一个单个字符, 要么是一个 \cs, 要么是 {} 组内容)
	 *  Get and return a TeX argument (either a single character or control sequence,
     *   or the contents of the next set of braces).
	 * @param name -- 为该 \name 命令读取参数. 例如取值为 "\frac"
	 * @return
	 *   如果该参数是一个 cs, 返回为该 cs 的字符串. (如 \mathop \log)
	 *   如果参数是单个字符, 返回该字符的字符串. (如 "x")
	 *   如果参数是用{}包围起来的组, 则返回该{}内的内容. 不含 {} 部分. (可能为 empty, 即空组)
	 *   
	 * 在原 jsMath 中此函数另有参数 noneOK, 但(暂)未见到有使用的地方.
	 */
	private String GetArgument(String name, boolean noneOK) {
		// 跳过(可能命令之后有)空白...
		skipSpace();
		
		// 后面没内容了? 报告错误... (或返回 null?)
		if (this.i >= this.string.length()) {
			if (noneOK) return null; // 允许没有参数.
			// ?name 参数在这里仅用作报告错误吗?
			throw new RuntimeException("Missing argument for " + name);
		}
		
		char ch = this.string.charAt(this.i);
		
		// 如果遇到 '}' 闭括号, 则报告错误, 因为没有匹配的开始的 '{' ...
		if (ch == CH_CLOSE) { /* CH_CLOSE == '}' */
			if (noneOK) return null; // 允许无参数时, 这个 '}' 可能结束的是更外层的一个 '{'
			throw new RuntimeException("Extra close brace '}'");
		}
		
		// 如果遇到 '\' 转义符, 则读取一个命令(Control Sequence)
		if (ch == CH_CMD) { /* CH_CMD == '\\' */
			++this.i;
			return CH_CMD + this.GetCommand(); // 将 cs 字符串直接返回...?
		}
		
		// 如果遇到 '{' 则读取直到 '}', 此分组中内容做为整个参数返回.
		if (ch == CH_OPEN) { /* CH_OPEN == '{' */
			int j = ++this.i;
			int brace_count = 1;
			while (this.i < this.string.length()) {
				char c = this.string.charAt(this.i++);
				if (c == CH_CMD) 
					++this.i;
				else if (c == CH_OPEN)
					++brace_count; // '{' 使得嵌套层数+1
				else if (c == CH_CLOSE) {
					if (0 == brace_count) {
						// 过多的 }, 但是这不可能发生, 因为在 brace_count == 1 的时候已经结束了.
						// 所以js 原来的代码逻辑不可能.
						throw new RuntimeException("Extra close brace.");
					}
					--brace_count; // '}' 使得嵌套层数-1, 如果减到 0 表示这个组结束.
					if (brace_count == 0)
						return this.string.substring(j, this.i - 1);
				}
			}
			// 如果到了这里, 表示 } 的数量不足以匹配 {, 则报错...
			throw new RuntimeException("Missing close brace.");
		}
		
		// 其它字符都做为单个字符看待, 并做为 name 命令的参数.
		++this.i;
		return String.valueOf(ch);
	}
	
	/**
	 * 读取指定名字的定界符(将检查它是否在定界符列表中).
	 * @return
	 */
	private DelimInfo GetDelimiter(String name) {
		// 跳过 \left 等命令之后的空格. (疑问: 我们在 GetCommand() 的时候已经跳过了, 这里还有必要吗?
		skipSpace();
		
		String delim;
		// 读取后一个字符.
		char c = this.string.charAt(this.i);
		if (this.i < this.string.length()) {
			++this.i;
			// 如果 \left 之后下一个有效字符是 \, 则读取命令(如例子 \left\lbrace)
			if (c == CH_CMD) {
				delim = CH_CMD + this.GetCommand(); // c 也许该换个名字...
			}
			else {
				delim = String.valueOf(c);
			}
			// 如果 c 在定界符映射表(delimiter) 中, 则返回该定界符的定义...
			if (Parser.delimiter.containsKey(delim)) {
				return Parser.delimiter.get(delim);
			}
		}
		throw new RuntimeException("Missing or unrecognized delimiter for " + name);
	}
	
	/**
	 * 读取一个 dimen(尺寸)信息, 包括其单位. 转换为 em 单位...
	 * Get a dimension (including its units).
     *  Convert the dimen to em's, except for mu's, which must be
     *  converted when typeset.
	 * @return
	 * 
	 * @description
	 * 在 tex 中使用的 dimen 单位及其转换. 
	 *  pt point (baselines in this manual are 12 pt apart)
	 *	pc pica (1 pc = 12 pt)
	 *	in inch (1 in = 72:27 pt)
	 *	bp big point (72 bp = 1 in)
	 *	cm centimeter (2:54 cm = 1 in)
	 *	mm millimeter (10mm = 1 cm)
	 *	dd didot point (1157 dd = 1238 pt)
	 *	cc cicero (1 cc = 12 dd)
	 *	sp scaled point (65536 sp = 1 pt)
	 */
	private Dimen GetDimen(String name, Object nomu) {
		if (this.nextIsSpace()) ++this.i; // 跳过可能的空白.
		char ch = this.string.charAt(this.i);
		String rest;
		int advance = 0;
		if (ch == '{') {
			// 是一个分组, 则读取它.
			rest = this.GetArgument(name, false);
		}
		else {
			rest = this.string.substring(this.i);
			advance  =1;
		}
		
		// 解析 dimen, ...
		return this.ParseDimen(rest, name, advance, nomu);
	}
	
	/** 从 GetDimen() 中调用, 解析 dimen 语法. */
	private Dimen ParseDimen(String dimen, String name, int advance, Object nomu) {
		// 原 jsmath 使用正则表达式来解析 dimen 语法... 而我们是不是应该改用自己
		//  的解析器?? 大致的语法: [-+]?d+(.d*)? (pt|em|ex|mu|px)
		
		this.skipSpace();   // 跳过前面可能的空白.
		// 1. 读取可选的 -+ 符号.
		boolean signed = true;
		char ch = this.string.charAt(this.i);
		if (ch == '-') {
			signed = false; ++this.i;
		} else if (ch == '+') {
			signed = true; ++this.i;
		}
		
		// 跳过 +/- 之后的空白.
		this.skipSpace();
		
		// 2. 读取小数点(. 或 ,) 前面部分的数字.
		int dig = 0; // 小数点前面的数字.
		while (this.i < this.string.length()) {
			ch = this.string.charAt(this.i);
			if (ch <= '9' && ch >= '0') {
				dig = dig*10 + (ch - '0');
			} else
				break;
		}
		
		float fdig = 0.0f;
		// 读取可能的 '.' 或 ','
		if (this.i >= this.string.length()) return null;
		ch = this.string.charAt(this.i);
		if (ch == '.' || ch == ',') {
			// 当做小数点看待.
			++this.i;
			
			// TODO: 读取小数点后面部分.
		}
		
		// TODO: 读取后面可能的单位.
		this.skipSpace();
		
		// 组装为 Dimen 对象返回...
		return new Dimen();
	}
	
	
	/**
	 * 读取一个参数, 并处理(解析)该参数为一个 mList.
	 * Get an argument and process it into an mList
	 * @param name -- 命令字符串, 如 \vcenter, \frac (注意, 带有 \ 转义符)
	 * @return
	 */
	private Field ProcessArg(String name) {
		// 因此函数分为两个步骤: 1. 读取参数; 2. 解析参数字符串. 其中步骤2 实际上递归调用 Parse().
		String arg = this.GetArgument(name, false);
		
		// 2. 解析该字符串.
		Field result = this.Process(arg);
		
		return result;
	}
	
	/**
	 * 读取和处理给上下标使用的参数, 语法为 {atom}_{subscript}, {atom}^{superscript}
	 * 
	 * Get and process an argument for a super- or subscript.
     *  (read extra args for \frac, \sqrt, \mathrm, etc.)
     *  This handles these macros as special cases, so is really
     *  rather a hack.  A more <B>general</B> method for indicating
     *  how to handle macros in scripts needs to be developed.
     * 
     * 这里英文注释, 也许与其代码不符...
     * 
	 * @param name - 命令名字(应该只用于显示错误信息使用)
	 * @return
	 */
	private Field ProcessScriptArg(String name) {
		String arg = this.GetArgument(name, false);
		if (arg.charAt(0) == '\\') { // 第一个字符是 \, 表示这是一个 tex 命令.
			String csname = arg.substring(1); // 得到该命令.
			// 这里对 \frac, \sqrt 等命令 HACK 读取参数的方式, 似乎和实际 HandleCS() 中的部分有重叠/重复
			//   如果: 重复是魔鬼, 则 => 这里是魔鬼.
			if ("frac".equals(csname)) {
				// HACK: 如果是 \frac 命令, 则读取第二个参数, 因为 frac 命令有两个参数...
				arg += "{" + this.GetArgument(csname, false) + "}";
				arg += "{" + this.GetArgument(csname, false) + "}";
			}
			else if ("sqrt".equals(csname)) {
				// HACK: 如果是 \sqrt 命令, 则有一个可选的 [] 根次参数.
				arg += "[" + this.GetBrackets(csname) + "]";
				arg += "{" + this.GetArgument(csname, false) + "}";
			}
			else if (this.matchScriptags(csname)) {
				// 原js: if (csname.match(this.scriptargs))
				// scriptargs: /^((math|text)..|mathcal|[hm]box)$/,
				// 实际匹配这些命令: mathcal, mathrm, mathbf, mathbb, mathit, textrm, textit, textbf, hbox, mbox
				arg += "{" + this.GetArgument(csname, false) + "}";
			}
		}
		
		// 解释这个参数.
		Field atom = this.Process(arg);
		return atom;
	}
	
	/** 
	 * 模拟实现 js 中 csname.match(this.scriptargs)
	 * 其中 this.scriptargs = /^((math|text)..|mathcal|[hm]box)$/
	 * scriptags 的注释为: pattern for macros to ^ and _ that should be read with arguments
	 * 实际匹配这些 tex 命令: mathcal, mathrm, mathbf, mathbb, mathit, textrm, textit, textbf, hbox, mbox
	 */
	private boolean matchScriptags(String csname) {
		if (csname.startsWith("math") && csname.length() == 6) return true;
		if (csname.startsWith("text") && csname.length() == 6) return true;
		if ("mathcal".equals(csname)) return true;
		if ("hbox".equals(csname)) return true;
		if ("mbox".equals(csname)) return true;
		return false;
	}
	
	
	/**
	 * 读取下一个非空白的字符. 如果当前指针在字符串末尾则返回 -1.
	 *  Get the next non-space character
	 * @return
	 */
	private char GetNext() {
		if (this.i >= this.string.length()) 
			return (char)-1;
		while (this.nextIsSpace()) ++this.i;
		return this.string.charAt(this.i);
	}
	
	/**
	 * 读取 latex 中可选参数, 其在 [] 里面.
	 * Get an optional LaTeX argument in brackets
	 * @param name -- 为这个命令读取参数.
	 * @return 返回方括号 [] 里面的可选参数. (没有 tex 解析处理过)
	 */
	private String GetBrackets(String name) {
		char c = this.GetNext();
		if (c != '[') return ""; // 没有可选参数.
		int start = ++this.i;
		int pcount = 0;
		while (this.i < this.string.length()) {
			c = this.string.charAt(this.i++);
			if (c == '{') { ++pcount; }
			else if (c == '}') {
				if (pcount == 0)
					throw new RuntimeException("Extra close brace while looking for ']'");
				--pcount;
			}
			else if (c == '\\') {
				++this.i;
			}
			else if (c == ']') {
				if (pcount == 0)
					return this.string.substring(start, this.i - 1);
			}
		}
		throw new RuntimeException("Couldn't find closing ']' for argument to " + CH_CMD + name);
	}
	
	// 判断 this.string 的下一个字符是否为空白.
	private boolean nextIsSpace() {
		if (this.i >= this.string.length()) 
			return false; // 越界了...
		char ch = this.string.charAt(this.i);
		return isSpace(ch);
	}
	
	/**
	 * 跳过(可能命令之后有)空白...
	 */
	private void skipSpace() {
		// 跳过(可能命令之后有)空白...
		while (this.nextIsSpace()) { 
			++this.i; 
		}
	}
	
	/**
	 * 调用指定命令 cmd 对应的宏处理过程.
	 * @param cmd
	 */
	private void callMacroFunc(String cmd) {
		MacroFunction mf = macros.get(cmd);
		if (mf == null) 
			throw new InternalError("内部错误, 没有此命令就不该调用入此处函数.");
		mf.doMacro(this, cmd);
	}
	
	// === 辅助函数 ====================================================================
	
	/**
	 * 判断字符 c 是否是一个字母(letter). 在 jsMath 中用正则 `/[a-z]/i' 进行判断.
	 * @param c
	 * @return 返回 true 表示是一个字母, false 表示不是字母.
	 */
	private static boolean isLetter(char c) {
		if (c >= 'a' && c <= 'z') return true;
		if (c >= 'A' && c <= 'Z') return true;
		return false;
	}
	
	
	/**
	 * 判断字符 c 是否是一个数字(number). jsMath 中用正则 `/[0-9]/'.
	 */
	private static boolean isNumber(char c) {
		if (c >= '0' && c <= '9') return true;
		return false;
	}

	/**
	 * 判断字符 c 是否是一个空白(space). 在 tex 中, ' '(空格), tab(制表符), cr, lf 作为空白.
	 * @param c
	 * @return
	 */
	private static boolean isSpace(char c) {
		if (c == ' ' || c == '\t' || c == '\r' || c == '\n')
			return true;
		return false;
	}

	private static final String ascend_chars = "acegmnopqrsuvwxyz"; // 也即 [acegm-su-z]
	/**
	 * 计算所给字符串对应的 ascend 值. 本函数在 NamedOp() 实现中调用.
	 * @param cmd_name
	 * @return
	 */
	private static float calcAscend(String cmd_name) {
		// 在 jsMath 中计算方式为 var a = (name.match(/[^acegm-su-z]/)) ? 1: 0;
		// 其中 ^ace... 指所有大写字母, 和带有上面部分的小写字母. 在 ascend_chars 中给出.
		for (int i = 0; i < cmd_name.length(); ++i) {
			char ch = cmd_name.charAt(i);
			// 如果至少有一个不在该列表中的字符, 如 'Abd' 等字符, 则 ascend 必为 1, 且不用再扫描而直接返回即可.
			if (ascend_chars.indexOf(ch) == -1)
				return 1;
		}
		return 0; // 没有超出上面的字符...
	}
	
	private static final String descend_chars = "gjpqy";
	/**
	 * 计算所给字符串对应的 descend 值. 本函数在 NamedOp() 实现中调用.
	 * @param cmd_name
	 * @return
	 */
	private static float calcDescend(String cmd_name) {
		// 在 jsMath 中计算方式为 var d = (name.match(/[gjpqy]/)) ? .2: 0;
		// 其语义就是小写字母的 gjpqy 下面超出部分有 descend.
		for (int i = 0; i < cmd_name.length(); ++i) {
			char ch = cmd_name.charAt(i);
			// 如果有至少一个, 则有 descend, 并返回.
			if (descend_chars.indexOf(ch) >= 0)
				return 0.2f;
		}
		return 0;
	}
}
