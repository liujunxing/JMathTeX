package jsmath;

/**
 * 表示 TeX 中字体的信息. 对应 jsMath.TeX
 * The TeX font information
 *
 */
public class CTeX {
	// jmathtex 将这些配置写在外部文件中的.
	
	// The TeX font parameters; 这些参数应该描述在 TeX Book 附录中(epsilon~X, tao~X)
	public static final float thinmuskip = 3.0f/18f;
	public static final float medmuskip = 4.0f/18f;
	public static final float thickmuskip = 5.0f/18f;
	
	public static final float x_height = 0.430554f;
	public static final float quad = 1.0f;
	public static final float num1 = 0.676508f;
	public static final float num2 = 0.393732f;
	public static final float num3 = 0.44373f;
	public static final float denom1 = 0.685951f;
	public static final float denom2 = 0.344841f;
	public static final float sup1 = 0.412892f;
	public static final float sup2 = 0.362893f;
	public static final float sup3 = 0.288888f;
	public static final float sub1 = 0.15f;
	public static final float sub2 = 0.247217f;
	public static final float sup_drop = 0.386108f;
	public static final float sub_drop = 0.05f;
	public static final float delim1 = 2.39f;
	public static final float delim2 = 1.0f;
	public static final float axis_height = 0.25f;
	public static final float default_rule_thickness = 0.06f;
	public static final float big_op_spacing1 = 0.111111f;
	public static final float big_op_spacing2 = 0.166666f;
	public static final float big_op_spacing3 =  0.2f;
	public static final float big_op_spacing4 =  0.6f;
	public static final float big_op_spacing5 =  0.1f;

	// conversion of em's to TeX internal integer
	public static final float integer =          6553.6f;
	public static final float scriptspace =        0.05f;
	public static final float nulldelimiterspace =  0.12f;
	public static final int delimiterfactor =     901;
	public static final float delimitershortfall =   0.5f;
	// scaling factor for font dimensions
	public static final float scale =                1f;

	// The TeX math atom types (see Appendix G of the TeXBook)
	public static final String[] atom = {
	//   0      1     2      3      4       5        6        7
		"ord", "op", "bin", "rel", "open", "close", "punct", "ord" // 7 也被当做 "ord" 看待.
	};
	
	// The TeX font families
	public static final String[] fam = {
		"cmr10", "cmmi10", "cmsy10", "cmex10", "cmti10", "", "cmbx10", ""
	};
	
	public String[] getFam() { return fam; }
	
	// 从名字到索引值的映射. famName: {cmr10:0, cmmi10:1, cmsy10:2, cmex10:3, cmti10:4, cmbx10:6},
	public static final FamNameMap famName = new FamNameMap();

	/**
	 * 根据字体名字(如 cmr10) 得到其对应的字体对象(FontInfo).
	 * @param name
	 * @return
	 */
	public FontInfo getFontByName(String name) {
		if ("cmr10".equals(name)) 
			return cmr10;
		else if ("cmmi10".equals(name))
			return cmmi10;
		else if ("cmsy10".equals(name))
			return cmsy10;
		else if ("cmex10".equals(name))
			return cmex10;
		
		// TODO: 更多字体
		return null;
	}
	
	/** Encoding used by jsMath fonts */
	public static final String[] encoding = new String[] {
	    "&#xC0;", "&#xC1;", "&#xC2;", "&#xC3;", "&#xC4;", "&#xC5;", "&#xC6;", "&#xC7;",
	    "&#xC8;", "&#xC9;", "&#xCA;", "&#xCB;", "&#xCC;", "&#xCD;", "&#xCE;", "&#xCF;",

	    "&#xB0;", "&#xD1;", "&#xD2;", "&#xD3;", "&#xD4;", "&#xD5;", "&#xD6;", "&#xB7;",
	    "&#xD8;", "&#xD9;", "&#xDA;", "&#xDB;", "&#xDC;", "&#xB5;", "&#xB6;", "&#xDF;",

	    "&#xEF;", "!", "&#x22;", "#", "$", "%", "&#x26;", "&#x27;",
	    "(", ")", "*", "+", ",", "-", ".", "/",

	    "0", "1", "2", "3", "4", "5", "6", "7",
	    "8", "9", ":", ";", "&#x3C;", "=", "&#x3E;", "?",

	    "@", "A", "B", "C", "D", "E", "F", "G",
	    "H", "I", "J", "K", "L", "M", "N", "O",

	    "P", "Q", "R", "S", "T", "U", "V", "W",
	    "X", "Y", "Z", "[", "&#x5C;", "]", "^", "_",

	    "`", "a", "b", "c", "d", "e", "f", "g",
	    "h", "i", "j", "k", "l", "m", "n", "o",

	    "p", "q", "r", "s", "t", "u", "v", "w",
	    "x", "y", "z", "{", "|", "}", "&#x7E;", "&#xFF;"
	};
	
