package jsmath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsmath.itm.*;

/**
 * 这个对象处理大多数 TeX-特定的处理.
 *   现在已知主要是 style, size 方面的处理. 根据 style, size 得到 factor 等.
 * 
 * The Typeset object handles most of the TeX-specific processing
 * 
 * 但是这里 Typeset 被构造为对象, 暂时不知道其含义.
 *
 */
public class Typeset {
	/** 在构造中设置为 "typeset", 现在暂时不知道其作用. */
	public String type;
	
	/** 应该是 MList 类型的. */
	public List<MItem> mlist;
	
	/** 在 DoTypeset() 函数中设置. */
	private String style = "";
	private int size;
	private float w;
	private float mw;
	private float Mw;
	private float h;
	private float d;
	private float bh;
	private float bd;
	
	// 下面这些字符串都初始化为空串. (不要是 null)
	private String tbuf = "";   // text buffer (具有相同 tclass, style 的文本缓存)
	private String tclass = ""; // tclass of text buffer (其 tclass 值)
	private String cbuf = "";   // classed buffer (具有相同 tclass, 但 style 不同的 html 缓存)
	private String hbuf = "";   // html buffer?? (产生的 html 的缓存)
	private float tx;
	private float hx;
	private float x;
	private float dx;
	
	
	/**
	 * 构造.
	 * @param mlist
	 */
	public Typeset(List<MItem> mlist) {
		this.type = "typeset";	// 这个对象有这个属性做什么用?
		this.mlist = mlist;
	}
	
	/** 上标的 显示样式表. (参见原本 441页, 附录) */
	public static final Map<String, String> upStyle = _init_up_style();
	// 这里用映射表实现的, 如果考虑将这些变成数值, 然后用数组也许也挺好的.
	public static Map<String, String> _init_up_style() {
		Map<String, String> m = new HashMap<String, String>();
		
		m.put("D", "S"); // D 的上标显示类型为 S, 以下类推.
		m.put("T", "S");
		m.put("D'", "S'");
		m.put("T'", "S'");
		m.put("S", "SS");
		m.put("SS", "SS");
		m.put("S'", "SS'");
		m.put("SS'", "SS'");
		
		return m;
	}
	
	
	/** 下标 显示样式表. (参见原本) */
	public static final Map<String, String> downStyle = _init_down_style();
	public static Map<String, String> _init_down_style() {
		Map<String, String> m = new HashMap<String, String>();
		
		// D: "S'", T: "S'",  "D'": "S'", "T'": "S'",
	    // S: "SS'",  SS: "SS'",  "S'": "SS'", "SS'": "SS'"
		m.put("D", "S'");
		m.put("T", "S'");
		m.put("D'", "S'");
		m.put("T'", "S'");
		m.put("S", "SS'");
		m.put("SS", "SS'");
		m.put("S'", "SS'");
		m.put("SS'", "SS'");
		
		return m;
	}

	/** 表示 DTsep, SSsep 表中的一个项 */
	public static class SepRow {
		public int ord;
		public int op;
		public int bin;
		public int rel;
		public int open;
		public int close;
		public int punct;
		public int inner;
		public int get(String type) {
			if ("ord".equals(type)) return ord;
			if ("op".equals(type)) return op;
			if ("bin".equals(type)) return bin;
			if ("rel".equals(type)) return rel;
			if ("open".equals(type)) return open;
			if ("close".equals(type)) return close;
			if ("punct".equals(type)) return punct;
			if ("inner".equals(type)) return inner;
			return 0;
		}
	}
	
	public static class SepTable {
		public SepRow ord;
		public SepRow op;
		public SepRow bin;
		public SepRow rel;
		public SepRow open;
		public SepRow close;
		public SepRow punct;
		public SepRow inner;
		public SepRow get(String type) {
			if ("ord".equals(type)) return ord;
			if ("op".equals(type)) return op;
			if ("bin".equals(type)) return bin;
			if ("rel".equals(type)) return rel;
			if ("open".equals(type)) return open;
			if ("close".equals(type)) return close;
			if ("punct".equals(type)) return punct;
			if ("inner".equals(type)) return inner;
			return null;
		}
	}
	
