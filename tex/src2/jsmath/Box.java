package jsmath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsmath.itm.*;

/** 
 * 处理 TeX 中的数学盒子以及 jsMath 中的等价的水平盒子(hbox)
 * 
 * jsMath.Box handles TeX's math boxes and jsMath's equivalent of hboxes.
 * 
 * 按照 Box 中具有的属性推断, 可能 Box 表示一个 hbox?
 * 问题:
 *   1. Box 和 Atom, MItem 的关系是什么?
 *   2. 有多少种(box.type) 类型?
 *   3. 哪里使用 Box?
 *
 */
public class Box extends TypeObject { /* 也许有个基类 MItem?? 或 Atom?? */
	/** default height for characters with none specified. */
	// 在 jsMath-fallback-pc.js 中被设置为 0.8f 了.
	public static final float defaultH = 0.8f; 
	
	// === box 的 format ===
	
	/** 未给出格式? */
	public static final String FORMAT_Null = "null";
	/** 文本格式. 在 Text() 函数中创建此格式的 Box 对象. */
	public static final String FORMAT_Text = "text";
	/** 表示此 Box 的格式已经转换为 HTML 了 */
	public static final String FORMAT_Html = "html";
	
	// === box 的 type ======
	
	public static final String TYPE_typeset = "typeset";
	
	// === 属性 =============
	
	/** 宽度 */
	public float w;
	/** 高度 */ 
	public float h;
	/** 深度 */
	public float d;
	
	// ??盒子高度,深度. ?为什么分别有高度, 盒子高度; 深度,盒子深度?
	// 在 Box.Text() 函数中, bh, bd 是通过 EmBoxFor() 计算出来的. 而 h,d 可能会根据配置
	//   做适当的缩减, 从而导致 h != bh, d != bd 的情况发生.
	public float bh;
	public float bd;
	
	// ?(绝对)坐标位置
	public float x;
	public float y;
	public float mw; // ?最小宽度.
	public float Mw; // ?最大宽度.
	public String html; // 输出的 HTML 字符串.
	   /** 内容的格式, 当前取值为 Null, Text, Html 几种. */
	public String format;
	
	// 在 Box.TeX() 函数中填写这三个字段.
	public String style;
	public int size; 
	public String tclass; /* 应该是 css 类名 */
	
	/** 为支持 matrix 功能, 在 Parser.HandleEntry() 中使用. */
	public Object entry;
	