	// 下面关于 cmr10, cmmi10 等字体的详细 metrics,ic,kern,lig 数据, 我们放程序里面还是放外面??
	// 放程序里面显然是方便编程. 其实也没必要放外面有人配置? 因为都是程序负责生成的......
	public static final FontInfo cmr10 = init_cmr10();
	// 里面构造 cmr10 等字体的代码是由 javascript 自动生成的, 代码见 
	//   crm10.html (可能略有改动...)
	private static FontInfo init_cmr10() {
		// 临时变量.
		FontInfo font = new FontInfo(); // cmr10 别名.
		font.name = "cmr10";
		int cc_begin = 0;
		KernInfo[] k;
		LigInfo[] l;
		@SuppressWarnings("unused") FontCharInfo c; // 现在暂时不用, 以后用.
		
		// 以下代码为自动生成的.
		font.setAdd(c = new FontCharInfo(cc_begin + 0, 0.5642f, 0.8866f, 0.2418f, 0f, "&Gamma;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 1, 0.6448f, 0.8866f, 0.2418f, 0f, "&Delta;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 2, 0.7254f, 0.8866f, 0.2418f, 0f, "&Theta;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 3, 0.7254f, 0.8866f, 0.2418f, 0f, "&Lambda;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 4, 0.6448f, 0.8866f, 0.2418f, 0f, "&Xi;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 5, 0.7254f, 0.8866f, 0.2418f, 0f, "&Pi;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 6, 0.6045f, 0.8866f, 0.2418f, 0f, "&Sigma;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 7, 0.7254f, 0.8866f, 0.2418f, 0f, "&Upsilon;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 8, 0.7254f, 0.8866f, 0.2418f, 0f, "&Phi;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 9, 0.7254f, 0.8866f, 0.2418f, 0f, "&Psi;", "greek"));
		font.setAdd(c = new FontCharInfo(cc_begin + 10, 0.7657f, 0.8866f, 0.2418f, 0f, "&Omega;", "greek"));
		k = new KernInfo[] { new KernInfo(33, 0.0778f), new KernInfo(39, 0.0778f), new KernInfo(41, 0.0778f), new KernInfo(63, 0.0778f), new KernInfo(93, 0.0778f) };
		l = new LigInfo[] { new LigInfo(105, 14), new LigInfo(108, 15) };
		font.setAdd(c = new FontCharInfo(cc_begin + 11, 0.7657f, 0.8866f, 0.1209f, 0.0778f, "ff", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 12, 0.6851f, 0.8866f, 0.1209f, 0f, "fi", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 13, 0.6851f, 0.8866f, 0.1209f, 0f, "fl", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 14, 1.0478f, 0.8866f, 0.1209f, 0f, "ffi", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 15, 1.0478f, 0.8866f, 0.1209f, 0f, "ffl", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 16, 0.2821f, 0.8866f, 0.1209f, 0f, "&#x131;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 17, 0.2821f, 0.8866f, 0.1209f, 0f, "j", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 18, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2CB;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 19, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2CA;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 20, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2C7;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 21, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2D8;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 22, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2C9;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 23, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2DA;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 24, 0.0000f, 0.8866f, 0.1209f, 0f, "&#x0327;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 25, 0.6045f, 0.8866f, 0.1209f, 0f, "&#xDF;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 26, 0.8463f, 0.8866f, 0.1209f, 0f, "&#xE6;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 27, 0.8463f, 0.8866f, 0.1209f, 0f, "&#x153;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 28, 0.5239f, 0.8866f, 0.1209f, 0f, "&#xF8;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 29, 1.0075f, 0.8866f, 0.1209f, 0f, "&#xC6;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 30, 0.9269f, 0.8866f, 0.1209f, 0f, "&#x152;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 31, 0.6851f, 0.8866f, 0.1209f, 0f, "&#xD8;", "normal"));
		k = new KernInfo[] { new KernInfo(76, -0.319f), new KernInfo(108, -0.278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 32, 0.5239f, 0.8866f, 0.1209f, 0f, "?", "normal", k, l));
		k = null;
		l = new LigInfo[] { new LigInfo(96, 60) };
		font.setAdd(c = new FontCharInfo(cc_begin + 33, 0.3224f, 0.8866f, 0.1209f, 0f, "!", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 34, 0.5239f, 0.8866f, 0.1209f, 0f, "&#x201D;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 35, 0.6045f, 0.8866f, 0.1209f, 0f, "#", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 36, 0.5642f, 0.8866f, 0.1209f, 0f, "$", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 37, 0.8463f, 0.8866f, 0.1209f, 0f, "%", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 38, 0.8463f, 0.8866f, 0.1209f, 0f, "&amp;", "normal"));
		k = new KernInfo[] { new KernInfo(33, 0.111f), new KernInfo(63, 0.111f) };
		l = new LigInfo[] { new LigInfo(39, 34) };
		font.setAdd(c = new FontCharInfo(cc_begin + 39, 0.2821f, 0.8866f, 0.1209f, 0f, "&#x2019;", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 40, 0.3627f, 0.8866f, 0.1209f, 0f, "(", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 41, 0.3627f, 0.8866f, 0.1209f, 0f, ")", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 42, 0.5239f, 0.8866f, 0.1209f, 0f, "*", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 43, 0.8463f, 0.8866f, 0.1209f, 0f, "+", "italic")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 44, 0.2821f, 0.8866f, 0.1209f, 0f, ",", "normal")); c.w = 0.278f; c.d = 0.2f; c.a = -0.3f;
		k = null;
		l = new LigInfo[] { new LigInfo(45, 123) };
		font.setAdd(c = new FontCharInfo(cc_begin + 45, 0.6045f, 0.8866f, 0.1209f, 0f, "-", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 46, 0.2821f, 0.8866f, 0.1209f, 0f, ".", "normal")); c.a = -0.25f;
		font.setAdd(c = new FontCharInfo(cc_begin + 47, 0.3627f, 0.8866f, 0.1209f, 0f, "/", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 48, 0.6045f, 0.8866f, 0.1209f, 0f, "0", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 49, 0.6045f, 0.8866f, 0.1209f, 0f, "1", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 50, 0.6045f, 0.8866f, 0.1209f, 0f, "2", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 51, 0.6045f, 0.8866f, 0.1209f, 0f, "3", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 52, 0.6045f, 0.8866f, 0.1209f, 0f, "4", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 53, 0.6045f, 0.8866f, 0.1209f, 0f, "5", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 54, 0.6045f, 0.8866f, 0.1209f, 0f, "6", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 55, 0.6045f, 0.8866f, 0.1209f, 0f, "7", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 56, 0.6045f, 0.8866f, 0.1209f, 0f, "8", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 57, 0.6045f, 0.8866f, 0.1209f, 0f, "9", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 58, 0.3224f, 0.8866f, 0.1209f, 0f, ":", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 59, 0.3224f, 0.8866f, 0.1209f, 0f, ";", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 60, 0.4030f, 0.8866f, 0.1209f, 0f, "&#xA1;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 61, 0.6045f, 0.8866f, 0.1209f, 0f, "=", "normal")); c.d = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 62, 0.5239f, 0.8866f, 0.1209f, 0f, "&#xBF;", "normal"));
		k = null;
		l = new LigInfo[] { new LigInfo(96, 62) };
		font.setAdd(c = new FontCharInfo(cc_begin + 63, 0.5239f, 0.8866f, 0.1209f, 0f, "?", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 64, 1.0075f, 0.8866f, 0.1209f, 0f, "@", "normal"));
		k = new KernInfo[] { new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f), new KernInfo(84, -0.0833f), new KernInfo(85, -0.0278f), new KernInfo(86, -0.111f), new KernInfo(87, -0.111f), new KernInfo(89, -0.0833f), new KernInfo(116, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 65, 0.7254f, 0.8866f, 0.1209f, 0f, "A", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 66, 0.6851f, 0.8866f, 0.1209f, 0f, "B", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 67, 0.6851f, 0.8866f, 0.1209f, 0f, "C", "normal"));
		k = new KernInfo[] { new KernInfo(65, -0.0278f), new KernInfo(86, -0.0278f), new KernInfo(87, -0.0278f), new KernInfo(88, -0.0278f), new KernInfo(89, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 68, 0.7254f, 0.8866f, 0.1209f, 0f, "D", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 69, 0.6851f, 0.8866f, 0.1209f, 0f, "E", "normal"));
		k = new KernInfo[] { new KernInfo(65, -0.111f), new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f), new KernInfo(97, -0.0833f), new KernInfo(101, -0.0833f), new KernInfo(111, -0.0833f), new KernInfo(114, -0.0833f), new KernInfo(117, -0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 70, 0.6448f, 0.8866f, 0.1209f, 0f, "F", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 71, 0.7254f, 0.8866f, 0.1209f, 0f, "G", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 72, 0.7657f, 0.8866f, 0.1209f, 0f, "H", "normal"));
		k = new KernInfo[] { new KernInfo(73, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 73, 0.3224f, 0.8866f, 0.1209f, 0f, "I", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 74, 0.4433f, 0.8866f, 0.1209f, 0f, "J", "normal"));
		k = new KernInfo[] { new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 75, 0.7657f, 0.8866f, 0.1209f, 0f, "K", "normal", k, l));
		k = new KernInfo[] { new KernInfo(84, -0.0833f), new KernInfo(86, -0.111f), new KernInfo(87, -0.111f), new KernInfo(89, -0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 76, 0.6448f, 0.8866f, 0.1209f, 0f, "L", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 77, 0.8866f, 0.8866f, 0.1209f, 0f, "M", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 78, 0.7657f, 0.8866f, 0.1209f, 0f, "N", "normal"));
		k = new KernInfo[] { new KernInfo(65, -0.0278f), new KernInfo(86, -0.0278f), new KernInfo(87, -0.0278f), new KernInfo(88, -0.0278f), new KernInfo(89, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 79, 0.7254f, 0.8866f, 0.1209f, 0f, "O", "normal", k, l));
		k = new KernInfo[] { new KernInfo(44, -0.0833f), new KernInfo(46, -0.0833f), new KernInfo(65, -0.0833f), new KernInfo(97, -0.0278f), new KernInfo(101, -0.0278f), new KernInfo(111, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 80, 0.6448f, 0.8866f, 0.1209f, 0f, "P", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 81, 0.7254f, 0.8866f, 0.1209f, 0f, "Q", "normal")); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f), new KernInfo(84, -0.0833f), new KernInfo(85, -0.0278f), new KernInfo(86, -0.111f), new KernInfo(87, -0.111f), new KernInfo(89, -0.0833f), new KernInfo(116, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 82, 0.6851f, 0.8866f, 0.1209f, 0f, "R", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 83, 0.6045f, 0.8866f, 0.1209f, 0f, "S", "normal"));
		k = new KernInfo[] { new KernInfo(65, -0.0833f), new KernInfo(97, -0.0833f), new KernInfo(101, -0.0833f), new KernInfo(111, -0.0833f), new KernInfo(114, -0.0833f), new KernInfo(117, -0.0833f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 84, 0.7657f, 0.8866f, 0.1209f, 0f, "T", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 85, 0.7657f, 0.8866f, 0.1209f, 0f, "U", "normal"));
		k = new KernInfo[] { new KernInfo(65, -0.111f), new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f), new KernInfo(97, -0.0833f), new KernInfo(101, -0.0833f), new KernInfo(111, -0.0833f), new KernInfo(114, -0.0833f), new KernInfo(117, -0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 86, 0.7657f, 0.8866f, 0.1209f, 0.0139f, "V", "normal", k, l));
		k = new KernInfo[] { new KernInfo(65, -0.111f), new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f), new KernInfo(97, -0.0833f), new KernInfo(101, -0.0833f), new KernInfo(111, -0.0833f), new KernInfo(114, -0.0833f), new KernInfo(117, -0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 87, 0.9269f, 0.8866f, 0.1209f, 0.0139f, "W", "normal", k, l));
		k = new KernInfo[] { new KernInfo(67, -0.0278f), new KernInfo(71, -0.0278f), new KernInfo(79, -0.0278f), new KernInfo(81, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 88, 0.6851f, 0.8866f, 0.1209f, 0f, "X", "normal", k, l));
		k = new KernInfo[] { new KernInfo(65, -0.0833f), new KernInfo(97, -0.0833f), new KernInfo(101, -0.0833f), new KernInfo(111, -0.0833f), new KernInfo(114, -0.0833f), new KernInfo(117, -0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 89, 0.6851f, 0.8866f, 0.1209f, 0.025f, "Y", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 90, 0.6045f, 0.8866f, 0.1209f, 0f, "Z", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 91, 0.5239f, 0.8866f, 0.1209f, 0f, "[", "normal")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 92, 0.5239f, 0.8866f, 0.1209f, 0f, "&#x201C;", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 93, 0.5239f, 0.8866f, 0.1209f, 0f, "]", "normal")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 94, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2C6;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 95, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2D9;", "accent"));
		k = null;
		l = new LigInfo[] { new LigInfo(96, 92) };
		font.setAdd(c = new FontCharInfo(cc_begin + 96, 0.2821f, 0.8866f, 0.1209f, 0f, "&#x2018;", "normal", k, l));
		k = new KernInfo[] { new KernInfo(106, 0.0556f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 97, 0.5239f, 0.8866f, 0.1209f, 0f, "a", "normal", k, l));
		k = new KernInfo[] { new KernInfo(99, 0.0278f), new KernInfo(100, 0.0278f), new KernInfo(101, 0.0278f), new KernInfo(106, 0.0556f), new KernInfo(111, 0.0278f), new KernInfo(113, 0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(120, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 98, 0.5642f, 0.8866f, 0.1209f, 0f, "b", "normal", k, l));
		k = new KernInfo[] { new KernInfo(104, -0.0278f), new KernInfo(107, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 99, 0.5239f, 0.8866f, 0.1209f, 0f, "c", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 100, 0.5642f, 0.8866f, 0.1209f, 0f, "d", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 101, 0.5642f, 0.8866f, 0.1209f, 0f, "e", "normal"));
		k = new KernInfo[] { new KernInfo(33, 0.0778f), new KernInfo(39, 0.0778f), new KernInfo(41, 0.0778f), new KernInfo(63, 0.0778f), new KernInfo(93, 0.0778f) };
		l = new LigInfo[] { new LigInfo(102, 11), new LigInfo(105, 12), new LigInfo(108, 13) };
		font.setAdd(c = new FontCharInfo(cc_begin + 102, 0.3627f, 0.8866f, 0.1209f, 0.0778f, "f", "normal", k, l));
		k = new KernInfo[] { new KernInfo(106, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 103, 0.6045f, 0.8866f, 0.1209f, 0.0139f, "g", "normal", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(98, -0.0278f), new KernInfo(116, -0.0278f), new KernInfo(117, -0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 104, 0.5642f, 0.8866f, 0.1209f, 0f, "h", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 105, 0.2821f, 0.8866f, 0.1209f, 0f, "i", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 106, 0.2821f, 0.8866f, 0.1209f, 0f, "j", "normal")); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(97, -0.0278f), new KernInfo(99, -0.0278f), new KernInfo(101, -0.0278f), new KernInfo(111, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 107, 0.5642f, 0.8866f, 0.1209f, 0f, "k", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 108, 0.2821f, 0.8866f, 0.1209f, 0f, "l", "normal"));
		k = new KernInfo[] { new KernInfo(98, -0.0278f), new KernInfo(116, -0.0278f), new KernInfo(117, -0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 109, 0.8463f, 0.8866f, 0.1209f, 0f, "m", "normal", k, l));
		k = new KernInfo[] { new KernInfo(98, -0.0278f), new KernInfo(116, -0.0278f), new KernInfo(117, -0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 110, 0.5642f, 0.8866f, 0.1209f, 0f, "n", "normal", k, l));
		k = new KernInfo[] { new KernInfo(99, 0.0278f), new KernInfo(100, 0.0278f), new KernInfo(101, 0.0278f), new KernInfo(106, 0.0556f), new KernInfo(111, 0.0278f), new KernInfo(113, 0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(120, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 111, 0.6045f, 0.8866f, 0.1209f, 0f, "o", "normal", k, l));
		k = new KernInfo[] { new KernInfo(99, 0.0278f), new KernInfo(100, 0.0278f), new KernInfo(101, 0.0278f), new KernInfo(106, 0.0556f), new KernInfo(111, 0.0278f), new KernInfo(113, 0.0278f), new KernInfo(118, -0.0278f), new KernInfo(119, -0.0278f), new KernInfo(120, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 112, 0.5642f, 0.8866f, 0.1209f, 0f, "p", "normal", k, l)); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 113, 0.5642f, 0.8866f, 0.1209f, 0f, "q", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 114, 0.4433f, 0.8866f, 0.1209f, 0f, "r", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 115, 0.5239f, 0.8866f, 0.1209f, 0f, "s", "normal"));
		k = new KernInfo[] { new KernInfo(119, -0.0278f), new KernInfo(121, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 116, 0.3627f, 0.8866f, 0.1209f, 0f, "t", "normal", k, l));
		k = new KernInfo[] { new KernInfo(119, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 117, 0.5642f, 0.8866f, 0.1209f, 0f, "u", "normal", k, l));
		k = new KernInfo[] { new KernInfo(97, -0.0278f), new KernInfo(99, -0.0278f), new KernInfo(101, -0.0278f), new KernInfo(111, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 118, 0.5642f, 0.8866f, 0.1209f, 0.0139f, "v", "normal", k, l));
		k = new KernInfo[] { new KernInfo(97, -0.0278f), new KernInfo(99, -0.0278f), new KernInfo(101, -0.0278f), new KernInfo(111, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 119, 0.8060f, 0.8866f, 0.1209f, 0.0139f, "w", "normal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 120, 0.6045f, 0.8866f, 0.1209f, 0f, "x", "normal"));
		k = new KernInfo[] { new KernInfo(44, -0.0833f), new KernInfo(46, -0.0833f), new KernInfo(97, -0.0278f), new KernInfo(101, -0.0278f), new KernInfo(111, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 121, 0.6045f, 0.8866f, 0.1209f, 0.0139f, "y", "normal", k, l)); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 122, 0.5239f, 0.8866f, 0.1209f, 0f, "z", "normal"));
		k = null;
		l = new LigInfo[] { new LigInfo(45, 124) };
		font.setAdd(c = new FontCharInfo(cc_begin + 123, 0.5239f, 0.8866f, 0.1209f, 0.0278f, "&#x2013;", "normal", k, l)); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 124, 1.0075f, 0.8866f, 0.1209f, 0.0278f, "&#x2014;", "normal")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 125, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2DD;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 126, 0.3224f, 1.0478f, 0.2821f, 0f, "&#x2DC;", "accent"));
		font.setAdd(c = new FontCharInfo(cc_begin + 127, 0.3224f, 1.0478f, 0.2821f, 0f, "&#xA8;", "accent"));
		
		
		return font;
	}
	
	public static final FontInfo cmmi10 = init_cmmi10();
	// 初始化 cmmi10 的数据.
	private static FontInfo init_cmmi10() {
		// 临时变量.
		FontInfo font = new FontInfo(); // cmr10 别名.
		font.name = "cmmi10";
		int cc_begin = 0;
		KernInfo[] k;
		LigInfo[] l;
		@SuppressWarnings("unused") FontCharInfo c;
		
		// 以下代码为自动生成的.
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 0, 0.8036f, 0.9107f, 0.2679f, 0.139f, "&Gamma;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 1, 0.8036f, 0.9107f, 0.2679f, 0f, "&Delta;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 2, 0.9643f, 0.9107f, 0.2679f, 0.0278f, "&Theta;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 3, 0.8571f, 0.9107f, 0.2679f, 0f, "&Lambda;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 4, 0.8571f, 0.9107f, 0.2679f, 0.0757f, "&Xi;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 5, 0.9643f, 0.9107f, 0.2679f, 0.0812f, "&Pi;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 6, 0.8571f, 0.9107f, 0.2679f, 0.0576f, "&Sigma;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 7, 0.7500f, 0.9107f, 0.2679f, 0.139f, "&Upsilon;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 8, 0.9643f, 0.9107f, 0.2679f, 0f, "&Phi;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 9, 0.9643f, 0.9107f, 0.2679f, 0.11f, "&Psi;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 10, 0.9643f, 0.9107f, 0.2679f, 0.0502f, "&Omega;", "igreek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 11, 0.5357f, 0.9107f, 0.2679f, 0.0037f, "&alpha;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 12, 0.4821f, 0.9107f, 0.2679f, 0.0528f, "&beta;", "greek", k, l)); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 13, 0.4286f, 0.9107f, 0.2679f, 0.0556f, "&gamma;", "greek")); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 14, 0.4821f, 0.9107f, 0.2679f, 0.0378f, "&delta;", "greek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 15, 0.5357f, 1.0179f, 0.2143f, 0f, "&epsilon;", "lucida", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 16, 0.4286f, 0.9107f, 0.2679f, 0.0738f, "&zeta;", "greek", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 17, 0.5357f, 0.9107f, 0.2679f, 0.0359f, "&eta;", "greek", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 18, 0.4821f, 0.9107f, 0.2679f, 0.0278f, "&theta;", "greek", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 19, 0.2679f, 0.9107f, 0.2679f, 0f, "&iota;", "greek", k, l)); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 20, 0.4821f, 0.9107f, 0.2679f, 0f, "&kappa;", "greek")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 21, 0.4821f, 0.9107f, 0.2679f, 0f, "&lambda;", "greek"));
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 22, 0.5357f, 0.9107f, 0.2679f, 0f, "&mu;", "greek", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 23, 0.4821f, 0.9107f, 0.2679f, 0.0637f, "&nu;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 24, 0.4286f, 0.9107f, 0.2679f, 0.046f, "&xi;", "greek", k, l)); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 25, 0.4821f, 0.9107f, 0.2679f, 0.0359f, "&pi;", "greek")); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 26, 0.4821f, 0.9107f, 0.2679f, 0f, "&rho;", "greek", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 27, 0.5357f, 0.9107f, 0.2679f, 0.0359f, "&sigma;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 28, 0.3750f, 0.9107f, 0.2679f, 0.113f, "&tau;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 29, 0.4821f, 0.9107f, 0.2679f, 0.0359f, "&upsilon;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 30, 0.5893f, 0.9107f, 0.2679f, 0f, "&phi;", "greek", k, l)); c.d = 0.2f; c.a = 0.1f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 31, 0.4286f, 0.9107f, 0.2679f, 0f, "&chi;", "greek", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 32, 0.6429f, 0.9107f, 0.2679f, 0.0359f, "&psi;", "greek", k, l)); c.d = 0.2f; c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 33, 0.6429f, 0.9107f, 0.2679f, 0.0359f, "&omega;", "greek")); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 34, 0.4286f, 0.9107f, 0.2679f, 0f, "&epsilon;", "greek", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 35, 0.5893f, 1.0179f, 0.2143f, 0f, "&#x3D1;", "lucida", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 36, 0.9107f, 1.0179f, 0.2143f, 0.0278f, "&#x3D6;", "lucida")); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 37, 0.5893f, 1.0179f, 0.2143f, 0f, "&#x3F1;", "lucida", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 38, 0.5893f, 1.0179f, 0.2143f, 0.0799f, "&#x3C2;", "lucida", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 39, 0.8571f, 1.0179f, 0.2143f, 0f, "&#x3D5;", "lucida", k, l)); c.d = 0.2f; c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 40, 0.6964f, 1.0714f, 0.2679f, 0f, "&#x21BC;", "arrows")); c.d = -0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 41, 0.6964f, 1.0714f, 0.2679f, 0f, "&#x21BD;", "arrows")); c.d = -0.1f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 42, 0.6964f, 1.0714f, 0.2679f, 0f, "&#x21C0;", "arrows")); c.d = -0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 43, 0.6964f, 1.0714f, 0.2679f, 0f, "&#x21C1;", "arrows")); c.d = -0.1f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 44, 0.2143f, 1.0714f, 0.2679f, 0f, "<span style=\"position:relative; top:-.1em; display:inline-block\">&#x02D3;</span>", "symbol")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 45, 0.2143f, 1.0714f, 0.2679f, 0f, "<span style=\"position:relative; top:-.1em; display:inline-block\">&#x02D2;</span>", "symbol")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 46, 0.5893f, 1.0714f, 0.2679f, 0f, "&#x25B9;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 47, 0.5893f, 1.0714f, 0.2679f, 0f, "&#x25C3;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 48, 0.5893f, 0.8571f, 0.1607f, 0f, "0", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 49, 0.5893f, 0.8571f, 0.1607f, 0f, "1", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 50, 0.5893f, 0.8571f, 0.1607f, 0f, "2", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 51, 0.5893f, 0.8571f, 0.1607f, 0f, "3", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 52, 0.5893f, 0.8571f, 0.1607f, 0f, "4", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 53, 0.5893f, 0.8571f, 0.1607f, 0f, "5", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 54, 0.5893f, 0.8571f, 0.1607f, 0f, "6", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 55, 0.5893f, 0.8571f, 0.1607f, 0f, "7", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 56, 0.5893f, 0.8571f, 0.1607f, 0f, "8", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 57, 0.5893f, 0.8571f, 0.1607f, 0f, "9", "normal"));
		font.setAdd(c = new FontCharInfo(cc_begin + 58, 0.2679f, 0.8571f, 0.1607f, 0f, ".", "normal")); c.a = -0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 59, 0.2679f, 0.8571f, 0.1607f, 0f, ",", "normal")); c.d = 0.2f; c.a = -0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 60, 0.6429f, 0.8571f, 0.1607f, 0f, "&lt;", "normal")); c.a = 0.1f;
		k = new KernInfo[] { new KernInfo(1, -0.0556f), new KernInfo(65, -0.0556f), new KernInfo(77, -0.0556f), new KernInfo(78, -0.0556f), new KernInfo(89, 0.0556f), new KernInfo(90, -0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 61, 0.4821f, 1.1786f, 0.1607f, 0f, "<span style=\"font-size:133%; position:relative; top:.1em; display:inline-block\">/</span>", "normal", k, l)); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 62, 0.6429f, 0.8571f, 0.1607f, 0f, "&gt;", "normal")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 63, 0.5357f, 1.0714f, 0.2679f, 0f, "&#x22C6;", "arial")); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 64, 0.4821f, 0.8571f, 0.1607f, 0.0556f, "&#x2202;", "normal", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 65, 0.8571f, 0.9107f, 0.2679f, 0f, "A", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 66, 0.8571f, 0.9107f, 0.2679f, 0.0502f, "B", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0278f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 67, 0.9107f, 0.9107f, 0.2679f, 0.0715f, "C", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 68, 0.9643f, 0.9107f, 0.2679f, 0.0278f, "D", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 69, 0.8571f, 0.9107f, 0.2679f, 0.0576f, "E", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 70, 0.8036f, 0.9107f, 0.2679f, 0.139f, "F", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 71, 0.9643f, 0.9107f, 0.2679f, 0f, "G", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 72, 0.9643f, 0.9107f, 0.2679f, 0.0812f, "H", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 73, 0.5357f, 0.9107f, 0.2679f, 0.0785f, "I", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 74, 0.6429f, 0.9107f, 0.2679f, 0.0962f, "J", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 75, 0.8571f, 0.9107f, 0.2679f, 0.0715f, "K", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 76, 0.8036f, 0.9107f, 0.2679f, 0f, "L", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 77, 1.0714f, 0.9107f, 0.2679f, 0.109f, "M", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0278f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 78, 0.9107f, 0.9107f, 0.2679f, 0.109f, "N", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 79, 0.9643f, 0.9107f, 0.2679f, 0.0278f, "O", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 80, 0.8036f, 0.9107f, 0.2679f, 0.139f, "P", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 81, 0.9643f, 0.9107f, 0.2679f, 0f, "Q", "italic", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 82, 0.8571f, 0.9107f, 0.2679f, 0.00773f, "R", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 83, 0.7500f, 0.9107f, 0.2679f, 0.0576f, "S", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0278f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 84, 0.8036f, 0.9107f, 0.2679f, 0.139f, "T", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.111f), new KernInfo(59, -0.111f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 85, 0.9643f, 0.9107f, 0.2679f, 0.109f, "U", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.167f), new KernInfo(59, -0.167f), new KernInfo(61, -0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 86, 0.8571f, 0.9107f, 0.2679f, 0.222f, "V", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.167f), new KernInfo(59, -0.167f), new KernInfo(61, -0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 87, 1.0179f, 0.9107f, 0.2679f, 0.139f, "W", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0278f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 88, 0.8571f, 0.9107f, 0.2679f, 0.0785f, "X", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.167f), new KernInfo(59, -0.167f), new KernInfo(61, -0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 89, 0.7500f, 0.9107f, 0.2679f, 0.222f, "Y", "italic", k, l));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(61, -0.0556f), new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 90, 0.8036f, 0.9107f, 0.2679f, 0.0715f, "Z", "italic", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 91, 0.4286f, 1.0714f, 0.2679f, 0f, "&#x266D;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 92, 0.4286f, 1.0714f, 0.2679f, 0f, "&#x266E;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 93, 0.4286f, 1.0714f, 0.2679f, 0f, "&#x266F;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 94, 0.6964f, 0.8036f, 0.2143f, 0f, "<span style=\"position:relative; top:-.3em; font-size:75%; display:inline-block\">&#x203F;</span>", "arial")); c.d = -0.1f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 95, 0.6964f, 0.8036f, 0.2143f, 0f, "<span style=\"position:relative; top:.4em; font-size:75%; display:inline-block\">&#x2040;</span>", "arial")); c.d = -0.1f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 96, 0.6964f, 0.8571f, 0.2679f, 0f, "&#x2113;", "italic", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 97, 0.7500f, 0.9107f, 0.2679f, 0f, "a", "italic")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 98, 0.6964f, 0.9107f, 0.2679f, 0f, "b", "italic"));
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 99, 0.6429f, 0.9107f, 0.2679f, 0f, "c", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(89, 0.0556f), new KernInfo(90, -0.0556f), new KernInfo(102, -0.167f), new KernInfo(106, -0.111f), new KernInfo(127, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 100, 0.7500f, 0.9107f, 0.2679f, 0f, "d", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 101, 0.6429f, 0.9107f, 0.2679f, 0f, "e", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(127, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 102, 0.5357f, 0.9107f, 0.2679f, 0.108f, "f", "italic", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 103, 0.7500f, 0.9107f, 0.2679f, 0.0359f, "g", "italic", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, -0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 104, 0.7500f, 0.9107f, 0.2679f, 0f, "h", "italic", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 105, 0.5357f, 0.9107f, 0.2679f, 0f, "i", "italic"));
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 106, 0.4821f, 0.9107f, 0.2679f, 0.0572f, "j", "italic", k, l)); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 107, 0.6429f, 0.9107f, 0.2679f, 0.0315f, "k", "italic"));
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 108, 0.5357f, 0.9107f, 0.2679f, 0.0197f, "l", "italic", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 109, 0.9643f, 0.9107f, 0.2679f, 0f, "m", "italic")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 110, 0.7500f, 0.9107f, 0.2679f, 0f, "n", "italic")); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 111, 0.7500f, 0.9107f, 0.2679f, 0f, "o", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 112, 0.7500f, 0.9107f, 0.2679f, 0f, "p", "italic", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 113, 0.7500f, 0.9107f, 0.2679f, 0.0359f, "q", "italic", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(58, -0.0556f), new KernInfo(59, -0.0556f), new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 114, 0.6429f, 0.9107f, 0.2679f, 0.0278f, "r", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 115, 0.6429f, 0.9107f, 0.2679f, 0f, "s", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 116, 0.5357f, 0.9107f, 0.2679f, 0f, "t", "italic", k, l));
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 117, 0.7500f, 0.9107f, 0.2679f, 0f, "u", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 118, 0.6429f, 0.9107f, 0.2679f, 0.0359f, "v", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 119, 0.8571f, 0.9107f, 0.2679f, 0.0269f, "w", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;										// TODO: 测试斜体宽度用
		font.setAdd(c = new FontCharInfo(cc_begin + 120, 0.4446f/*0.6429f*/, 0.9107f, 0.2679f, 0f, "x", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 121, 0.6429f, 0.9107f, 0.2679f, 0.0359f, "y", "italic", k, l)); c.d = 0.2f; c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 122, 0.6429f, 0.9107f, 0.2679f, 0.044f, "z", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 123, 0.5357f, 0.9107f, 0.2679f, 0f, "&#x131;", "italic", k, l)); c.a = 0f;
		k = new KernInfo[] { new KernInfo(127, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 124, 0.4821f, 0.9107f, 0.2679f, 0f, "j", "italic", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(127, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 125, 0.7500f, 1.0714f, 0.2679f, 0f, "&#x2118;", "arial", k, l)); c.d = 0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 126, 0.4821f, 0.7500f, 0.1607f, 0.154f, "<span style=\"position:relative; left:.3em; top:-.65em; font-size: 67%; display:inline-block\">&#x2192;</span>", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 127, 0.0000f, 0.8571f, 0.1607f, 0.399f, "&#x0311;", "normal"));

		return font;
	}

	public static final FontInfo cmsy10 = init_cmsy10();
	
	/**
	 * 某些字符使用的 Unicode 代码代表的图形, 可以参见如下地址:
	 *   http://www.unicode.org/charts/PDF/U2200.pdf
	 */
	private static FontInfo init_cmsy10() {
		// 临时变量.
		FontInfo font = new FontInfo(); // cmr10 别名.
		font.name = "cmsy10";
		int cc_begin = 0;
		KernInfo[] k;
		LigInfo[] l;
		@SuppressWarnings("unused") FontCharInfo c;
		
		// 以下代码为 js 自动产生的.
		font.setAdd(c = new FontCharInfo(cc_begin + 0, 0.5786f, 1.0821f, 0.2571f, 0f, "<span style=\"position:relative; top:.1em; display:inline-block\">&#x2212;</span>", "symbol")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 1, 0.3321f, 0.8679f, 0.1286f, 0f, "&#xB7;", "normal")); c.d = -0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 2, 0.8036f, 0.8679f, 0.1286f, 0f, "&#xD7;", "normal")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 3, 0.5036f, 0.8679f, 0.1286f, 0f, "<span style=\"position:relative; top:.3em; display:inline-block\">&#x2A;</span>", "normal")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 4, 0.8036f, 0.8679f, 0.1286f, 0f, "&#xF7;", "normal")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 5, 0.5893f, 1.0821f, 0.2571f, 0f, "&#x25CA;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 6, 0.8036f, 0.8679f, 0.1286f, 0f, "&#xB1;", "normal")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 7, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2213;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 8, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2295;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 9, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2296;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 10, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2297;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 11, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2298;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 12, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2299;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 13, 0.5893f, 1.0821f, 0.2571f, 0f, "&#x25EF;", "arial"));
		font.setAdd(c = new FontCharInfo(cc_begin + 14, 0.3964f, 1.0821f, 0.2571f, 0f, "&#x2218;", "symbol2")); c.d = -0.1f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 15, 0.3536f, 1.0821f, 0.2571f, 0f, "&#x2022;", "symbol")); c.d = -0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 16, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x224D;", "symbol2")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 17, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2261;", "symbol2")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 18, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2286;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 19, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2287;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 20, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2264;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 21, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2265;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 22, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x227C;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 23, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x227D;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 24, 0.7393f, 0.8679f, 0.1286f, 0f, "~", "normal")); c.d = -0.2f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 25, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2248;", "symbol")); c.d = -0.1f; c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 26, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2282;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 27, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2283;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 28, 0.9643f, 1.0821f, 0.2571f, 0f, "&#x226A;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 29, 0.9643f, 1.0821f, 0.2571f, 0f, "&#x226B;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 30, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x227A;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 31, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x227B;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 32, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2190;", "arrow1")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 33, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2192;", "arrow1")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 34, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2191;", "arrow1a")); c.d = 0f; c.a = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 35, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2193;", "arrow1a")); c.d = 0f; c.a = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 36, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2194;", "arrow1")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 37, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2197;", "arrows")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 38, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2198;", "arrows")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 39, 0.5786f, 1.0821f, 0.2571f, 0f, "&#x2243;", "symbol2")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 40, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x21D0;", "arrow2")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 41, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x21D2;", "arrow2")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 42, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x21D1;", "arrow1a")); c.d = 0.1f; c.a = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 43, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x21D3;", "arrow1a")); c.d = 0.1f; c.a = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 44, 0.8571f, 1.0821f, 0.2571f, 0f, "&#x21D4;", "arrow2")); c.a = -0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 45, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2196;", "arrows")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 46, 0.7071f, 1.0821f, 0.2571f, 0f, "&#x2199;", "arrows")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 47, 0.9964f, 0.8679f, 0.1286f, 0f, "&#x221D;", "normal")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 48, 0.4071f, 1.3393f, 0.2679f, 0f, "<span style=\"font-size:133%; margin-right:-.1em; position: relative; top:.4em; display:inline-block\">&#x2032;</span>", "lucida")); c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 49, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x221E;", "symbol")); c.a = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 50, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x2208;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 51, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x220B;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 52, 0.9000f, 1.5964f, 0.4071f, 0f, "<span style=\"font-size:150%; position:relative; top:.2em; display:inline-block\">&#x25B3;</span>", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 53, 0.9000f, 1.5964f, 0.4071f, 0f, "<span style=\"font-size:150%; position:relative; top:.2em; display:inline-block\">&#x25BD;</span>", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 54, 0.5036f, 1.1571f, 0.1714f, 0f, "<span style=\"font-size:133%; position:relative; top:.2em; display:inline-block\">/</span>", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 55, 0.3107f, 0.7179f, 0.1714f, 0f, "<span style=\"font-size:67%; position: relative; top:-.15em; margin-right:-.3em; display:inline-block\">&#x22A2;</span>", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 56, 0.5893f, 1.0821f, 0.2571f, 0f, "&#x2200;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 57, 0.5893f, 1.0821f, 0.2571f, 0f, "&#x2203;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 58, 0.5786f, 1.0821f, 0.2571f, 0f, "&#xAC;", "symbol")); c.d = -0.1f; c.a = 0f;
		font.setAdd(c = new FontCharInfo(cc_begin + 59, 0.7929f, 1.0821f, 0.2571f, 0f, "&#x2205;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 60, 0.7821f, 1.0821f, 0.2571f, 0f, "&#x211C;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 61, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x2111;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 62, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x22A4;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 63, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x22A5;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 64, 0.6857f, 1.0821f, 0.2571f, 0f, "&#x2135;", "symbol"));
		k = new KernInfo[] { new KernInfo(48, 0.194f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 65, 0.6750f, 0.9536f, 0.2357f, 0f, "A", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 66, 0.7607f, 0.9536f, 0.2357f, 0.0304f, "B", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 67, 0.5571f, 0.9536f, 0.2357f, 0.0583f, "C", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 68, 0.7607f, 0.9536f, 0.2357f, 0.0278f, "D", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 69, 0.5143f, 0.9536f, 0.2357f, 0.0894f, "E", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 70, 0.6000f, 0.9536f, 0.2357f, 0.0993f, "F", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 71, 0.6321f, 0.9536f, 0.2357f, 0.0593f, "G", "cal", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 72, 0.8786f, 0.9536f, 0.2357f, 0.00965f, "H", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 73, 0.6000f, 0.9536f, 0.2357f, 0.0738f, "I", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.167f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 74, 0.5143f, 0.9536f, 0.2357f, 0.185f, "J", "cal", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(48, 0.0556f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 75, 0.8357f, 0.9536f, 0.2357f, 0.0144f, "K", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 76, 0.6321f, 0.9536f, 0.2357f, 0f, "L", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 77, 0.9964f, 0.9536f, 0.2357f, 0f, "M", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 78, 0.8786f, 0.9536f, 0.2357f, 0.147f, "N", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 79, 0.6321f, 0.9536f, 0.2357f, 0.0278f, "O", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 80, 0.6750f, 0.9536f, 0.2357f, 0.0822f, "P", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.111f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 81, 0.6321f, 0.9536f, 0.2357f, 0f, "Q", "cal", k, l)); c.d = 0.2f;
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 82, 0.7929f, 0.9536f, 0.2357f, 0f, "R", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 83, 0.5571f, 0.9536f, 0.2357f, 0.075f, "S", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 84, 0.6750f, 0.9536f, 0.2357f, 0.254f, "T", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 85, 0.7607f, 0.9536f, 0.2357f, 0.0993f, "U", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0278f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 86, 0.6750f, 0.9536f, 0.2357f, 0.0822f, "V", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 87, 0.9536f, 0.9536f, 0.2357f, 0.0822f, "W", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 88, 0.7179f, 0.9536f, 0.2357f, 0.146f, "X", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.0833f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 89, 0.7607f, 0.9536f, 0.2357f, 0.0822f, "Y", "cal", k, l));
		k = new KernInfo[] { new KernInfo(48, 0.139f) };
		l = null;
		font.setAdd(c = new FontCharInfo(cc_begin + 90, 0.5571f, 0.9536f, 0.2357f, 0.0794f, "Z", "cal", k, l));
		font.setAdd(c = new FontCharInfo(cc_begin + 91, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x22C3;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 92, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x22C2;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 93, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x228E;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 94, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x22C0;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 95, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x22C1;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 96, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x22A2;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 97, 0.5464f, 1.0821f, 0.2571f, 0f, "&#x22A3;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 98, 0.5250f, 0.9964f, 0.2036f, 0f, "&#x230A;", "lucida")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 99, 0.5250f, 0.9964f, 0.2036f, 0f, "&#x230B;", "lucida")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 100, 0.5250f, 0.9964f, 0.2036f, 0f, "&#x2308;", "lucida")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 101, 0.5250f, 0.9964f, 0.2036f, 0f, "&#x2309;", "lucida")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 102, 0.5036f, 0.8679f, 0.1286f, 0f, "{", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 103, 0.5036f, 0.8679f, 0.1286f, 0f, "}", "normal")); c.d = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 104, 0.3321f, 1.0821f, 0.2571f, 0f, "&#x2329;", "symbol")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 105, 0.3321f, 1.0821f, 0.2571f, 0f, "&#x232A;", "symbol")); c.d = 0.2f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 106, 0.2571f, 1.0821f, 0.2571f, 0f, "&#x2223;", "symbol")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 107, 0.4179f, 1.0821f, 0.2571f, 0f, "&#x2225;", "symbol")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 108, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2195;", "arrow1a")); c.d = 0f; c.a = 0.2f;
		font.setAdd(c = new FontCharInfo(cc_begin + 109, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x21D5;", "arrow1a")); c.d = 0f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 110, 0.2786f, 1.0821f, 0.2571f, 0f, "&#x2216;", "symbol")); c.d = 0.1f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 111, 0.4179f, 1.0821f, 0.2571f, 0f, "&#x2240;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 112, 0.5893f, 1.0821f, 0.2571f, 0f, "<span style=\"display:inline-block; position:relative; top:.8em\">&#x221A;</span>", "symbol")); c.h = 0.04f; c.d = 0.8f;
		font.setAdd(c = new FontCharInfo(cc_begin + 113, 0.8143f, 1.0821f, 0.2571f, 0f, "&#x2210;", "symbol")); c.a = 0.4f;
		font.setAdd(c = new FontCharInfo(cc_begin + 114, 0.6107f, 1.0821f, 0.2571f, 0f, "&#x2207;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 115, 0.4393f, 0.8464f, 0.1714f, 0.111f, "<span style=\"display:inline-block; position:relative; font-size:85%; left:-.1em; margin-right:-.2em\">&#x222B;</span>", "lucida")); c.d = 0.1f; c.a = 0.4f;
		font.setAdd(c = new FontCharInfo(cc_begin + 116, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x2294;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 117, 0.5571f, 1.0821f, 0.2571f, 0f, "&#x2293;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 118, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2291;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 119, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2292;", "symbol"));
		font.setAdd(c = new FontCharInfo(cc_begin + 120, 0.5036f, 0.8679f, 0.1286f, 0f, "&#xA7;", "normal")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 121, 0.5893f, 0.8679f, 0.1286f, 0f, "&#x2020;", "normal")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 122, 0.5893f, 0.8679f, 0.1286f, 0f, "&#x2021;", "normal")); c.d = 0.1f;
		font.setAdd(c = new FontCharInfo(cc_begin + 123, 0.6321f, 0.9964f, 0.2036f, 0f, "&#xB6;", "lucida")); c.d = 0.1f; c.a = 0.3f;
		font.setAdd(c = new FontCharInfo(cc_begin + 124, 0.8036f, 1.0821f, 0.2571f, 0f, "&#x2663;", "arial"));
		font.setAdd(c = new FontCharInfo(cc_begin + 125, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2662;", "arial"));
		font.setAdd(c = new FontCharInfo(cc_begin + 126, 0.7286f, 1.0821f, 0.2571f, 0f, "&#x2661;", "arial"));
		font.setAdd(c = new FontCharInfo(cc_begin + 127, 0.7179f, 1.0821f, 0.2571f, 0f, "&#x2660;", "arial"));

		
		return font;
	}
	
	public static final FontInfo cmex10 = init_cmex10();
	// 初始化 cmex10 的信息
	private static FontInfo init_cmex10() {
		// 临时变量.
		FontInfo font = new FontInfo(); // cmr10 别名.
		font.name = "cmex10";
		int cc_begin = 0;
		KernInfo[] k;
		LigInfo[] l;
		@SuppressWarnings("unused") FontCharInfo c;

		// 以下代码是 js 自动生成的.
		font.setAdd(c = new FontCharInfo(cc_begin + 0, 0.4393f, 1.2000f, 0.2893f, 0f, "(", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 16;
		font.setAdd(c = new FontCharInfo(cc_begin + 1, 0.4393f, 1.2000f, 0.2893f, 0f, ")", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 17;
		font.setAdd(c = new FontCharInfo(cc_begin + 2, 0.4393f, 1.2000f, 0.2893f, 0f, "[", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 104;
		font.setAdd(c = new FontCharInfo(cc_begin + 3, 0.4286f, 1.2000f, 0.2893f, 0f, "]", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 105;
		font.setAdd(c = new FontCharInfo(cc_begin + 4, 0.7071f, 1.3393f, 0.2679f, 0f, "&#x230A;", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 106;
		font.setAdd(c = new FontCharInfo(cc_begin + 5, 0.7071f, 1.3393f, 0.2679f, 0f, "&#x230B;", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 107;
		font.setAdd(c = new FontCharInfo(cc_begin + 6, 0.7071f, 1.3393f, 0.2679f, 0f, "&#x2308;", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 108;
		font.setAdd(c = new FontCharInfo(cc_begin + 7, 0.7071f, 1.3393f, 0.2679f, 0f, "&#x2309;", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 109;
		font.setAdd(c = new FontCharInfo(cc_begin + 8, 0.6107f, 1.2000f, 0.2893f, 0f, "<span style=\"display:inline-block; margin-left:-.1em\">{</span>", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 110;
		font.setAdd(c = new FontCharInfo(cc_begin + 9, 0.6107f, 1.2000f, 0.2893f, 0f, "<span style=\"display:inline-block; margin-right:-.1em\">}</span>", "delim1")); c.h = 0.04f; c.d = 1.16f; c.n = 111;
		font.setAdd(c = new FontCharInfo(cc_begin + 10, 0.4393f, 1.4357f, 0.3429f, 0f, "&#x2329;", "delim1b")); c.h = 0.04f; c.d = 1.16f; c.n = 68;
		font.setAdd(c = new FontCharInfo(cc_begin + 11, 0.4393f, 1.4357f, 0.3429f, 0f, "&#x232A;", "delim1b")); c.h = 0.04f; c.d = 1.16f; c.n = 69;
		font.setAdd(c = new FontCharInfo(cc_begin + 12, 0.2571f, 1.0821f, 0.2571f, 0f, "&#x2223;", "symbol")); c.h = 0.7f; c.d = 0.1f; c.delim = new DelimChar(0, 0, 0, 12);
		font.setAdd(c = new FontCharInfo(cc_begin + 13, 0.4179f, 1.0821f, 0.2571f, 0f, "&#x2225;", "symbol")); c.h = 0.7f; c.d = 0.1f; c.delim = new DelimChar(0, 0, 0, 13);
		font.setAdd(c = new FontCharInfo(cc_begin + 14, 0.6964f, 1.3393f, 0.2679f, 0f, "/", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 46;
		font.setAdd(c = new FontCharInfo(cc_begin + 15, 0.6964f, 1.3393f, 0.2679f, 0f, "&#x2216;", "delim1a")); c.h = 0.04f; c.d = 1.16f; c.n = 47;
		font.setAdd(c = new FontCharInfo(cc_begin + 16, 0.6000f, 1.6071f, 0.4071f, 0f, "(", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 18;
		font.setAdd(c = new FontCharInfo(cc_begin + 17, 0.6000f, 1.6071f, 0.4071f, 0f, ")", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 19;
		font.setAdd(c = new FontCharInfo(cc_begin + 18, 0.8250f, 2.2179f, 0.5357f, 0f, "(", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 32;
		font.setAdd(c = new FontCharInfo(cc_begin + 19, 0.8250f, 2.2179f, 0.5357f, 0f, ")", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 33;
		font.setAdd(c = new FontCharInfo(cc_begin + 20, 0.8357f, 2.2179f, 0.5357f, 0f, "[", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 34;
		font.setAdd(c = new FontCharInfo(cc_begin + 21, 0.8357f, 2.2179f, 0.5357f, 0f, "]", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 35;
		font.setAdd(c = new FontCharInfo(cc_begin + 22, 1.3179f, 2.7429f, 1.0929f, 0f, "&#x230A;", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 36;
		font.setAdd(c = new FontCharInfo(cc_begin + 23, 1.3179f, 2.7429f, 1.0929f, 0f, "&#x230B;", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 37;
		font.setAdd(c = new FontCharInfo(cc_begin + 24, 1.3179f, 2.7429f, 1.0929f, 0f, "&#x2308;", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 38;
		font.setAdd(c = new FontCharInfo(cc_begin + 25, 1.3179f, 2.7429f, 1.0929f, 0f, "&#x2309;", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 39;
		font.setAdd(c = new FontCharInfo(cc_begin + 26, 1.1786f, 2.2179f, 0.5357f, 0f, "<span style=\"display:inline-block; position:relative; left:-.1em; margin-right:-.1em\">{</span>", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 40;
		font.setAdd(c = new FontCharInfo(cc_begin + 27, 1.1786f, 2.2179f, 0.5357f, 0f, "<span style=\"display:inline-block; position:relative; left:-.05em; margin-right:-.1em\">}</span>", "delim3")); c.h = 0.04f; c.d = 2.36f; c.n = 41;
		font.setAdd(c = new FontCharInfo(cc_begin + 28, 0.8250f, 2.6679f, 0.6750f, 0f, "&#x2329;", "delim3b")); c.h = 0.04f; c.d = 2.36f; c.n = 42;
		font.setAdd(c = new FontCharInfo(cc_begin + 29, 0.8250f, 2.6679f, 0.6750f, 0f, "&#x232A;", "delim3b")); c.h = 0.04f; c.d = 2.36f; c.n = 43;
		font.setAdd(c = new FontCharInfo(cc_begin + 30, 1.3071f, 2.7429f, 1.0929f, 0f, "/", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 44;
		font.setAdd(c = new FontCharInfo(cc_begin + 31, 1.3071f, 2.7429f, 1.0929f, 0f, "&#x2216;", "delim3a")); c.h = 0.04f; c.d = 2.36f; c.n = 45;
		font.setAdd(c = new FontCharInfo(cc_begin + 32, 1.0821f, 2.9036f, 0.6857f, 0f, "(", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 48;
		font.setAdd(c = new FontCharInfo(cc_begin + 33, 1.0821f, 2.9036f, 0.6857f, 0f, ")", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 49;
		font.setAdd(c = new FontCharInfo(cc_begin + 34, 1.0607f, 2.9036f, 0.6857f, 0f, "[", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 50;
		font.setAdd(c = new FontCharInfo(cc_begin + 35, 1.0821f, 2.9036f, 0.6857f, 0f, "]", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 51;
		font.setAdd(c = new FontCharInfo(cc_begin + 36, 1.7250f, 3.5571f, 1.4250f, 0f, "&#x230A;", "delim4a")); c.h = 0.04f; c.d = 2.96f; c.n = 52;
		font.setAdd(c = new FontCharInfo(cc_begin + 37, 1.7250f, 3.5571f, 1.4250f, 0f, "&#x230B;", "delim4a")); c.h = 0.04f; c.d = 2.96f; c.n = 53;
		font.setAdd(c = new FontCharInfo(cc_begin + 38, 1.7250f, 3.5571f, 1.4250f, 0f, "&#x2308;", "delim4a")); c.h = 0.04f; c.d = 2.96f; c.n = 54;
		font.setAdd(c = new FontCharInfo(cc_begin + 39, 1.7250f, 3.5571f, 1.4250f, 0f, "&#x2309;", "delim4a")); c.h = 0.04f; c.d = 2.96f; c.n = 55;
		font.setAdd(c = new FontCharInfo(cc_begin + 40, 1.5321f, 2.9036f, 0.6857f, 0f, "<span style=\"display:inline-block; position:relative; left:-.1em; margin-right:-.1em\">{</span>", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 56;
		font.setAdd(c = new FontCharInfo(cc_begin + 41, 1.5321f, 2.9036f, 0.6857f, 0f, "<span style=\"display:inline-block; position:relative; left:-.1em; margin-right:-.1em\">}</span>", "delim4")); c.h = 0.04f; c.d = 2.96f; c.n = 57;
		font.setAdd(c = new FontCharInfo(cc_begin + 42, 1.0607f, 3.4714f, 0.8679f, 0f, "&#x2329;", "delim4b")); c.h = 0.04f; c.d = 2.96f;
		font.setAdd(c = new FontCharInfo(cc_begin + 43, 1.0607f, 3.4714f, 0.8679f, 0f, "&#x232A;", "delim4b")); c.h = 0.04f; c.d = 2.96f;
		font.setAdd(c = new FontCharInfo(cc_begin + 44, 1.7036f, 3.5571f, 1.4250f, 0f, "/", "delim4a")); c.h = 0.04f; c.d = 2.96f;
		font.setAdd(c = new FontCharInfo(cc_begin + 45, 1.7036f, 3.5571f, 1.4250f, 0f, "&#x2216;", "delim4a")); c.h = 0.04f; c.d = 2.96f;
		font.setAdd(c = new FontCharInfo(cc_begin + 46, 0.9429f, 1.8214f, 0.3643f, 0f, "/", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 30;
		font.setAdd(c = new FontCharInfo(cc_begin + 47, 0.9429f, 1.8214f, 0.3643f, 0f, "&#x2216;", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 31;
		font.setAdd(c = new FontCharInfo(cc_begin + 48, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xE6;", "delimx")); c.h = 1f; c.delim = new DelimChar(48, 0, 64, 66);
		font.setAdd(c = new FontCharInfo(cc_begin + 49, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xF6;", "delimx")); c.h = 1f; c.delim = new DelimChar(49, 0, 65, 67);
		font.setAdd(c = new FontCharInfo(cc_begin + 50, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xE9;", "delimx")); c.h = 1f; c.delim = new DelimChar(50, 0, 52, 54);
		font.setAdd(c = new FontCharInfo(cc_begin + 51, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xF9;", "delimx")); c.h = 1f; c.delim = new DelimChar(51, 0, 53, 55);
		font.setAdd(c = new FontCharInfo(cc_begin + 52, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xEB;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 52, 54);
		font.setAdd(c = new FontCharInfo(cc_begin + 53, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xFB;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 53, 55);
		font.setAdd(c = new FontCharInfo(cc_begin + 54, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xEA;", "delimx")); c.h = 1f; c.delim = new DelimChar(50, 0, 0, 54);
		font.setAdd(c = new FontCharInfo(cc_begin + 55, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xFA;", "delimx")); c.h = 1f; c.delim = new DelimChar(51, 0, 0, 55);
		font.setAdd(c = new FontCharInfo(cc_begin + 56, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xEC;", "delimx")); c.h = 1f; c.delim = new DelimChar(56, 60, 58, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 57, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xFC;", "delimx")); c.h = 1f; c.delim = new DelimChar(57, 61, 59, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 58, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xEE;", "delimx")); c.h = 1f; c.delim = new DelimChar(56, 0, 58, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 59, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xFE;", "delimx")); c.h = 1f; c.delim = new DelimChar(57, 0, 59, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 60, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xED;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 0, 63);
		font.setAdd(c = new FontCharInfo(cc_begin + 61, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xFD;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 0, 119);
		font.setAdd(c = new FontCharInfo(cc_begin + 62, 0.4929f, 0.9857f, 0.2250f, 0f, "&#xEF;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 0, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 63, 0.6857f, 0.8679f, 0.1286f, 0f, "<span style=\"display:inline-block; margin-left:.14em; margin-right:.36em\">|</span>", "normal")); c.delim = new DelimChar(120, 0, 121, 63);
		font.setAdd(c = new FontCharInfo(cc_begin + 64, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xE8;", "delimx")); c.h = 1f; c.delim = new DelimChar(56, 0, 59, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 65, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xF8;", "delimx")); c.h = 1f; c.delim = new DelimChar(57, 0, 58, 62);
		font.setAdd(c = new FontCharInfo(cc_begin + 66, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xE7;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 0, 66);
		font.setAdd(c = new FontCharInfo(cc_begin + 67, 0.3750f, 0.9857f, 0.2250f, 0f, "&#xF7;", "delimx")); c.h = 1f; c.delim = new DelimChar(0, 0, 0, 67);
		font.setAdd(c = new FontCharInfo(cc_begin + 68, 0.5893f, 1.9286f, 0.4821f, 0f, "&#x2329;", "delim2b")); c.h = 0.04f; c.d = 1.76f; c.n = 28;
		font.setAdd(c = new FontCharInfo(cc_begin + 69, 0.5893f, 1.9286f, 0.4821f, 0f, "&#x232A;", "delim2b")); c.h = 0.04f; c.d = 1.76f; c.n = 29;
		font.setAdd(c = new FontCharInfo(cc_begin + 70, 0.7071f, 1.3607f, 0.3429f, 0f, "&#x2294;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 71;
		font.setAdd(c = new FontCharInfo(cc_begin + 71, 1.2750f, 2.4429f, 0.6107f, 0f, "&#x2294;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 72, 0.4286f, 0.9000f, 0.2250f, 0.095f, "&#x222E;", "bigop1c")); c.h = 0f; c.d = 1.11f; c.n = 73;
		font.setAdd(c = new FontCharInfo(cc_begin + 73, 0.9107f, 1.9714f, 0.5036f, 0.222f, "&#x222E;", "bigop2c")); c.h = 0f; c.d = 2.22f;
		font.setAdd(c = new FontCharInfo(cc_begin + 74, 1.0179f, 1.3607f, 0.3429f, 0f, "&#x2299;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 75;
		font.setAdd(c = new FontCharInfo(cc_begin + 75, 1.8214f, 2.4429f, 0.6107f, 0f, "&#x2299;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 76, 1.0179f, 1.3607f, 0.3429f, 0f, "&#x2295;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 77;
		font.setAdd(c = new FontCharInfo(cc_begin + 77, 1.8214f, 2.4429f, 0.6107f, 0f, "&#x2295;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 78, 1.0179f, 1.3607f, 0.3429f, 0f, "&#x2297;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 79;
		font.setAdd(c = new FontCharInfo(cc_begin + 79, 1.8214f, 2.4429f, 0.6107f, 0f, "&#x2297;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 80, 0.7821f, 1.1893f, 0.2893f, 0f, "&#x2211;", "bigop1a")); c.h = 0f; c.d = 1f; c.n = 88;
		font.setAdd(c = new FontCharInfo(cc_begin + 81, 0.9107f, 1.1893f, 0.2893f, 0f, "&#x220F;", "bigop1a")); c.h = 0f; c.d = 1f; c.n = 89;
		font.setAdd(c = new FontCharInfo(cc_begin + 82, 0.4286f, 0.9000f, 0.2250f, 0.095f, "&#x222B;", "bigop1c")); c.h = 0f; c.d = 1.11f; c.n = 90;
		font.setAdd(c = new FontCharInfo(cc_begin + 83, 1.0071f, 1.9286f, 0.4821f, 0f, "&#x222A;", "bigop1b")); c.h = 0f; c.d = 1f; c.n = 91;
		font.setAdd(c = new FontCharInfo(cc_begin + 84, 1.0071f, 1.9286f, 0.4821f, 0f, "&#x2229;", "bigop1b")); c.h = 0f; c.d = 1f; c.n = 92;
		font.setAdd(c = new FontCharInfo(cc_begin + 85, 1.0071f, 1.9286f, 0.4821f, 0f, "&#x228E;", "bigop1b")); c.h = 0f; c.d = 1f; c.n = 93;
		font.setAdd(c = new FontCharInfo(cc_begin + 86, 0.7071f, 1.3607f, 0.3429f, 0f, "&#x22C0;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 94;
		font.setAdd(c = new FontCharInfo(cc_begin + 87, 0.7071f, 1.3607f, 0.3429f, 0f, "&#x22C1;", "bigop1")); c.h = 0f; c.d = 1f; c.n = 95;
		font.setAdd(c = new FontCharInfo(cc_begin + 88, 1.3179f, 1.9714f, 0.5036f, 0f, "&#x2211;", "bigop2a")); c.h = 0.1f; c.d = 1.6f;
		font.setAdd(c = new FontCharInfo(cc_begin + 89, 1.5214f, 1.9714f, 0.5036f, 0f, "&#x220F;", "bigop2a")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 90, 0.9107f, 1.9714f, 0.5036f, 0.222f, "&#x222B;", "bigop2c")); c.h = 0f; c.d = 2.22f;
		font.setAdd(c = new FontCharInfo(cc_begin + 91, 1.5429f, 2.9571f, 0.7286f, 0f, "&#x222A;", "bigop2b")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 92, 1.5429f, 2.9571f, 0.7286f, 0f, "&#x2229;", "bigop2b")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 93, 1.5429f, 2.9571f, 0.7286f, 0f, "&#x228E;", "bigop2b")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 94, 1.2750f, 2.4429f, 0.6107f, 0f, "&#x22C0;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 95, 1.2750f, 2.4429f, 0.6107f, 0f, "&#x22C1;", "bigop2")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 96, 0.9107f, 1.1893f, 0.2893f, 0f, "&#x2210;", "bigop1a")); c.h = 0f; c.d = 1f; c.n = 97;
		font.setAdd(c = new FontCharInfo(cc_begin + 97, 1.5214f, 1.9714f, 0.5036f, 0f, "&#x2210;", "bigop2a")); c.h = 0.1f; c.d = 1.5f;
		font.setAdd(c = new FontCharInfo(cc_begin + 98, 0.6750f, 0.7179f, 0.1714f, 0f, "&#xFE3F;", "wide1")); c.w = 0.65f; c.h = 0.8f; c.d = 0f; c.n = 99;
		font.setAdd(c = new FontCharInfo(cc_begin + 99, 1.1036f, 1.1893f, 0.2893f, 0f, "&#xFE3F;", "wide2")); c.w = 1.1f; c.h = 0.85f; c.n = 100;
		font.setAdd(c = new FontCharInfo(cc_begin + 100, 1.7464f, 1.8750f, 0.4607f, 0f, "&#xFE3F;", "wide3")); c.w = 1.65f; c.h = 0.99f;
		font.setAdd(c = new FontCharInfo(cc_begin + 101, 0.4071f, 0.6750f, 0.1714f, 0f, "~", "wide1a")); c.w = 0.5f; c.h = 1f; c.n = 102;
		font.setAdd(c = new FontCharInfo(cc_begin + 102, 0.7179f, 1.2000f, 0.2893f, 0f, "~", "wide2a")); c.w = 0.8f; c.h = 1f; c.n = 103;
		font.setAdd(c = new FontCharInfo(cc_begin + 103, 1.0821f, 1.7893f, 0.4286f, 0f, "~", "wide3a")); c.w = 1.3f; c.h = 0.99f;
		font.setAdd(c = new FontCharInfo(cc_begin + 104, 0.6000f, 1.6071f, 0.4071f, 0f, "[", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 20;
		font.setAdd(c = new FontCharInfo(cc_begin + 105, 0.6000f, 1.6071f, 0.4071f, 0f, "]", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 21;
		font.setAdd(c = new FontCharInfo(cc_begin + 106, 0.9536f, 1.8214f, 0.3643f, 0f, "&#x2308;", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 22;
		font.setAdd(c = new FontCharInfo(cc_begin + 107, 0.9536f, 1.8214f, 0.3643f, 0f, "&#x2309;", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 23;
		font.setAdd(c = new FontCharInfo(cc_begin + 108, 0.9536f, 1.8214f, 0.3643f, 0f, "&#x230A;", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 24;
		font.setAdd(c = new FontCharInfo(cc_begin + 109, 0.9536f, 1.8214f, 0.3643f, 0f, "&#x230B;", "delim2a")); c.h = 0.04f; c.d = 1.76f; c.n = 25;
		font.setAdd(c = new FontCharInfo(cc_begin + 110, 0.8464f, 1.6071f, 0.4071f, 0f, "<span style=\"display:inline-block; position:relative; left:-.1em; margin-right:-.1em\">{</span>", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 26;
		font.setAdd(c = new FontCharInfo(cc_begin + 111, 0.8464f, 1.6071f, 0.4071f, 0f, "<span style=\"display:inline-block; position:relative; margin-right:-.1em; left:-.05em\">}</span>", "delim2")); c.h = 0.04f; c.d = 1.76f; c.n = 27;
		font.setAdd(c = new FontCharInfo(cc_begin + 112, 0.8893f, 1.5964f, 0.4071f, 0f, "<span style=\"font-size:150%; position:relative; top:.8em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.04f; c.d = 1.16f; c.n = 113;
		font.setAdd(c = new FontCharInfo(cc_begin + 113, 1.3071f, 2.3571f, 0.5786f, 0f, "<span style=\"font-size:220%; position:relative; top:.8em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.04f; c.d = 1.76f; c.n = 114;
		font.setAdd(c = new FontCharInfo(cc_begin + 114, 1.8429f, 3.3214f, 0.8250f, 0f, "<span style=\"font-size:310%; position:relative; top:.8em; margin-right:-.01em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.06f; c.d = 2.36f; c.n = 115;
		font.setAdd(c = new FontCharInfo(cc_begin + 115, 2.3786f, 4.2750f, 1.0714f, 0f, "<span style=\"font-size:400%; position:relative; top:.8em; margin-right:-.025em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.08f; c.d = 2.96f; c.n = 116;
		font.setAdd(c = new FontCharInfo(cc_begin + 116, 2.9143f, 5.2393f, 1.3179f, 0f, "<span style=\"font-size:490%; position:relative; top:.8em; margin-right:-.03em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.1f; c.d = 3.75f; c.n = 117;
		font.setAdd(c = new FontCharInfo(cc_begin + 117, 3.4607f, 6.2036f, 1.5643f, 0f, "<span style=\"font-size:580%; position:relative; top:.775em; margin-right:-.04em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.12f; c.d = 4.5f; c.n = 118;
		font.setAdd(c = new FontCharInfo(cc_begin + 118, 4.4786f, 8.0143f, 2.0357f, 0f, "<span style=\"font-size:750%; position:relative; top:.775em; margin-right:-.04em; display:inline-block\">&#x221A;</span>", "root")); c.h = 0.14f; c.d = 5.7f;
		font.setAdd(c = new FontCharInfo(cc_begin + 119, 1.2000f, 0.8679f, 0.1286f, 0f, "<span style=\"display:inline-block; margin-left:.04em\">|</span><span style=\"display:inline-block; margin-left:.08em; margin-right:.125em\">|</span>", "normal")); c.delim = new DelimChar(126, 0, 127, 119);
		font.setAdd(c = new FontCharInfo(cc_begin + 120, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2191;", "arrow1a")); c.h = 0.7f; c.d = 0f; c.delim = new DelimChar(120, 0, 0, 63);
		font.setAdd(c = new FontCharInfo(cc_begin + 121, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x2193;", "arrow1a")); c.h = 0.7f; c.d = 0f; c.delim = new DelimChar(0, 0, 121, 63);
		font.setAdd(c = new FontCharInfo(cc_begin + 122, 0.4286f, 1.0821f, 0.2571f, 0f, "<span style=\"margin-left:-.1em\"></span><span style=\"position:relative; top:.55em; margin-right:-.3em; display:inline-block\">&#x25DC;</span>", "symbol")); c.h = 0.05f;
		font.setAdd(c = new FontCharInfo(cc_begin + 123, 0.2679f, 1.0821f, 0.2571f, 0f, "<span style=\"margin-left:-.3em\"></span><span style=\"position:relative; top:.55em; margin-right:-.1em; display:inline-block\">&#x25DD;</span>", "symbol")); c.h = 0.05f;
		font.setAdd(c = new FontCharInfo(cc_begin + 124, 0.4286f, 1.0821f, 0.2571f, 0f, "<span style=\"margin-left:-.1em\"></span><span style=\"position:relative; top:.15em; margin-right:-.3em; display:inline-block\">&#x25DF;</span>", "symbol")); c.h = 0.05f;
		font.setAdd(c = new FontCharInfo(cc_begin + 125, 0.2679f, 1.0821f, 0.2571f, 0f, "<span style=\"margin-left:-.3em\"></span><span style=\"position:relative; top:.15em; margin-right:-.1em; display:inline-block\">&#x25DE;</span>", "symbol")); c.h = 0.05f;
		font.setAdd(c = new FontCharInfo(cc_begin + 126, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x21D1;", "arrow1a")); c.h = 0.65f; c.d = 0f; c.delim = new DelimChar(126, 0, 0, 119);
		font.setAdd(c = new FontCharInfo(cc_begin + 127, 0.6750f, 1.0821f, 0.2571f, 0f, "&#x21D3;", "arrow1a")); c.h = 0.65f; c.d = 0f; c.delim = new DelimChar(0, 0, 127, 119);


		return font;
	}
	
	
	// 如下四个字段, 是在调试的时候看到的, 估计是动态计算出来的, 放入到这个对象中.
	// 似乎是根据字符 M 的宽度/高度计算出来的(高度, 深度等信息).
	public static float M_height = 0.823f;
	public static float h = 0.886f;
	public static float d = 0.121f;
	public static float hd = 1.007f;
	
}

/**
 * 实现 CTeX.famName 字段所需结构. 从 name => integer  
 *
 */
class FamNameMap {
	public static final int cmr10 = 0; 
	public static final int cmmi10 = 1;
	public static final int cmsy10 = 2; 
	public static final int cmex10 = 3; 
	public static final int cmti10 = 4; 
	public static final int cmbx10 = 6;
}

/**
 * 表示一个 CTeX 类中的一个字体的信息.
 * 问题: 我们如何在 java 中表示这种字体信息? 即容易理解, 又容易构造??
 *   我们假定这个程序一次初始化即加载在服务中, 这样我们可以不顾及初始化时候的复杂性, 
 *   而只考虑简洁易懂, 性能即可.
 *   
 * 每一个字体包含 N 个字符的信息. 每个字符的信息有 width, height, depth, italic_correction(ic)
 *   kern, lig. 其中 kern,lig 较少. jtexmath 使用 map 来保存 kern,lig, 使得访问比较容易, 但
 *   不易理解.
 * 逻辑上, 一个字符信息为:
 *   class FontCharInfo
 *     int char_code -- 此字符的码值.
 *     float width, height, depth, ic -- 宽度, 高度, 深度, 倾斜校正.
 *     kern -- 以数组形式放. 每元素为 {char_code,float} 的对.
 *     lig -- 以数组形式放. 每元素为 {char_code,float} 的对.
 */
class FontInfo {
	/** 此字体的名字 */
	public String name;
	
	/** 此字体下字符的总的高度, 包括基线上的 ascent 和基线下的 descent */
	public float hd;
	
	/** 基线上高度 (ascent) */
	public float h;
	/** 基线下深度 (descent) */
	public float d;
	
	public char skewchar = (char)-1;
	
	/** 此字体中所有字符的信息, 当前支持 256 个. 如果更多, 需要想其它办法了. */
	private FontCharInfo[] fci_arr = new FontCharInfo[256];
	
	/**
	 * 添加/设置一个字符信息.
	 */
	public void setAdd(FontCharInfo fci) {
		fci_arr[fci.char_code] = fci;
	}
	
	/**
	 * 得到指定索引的字符信息.
	 * @param index
	 * @return
	 */
	public FontCharInfo get(int index) {
		return fci_arr[index];
	}
	
	/**
	 * 得到所有字符的信息.
	 * @return
	 */
	public FontCharInfo[] getAll() {
		return fci_arr;
	}
	
	/** ?? krn,lig 的 map 实现? 我们需要吗? */
}

/**
 * 表示 kern 的信息对, 是:字符代码+float间距调整值的对.
 *
 */
class KernInfo {
	int char_code;
	float kern;
	
	public KernInfo(int char_code, float kern) {
		this.char_code = char_code;
		this.kern = kern;
	}
	
	public int getCharCode() {
		return this.char_code;
	}
	
	public float getKern() {
		return this.kern;
	}
}

/**
 * 表示 ligature 的信息. 是字符代码, 字符代码的对.
 *
 */
class LigInfo {
	int char_code; // 和 char_code 可以构成 ligature.
	int lig_char;  // 构成之后的 ligature 的字符代码.
	
	public LigInfo(int char_code, int lig_char) {
		this.char_code = char_code;
		this.lig_char = lig_char;
	}
	
	public int getCharCode() {
		return char_code;
	}
	
	public int getLigChar() {
		return lig_char;
	}
}

/**
 * FontCharInfo 的 img 字段的类型.
 *
 */
class CharImg {
	public static CharImg Empty = new CharImg();
	
	public final Integer size;
	public final Integer best;
	
	public float bh;
	public float bd;
	
	public CharImg() {
		this.size = null;
		this.best = null;
	}
	
	public CharImg(Integer size, Integer best) {
		this.size = size;
		this.best = best;
	}
}