	/**
	 * 原子(atom) 之间的间距表.
	 * The spacing tables for inter-atom spacing
     *  (See rule 20, and Chapter 18, p 170)
	 */
	public static final SepTable DTsep = _init_DTsep(); 
	public static final SepTable SSsep = _init_SSsep();

	private static final SepTable _init_DTsep() {
		SepTable t = new SepTable();
		
		SepRow r = t.ord = new SepRow(); 
		r.op = 1; r.bin = 2; r.rel = 3; r.inner = 1;
		
		r = t.op = new SepRow();
		r.ord = 1; r.op = 1; r.rel = 3; r.inner = 1;
		
		r = t.bin = new SepRow();
		r.ord = 2; r.op = 2; r.open = 2; r.inner = 2;
		
		r = t.rel = new SepRow();
		r.ord = 3; r.op = 3; r.open = 3; r.inner = 3;
		
		r = t.open = new SepRow();
		
		r = t.close = new SepRow();
		r.op = 1; r.bin = 2; r.rel = 3; r.inner = 1;
		
		r = t.punct = new SepRow();
		r.ord = 1; r.op = 1; r.rel = 1; r.open = 1; r.close = 1; r.punct = 1; r.inner = 1;
		
		r = t.inner = new SepRow();
		r.ord = 1; r.op = 1; r.bin = 2; r.rel = 3; r.open = 1; r.punct = 1; r.inner = 1;
		
		return t;
	}

	private static final SepTable _init_SSsep() {
		SepTable t = new SepTable();
		
		SepRow r = t.ord = new SepRow();
		r.op = 1;
		
		r = t.op = new SepRow();
		r.ord = 1; r.op = 1;
		
		r = t.bin = new SepRow();
		
		r = t.rel = new SepRow();
		
		r = t.open = new SepRow();
		
		r = t.close = new SepRow();
		r.op = 1;
		
		r = t.punct = new SepRow();
		
		r = t.inner = new SepRow();
		r.op = 1;
		
		return t;
	}
	
	/** The sizes used in the tables above */
	public static final String[] sepW = new String[] {
		"", "thinmuskip", "medmuskip", "thickmuskip"
	};
	// 这个值是直接从 jsMath.TeX 中得到的.
	public static final float[] sepW_v = new float[] {
		0f, 3/18f, 4/18f, 5/18f
	};
	
	/**
	 * 得到所给 style(显示样式)的上标()样式.
	 * @param style
	 * @return
	 */
	public static String UpStyle(String style) {
		return upStyle.get(style);
	}
	
	/**
	 * 得到所给 style(显示样式)的下标样式.
	 * @param style
	 * @return
	 */
	public static String DownStyle(String style) {
		return downStyle.get(style);
	}

	/**
	 * ? 加上 ' 号对应的显示样式.
	 * @param style
	 * @return
	 */
	public static String PrimeStyle(String style) {
		if (style.endsWith("'")) // 如果已经有 ' 了(加撇符号了), 则直接返回即可.
			return style;
		return style + "'"; // 否则加上 撇符号.
	}
	
	/**
	 * 得到指定显示样式下的显示尺寸(比例, size factor)
	 *  A value scaled to the appropriate size for scripts
	 * @param style
	 * @param v
	 * @return
	 */
	public static float StyleValue(String style, float v) {
		if ("S".equals(style) || "S'".equals(style)) 
			return 0.7f * v;
		if ("SS".equals(style) || "SS'".equals(style))
			return 0.5f * v;
		return v;
	}

	/**
	 * 返回指定显示样式和尺寸下关联的大小.
	 *   Return the size associated with a given style and size
	 * @param style -- 显示样式.
	 * @param size -- 尺寸.
	 * @return (?)返回的尺寸索引应该是指向 Img.fonts[] 的索引? "S" 字体小两级, "SS" 小四级.
	 */
	public static int StyleSize(String style, int size) {
		if ("S".equals(style) || "S'".equals(style))
			size = Math.max(0, size - 2);
		else if ("SS".equals(style) || "SS'".equals(style))
			size = Math.max(0, size - 4);
		return size;
	}