	/**
	 * 使用指定参数构造一个 Box 的新实例.
	 * @param format
	 * @param text
	 * @param w
	 * @param h
	 * @param d
	 */
	protected Box(String format, String text, float w, float h, float d) {
		// TODO: if (d == null) d = jsMath.d;  // 也许在外面实现.
		this.type = TYPE_typeset; // 做什么用的? 为什么名字和 item.type 相同?
			// 我们现在让 Box 也实现接口 IObjectWithType, 从而可以得到 type 属性.
		this.w = w; 
		this.h = h;
		this.d = d;
		this.bh = h; // 推测: box.h 高度
		this.bd = d; // 推测: box.d 深度
		this.x = 0;  // 推测: 坐标位置初始为 0. (相对其应该所在位置)
		this.y = 0;
		this.mw = 0;
		this.Mw = w;
		this.html = text;
		this.format = format; // 此 Box 当前的格式
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Box{type=").append(this.type)
			.append(",w=").append(w).append(",h=").append(h).append(",d=").append(d)
			.append(",bh=").append(bh).append(",bd=").append(bd);
		str.append(",x=").append(x).append(",y=").append(y);
		str.append(",tclass=").append(tclass).append(",format=").append(format);
		str.append("}");
		return str.toString();
	}
	
	/**
	 * 添加类(class)和样式(style)到一个文本盒子(box) (即, 最后完成未定位的 HTML 为盒子)
     *  Add the class and style to a text box (i.e., finalize the
     *  unpositioned HTML for the box).
	 */
	public Box Styled() {
		if (FORMAT_Text.equals(this.format)) {
			// 给 html 添加上 <span class='tclass'>...</span> 外壳
			this.html = Typeset.AddClass(this.tclass, this.html);
			// 为所给的 tex 显示样式加上 css 类
			this.html = Typeset.AddStyle(this.style, this.size, html);
			this.tclass = null; // js: delete this.tclass;
			this.style = null;  // js: delete this.style;
			this.format = FORMAT_Html;
		}
		return this;
	}
	
	/**
	 * Recompute the box width to make it more accurate.
	 */
	public Box Remeasured() {
		// 原js 使用 EmBoxFor() 重新计算 html 的大小, 我们没有条件使用, 就暂时不实现了.
		return this;
	}
	
	/**
	 * 构造一个空的 box 并返回.
	 * @return
	 */
	public static Box Null() {
		return new Box("null", "", 0f, 0f, 0f);
	}
	
	/**
	 * 一个盒子, 其仅包含文本, 文本的 class, style 尚未添加. (这样我们能合并...)
	 *   它获得文本的尺寸, 如果需要的话.
	 * A box containing only text whose class and style haven't been added
     *  yet (so that we can combine ones with the same styles).  It gets
     *  the text dimensions, if needed.  (In general, this has been
     *  replaced by TeX() below, but is still used in fallback mode.)
     * (一般
	 * @return
	 */
	public static Box Text(String text, String tclass, String style, int size, float a, float d) {
		String html = Typeset.AddClass(tclass, text); // 在 text 外面包装上 css=tclass
		html = Typeset.AddStyle(style, size, html); // class=sizeN
		
		// TODO: Object BB = JsMath.EmBoxFor(html); // 这里需要对产生的 html 用浏览器计算其高度, 宽度信息.
			// 而我们在 java 中该怎么办呢?
		
		throw new UnsupportedOperationException();
	}
	
	// Text() 函数的简化版本, c 是一个字符, 而 fci 是字符 c 的字符信息.
	private static Box simple_Text(FontCharInfo fci, String c, String tclass, String style, int size, Float a, Float d) {
		// 得到 box.w, h; 由于 我们这里 fci 中可能只有 tclass 的部分, 没有 style,size 部分
		//   所以我们还需乘上一个比例因子.
		BoxSize BB = fci.emBoxFor(); 
		
		TeXParam TeX = Typeset.TeX(style, size);
		float scale_bb = TeX.scale;
		BB.w *= scale_bb; BB.h *= scale_bb;

		// 这里似乎是在计算盒子的深度, 但是我们实际上可以将深度也预先计算出来放在 fci 里面的...
		float bd; // 推测是 box depth?
		if ("cmsy10".equals(tclass) || "cmex10".equals(tclass)) {
			bd = BB.h - TeX.h;
		} else {
			bd = TeX.d * BB.h / TeX.hd; // 按比例计算?? 仔细研究: TeX.d, TeX.hd 怎么计算的?
		}
		// 构造该字符对应的一个盒子. 格式现在为 "text", 宽度, 高度, 深度分别给出
		Box box = new Box(FORMAT_Text, c, BB.w, BB.h-bd, bd);
		box.style = style; box.size = size; box.tclass = tclass;
		// 有少量字符配置有 d, a 的值.
		if (d != null) {
			box.d = d*TeX.scale;
		} else {
			box.d = 0;
		}
		if (a == null || a == 1) { // 含义是什么?
			box.h = 0.9f * TeX.M_height;
		} else {
			// 似乎只比 x 的高度多一点点?? 再加上 a 的修正?
			box.h = 1.1f * TeX.x_height + TeX.scale*a;
		}
		
		return box;
	}


	/**
	 * 产生一个包含指定 TeX 字符以指定字体的盒子. 
	 *   这个盒子是一个文本盒子(text box)(类似于上面那个), 因此使用相同字体的字符可以合并(combine,组合).
	 *   ?这里将盒子合并/组合为一个?
	 *   
	 * Produce a box containing a given TeX character from a given font.
     *  The box is a text box (like the ones above), so that characters from
     *  the same font can be combined.
	 * @param c -- 字符编码值
	 * @param font -- 字体名字, 如 "cmr10"
	 * @param style -- 显示样式, 如 "S", "D'" 等.
	 * @param size -- 大小
	 * 
	 * @return 返回一个 Box 对象, 其 type="typeset", format="text", 内容为字符 C.
	 */
	public static Box TeX(int C, String font, String style, int size) {
		return TeXfallback(C, font, style, size);
	}
	
	// 原本的 TeX() 函数的实现. 其假设有 TeX 字体安装在用户的机器上.
	public static Box TeX_origin(int C, String font, String style, int size) {
		FontInfo fi = JsMath.TeX.getFontByName(font);
		FontCharInfo c = fi.get(C);	// 该字体下该字体的信息. 每个字符包括 宽度, 深度, 倾斜, krn, lig 等信息.
		// 原js 有一些奇怪的代码, 如 if (c.d == null) {c.d = 0}; if (c.h == null) {c.h = 0} 我们暂时不需要.
		if (c.img != null && c.c != null) {
			int ss = Typeset.StyleSize(style, size); // 推断: 得到该 style 下实际 size (到 Img.fonts[] 的索引)
			Box.TeXIMG(font, C, ss); // 给该字符 c 的字体信息添加上 img 信息.
		}
		float scale = Typeset.TeX(style, size).scale;
		Box box = new Box(FORMAT_Text, c.c, c.width*scale, c.height*scale, c.depth*scale);
		box.style = style;
		box.size = size;
		
		if (c.tclass != null) {
			box.tclass = c.tclass;
			if (c.img != null) {
				box.bh = c.img.bh;
				box.bd = c.img.bd;
			} else {
				box.bh = scale*JsMath.h;
				box.bd = scale*JsMath.d;
			}
		} else {
			// 推测: tclass 是指 css 的 class 名.
			box.tclass = font;
			box.bh = scale * fi.h;  // h, d 是某个地方计算出来的. 在初始化字体的时候计算的.
			box.bd = scale * fi.d; 
		}
		
		return box;
	}
	
	/**
	 * 原来的js 代码是处理没有预计算字符尺寸的情况, 现在在 java 中我们将适当改造.
	 * In fallback modes, handle the fact that we don't have the
     *  sizes of the characters precomputed
	 * @param c
	 * @param font
	 * @param style
	 * @param size
	 * @return
	 */
	public static Box TeXfallback(int C, String font, String style, int size) {
		FontInfo fi = JsMath.TeX.getFontByName(font);
		FontCharInfo c = fi.get(C);
		if (c.tclass == null || c.tclass.length() == 0) {
			c.tclass = font; // 这里直接改 t.class 可能不好.
		}
		if (c.img != null) {
			// 给出了图像字体, 则使用原来的 TeX() 处理.
			return TeXnonfallback(C, font, style, size);
		}
		if (c.h != null && c.a == null) { // 某些字符配置有 h, a 的值. 见 CTeX 中关于字体信息初始化部分.
			c.a = c.h - 1.1f * CTeX.x_height;
		}
		Float a = c.a; Float d = c.d;  // ascend, 指字母 a 的高度吗?
		
		// 这里我们使用 Text() 函数的简化版本, 产生 box 对象. 其中 FontCharInfo 已经预先计算了 BB.w, BB.h 了.
		Box box = Box.simple_Text(c, c.c, c.tclass, style, size, a, d);
		float scale = Typeset.TeX(style, size).scale;
		if (true /*c.bh != 0*/) {
			// 也就意味着这里要预先计算出 c.bh, c.bd 的值. 也就是用下面的 EmBoxFor() 的方法来计算.
			// 我们在 s2.html 中, 使用 EmBoxFor() 预先计算出来了.
			box.bh = c.bh * scale;
			box.bd = c.bd * scale;
		} else {
			// js 在这里使用了 EmBoxFor() 来计算 box.bd, box.bh; 我们已经预先计算出来放在了 c.bh, c.bd 
			//   字段了, 所以不会走到此分支.
			throw new UnsupportedOperationException();
		}
		// 原 js 中修复 msie 的代码, 先忽略.
		return box;
	}
	
	// 暂时未实现的.
	public static Box TeXnonfallback(int C, String font, String style, int size) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 设置字符的字符串到适合的图像文件. (如果不考虑图像字体, 则可以先忽略这里?)
	 * 
	 * Set the character's string to the appropriate image file
	 * @param font - 字体名字.
	 * @param C - 字符编码
	 * @param size - 大小. 从 Typeset.StyleSize() 中计算出来的, 假定是一个索引值(到 Img.fonts[]).
	 * @return
	 */
	public static void TeXIMG(String font, int C, int size) {
		FontInfo fi = JsMath.TeX.getFontByName(font);
		FontCharInfo c = fi.get(C); // 得到该字符的信息.
		
		// ?语义: 如果 img 的 size,best 已经相同了, 则不用再重新计算了...
		if (c.img != null && c.img.size != null && c.img.size == size &&
				c.img.best != null && c.img.best == JsMath.Img.best) return;
		boolean mustScale = (JsMath.Img.scale != 1); // 是否要缩放图像字.?
		int id = JsMath.Img.best + size - 4; // 计算一个新索引. 
		if (id < 0) { // 确保 id 在 [0..lenght) 区间内...
			id = 0;
			mustScale = true;
		}
		else if (id >= JsMath.Img.fonts.length) {
			id = JsMath.Img.fonts.length-1;
			mustScale = true;
		}
		
		int font_size = JsMath.Img.fonts[id];
		//TODO:Object imgFont = JsMath.Img[font_size];
		
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 创建一个具有指定宽度 width 的 spacer.
	 * A box containing a spacer of a specific width
	 * @param w
	 * @return 返回一个 Box 对象, 其已经构造好了相应的 html.
	 */
	public static Box Space(float w) {
		return new Box("html", HTML.Spacer(w), w, 0f, 0f);
		
	}
	
	/**
	 * 构造包含一个水平标尺(rule)的盒子.
	 * A box containing a horizontal rule
	 * @param w
	 * @param h
	 * @return
	 */
	public static Box Rule(float w, float h) {
		if (h < 0) h = JsMath.TeX.default_rule_thickness;
		String html = HTML.Rule(w, h);
		return new Box("html", html, w, h, 0);
	}
	
	/**
	 * 从 TeX 字体中获取字符信息, 以及确保其尺寸信息存在.
	 * Get a character from a TeX font, and make sure that it has
     *  its metrics specified.
	 * @param code
	 * @param font
	 * @return
	 */
	public static FontCharInfo GetChar(int code, String font) {
		FontInfo fi = JsMath.TeX.getFontByName(font);
		FontCharInfo c = fi.get(code);
		if (c.img != null) {
			TeXIMG(font, code, 4); // 可能我们现在不要支持此功能??
		}
		
		if (c.tclass == null) {
			// TODO: 这里怎么又改...?
			c.tclass = font; 
		}
		
		// 这里要计算出 w 的值, 现在问题是部分字体没有预先计算 w 值, 且其 tclass 可能也变??
		//if (!c.computedW) { // 又冒出这个属性了...
			// 看来这里要预先算出 EmBoxFor() 了... 
			// TODO: c.w = EmBoxFor(Typeset.AddClass(c.tclass, c.c)).w;
			if (c.w == null) c.w = c.width; // 我们要想办法计算出较精确的 w 值.
			if (c.h == null) c.h = (float)Box.defaultH;
			if (c.d == null) c.d = 0f;
			//c.computedW = true;
		//}
		
		return c;
	}
	
	/** 表示 DelimBestFit() 函数的返回值: RI -- Return Info. */
	public static class BestDelimRI {
		// JS 中表示为: [c, font, '', H];
		public int c; // 字符编码.
		public String font; // 字体名字.
		public String style;
		public float H;
		
		public BestDelimRI(int c, String font, String style, float H) {
			this.c = c; this.font = font; this.style = style; this.H = H;
		}
	}
	
	/**
	 * 找一个 TeX 定界符字符, 其适合(匹配)指定的高度.
	 *  返回该字符,字体,样式和实际使用的高度.
	 * Locate the TeX delimiter character that matches a given height.
     *  Return the character, font, style and actual height used.
	 * @param H -- 需要的定界符高度, 或说该定界符能包含起该高度的内容.
	 * @param c -- 字符编码值 (如 =delim[2])
	 * @param font -- 字体索引 (如 =delim[1])
	 * @param style -- 显示样式.
	 * @return 返回 BestDelimRI
	 */
	public static BestDelimRI DelimBestFit(float H, int cc, int font, String style) {
		if (cc == 0 && font == 0) return null;
		Integer c = cc;
		
		FontCharInfo C; 
		float h;
		String font_name = CTeX.fam[font];
		boolean isSS = (style.length() >= 2 && style.charAt(1) == 'S'); // SS, 或 SS'
		boolean isS = (style.charAt(0) == 'S');  // S, S', SS, SS' 几种.
		
		while (c != null) {
			FontInfo fi = JsMath.TeX.getFontByName(font_name);
			C = fi.get(c);
			// 我们这里略微修改了原 js 语义, 使得不修改 FontCharInfo 对象. 现在这里 c.h, c.d 总还有些疑义??
			float C_h = (C.h == null) ? (float)Box.defaultH : C.h;
			float C_d = (C.d == null) ? 0f : C.d;
/*			if (C.h == null) {
				C.h = (float)Box.defaultH; // 这里直接修改 FontChar 对象, 不太好...
			}
			if (C.d == null) {
				C.d = 0.0f;
			} */
			h = C_h + C_d; 
			if (C.delim != null) { 
				// 这不是一个普通定界符... 它需要自己组装出来......
				return new BestDelimRI(c, font_name, "", H);
			}
			if (isSS && 0.5f*h >= H) {
				return new BestDelimRI(c, font_name, "SS", 0.5f*h);
			}
			if (isS && 0.7f*h >= H) {
				return new BestDelimRI(c, font_name, "S", 0.7f*h);
			}
			if (h >= H || C.n == null) { // C.n 表示下一个更大的同形状的定界符.
				return new BestDelimRI(c, font_name, "T", h);
			}
			c = C.n; // 找下一个大一点的定界符.
		}
		
		return null;
	}
	
	/**
	 * 实现: 根据 browser 选择 DelimExtendRelative() 或 DelimExtendAbsolute()
	 * @param H -- 高度.
	 * @param c -- 字符编码值
	 * @param font -- 字体名
	 * @param a -- 在调用处有给出为 TeX.axis_height, 
	 * @return
	 */
	public static Box DelimExtend(float H, int c, String font, float a, boolean nocenter) {
		// 当前我们先选择 DelimExtendRelative() 的实现.
		return DelimExtendRelative(H, c, font, a, nocenter); 
	}
	
	/**
	 * 为可伸缩的定界符创建指定高度的 HTML, 居中或不居中. 这个实现使用相对定位.
	 * Create the HTML needed for a stretchable delimiter of a given height,
     *  either centered or not.  This version uses relative placement (i.e.,
     *  backspaces, not line-breaks).  This works with more browsers, but
     *  if the font size changes, the backspacing may not be right, so the
     *  delimiters may become jagged.
	 * @param H
	 * @param c
	 * @param font
	 * @param a
	 * @param nocenter
	 * @return
	 */
	public static Box DelimExtendRelative(float H, int c, String font, float a, boolean nocenter) {
		FontInfo fi = JsMath.TeX.getFontByName(font);
		FontCharInfo C = fi.get(c);
		assert(C.delim != null); // CFSH.style 非空, 保证这里的 C.delim 存在.
		
		FontCharInfo top = GetChar(C.delim.top != 0 ? C.delim.top : C.delim.rep, font);
		FontCharInfo rep = GetChar(C.delim.rep, font);
		FontCharInfo bot = GetChar(C.delim.bot != 0 ? C.delim.bot : C.delim.rep, font);
		String ext = Typeset.AddClass(rep.tclass, rep.c);
		float w = rep.getCalcW();   // rep.w;
		float h = rep.h + rep.d;
		float y; float Y; String html; float dx; int i; int n;
		
		if (C.delim.mid != 0) { // braces `{'
			FontCharInfo mid = GetChar(C.delim.mid, font);
			// 计算需要多少个重复的 rep 元素.
			n = (int)Math.ceil((H-(top.h+top.d)-(mid.h+mid.d)-(bot.h+bot.d))/(2*(rep.h+rep.d)));
			// 计算 top + n 个 rep + mid + bot 的高度.
			H = 2*n*(rep.h+rep.d) + (top.h+top.d) + (mid.h+mid.d) + (bot.h+bot.d);
			if (nocenter) { 
				y = 0;
			} else {
				y = H/2 + a;
			}
			Y = y;
			// 推断: 按照计算的位置放置 top,mid,bot, 以及 2n 个 rep.
			html = HTML.Place(Typeset.AddClass(top.tclass, top.c), 0, y-top.h);
			html += HTML.Place(Typeset.AddClass(bot.tclass, bot.c), -(top.w+bot.w)/2, y-(H-bot.d));
			html += HTML.Place(Typeset.AddClass(mid.tclass, mid.c), -(bot.w+mid.w)/2, y-(H+mid.h-mid.d)/2);
			dx = (w-mid.w)/2;
			if (Math.abs(dx) < 0.0001f) dx = 0;
			if (dx != 0) {
				html += HTML.Spacer(dx);
			}
			y -= top.h + top.d + rep.h;
			for (i = 0; i < n; ++i) {
				html += HTML.Place(ext, -w, y-i*h);
			}
			y -= H/2 - rep.h/2;
			for (i = 0; i < n; ++i) {
				html += HTML.Place(ext, -w, y-i*h);
			}
		} else { // everything else
			n = (int)Math.ceil((H - (top.h+top.d) - (bot.h+bot.d))/(rep.h+rep.d));
			// make sure two-headed arrows have an extender 
			if (top.h+top.d < 0.9f*(rep.h+rep.d)) {
				n = Math.max(1, n);
			}
			H = n*(rep.h+rep.d) + (top.h+top.d) + (bot.h+bot.d);
			if (nocenter) { y = 0; } else { y = H/2+a; }
			Y = y;
			html = HTML.Place(Typeset.AddClass(top.tclass, top.c), 0, y-top.h);
			dx = (w-top.w)/2;
			if (Math.abs(dx) < 0.0001) { dx = 0; }
			if (dx != 0) { 
				html += HTML.Spacer(dx);
			}
			y -= top.h+top.d + rep.h;
			for (i = 0; i < n; ++i) {
				html += HTML.Place(ext, -w, y-i*h);
			}
			html += HTML.Place(Typeset.AddClass(bot.tclass, bot.c), -(w+bot.w)/2, Y-(H-bot.d)); 
		}
		
		if (nocenter) { h = top.h; } else { h = H/2+a; }
		Box box = new Box("html", html, rep.w, h, H-h);
		
		fi = JsMath.TeX.getFontByName(font);
		box.bh = fi.h; box.bd = fi.d;
		
		return box;
	}
	
	public static Object DelimExtendAbsolute(Object H, Object c, Object font, Object a, Object nocenter) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 得到指定定界符(delimiter)在指定高度的的 HTML.
	 * 要么返回一个单个字符, 或更复杂的 HTML 为需要伸缩的定界符.
	 * Get the HTML for a given delimiter of a given height.
     *  It will return either a single character, if one exists, or the
     *  more complex HTML needed for a stretchable delimiter.
	 * @param H -- 需要的定界符的高度, 根据该高度选择/生成适合大小的定界符.
	 * @param delim -- 推测是定界符对象, 类型为 DelimInfo
	 * @param style -- 显示样式, 如 D, T 等.
	 * @param nocenter -- 暂时略.
	 * @return
	 */
	public static Box Delimiter(float H, DelimInfo delim, String style, boolean nocenter) {
		int size = 4; // ### pass this?
		TeXParam TeX = Typeset.TeX(style, size);
		if (delim == null) {
			// 如果没有任何 delim, 则创建一点指定的空白.
			return Box.Space(TeX.nulldelimiterspace);
		}
		
		// 在研究根式的时候, 必须要处理 delim 的选择代码了. 那现在就让我们研究吧!
		// CFSH: [Char, Font, Style, H] 四元组, 在 js 中的表示方式.
		BestDelimRI CFSH = DelimBestFit(H, delim.indx(2), delim.indx(1), style);
		if (CFSH == null || CFSH.H < H) {
			// 换用大一点的那个版本.
			CFSH = DelimBestFit(H, delim.indx(4), delim.indx(3), style);
		}
		if (CFSH == null)
			return Space(TeX.nulldelimiterspace); // 没有适当的定界符, 或根本没有, 则用空的代替...
		
		// JS: style == "", 程序含义: 该定界符是可组装的, 此时 C.delim 非空.
		if (CFSH.style.length() == 0) {
			// 使用垂直方式组装的方法组装起这个复杂的定界符!
			return DelimExtend(H, CFSH.c, CFSH.font, TeX.axis_height, nocenter);
		}
		
		int a;
		Box box = Box.TeX(CFSH.c, CFSH.font, CFSH.style, size);
		box = box.Styled();
		// 计算 box.y 的值.
		if (!nocenter) {
			box.y = -((box.h+box.d)/2 - box.d - TeX.axis_height);
		}
		if (Math.abs(box.y) < 0.0001) { 
			box.y = 0; 
		}
		if (box.y != 0) {
			// 为什么 box.y 非0 的时候要调用 SetList() 呢?? SetList() 可以额外完成垂直定位?
			List<TypeObject> boxes = new ArrayList<TypeObject>();
			boxes.add(box);
			box = Box.SetList(boxes, CFSH.style, size);
		}
		
		return box;
	}
	
	public static Object GetCharCode(Object code) {
		throw new UnsupportedOperationException();
	}
	
	public static Object AddClass(Object tclass, Object html, Object font) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 布局一个表格.
	 * @param size
	 * @param table
	 * @param align
	 * @param cspacing -- 推测 c表示 col, 意为列之间的间距.
	 * @param rspacing -- 推测 r表示 row, 意为行之间的间距.
	 * @param vspace
	 * @param useStrut
	 * @param addWidth
	 * @return
	 */
	public static Box Layout(int size, MatrixTable table, SafeList<String> align, 
			SafeList<Float> cspacing, SafeList<Dimen> rspacing, Float vspace, Float useStrut, Float addWidth) {
		// 为了后面 LayoutAbsolute() 或 relative 方便, 这里对参数 align, cspacing, rspacing 
		//    预处理一下可能比较好...
		if (align == null) align = new SafeList<String>("");
		if (cspacing == null) cspacing = new SafeList<Float>(new Float(0f));
		if (rspacing == null) rspacing = new SafeList<Dimen>(Dimen.ZERO_D);
		if (useStrut == null) useStrut = 1.0f;
		if (vspace == null) vspace = 0f;
		if (addWidth == null) addWidth = 1.0f;

		// 在这里根据某条件选择 LayoutRelative() 或 LayoutAbsolute()
		//   我们这次选择 absolute 模式进行试验.
		return LayoutAbsolute(size, table, align, cspacing, rspacing, vspace, useStrut, addWidth);
	}
	
	
	public static Box LayoutRelative(int size, Object table, String[] align, 
			float[] cspacing, Dimen[] rspacing, Object vspace, Object useStrut, Object addWidth) {
		throw new UnsupportedOperationException("Not impl");
	}
	
	/* 测试用例:
	 *  \cases {x \cr y} 最简单的一个表格.
	 */
	/**
	 * 绝对定位?方式进行表格排版(array 或 matrix).
	 * Create the HTML for an alignment (e.g., array or matrix)
     *  Use absolute position for elements in the array.
     * 
     * @param size -- 缺省字体尺寸.
     * @param table -- 表格的数据, 抽象类型为 MatrixTable ~= List<Row> ~= List<List<Box>>
     * @param align -- 列的对齐方式.
     * @param cspacing -- 列间距.
     * @param rspacing -- 行间距. [0] 表示第一行上面的间距, [1] 表示第一行和第二行之间的间距. 依次类推. 
     *    此数组的元素数量=表格行数+1
	 */
	public static Box LayoutAbsolute(int size, MatrixTable table, SafeList<String> align, 
			SafeList<Float> cspacing, SafeList<Dimen> rspacing, float vspace, float useStrut, float addWidth) {
		assert(table.notNull());
		assert(align.notNull());
		assert(cspacing.notNull());
		assert(rspacing.notNull());
		
		int rows = table.size(); // 为下面方便使用. 表示表格的行数.
		// get row and column maximum dimensions
		float scale = JsMath.sizes[size]/100f;
		float HD = useStrut*(JsMath.hd - 0.01f)*scale; // 推测: 最小单元高度, 在使用 Strut 情况下. 否则应该为0
		float dy = (vspace)*scale/6f; // 原 js: (vspace || 1)
		float[] W = new float[table.get_max_cols()];   // 每列的最大宽度.  
		float[] H = new float[rows]; // 所有行的高度.
		float[] D = new float[rows]; // 所有行的深度
		
		int i; int j; MatrixRow row;
		// 第0步: 初始化 W,H,D
		for (i = 0; i < W.length; ++i) W[i] = 0.0f;
		for (i = 0; i < H.length; ++i) H[i] = 0.0f;
		for (i = 0; i < D.length; ++i) D[i] = 0.0f;
		
		// 第1步: 计算每行的高度(H),深度(D),每列的最大宽度(W)
		for (i = 0; i < rows; ++i) { // 遍历所有行.
			// 根据这里, rspacing[] 的数组元素个数=表的行数
			if (rspacing.get(i) == null) {
				rspacing.set(i, Dimen.ZERO_D);
			}
			
			row = table.get(i); // 得到该行.
			H[i] = useStrut * JsMath.h * scale; // ?设置高度 初值.
			D[i] = useStrut * JsMath.d * scale; // 深度.
			for (j = 0; j < row.size(); ++j) { // row.size() -- 列数.
				Box box_j = row.get(j);   // 可能为 null??
				// 无法支持, 只好略: row[j] = row[j].Remeasured();
				if (box_j.h > H[i]) { H[i] = box_j.h; } // 该行的最大高度.
				if (box_j.d > D[i]) { D[i] = box_j.d; } // 最大深度.
				if (box_j.w > W[j]) { // 该列的最大宽度.
					W[j] = box_j.w;
				}
			}
		}
		
		if (rspacing.get(rows) == null) {
			rspacing.set(rows, Dimen.ZERO_D);
		}
		
		// 上面已经计算出每行的最大 h,d, 每列的最大 w;
		// 第2步: 计算表格总高度 -- y. 以及表格总的 h,d 值. (沿着中轴垂直对齐)
		// get the height and depth of the centered table.
		float x = 0f;
		float y = (rows - 1)*dy + rspacing.get(0).fvalue(); // ? rspacing[0] 可能不存在?
		for (i = 0; i < rows; ++i) { // H,D .length 相同, 应该都是行数(rows)
			// 推测: y 这里计算的是表格总高度. 加上行之间的间距值.
			y += Math.max(HD, H[i]+D[i]) + rspacing.get(i+1).fvalue();
		}
		float h = y/2f + JsMath.TeX.axis_height; // 在轴之上部分的高度吧...
		float d = y - h; // 轴之下的深度...
		
		// 第3步: 布局单元格. 这里是先循环列, 再循环行.
		// lay out the columns
		String html = ""; 
		Box entry; 
		float w = addWidth/6f * scale; // 相当于单元格的 x 坐标初值.
		for (j = 0; j < W.length; j++) { // W: 每列的最大宽度.
			y = H[0] - h + rspacing.get(0).fvalue(); // 单元格垂直 y 坐标的值...
			for (i = 0; i < rows; ++i) { 
				row = table.get(i);
				entry = row.get(j);
				if (entry != null && !entry.format.equals("null")) {
					// 该单元存在, 且不是 null_box
					String row_align = align.get(j); 
					if ("l".equals(row_align)) {
						x = 0; // 左对齐
					} else if ("r".equals(row_align)) {
						x = W[j] - entry.w;  // 右对齐.
					} else {
						x = (W[j] - entry.w)/2f;  // 居中对齐.
					}
					// 完成`绝对'布局. 该单元格位于{x=c_x, y=c_y}; 我们要仔细研究 absolute 布局的本质才能理解这里的坐标. 
					float c_x = w+x;
					float c_y = y - Math.max(0, entry.bh-JsMath.h*scale);
					html += HTML.PlaceAbsolute(entry.html, c_x, c_y, 
							entry.mw, entry.Mw, entry.w);
				}
				if (i+1 < rows) {
					y += Math.max(HD, D[i]+H[i+1]) + dy + rspacing.get(i+1).fvalue();
				}
			}
			if (cspacing.get(j) == null) {
				cspacing.set(j, scale);
			}
			w += W[j] + cspacing.get(j); // 下一个单元格的位置.
		}
		
		// 计算表格的总宽度 -- w.
		// get the full width
		w = -cspacing.get(W.length-1) + addWidth*scale/3; // 由于下面叠加会多加上 cspacing[W.size-1], 所以这里减去?
		for (i = 0; i < W.length; ++i) {
			w += W[i] + cspacing.get(i);
		}
		
		// 表格两侧各添加 1/6 的间距.
		html = HTML.Spacer(addWidth*scale/6f) + html + HTML.Spacer(addWidth*scale/6f);
		if (JsMath.Browser.spanHeightVaries) { 
			y = h - JsMath.h; 
		} else  {
			y = 0;
		}
		if (false/*jsMath.Browser.msie8HeightBug*/) {
			y = d-JsMath.d;
		}
		
		// 构造为最后的表格.
		html = HTML.Absolute(html, w, h+d, d, y);
		Box box = new Box("html", html, w+addWidth*scale/3f, h, d);
		return box;
	}

	
	
	public static Object InternalMath(Object text, Object size) {
		throw new UnsupportedOperationException();
	}
	
	public static Object safeHTML(Object s) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 转换一个任意的 box 到一个 typeset box. 例如, 制作一个 HTML 版本的内容的盒子, 
	 *   在期待的 (x,y) 的位置.
	 * Convert an abitrary box to a typeset box.  I.e., make an
     *  HTML version of the contents of the box, at its desired (x,y)
     *  position.
     *  
	 * @param box -- 现在规定一定是 Atom 类型, 是这样吗? (这个参数名字叫 atom 可能更准确一些.)
	 * @param style
	 * @param size
	 * @param addstyle - 控制是否调用 box.Styled()
	 * @return 现在已知主要路径返回为 Box 对象.
	 * 
	 * (现在已知) 在 MList.AtomizeRaise() 里面调用 Box.Set()
	 */
	public static Box Set(TypeObject box, String style, int size, boolean addstyle) {
		if (box != null && box.type != null) {
			// box.type == "typeset", ? 表示已经 typeset 过了? 我们是否可以不用重复调用入?
			if ("typeset".equals(box.type)) {
				return (Box)box;   // box 此时应该是 Box 类型的.
			}
			
			// 如果 box.type=="mlist", 则其内容是 "mlist", box 是一个 MlistAtom 类的实例.
			if ("mlist".equals(box.type)) {
				MListField mlist_atom = (MListField)box;
				mlist_atom.mlist.Atomize(style, size);
				
				return mlist_atom.mlist.Typeset(style, size);
			}
			
			// 如果 box.type=="text", 在 JsMath.mItem.TextAtom() 中设置.
			//   我们在 java 中实现为 HtmlTextAtom.
			if ("text".equals(box.type)) {
				HtmlTextField txt_atom = (HtmlTextField)box;
				// box 也是 IObjectWithType 类型的.
				// Box.Text() 函数我们现在暂时无法实现... 这里假定为抛出异常! EXCEPTION!
				box = Box.Text(txt_atom.text, txt_atom.tclass, style, size, txt_atom.ascend,
						txt_atom.descend); // 原js: .descend || null.
			}
			// else 则现在 box.type 应该是 "TeX" 表示是一个 CharAtom.
			
			// 调用 TeX() 方法, 其中... (构造一个测试用例, 进入这里)
			TeXCharField ch_a = (TeXCharField)box;
			// 将 box 转换为 Box{type=typeset, format=text} 对象.
			Box rbox = Box.TeX(ch_a.c, ch_a.font, style, size);
			if (addstyle) {
				rbox.Styled();
			}
			return rbox;
		}
		
		// 否则返回一个空的盒子.
		return Box.Null();
	}

	/**
	 * 转换一个 box 的列表为一个单一的 typeset 盒子. 也即, 最终产生该 box 列表的 HTML, 进行合适的定位.
	 * Convert a list of boxes to a single typeset box.  I.e., finalize
     *  the HTML for the list of boxes, properly spaced and positioned.
	 * @param boxes -- 在调用处, 可能是 mlist , 或者是 [] 数组. 我们该如何处理?
	 *    我们假设其是 List<MItem> 介于两者之间比较好?
	 * @param style
	 * @param size
	 * @return
	 * 
	 * boxes 中的元素, 现在已知有 MItem, Box 两种类型.
	 */
	public static Box SetList(List<TypeObject> boxes, String style, int size) {
		// 结果.
		List<MItem> mlist = new ArrayList<MItem>();
		
		for (int i = 0; i < boxes.size(); ++i) {
			TypeObject box = boxes.get(i); // 遍历 boxes. 其中可能有 Box, MItem 等项目.
			if ("typeset".equals(box.type)) { 
				// 这应该是一个 Box 对象..., aitem.nuc = Box 对象. 
				//  (本质是 Box 包装为 MItem 项目, 其 item.type = '??' )
				AtomItem aitem = MItem.Typeset((Box)box);
				mlist.add(aitem);
			}
			else {
				// 否则类型一定应该是 MItem 的. 直接加入到 mlist 中.
				mlist.add((MItem)box);
			}
		}
		
		// 此时 mlist 中应都是 MItem 项目. 执行 Typeset() 过程.
		Typeset typeset = new Typeset(mlist);
		return typeset.DoTypeset(style, size); // 返回为一个单一的 Box.
	}
}
