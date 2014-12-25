package jsmath;

/**
 * 处理大部分 HTML 的创建, 以显示数学公式在 HTML 页面上.
 * jsMath.HTML handles creation of most of the HTML needed for
 *  presenting mathematics in HTML pages.
 *
 */
public class HTML {
	public static final float EPSILON = 0.000001f;
	
	/**
	 * 产生一个字符串版本的度量(measurement), 以 em 为单位, 只使用有限精度(小数点之后3位)的数字,
	 *   如果数字接近 0, 则使用数字 0.
	 * Produce a string version of a measurement in ems,
     *  showing only a limited number of digits, and 
     *  using 0 when the value is near zero.
	 * @param m
	 * @return
	 */
	public static String Em(float m) {
		if (Math.abs(m) < EPSILON)
			m = 0;
		// 原来 jsMath 只保留 m 的三位小数点, 但使用的方法是字符串替换, 因而不进行舍入操作.
		String s = String.format("%.3f", m);
		return s + "em"; // 如 0.337em
	}
	
	/**
	 * 创建一个宽度为 w 的水平间距(空白).
	 * Create a horizontal space of width w
	 * @param w
	 * @return 返回产生的 html.
	 * 使用 <SPAN> css 类为 spacer. 为此我们需要研究 spacer 类的具体含义?
	 * 
	 * 在 jsMath 中定义了 CSS '.typeset .spacer' 为 { display: inline-block' }
	 * 其中 inline-block 的定义是 The element is placed as an inline element (on the same line 
	 *   as adjacent content), but it behaves as a block element.
	 *   也即显示类似于 <SPAN>, 行为类似于 <P><TABLE> ?? 行内块元素。
	 * 即display为inline-block的元素既可以像块状元素一样定义高度宽度，又可以和内联元素（比如文字）排列在一行。
	 */
	public static String Spacer(float w) {
		if (w == 0) return ""; // 此时不产生最好, 节省.
		// 原js: jsMath.Browser.msieSpaceFix+'<span class="spacer" style="margin-left:'+this.Em(w)+'"></span>';
		String html = "<span class=\"spacer\" style=\"margin-left:" + HTML.Em(w) + "\"></span>";
		// TODO: ?? msie bug 修复?
		return html;
	}
	
	/**
	 * 创建一个空白的指定尺寸的矩形.
	 *   如果高度(height)比较小, 它被转换为像素, 这样它就在小尺寸下不会不显示出来.
	 * Create a blank rectangle of the given size
     *  If the height is small, it is converted to pixels so that it
     *  will not disappear at small font sizes.
	 * @param w -- ?宽度
	 * @param h -- ?高度
	 * @param d -- ?深度
	 * @param isRule -- ?是否是尺子?
	 * @return
	 * 
	 * TODO: 如果忽略掉 Browser 的兼容性, 该怎么办呢?
	 * 产生的 HTML 例子:
	 *   <span class="blank" style="width:3.142em;height:1.618;vertical-align:-0.386em;"></span>
	 * 在 jsMath 中 CSS blank 为: 
	 *    display:  'inline-block',
     *    overflow: 'hidden',
     *    border:   '0px none',
     *    width:    '0px',  宽高设置为0, 然后在 style 中设置有宽,高.
     *    height:   '0px'
	 */
	public static String Blank(float w, float h, float d, boolean isRule) {
		String backspace = "";
		String style = "";
		if (isRule) {
			// 用宽度为 w 的左边线实现 rule?
			style += "border-left:" + HTML.Em(w) + " solid; ";
			if (JsMath.Browser.widthAddsBorder) { w = 0; }
		}
		
		if (w == 0) {
			if (JsMath.Browser.blankWidthBug) {
				if (JsMath.Browser.quirks) {
					style += "width:1px; ";
					backspace = "<span class=\"spacer\" style=\"margin-right:-1px\"></span>";
				}
			}
		}
		else {
			style += "width:" + HTML.Em(w) + "; ";
		}
		
		//if (d == null) { d = 0; }
		if (h != 0) {
			String H = HTML.Em(h + d); // 总高度.
			if (isRule && h*JsMath.em <= 1.5) {
				H = "1.5px";
				h = 1.5f / JsMath.em;
			}
			style += "height:" + H + "; ";
		}
		if (JsMath.Browser.mozInlineBlockBug) d = -h;
		if (JsMath.Browser.msieBlockDepthBug && !isRule) {
			d -= JsMath.d;
		}
		if (d != 0) {
			style += "vertical-align:" + HTML.Em(-d); // 深度调整, 通过 vertical-align 实现.
		}
		
		return backspace + "<span class=\"blank\" style=\"" + style.trim() + "\"></span>";
	}
	
	/**
	 * 创建一个标尺行(用做分数线).
	 *  Create a rule line for fractions, etc.
	 */
	public static String Rule(float w, float h) {
		if (h < 0) {
			h = JsMath.TeX.default_rule_thickness;    // 0.06f;
		}
		// 实际缺省用矩形实现.
		return Blank(w, h, 0f, true);
	}
	