	/**
	 * 返回给定显示样式的字体参数表... (和 StyleSize() 函数高度类似...)
     *  Return the font parameter table for the given style
	 * @param style
	 * @param size
	 */
	public static TeXParam TeX(String style, int size) {
		if ("S".equals(style) || "S'".equals(style))
			size = Math.max(0, size - 2);
		else if ("SS".equals(style) || "SS'".equals(style))
			size = Math.max(0, size - 4);
		return JsMath.TeXparams[size];
	}
	
	/**
	 * 为所给的 tex 显示样式加上 css 类...
     *  Add the CSS class for the given TeX style
	 * @param style
	 * @param size
	 * @param html
	 * 
	 * 定义在 jsMath.styles 中的 CSS '.typeset .size0' 到 .size9 如下:
	 *   .typeset .size0 { font-size: 50%; }  tiny -- \scriptscriptsize
	 *   .typeset .size1 { font-size: 60%; }  ...
	 *   ...
	 *   .typeset .size4 { font-size: 100%; } normalsize
	 *   ...
	 *   .typeset .size9 { font-size: 249%; } Huge
	 * 几乎是对应于 size 命令(\tiny, \small 等)  
	 */
	public static String AddStyle(String style, int size, String html) {
		// 可以根据 style 得到 size 的大小情况. 列表得出.
		if ("S".equals(style) || "S'".equals(style))
			size = Math.max(0, size - 2);
		else if ("SS".equals(style) || "SS'".equals(style))
			size = Math.max(0, size - 4);
		if (size != 4) { // size==4 font-size 对应为 100%, 可以不用添加 span.
			// 这里 css class sizeN 需要看实际数据.
			html = "<span class='size" + size + "'>" + html + "</span>";
		}
		return html;
	}
	
	/** 添加字体类, 如果需要的话.
	 *  Add the font class, if needed
	 * @param tclass -- 
	 * @param html --
	 */
	public static String AddClass(String tclass, String html) {
		if (tclass != null && tclass.length() > 0 /*&& !"normal".equals(tclass)*/) {
			html = HTML.Class(tclass, html);
		}
		return html;
	}

	/**
	 * 寻找两个临近的 mItem 的间距(根据配置), 在指定样式下.
	 *  Find the amount of separation to use between two adjacent
     *  atoms in the given style
	 */
	private float GetSeparation(MItem l, MItem r, String style) {
		if (l == null) return 0; // l=调用者的prev, 其可能为 null. 其中 r 肯定非 null.
		if (l.atom && r.atom) {
			SepTable table = Typeset.DTsep;
			if (style.charAt(0) == 'S')
				table = Typeset.SSsep;
			SepRow row = table.get(l.type);
			if (row != null && row.get(r.type) != 0) {
				int sep = row.get(r.type);
				// String w = sepW[sep];
				// 原 js 代码: return JsMath.TeX[w];
				return sepW_v[sep];
			}
		}
		return 0;
	}
	