	/**
	 * 创建支架(Strut) 为??
	 *  Create a strut for measuring position of baseline
	 * @param h
	 * @return
	 */
	public static String Strut(float h) {
		return Blank(1.0f, h, 0, true);
	}
	
	public Object msieStrut(Object h) {
		throw new UnsupportedOperationException();
	}
	
	// 叫 AddClass() 是不是更好?
	/**
	 * 在指定 html 外面添加 <SPAN> 以应用/使能/激活 CSS 类 tclass.
	 * Add a <SPAN> tag to activate a specific CSS class
	 * @param tclass -- &lt;span&gt; 标签的 CSS 的 class.
	 * @param html -- 包装在 &lt;span&gt; 中的 html 代码.
	 * @return <span class=$tclass>$html</span>
	 */
	public static String Class(String tclass, String html) {
		return "<span class=\"" + tclass + "\">" + html + "</span>";
	}
	
	/**
	 * 使用 <SPAN> 放置一些 HTML 在指定的位置.
	 * Use a <SPAN> to place some HTML at a specific position.
     *  (This can be replaced by the ones below to overcome
     *   some browser-specific bugs.)
	 * @param html
	 * @param x -- 左边距离, 使用 CSS margin-left 实现.
	 * @param y -- 上下位置, 使用 CSS top 实现.
	 * @return
	 */
	public static String Place(String html, float x, float y) {
		if (Math.abs(x) < EPSILON) x = 0f;
		if (Math.abs(y) < EPSILON) y = 0f;
		if (x != 0 || y != 0) {
			String span = "<span style=\"position: relative;"; // 用相对定位. 这里需要深入了解 CSS 的知识.
			if (x != 0)
				span += " margin-left:" + HTML.Em(x) + ";";
			if (y != 0)
				span += " top:" + HTML.Em(-y) + ";";
			html = span + "\">" + html + "</span>";
		}
		return html;
	}
	
	/**
	 * 这里 x 和 y 定位是在不同的 <SPAN> 中完成的.
	 * For MSIE on Windows, backspacing must be done in a separate
     *  <SPAN>, otherwise the contents will be clipped.  Netscape
     *  also doesn't combine vertical and horizontal spacing well.
     *  Here the x and y positioning are done in separate <SPAN> tags
	 * @param html
	 * @param x
	 * @param y
	 * @param mw
	 * @param Mw
	 * @param w
	 * @return
	 */
	public static String PlaceSeparateSkips(String html, float x, float y, Object mw, Object Mw, Object w) {
		if (Math.abs(x) < EPSILON) x = 0f;
		
		// return html;
		throw new RuntimeException();
	}
	
	/**
	 * 放置 <SPAN> 在绝对坐标位置.
	 * Place a SPAN with absolute coordinates
	 * @param html
	 * @param x
	 * @param y
	 * @param mw
	 * @param Mw
	 * @param w
	 * @return
	 */
	public static String PlaceAbsolute(String html, float x, float y, float mw, float Mw, float w) {
		if (Math.abs(x) < EPSILON) x = 0;
		if (Math.abs(y) < EPSILON) y = 0;
		String leftSpace = "";  // ? html 左边添加的空白.
		String rightSpace = ""; // ? html 右边添加的空白.
		String width = "";
		
		if (JsMath.Browser.msieRelativeClipBug && mw != 0) {
			leftSpace = HTML.Spacer(-mw);
			x += mw;
			rightSpace = HTML.Spacer(Mw-w);
		}
		if (JsMath.Browser.operaAbsoluteWidthBug) {
			width += " width: " + HTML.Em(w+2);
		}
		
		html = "<span style=\"position:absolute; left:" + HTML.Em(x) + "; " +
				"top:" + HTML.Em(y) + ";" + width + "\">" +
				leftSpace + html + rightSpace +
				"&nbsp;" + // space normalizes line height in script styles
			"</span>";
		
		return html;
	}
	
	/**
	 * ?? 语义是什么呢?
	 * @param html
	 * @param w
	 * @param h
	 * @param d
	 * @param y
	 * @return
	 */
	public static String Absolute(String html, float w, float h, float d, float y) {
		if (y != 0) { // jsMath: y != "none"
			if (Math.abs(y) < EPSILON) y = 0;
			html = "<span style=\"position:absolute; "
					+ "top:" + HTML.Em(y) + "; left:0em;\">"
					+ html + "&nbsp;" // space normalizes line height in script styles
				+ "</span>";
		}
		if (d == 0 /*js: d == "none" */) { d = 0; }
		html += HTML.Blank(w, h-d, d, false);
		if (JsMath.Browser.msieAbsoluteBug) {
			html = "<span style=\"position:relative;\">" + html + "</span>";
		}
		html = "<span style=\"position:relative;" + 
			"display:inline-block;" + 
			"\">" + html + "</span>";
		if (JsMath.Browser.lineBreakBug) {
			html = "<span style=\"display:inline-block; width:" +
				HTML.Em(w) + "\">" + html + "</span>";
		}
		
		return html;
	}
}