	/**
	 * 排版一个 mlist (也即, 将其转换为 HTML)
	 * Typeset an mlist (i.e., turn it into HTML).
	 * 这里, 文本项目有着相同的类和样式的被组合为一个, 以减少 <SPAN> 标记的使用数量 (当然数量还是很多)
	 * 间距被组合合并, 如果可能的话.
     *  Here, text items of the same class and style are combined
     *  to reduce the number of <SPAN> tags used (though it is still
     *  huge).  Spaces are combined, when possible.
     *  ###  More needs to be done with that.  ###
     *  The width of the final box is recomputed at the end, since
     *  the final width is not necessarily the sum of the widths of
     *  the individual parts (widths are in pixels, but the browsers
     *  puts pieces together using sub-pixel accuracy).
     *  
	 * @param style
	 * @param size
	 * @return
	 * 
	 * 1. 原来的函数名字为 Typeset(), 因为和类的名字一致, 所以改为 DoTypeset().
	 * 2. 调用的地方现在已知是 Box.SetList() 函数. 
	 */
	public Box DoTypeset(String style, int size) {
		this.style = style; this.size = size;
		int unset = -10000;
		this.w = 0; this.mw = 0; this.Mw = 0;
		this.h = unset; this.d = unset;
		this.bh = this.h; this.bd = this.d;
		this.tbuf = ""; this.tx = 0; this.tclass = ""; // 
		this.cbuf = ""; this.hbuf = ""; this.hx = 0;
		this.x = 0; this.dx = 0;
		
		MItem mitem = null;
		MItem prev = null;
		
		// ?? DoTypeset() 函数的原理,本质是什么: 将一组东西横向排列.
		for (int i = 0; i < this.mlist.size(); ++i) {
			prev = mitem; mitem = this.mlist.get(i);
			
			switch (1) { 
			case 1: // 原 js 使用 switch 结构, 我们为下面的 break 控制使用此结构.
				if ("size".equals(mitem.type)) { // SizeItem
					throw new UnsupportedOperationException();
				}
				else if ("style".equals(mitem.type)) { // StyleItem
					throw new UnsupportedOperationException();
				}
				else if ("space".equals(mitem.type)) { // SpaceItem
					SpaceItem sitem = (SpaceItem)mitem;
					// 测试用例: "a \quad b", 其中 \quad 产生为一个 SpaceItem.
					// 在原js中, item.w 还能这样?? if (typeof(mitem.w) == 'object')
					
					// 这里 "space" 项产生 dx 值, 后面一起合并到 "text" 项上.
					this.dx += sitem.getWfloat();
					mitem = prev; // hide this from TeX (不计入到邻接项中)
				}		
				else if ("html".equals(mitem.type)) { // ?HtmlItem
					throw new UnsupportedOperationException();
				}
				else { 
					// 例如实测中"x^2"的例子中, mitem={type:'ord', atom:true, nuc:是一个Box对象 }
					if (!mitem.atom && "box".equals(mitem.type) == false) 
						break; // 注意使用 switch 结构
					AtomItem a_item = (AtomItem)mitem;
					assert(a_item.nuc instanceof Box); // 一定如此?
					Box box = (Box)(a_item.nuc);
					// 加上 dx, 以及两个 atom 的应有的间距. (如 bin ord 之间的间距等)
					// 推测: dx 在前面 "space" 等分支处理时产生的.
					float tmp_sep = this.GetSeparation(prev, mitem, this.style);
					box.x += this.dx + tmp_sep;
					// 如果 box.x 及或 box.y 非0, 则需使用 <SPAN>定位, 故而改为 html 格式的.
					if (box.x != 0 || box.y != 0)
						box.Styled(); // box 有 Styled() 方法. --> box.format 会变成 html
					this.dx = 0; this.x = this.x + this.w;
					if (this.x + box.x + box.mw < this.mw) { // 计算合适的盒子大小...
						this.mw = this.x + box.x + box.mw;
					}
					if (this.w + box.x + box.mw > this.Mw) {
						this.Mw = this.w + box.x + box.Mw;
					}
					this.w += box.w + box.x;
					  // 上面计算好宽度
					
					if ("text".equals(box.format)) {
						// 表示此 box 中是文本, 还未转换为 html?
						if (!box.tclass.equals(this.tclass) && this.tclass.length() != 0)
							this.FlushText();
						// 如果 tbuf, cbuf 都为空, 则 tx?? 就是 x??
						if (this.tbuf.length() == 0 && this.cbuf.length() == 0) {
							this.tx = this.x;  // 推测 tx: tbuf 的 x 值
						}
						this.tbuf += box.html;
						this.tclass = box.tclass;
					}
					else {
						// 此时 box 格式必然是 "html", 一般 box.x 或 box.y 不为0 (前面)
						this.FlushClassed(); // 清空 cbuf.
						if (box.x != 0 || box.y != 0) 
							this.Place(box); // 使用 SPAN 定位这个带偏移的 box (里面是 html)
						if (this.hbuf.length() == 0) {
							this.hx = this.x;  // 推测 hx: hbuf 的 x 值
						}
						this.hbuf += box.html;
					}
					
					// 计算合在一起之后的总的高度,宽度等数据. 
					this.h = Math.max(this.h, box.h + box.y); 
					this.bh = Math.max(this.bh, box.bh);
					this.d = Math.max(this.d, box.d - box.y);
					this.bd = Math.max(this.bd, box.bd);
					break; // 注意这里的控制流程.
				}
			}
		}
		
		// 处理剩余的.
		this.FlushClassed(); // make sure scaling is included
		if (this.dx != 0) { // 如果最后还剩下一些 space, 则创建对应的 Spacer html.
			this.hbuf += HTML.Spacer(this.dx); 
			this.w += this.dx;
			if (this.w > this.Mw) { 
				this.Mw = this.w;
			}
			if (this.w < this.mw) {
				this.mw = this.w;
			}
		}
		if (this.hbuf.length() == 0) {
			// 返回 format='null' 的空盒子.
			return Box.Null();
		}
		if (this.h == unset) {
			this.h = 0;
		}
		if (this.d == unset) {
			this.d = 0;
		}
		Box box = new Box("html", this.hbuf, this.w, this.h, this.d);
		box.bh = this.bh; box.bd = this.bd;
		box.mw = this.mw; box.Mw = this.Mw;
		
		return box;
	}
	
	/**
	 * 将字体属性(用 span 实现)添加到缓存的文本(text buf)上, 
	 *   然后将缓存移到 classed-text buf(cbuf) 中. 
	 * Add the font to the buffered text and move it to the
     *  classed-text buffer. 
	 */
	private void FlushText() {
		if (this.tbuf.length() == 0) return; // text-buffer 为空, 则不用处理
		// 加上 <SPAN class='tclass'> 包装, 然后附加到 cbuf(classed-text buffer)
		this.cbuf += Typeset.AddClass(this.tclass, this.tbuf);
		this.tbuf = ""; this.tclass = ""; // 清空 tbuf, tclass.
	}
	
	/**
	 * Add the script or scriptscript style to the text and
     *  move it to the HTML buffer
	 */
	private void FlushClassed() {
		this.FlushText();
		if (this.cbuf.length() == 0) return;
		if (this.hbuf.length() == 0) {
			this.hx = this.tx;
		}
		this.hbuf += Typeset.AddStyle(this.style, this.size, this.cbuf);
		this.cbuf = "";
	}
	
	/**
	 * 替代 Place() 函数的. 对于 MSIE 等浏览器, 使用分立的 <SPAN>
     *    实现间距(backspacing), 否则内容会被裁剪掉.
     *    
	 * 添加一个 <SPAN> 以定位一个项目的 HTML, 并调整项目的高度和深度(因为 x 及或 y 非0, 所以需要调整)
	 *  Add a <SPAN> to position an item's HTML, and
     *  adjust the item's height and depth.
     *  (This may be replaced buy one of the following browser-specific
     *   versions by Browser.Init().)
	 * @param item
	 */
	private void Place(Box item) {
		// ?先垂直调整.
		if (item.y != 0) {
			float rw = item.Mw - item.w;
			float lw = item.mw;
			float W = item.Mw - item.mw;
			// 实现: 使用 relative 定位. 仔细研究产生的 HTML
			item.html =
				HTML.Spacer(lw - rw) +
				"<span style=\"position: relative; "
					+ "top:" + HTML.Em(-item.y) + "; "
					+ "left:" + HTML.Em(rw) + "; "
					+ "width:" + HTML.Em(W) + ";\">"
				+ HTML.Spacer(-lw) +
				item.html +
				HTML.Spacer(rw) +
				"</span>";
		}
		// 水平调整. 方法: 在前面加上一个 Spacer HTML 项. (可能导致误差?)
		if (item.x != 0) {
			item.html = HTML.Spacer(item.x) + item.html;
		}
		
		item.h += item.y; item.d -= item.y;
		item.x = 0; item.y = 0;
	}
	
	// 这个方式使用一个 span 完成定位, 某些浏览器可能支持的不好. 
	@SuppressWarnings("unused")
	private void Place_Single(Box item) {
		// 计算 html. 这里 span 用 relative 定位...
		String html = "<span style=\"position: relative;";
		if (item.x != 0) {
			html += " margin-left:" + HTML.Em(item.x) + ";";
		}
		if (item.y != 0) {
			html += " top:" + HTML.Em(-item.y) + ";";
		}
		item.html = html + "\">" + item.html + "</span>";
		
		item.h += item.y; // 为什么?
		item.d -= item.y;
		item.x = 0; item.y = 0; // 表示已经定位了?
	}
}
