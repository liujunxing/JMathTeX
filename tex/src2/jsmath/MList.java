package jsmath;

import java.util.ArrayList;
import java.util.List;

import jsmath.itm.BoundaryItem;
import jsmath.itm.FractionItem;
import jsmath.itm.RadicalItem;
import jsmath.itm.SpaceItem;
import jsmath.itm.TypeObject;
import jsmath.itm.TeXCharField;
import jsmath.itm.Field;
import jsmath.itm.AtomItem;
import jsmath.itm.MListField;
import jsmath.itm.MItem;

/**
 * MList 类是 MItem 的列表, 并编码(encode)数学表达式和子表达式的内容.
 * 它们作用像表达式"栈", 当在数学表达式解析时, 以及保存部分状态信息(解析状态),
 * 例如最近的开括号位置和 \over 命令位置, 以及当前字体.
 * 
 * mLists are lists of mItems, and encode the contents of
 *  mathematical expressions and sub-expressions.  They act as
 *  the expression "stack" as the mathematics is parsed, and
 *  contain some state information, like the position of the
 *  most recent open paren and \over command, and the current font.
 *  
 * MList 中 Atomize() 的含义是什么呢? 原子化?
 * 
 */
public class MList {
	/** 此数学列表中 MItem 的集合. */
	private List<MItem> mlist = new ArrayList<MItem>();
	
	/** data.overF 字段使用的类型.
	 */
	public static class OverProp {
		/** \over 系列命令的名字, 如 \over, \overwithdelims 等多个 */
		public String cmd_name;
		
		/** 左定界符字符串, 如 \{, (, [ */
		public DelimInfo left_delim;
		
		/** 右定界符字符串, 如 \}, ), ] */
		public DelimInfo right_delim;
		
		/** 分隔线的宽度 */
		public Object thickness;
		
		public OverProp() {}
		public OverProp(String cmd_name) { this.cmd_name = cmd_name; }
	}
	
	// 因为是从 js 转换过来, 某些数据结构难以动态表示, 只能暂时这样.
	// 这个数据结构应该就是保存(解析)状态的数据...
	// this.data = { ... }
	public static class Data {
		/** 表示前一个 open 符号的在 mlist 中的索引位置. 为 null 表示没有. */
		public Integer openI;
		
		/** 前一个 \over (或等价命令) 的位置索引. 为 null 表示没有. */
		public Integer overI;
		
		public OverProp overF = null;
		
		/** 当前字体(索引), ?别处有当做 string 使用的吗? (?类型增强为 int) */
		public Integer font;
		
		/** 字(体)大小. */
		public int size;
		
		/** 当前显示样式 (display style), 取值为 [D, D', T, T', S, S', SS, SS'] */
		public String style;
		
		/** 在 Parser.HandleEntry() 中使用. 暂不知道哪里设置该值. */
		public Object entry;
		
		/**
		 * 得到这个对象的复制品, 类似于 clone().
		 * @return
		 */
		public Data copy() {
			Data clone = new Data();
			clone.openI = this.openI;
			clone.overI = this.overI;
			clone.overF = this.overF;
			clone.font = this.font;
			clone.size = this.size;
			clone.style = this.style;
			return clone;
		}
	}
	
	/** (主要)保存在 parse 过程中记录的状态信息, 包括字大小, 样式, over位置, open位置 */
	public Data data = new Data();
	
	// this.init = {...}
	public static class Init {
		/** 显示样式, 应该取值为 "Display", "Text" 等 */
		public String style;
		
		/** 大小 */
		public int size;
	}
	
	public Init init = new Init();
	
	// 当前 style, 在 Atomize() 中设置为指定参数.
	private String style;
	
	// 当前 size, 在 Atomize() 中设置为指定参数.
	private int size;
	
	
	// === atomize 系列函数 ==============================================================
	
	/**
	 * 
	 */
	private void atomize_style(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	private void atomize_size(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	private void atomize_phantom(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	private void atomize_smash(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	private void atomize_raise(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 */
	private void atomize_lap(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 处理 'bin' 原子 (规则5)
	 * 
	 * 如果当前 item 是一个 bin 原子, 以及如果这是列表中的第一个原子,
     *  或前一个原子是 bin, op, rel, open 或 punct , 将该 bin 类型改为 ord.
     *  继续规则 14. 否则继续规则 17.
	 */
	private void atomize_bin(String style, int size, MItem mitem, MItem prev, int i) {
		if (prev != null && prev.type != null) {
			String type = prev.type;
			// 注意: 原 js 下面条件还有一些, 与规则描述不太一样. 我们先用规则所描述的那种.
			if ("bin".equals(type) || "op".equals(type) || "rel".equals(type)
				|| "open".equals(type) || "punct".equals(type) || "".equals(type)
				|| ("boundary".equals(type) && ((BoundaryItem)prev).left != null)) {
				mitem.type = "ord"; // MItem.TYPE_ord;
			}
		}
		else {
			mitem.type = "ord"; // MItem.TYPE_ord;
		}
		
		// 继续规则14,17
		this.atomize_SupSub(style, size, mitem);
	}

	private void atomize__rule6(String style, int size, MItem mitem, MItem prev, int i) {
		if (prev != null && prev.type != null && "bin".equals(prev.type)) {
			prev.type = "ord";
		}
		
	    atomize_SupSub(style, size, mitem);		
	}
	
	/**
	 * Handle a Rel atom.  (Rule 6)
	 */
	private void atomize_rel(String style, int size, MItem mitem, MItem prev, int i) {
		atomize__rule6(style, size, mitem, prev, i);
	}
	
	/**
	 * Handle a Close atom.  (Rule 6)
	 */
	private void atomize_close(String style, int size, MItem mitem, MItem prev, int i) {
		atomize__rule6(style, size, mitem, prev, i);
	}
	
	/**
	 * 处理 punct 类型的原子.
	 * Handle a Punct atom.  (Rule 6)
	 */
	private void atomize_punct(String style, int size, MItem mitem, MItem prev, int i) {
		atomize__rule6(style, size, mitem, prev, i);
	}
	
	/**
	 * 处理 inner 类型的原子 (规则7)
	 * 规则7. 如果当前 item 是 open 或 inner , 转到规则 17.
	 *  Handle an Open atom.  (Rule 7) 
	 */
	private void atomize_open(String style, int size, MItem mitem, MItem prev, int i) {
		this.atomize_SupSub(style, size, mitem);
	}

	/**
	 * 处理 inner 类型的原子 (规则7)
	 * 规则7. 如果当前 item 是 open 或 inner , 转到规则 17.
     *  Handle an Inner atom.  (Rule 7)
	 */
	private void atomize_inner(String style, int size, MItem mitem, MItem prev, int i) {
		this.atomize_SupSub(style, size, mitem);
	}
	
	/**
	 * 
	 */
	private void atomize_vcenter(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	private void atomize_overline(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	private void atomize_underline(String style, int size, MItem mitem, MItem prev, int i) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 处理根式原子. (规则11: 如果item 是 rad 原子 ......,  继续规则16)
	 * Handle a Rad atom.  (Rule 11 plus stuff for \root..\of)
	 */
	private void atomize_radical(String style, int size, RadicalItem mitem, MItem prev, int i) {
		TeXParam TeX = Typeset.TeX(style, size);
		String Cp = Typeset.PrimeStyle(style); // 设置盒子x为style=C'的核.?
		
		// Box box 是根式项的盒子. 排列顺序为 [surd,root,rule,box]
		Box box = Box.Set(mitem.nuc, Cp, size, true);
		box = box.Remeasured(); // 如何解决 Remeasured() 是一个烦恼的问题.
		float t = TeX.default_rule_thickness;   // 根式线的宽度
		float p = t;
		if ("D".equals(style) || "D'".equals(style)) {
			p = TeX.x_height;
		}
		float r = t + p/4.0f; // 根式线(包含)和到根式项的距离吧.?
		
		// 原js: [0,2,0x70,3,0x70] clazz, small, large -- 这个是根号那个字符吗? 是根号符号.
		DelimInfo delim = new DelimInfo(0, 0x0270, 0x0370);
		Box surd = Box.Delimiter(box.h+box.d +r+t, delim, style, true); // surd: 不尽根,无理数的意思
		if (surd.d > box.h+box.d+r) {
			r = (r+surd.d-box.h-box.d)/2; // 重新计算 r 的值, 使根式项居中吧.
		}
		surd.y = box.h+r;
		
		Box rule = Box.Rule(box.w, t); // 根式的线, 宽度等于根式项的宽度.
		rule.y = surd.y - t/2f;
		rule.h += 3f*t/2f;
		box.x = -box.w; // 向左 relative(相对)移动 box.w 宽度.
		
		String Cr = Typeset.UpStyle(Typeset.UpStyle(style)); // 两次上标的样式.
		Box root = Box.Set(mitem.root, Cr, size, false);
		root = root.Remeasured();
		if (mitem.root != null) {
			root.y = 0.55f*(box.h + box.d + 3*t + r) - box.d;
			surd.x = Math.max(root.w - (11f/18f)*surd.w, 0);
			rule.x = (7f/18f)*surd.w;
			root.x = -(root.w + rule.x);
		}
		
		// 注意顺序: [surd,root,rule,box]
		List<TypeObject> boxes = MakeTOList(surd, root, rule, box);
		mitem.nuc = Box.SetList(boxes, style, size);
		mitem.type = "ord";
		
		// 处理根式的可能的上下标...
		atomize_SupSub(style, size, mitem);
	}
	
	/**
	 * Handle an Acc atom. 处理重音原子. mitem 是一个 AtomItem, 但有值 accent.
	 */
	private void atomize_accent(String style, int size, AtomItem mitem, MItem prev, int i) {
		TeXParam TeX = Typeset.TeX(style, size);
		String Cp = Typeset.PrimeStyle(style); // 加上'号的样式.
		Box box = Box.Set(mitem.nuc, Cp, size, true); // 产生核的盒子.
		float u = box.w; // 内容的宽度.
		
		Float s = null;  // 暂时先用 Float 类型. 一般该值可认为是 0.
		FontInfo Font;
		float ic = 0;
		
		if ("TeX".equals(mitem.nuc.type)) { // nuc 是 TeXCharField
			TeXCharField ch_field = (TeXCharField)mitem.nuc;
			Font = JsMath.TeX.getFontByName(ch_field.font);
			FontCharInfo fci = Font.get(ch_field.c);
			if (fci.krn != null && Font.skewchar != 0) {
				// TODO: s = Font.krn[Font.skewchar];
			}
			ic = fci.ic; /*if (ic == null) ic = 0;*/
		}
		if (s == null) { s = 0f; }
		
		int c = mitem.accent[2]; // accent 当前先认为是 int[3] 类型. 对应字符编码.
		String font = JsMath.TeX.fam[mitem.accent[1]]; // accent[1] 对应字体索引.
		Font = JsMath.TeX.getFontByName(font);
		// 选择合适于宽度 u 的'重音'符号.
		while (true) {
			FontCharInfo tmp_fci = Font.get(c);
			if (tmp_fci.n == null) break; // 没有更宽的版本时, 就用当前最合适宽度的.
			int n = tmp_fci.n; // 下一个较宽版本的该字符的索引.
			FontCharInfo next_fci = Font.get(n);
			if (next_fci.getCalcW() <= u) // 宽度可用.
				c = n;
			else
				break;
		}
		
		float delta = Math.min(box.h, TeX.x_height);
		if ("TeX".equals(mitem.nuc.type)) { // 这里如果没有 sub, sup 则似乎不用进入此分支.
			// 组件一个新原子? 这里看起来是处理原子的可能的上标下标..., 但是取 delta=0
			AtomItem nitem = MItem.Atom("ord", (Field)mitem.nuc);
			nitem.sub = mitem.sub; nitem.sup = mitem.sup; nitem.delta = 0;
			this.atomize_SupSub(style, size, nitem); // 对该新建的原子排版...?
			Box nitem_nuc = (Box)nitem.nuc;
			delta += (nitem_nuc.h - box.h);
			mitem.nuc = nitem.nuc;
			box = (Box)mitem.nuc;
			mitem.sub = null; mitem.sup = null;
		}
		
		// 在这里我们遇到一个难题: 对于字符 x (在数学斜体下), 根据宽度计算的 acc 位置需要偏移一点;
		//   而对于字符 1-9 (数字符号), 根据宽度计算的 acc 位置是合适的. 
		//   而 class='accent' 设置有 position:relative;left:0.15em 适合于 x, 不适合 2! 
		Box acc = Box.TeX(c, font, style, size);
		acc.y = box.h - delta;
		acc.x = -box.w + s + (u-acc.w)/2;
		if (false /*jsMath.Browser.msieAccentBug*/) { // 某种 ie 特殊的 bug?
			acc.html += HTML.Spacer(0.1f);
			acc.w += 0.1f; acc.Mw += 0.1f;
		}
		if (Font.get(c).ic != 0) {
			acc.x += (ic - Font.get(c).ic)*TeX.scale;
		}
		
		// 重音符号+内容构成一个 List, 将其排版.
		List<TypeObject> tmp_boxes = MakeTOList(box, acc);
		mitem.nuc = Box.SetList(tmp_boxes, style, size);
		Box mitem_nuc = (Box)mitem.nuc;
		if (mitem_nuc.w != box.w) {
			SpaceItem space = MItem.Space(box.w - mitem_nuc.w);
			List<TypeObject> tmp_boxes2 = MakeTOList(mitem.nuc, space);
			mitem.nuc = Box.SetList(tmp_boxes2, style, size);
		}
		mitem.type = "ord";
		
		this.atomize_SupSub(style, size, mitem);
	}
	
	/**
	 * item_type (原子的类型) 为 'op' 时候. 规则 13 & 13a.
	 * 可能叫做 bigop 比较合适.
	 */
	private void atomize_op(String style, int size, MItem mitem_param, MItem prev, int i) {
		AtomItem mitem = (AtomItem)mitem_param; // 由于 item_type='op' 所以必然成立?
		
		TeXParam TeX = Typeset.TeX(style, size);
		Box box = null;
		mitem.delta = 0;
		boolean isD = (style.charAt(0) == 'D'); // D 或者 D' .
		// 如果未给出 \limits 或 \nolimits 则在 'D' 模式下缺省为 \limits (\sum 的限制表达式 放在上方和下方.)
		if (mitem.limits == null && isD) {
			mitem.limits = Boolean.TRUE; // limit 的取值似乎有 null, true, false 三种.
		}
		
		if ("TeX".equals(mitem.nuc.type)) {
			// 必然是一个 CharAtom (实际以后可能不止是一个 Char? 怎么办?)
			TeXCharField c_atom = (TeXCharField)mitem.nuc;
			FontInfo _fi = JsMath.TeX.getFontByName(c_atom.font);
			FontCharInfo C = _fi.get(c_atom.c);
			if (isD && C.n != null) { // 部分字符配置有字段 n, 其含义是什么呢?
				c_atom.c = (char)(int)(C.n);  // 从这里来推断, n 意思是在 bigop 下用比较大的一个字符.
					// D 模式的 Sum,Int 比较大一些. (比起 T 模式)
				C = _fi.get((int)C.n);
			}
			mitem.nuc = box = Box.Set(mitem.nuc, style, size, false);
			
			if (C.ic != 0) { // ?倾斜校正吗?
				mitem.delta = C.ic * TeX.scale;
				if (mitem.limits == Boolean.TRUE || mitem.sub == null /* || msie some bug */) {
					// TODO:
					// mitem.nuc = box = Box.SetList([box,jsMath.mItem.Space(mitem.delta)],style,size);
				}
			}
			
			box.y = -((box.h + box.d)/2 - box.d - TeX.axis_height); // 相对轴中心对齐...
			if (Math.abs(box.y) < 0.0001) // 如果很小, 则就当做0处理也很OK.
				box.y = 0;
		}
		
		if (box == null) {
			box = Box.Set(mitem.nuc, style, size, false);
			box = box.Remeasured(); // 这里我们又无法实现 Remeasure() 了...
			mitem.nuc = box;
		}
		
		// TODO: 比较复杂, 我们先调试一下 js 中代码, 弄懂之后再修改比较好.
		if (mitem.limits == Boolean.TRUE) {
			// 可能与上下标位置有关的代码在这里...
		} else {
			
		}
		
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 假设输入为 'XY', mitem='X', nitem='Y' (其下一个item)
	 *
	 * @param style
	 * @param size
	 * @param mitem
	 * @param prev
	 * @param i
	 */
	private void atomize_ord(String style, int size, MItem mitem, MItem prev, int i) {
		assert(mitem.atom == true); // 必须是一个 atom
		AtomItem atom = (AtomItem)mitem;
		// 判断 mitem 是一个 tex 字符, 且没有上下标. (atom.nuc 必须非空) ==> 可能进行 krn 调整. 我们可暂时略.
		if ("TeX".equals(atom.nuc.type) && atom.sub == null && atom.sup == null) {
			// 这个转换应该必然成立.
			//AtomItem a_mitem = (AtomItem)mitem; 
			MItem nitem = this.Get(i+1); // 找它的下一个 item (next-item). 两个可能要产生 krn 调整等.
			// TODO: 解释下面的语义.
			if (nitem != null && nitem.atom && nitem_ord_types(nitem)) {
				// 可以确保这一转换吗?
				AtomItem a_nitem = (AtomItem)nitem;
				// ?解释下面的语义.
				if (item_tex_font_eq(a_nitem, atom)) {
					TeXCharField n_field = (TeXCharField)(a_nitem.nuc);
					atom.textsymbol = true; 
					KernInfo[] krn = item_get_krn_info(atom); // 得到字符的 krn 信息
					// 原来代码, 我认为他实现的不对: krn *= ... 但是这里怎么能做乘法???
					int krn_index = index_of_krn(krn, n_field.c);
					if (krn_index >= 0) {  // 找到了 M,N 项目的 krn 信息.
						// 在 M,N 之间添加一个 space 项, 做间距调整.
						MItem space_item = MItem.Space(krn[krn_index].kern);
						this.mlist.add(i+1, space_item);
					}
				}
				
			}
		}
		
		// 处理后面可能的上标,下标.
		this.atomize_SupSub(style, size, mitem);
	}
	
	/** 
	 * 处理通用(一般)分数. (如 \frac, \over 等命令)
	 * 
	 * Handle a generalized fraction.  (Rules 15 to 15e)
	 */
	private void atomize_fraction(String style, int size, FractionItem mitem, MItem prev, int i) {
		TeXParam TeX = Typeset.TeX(style, size);
		float t = 0;  // 分数线的宽度
		if (mitem.thickness != null) {
			// TODO: t = mitem.thickness;
			throw new UnsupportedOperationException();
		} else if (mitem.from.indexOf("over") >= 0) {
			// js: mitem.from.match(/over/), 语义表示 cs 命令中包含 over 子串, 即 \over, \overwithdelims 命令
			t = TeX.default_rule_thickness;
		} else
			t = 0; // 分数线宽度为0.
		
		// Cn -- 分子的样式(style), Cd -- 分母的样式.
		boolean isD = (style.charAt(0) == 'D');	// D 或 D'
		String Cn = ("D".equals(style)) ? "T" : ("D'".equals(style)) ? "T'" :
			Typeset.UpStyle(style);
		String Cd = (isD) ? "T'" : Typeset.DownStyle(style);
		// 分别生成分子和分母的 Box.
		Box num = Box.Set(mitem.num, Cn, size, true); 
		num = num.Remeasured();
		Box den = Box.Set(mitem.den, Cd, size, true);
		den = den.Remeasured();
		
		float u; float v; float w; float p; float r;
		float H = (isD) ? TeX.delim1 : TeX.delim2;
		Box left_delim_box = Box.Delimiter(H, mitem.left, style, false/*null*/);
		List<TypeObject> mlist = new ArrayList<TypeObject>(); 
		mlist.add(left_delim_box);
		Box right_delim_box = Box.Delimiter(H, mitem.right, style, false/*null*/);
		
		// 根据分子,分母宽度设置 x 偏移, 以及 mlist 中分子,分母项的顺序
		if (num.w < den.w) { 
			// 分母宽一些. 则给分子加一个偏移.
			num.x = (den.w - num.w)/2;
			den.x = -(num.w + num.x);
			w = den.w; 
			mlist.add(num); mlist.add(den); // mlist 顺序为 [左定界符,分子,分母]
		} else {
			// 分子宽一些. 如 {x+y}/M
			den.x = (num.w - den.w)/2;
			num.x = -(den.w + den.x);
			w = num.w;
			mlist.add(den); mlist.add(num); // 和上面的顺序是反的.
		}
		
		// D vs T 模式, 得到不同的 u,v 参数.
		if (isD) {
			u = TeX.num1;
			v = TeX.denom1;
		} else {
			u = (t != 0) ? TeX.num2 : TeX.num3;
			v = TeX.denom2;
		}
		
		if (t == 0) { // atop
			p = (isD) ? 7*TeX.default_rule_thickness : 3*TeX.default_rule_thickness;
			r = (u - num.d) - (den.h - v);
		} else { // over
			p = (isD) ? 3*t : t;
			float a = TeX.axis_height; // 轴的高度 = 0.25
			r = (u - num.d) - (a + t/2);
			if (r < p) u += p-r;
			r = (a - t/2) - (den.h - v);
			if (r < p) v += p-r;
			Box rule = Box.Rule(w, t); // 构造横线.
			rule.x = -w; rule.y = a - t/2;  // 横线的位置: 前缩w, 向上移 y
			mlist.add(rule);
		}
		
		num.y = u; den.y = -v;
		mlist.add(right_delim_box); // 现在 mlist 里面是 [left, num `or' den, rule, right]
		
		// 将上面构造的 mlist=[left, num or den, rule, right] 序列排版为一个 box.
		mitem.nuc = Box.SetList(mlist, style, size); 
		mitem.type = "ord";
		mitem.atom = true; // 实际上等于变 FractionItem mitem 为真正的 AtomItem 了.
		mitem.num = null; mitem.den = null;

		// 继续其上标下标.
		atomize_SupSub(style, size, mitem);
	}

	/**
	 * 添加上标,下标 (规则 17-18f);
	 *  ? 如果没有上标,下标, 则做什么了呢?
	 * @param style
	 * @param size
	 * @param mitem
	 */
	private void atomize_SupSub(String style, int size, MItem mitem) {
		// 得到参数.
		TeXParam TeX = Typeset.TeX(style, size);
		// 上下标要保证肯定有一个原子的核(nuc). 因为这里应保证是 AtomItem. 
		AtomItem a_mitem = (AtomItem)mitem;
		TypeObject nuc = a_mitem.nuc;   // 一般应该是 Atom(CharAtom) 类型的.
		
		// 此函数核心处理(一)
		// 由于这里设置 a_mitem.nuc = box, 和原来的 atom 类型不同, 导致我使用了基类 TypeObject 作为 nuc 的类型.
		// Box.Set() 函数为 nuc 创建出对应的 box(计算出大小的盒子);
		Box box = Box.Set(a_mitem.nuc, style, size, false);
		
		a_mitem.nuc = box;
		// 空的 Field 可能产生的内容为 '', 此时格式为 'null'. 作为测试用例, 可以试验: "^3" (不给出x^3 中的x)
		if ("null".equals(box.format)) {
			// Box.Text() 函数我们暂时不能实现, 因此这里不应调入才好...
			// box = a_mitem.box = Box.Text("", "normal", style, size, 0f, 0f);
			throw new UnsupportedOperationException();
		}
		
		// nuc 是 TeX 字符, 则考虑其 ic(倾斜校正)
		if ("TeX".equals(nuc.type)) {
			// 检测 textsymbol 标志, 该标志在哪里设置? 是什么含义呢? 
			//   在 Atomize.ord() 函数中设置 mitem.textsymbol 属性为 true. (只在该地方设置了, 并且只在这里使用了)
			// TODO: 做测试用例.
			if (!a_mitem.textsymbol) {
				TeXCharField c_atom = (TeXCharField)nuc;
				FontInfo fi = JsMath.TeX.getFontByName(c_atom.font);
				FontCharInfo C = fi.get(c_atom.c);
				if (C.ic != 0) {
					// 部分字符(如斜体大写 X, 注意:在该字体下)配置有 ic(倾斜校正?), 这里给有倾斜校正的添加额外间距.
					a_mitem.delta = C.ic * TeX.scale;
					if (a_mitem.sub == null) {
						Box tmp_space = Box.Space(a_mitem.delta);
						List<TypeObject> tmp_boxes = MakeTOList(box, tmp_space);
						a_mitem.nuc = box = Box.SetList(tmp_boxes, style, size);
						a_mitem.delta = 0f;
					}
				}
			}
			else {
				mitem.delta = 0f;
			}
		}
		
		// 如果 mitem 没有下标也没有上标, 则返回即可;  在大部分情况下, 项目没有上下标.
		if (a_mitem.sup == null && a_mitem.sub == null)
			return; // 普通情况这里就返回.
		
		box.Styled(); // 加上 span 转为 html 格式. 此时 format 变为 'html' 从 'text'.
		
		// 得到 style 对应的下标字体样式 Cd, 上标字体样式 Cu.
		String Cd = Typeset.DownStyle(style);
		String Cu = Typeset.UpStyle(style);
		float q = Typeset.TeX(Cu, size).sup_drop; // 某个 tex 参数, 我想这个参数的值和含义, 还是需要去 Tex 的书里面找的.
		float r = Typeset.TeX(Cd, size).sub_drop;
		float u = 0; float v = 0; 
		float p;
		if (nuc.type != null && "text".equals(nuc.type) == false 
				&& "TeX".equals(nuc.type) == false 
				&& "null".equals(nuc.type) == false) {
			u = box.h - q; // 做这种调整是什么原因?
			v = box.d + r;
		}
		
		// 如果有下标.
		Box sub = null; // 因为变量作用域, 放在这里. (原js 在 if 里面)
		if (a_mitem.sub != null) {
			// 产生下标的 box, 用下标的尺寸.
			sub = Box.Set(a_mitem.sub, Cd, size, true);
			SpaceItem tmp_sp1 = MItem.Space(TeX.scriptspace);
			List<TypeObject> tmp_lst1 = MakeTOList(sub, tmp_sp1);
			sub = Box.SetList(tmp_lst1, style, size);
		}
		
		// 如果没有上标. (一定有上标, 故满足此条件表示 '有且只有下标')
		if (a_mitem.sup == null) {
			float tmp = Math.max(v, TeX.sub1);
			tmp = Math.max(tmp, sub.h-(4/5)*Typeset.TeX(Cd, size).x_height);
			sub.y = -tmp;
			List<TypeObject> tmp_lst2 = MakeTOList(box, sub);
			a_mitem.nuc = Box.SetList(tmp_lst2, style, size);
			a_mitem.sup = null; // 清空该域.
			return;
		}
		
		// 上标 -> Box 对象. 使用对应的上标样式 Cu, 尺寸 size.
		Box sup = Box.Set(a_mitem.sup, Cu, size, true); // true 表示产生对应的 html
		// 现在我们有 box, sup 两个 Box.
		// 产生一个 tmp_space 的 MItem 项目, 其 type='space'
		SpaceItem tmp_space = MItem.Space(TeX.scriptspace);
		List<TypeObject> boxes = MakeTOList(sup, tmp_space); // 为调用 SetList() 做准备.
		// 调用 SetList() 函数. (当前主要要解决 SetList() 函数的实现)
		sup = Box.SetList(boxes, style, size); // 实际和 MakeTOList() 可以合并...?
			// 这里给我们提出的问题是, 要认真思考 MItem,Box,Field 几种的关系. 
		
		if ("D".equals(style)) {
			p = TeX.sup1;
		} else if (style.endsWith("'")) {
			p = TeX.sup3; // D', T', S', SS'
		} else {
			p = TeX.sup2;
		}
		// 这里应该是计算上标的 y 的位置. (升高的数量)
		u = Math.max(u, p);
		TeXParam up_texp = Typeset.TeX(Cu, size);
		u = Math.max(u, sup.d + up_texp.x_height/4);
		
		if (a_mitem.sub == null) {
			// 此情况是: 只有上标没有下标. 例如测试用例: 'x^3'
			sup.y = u;
			boxes = MakeTOList(box, sup); // x和提升过的3 构成一个列表.
			a_mitem.nuc = Box.SetList(boxes, style, size); // 而后排版.
			a_mitem.sup = null; // js: delete itm.sup;
			return;
		}

		// 其它情况: 应该是既有上标, 又有下标. 如测试用例: x^m_n
		TeXParam down_texp = Typeset.TeX(Cd, size);  // Cd -- DownStyle
		v = Math.max(v, down_texp.sub2);
		float t = TeX.default_rule_thickness;
		if ((u-sup.d) - (sub.h-v) < 4*t) { //计算某些神奇的 tex 参数!
			v = 4*t + sub.h - (u-sup.d);
			p = (4f/5f)*TeX.x_height - (u-sup.d);
			if (p > 0) { 
				u += p; v -= p;
			}
		}
		sup.Remeasured(); sub.Remeasured(); // 为了更精确一些, 可惜我们无法实现.
		sup.y = u; // 上标 
		sub.y = -v; // 下标 
		sup.x = a_mitem.delta;
		if (sup.w + sup.x > sub.w) {
			sup.x -= sub.w;
			List<TypeObject> tmp_sp3 = MakeTOList(box, sub, sup);
			a_mitem.nuc = Box.SetList(tmp_sp3, style, size);
		}
		else {
			sub.x -= (sup.w+sup.x);
			List<TypeObject> tmp_sp3 = MakeTOList(box, sub, sup);
			a_mitem.nuc = Box.SetList(tmp_sp3, style, size);
		}
		
		a_mitem.sup = null; a_mitem.sub = null;
	}
	

	// === atomize_xxx() 函数使用的辅助子函数.
	
	private static int index_of_krn(KernInfo[] krn, int c) {
		if (krn == null) return -1;
		for (int i = 0; i < krn.length; ++i)
			if (krn[i].char_code == c)
				return i;
		return -1;
	}
	
	// 语义: krn = jsMath.TeX[mitem.nuc.font][mitem.nuc.c].krn; 
	private static KernInfo[] item_get_krn_info(AtomItem aitem) {
		TeXCharField atom = (TeXCharField)aitem.nuc;
		FontInfo fi = JsMath.TeX.getFontByName(atom.font);
		FontCharInfo fci = fi.get(atom.c);
		return fci.krn;
	}
	
	// 语义: nitem.nuc.type == 'TeX' && nitem.nuc.font == mitem.nuc.font
	private static boolean item_tex_font_eq(AtomItem nitem, AtomItem mitem) {
		if (! "TeX".equals(nitem.nuc.type))
			return false;
		// 这一转换必须成立!
		TeXCharField n_nuc = (TeXCharField)(nitem.nuc);
		TeXCharField m_nuc = (TeXCharField)(mitem.nuc);
		
		return n_nuc.font.equals(m_nuc.font);
	}
	
	
	private static final String[] _ntypes = new String[] {
		"ord", "op", "bin", "rel", "open", "close", "punct"
	};
	/** 判断 nitem.type 的取值在 ["ord", "op", "bin", "rel", "open", "close", "punct"], 提供给函数 atomize_ord() 使用 */
	private static boolean nitem_ord_types(MItem nitem) {
		String type = nitem.type;
		for (String t : _ntypes) {
			if (t.equals(type)) return true;
		}
		return false;
	}
	
	public static List<TypeObject> MakeTOList(TypeObject... t) {
		List<TypeObject> list = new ArrayList<TypeObject>();
		if (t != null) {
			for (int i = 0 ; i < t.length; ++i)
				list.add(t[i]);
		}
		return list;
	}
	
	// ====================================================================
	
	
	/**
	 * 使用指定参数构造一个 MList 的新实例.
	 * @param list
	 * @param font -- 字体(?索引, 名字)
	 * @param size -- 字体尺寸
	 * @param style -- 显示样式
	 */
	public MList(List<MItem> list, Integer font, Integer size, String style) {
		if (list != null) {
			this.mlist = list;
		}
		else {
			this.mlist = new ArrayList<MItem>();
		}
		if (style == null) style = "T";
		if (size == null) size = 4;
		
		this.data.openI = null;
		this.data.overI = null;
		this.data.overF = null;
		this.data.font = font;
		this.data.size = size;
		this.data.style = style;
		
		this.init.size = size;
		this.init.style = style;
	}

	/** 添加一个 mItem 到末尾. (数组的末尾)
	 *  Add an mItem to the list
	 *  @return 原js 函数返回有东西..., 其实不返回也行...?
	 */
	public MItem Add(MItem box) {
		this.mlist.add(box);
		return box;
	}
	
	/** 
	 * 从数组中得到第 i 个 mItem. 和 java 的 List.get() 不同之处是该项目不存在则返回 null.
	 *  Get the i-th mItem from the list
	 */
	public MItem Get(int index) {
		if (index < 0 || index >= this.mlist.size())
			return null;
		return this.mlist.get(index);
	}
	
	/** 得到列表长度.
     *  Get the length of the list
     */
	public int Length() {
		return this.mlist.size();
	}

	/**
	 * 得到当前开符号的数据, 如果没有则返回 null.
	 * @return
	 */
	public BoundaryItem getOpenItem() {
		// 如果 openI 为空, 则表示没有开符号, 此时返回 null.
		if (this.data.openI == null) return null;
		
		// 找到该项目.
		int open_index = this.data.openI;
		MItem item = this.mlist.get(open_index);
		// 必须是一个 OpenItem (或其子类?); 如果不是则抛出异常, 因为 openI 记录的数据类型不对...
		if ((item instanceof BoundaryItem) == false)
			throw new RuntimeException("Internal error: openI not an OpenItem?");
		
		return (BoundaryItem)item;
	}
	
	/**
	 * 得到当前显示样式 (display style).
	 * @return
	 */
	public String getDisplayStyle() {
		return this.data.style;
	}
	
	/**
	 * 设置此 mList 的当前显示样式(display style).
	 * 取代原来的直接访问 mlist.data.style = style; 以提高封装性.
	 * @param style
	 */
	public void setDisplayStyle(String style) {
		this.data.style = style;
	}
	
	/**
	 * 得到当前字体(索引).
	 * @return
	 */
	public Integer getFont() {
		return this.data.font;
	}
	
	/**
	 * 设置当前字体(索引).
	 * @param font_index
	 */
	public void setFont(Integer font_index) {
		this.data.font = font_index;
	}
	
	/**
	 * 得到数组中最后一个. 如果没有任何 mitem, 则返回 null.
     *  Get the tail mItem of the list
	 * @return
	 */
	public MItem Last() {
		if (this.mlist.size() == 0) 
			return null;
		return this.mlist.get(this.mlist.size() - 1);
	}

	/**
	 * 得到从位置 i开始一直到结束的项目的子列表.
	 * @param i
	 * @return
	 */
	public MList Range(int i) {
		return Range(i, -1);
	}
	
	/**
	 * 得到指定范围的子列表. 范围为 [i, j)
     *  Get a sublist of an mList
	 * @param i
	 * @param j -- 如果给出 -1 则表示直到列表末尾.
	 * @return
	 */
	public MList Range(int i, int j) {
		if (j < 0)
			j = this.mlist.size();
		// 这里不能使用 List.subList() 方法, 因为后面我们会改变 List 对象.
		List<MItem> sublist = new ArrayList<MItem>();
		for (int x = i; x < j; ++x) 
			sublist.add(this.mlist.get(x));
		return new MList(sublist, null, null, null);
	}
	
	/** 
	 * 删除指定范围的子列表.
	 *  Remove a range of mItems from the list.
	 * @param j 如果 < 0, 则删除 i 这一个项.
	 */
	public void Delete(int i, int j) {
		if (j < 0) j = i;
		for (int k = i; k < j; ++k)
			this.mlist.remove(i);
	}
	
	/** 
	 * 添加一个 开括号(open brace), 维护栈信息...
	 *  Add an open brace and maintain the stack information
	 *  about the previous open brace so we can recover it
	 *  when this one os closed.
	 * @param left -- 左定界符, 如果=null, 则表示左边是 '{' 组开始符号. (在 HandleOpen() 函数中以 null 为参数调用)
	 */
	public BoundaryItem Open(DelimInfo left) {
		// 烦恼的问题, js 可以灵活地使用 map..., 我们怎么办呢??????
		// 解决方法: 为每一种 atom 使用不同的类, 都从基类 MItem 来派生.
		// TAG:MITEM(OpenItem)
		BoundaryItem box = new BoundaryItem(this.data, left, null); // js: var box = new CItem("boundary", {data: this.data});
		this.Add(box);
		
		// 构造一个新的 this.data, 其复制原来的信息, 但去掉 overI, overF 项. 
		// 设置 openI 项为此开符号在 mlist[] 中的索引.
		Data olddata = this.data;
		this.data = olddata.copy();
		// js: delete this.data.overI, delete this.data.overF
		this.data.overI = null;
		this.data.overF = null; // 我们使用设置为 null 表示没有该字段(delete...)
		this.data.openI = this.mlist.size() - 1; // openI 做索引.
		// 构造中已经给出: if (left != null) box.left = left;
		return box;
	}
	
	/** 
	 * 尝试关闭一个括号(闭括号). 恢复栈信息...
	 *  Attempt to close a brace.  Recover the stack information
	 *  about previous open braces and \over commands.  If there was an
	 *  \over (or \above, etc) in this set of braces, create a fraction
	 *  atom from the two halves, otherwise create an inner or ord
	 *  from the contents of the braces.
	 *  Remove the braced material from the list and add the newly
	 *  created atom (the fraction, inner or ord).
	 * @param right -- 定界符对象. 如果为空, 表示 '}' 组结束.
	 */
	public MItem Close(DelimInfo right) {
		// 根据 right 是否有, 构建 CloseItem. 如果 right == null, 则表示为 '}' 组结束字符的处理.
		BoundaryItem right_item = null;
		if (right != null) {
			// TAG:MITEM 创建为 mitem = {type='boundary', right=delimiter}
			right_item = new BoundaryItem(null, null, right);
		}
		
		MItem atom = null; // 最后创建的原子项目.
		// 此时一定有 openI.
		int open = this.data.openI; assert(this.data.openI != null);
		// over, from 可能有,也可能没有. 如果有, 则表示在定界符之中有 \over 等命令, 如 { x \over y }
		//  这里保存 over, from 的值.
		Integer over = this.data.overI;
		MList.OverProp from = this.data.overF;
		
		// 恢复保存在 open item 中 data 信息(其包含前一个 openI, over, from 的信息)
		BoundaryItem oitm = (BoundaryItem)this.mlist.get(open);
		this.data = oitm.data; // 这是上一次 open 符号时保存的 this.data 信息.

		// 如果 over 存在, 则 \over 命令嵌套在 {} (或定界符) 之中, 此时(按照tex语法??)要将 \over
		//  终止在定界符之内. 在 TeXbook p.139 给出的例子说明了用组符号{} 分组 \over 命令.
		if (over != null) {
			// 制作一个分数原子, 取代现有定界符之内的内容. 成为一个独立的分数原子.
			Field num = new MListField(this.Range(open+1, over-1));
			Field den = new MListField(this.Range(over));
				
			atom = new FractionItem(from.cmd_name, num, den, from.thickness, 
					from.left_delim, from.right_delim);
			
			if (right != null) {
				// 等价 js: [this.mlist[open], atom, right]
				List<MItem> tmp = new ArrayList<MItem>();
				tmp.add(this.mlist.get(open));
				tmp.add(atom);
				tmp.add(right_item);
				
				MList mlist = new MList(tmp, null, null, null);
				Field mlst_field = new MListField(mlist);
				atom = MItem.Atom("inner", mlst_field);
			}
		}
		else {
			// 没有 \over 项, 则被定界符包围在之内的所有项目制作为原子.
			if (right_item != null) { 
				// 有右定界符项, 规则 27*, \left<delim><math mode material>\right<delim>
				// 必须有 left boundary item(first delimiter), 以 \right 结束, right boundary 
				//   item(second delimiter). 创建 Inner 原子, 该原子的核为 \left...\right 的内部数学列表.
				this.Add(right_item);
				// 从开定界符到闭定界符全部构成一个新的 mlist, 做为 inner 原子的核.
				MList mlist = this.Range(open);
				this.Delete(open, this.Length()); // TODO: Range, Delete 合并为一个函数更好一些?
				
				MListField inner_atom = new MListField(mlist);
				// 原js: atom = jsMath.mItem.Atom('inner', {type="mlist", mlist=mlist}); 
				atom = new AtomItem("inner", inner_atom);
				//this.Add(item);
			}
			else { 
				// 是组结束, 见 texbook p.291 {math mode material} 处理.
				// 构建 "inner" 类型的原子.
				MList mlist = this.Range(open+1); // 取 {} 之间的内容.
				this.Delete(open, -1);
				MListField mlst_field = new MListField(mlist); // 构成一个 mlist 为核的原子.
				atom = new AtomItem("inner", mlst_field); // 然后构建为 mItem
				//this.Add(item);
				// TODO: 这里的语义要仔细检查.
			}
		}
		this.Delete(open, this.Length());
		this.Add(atom);
		return atom;
	}
	
    /** 
     * 创建一般化分式 mlist... (mlist 也当做 vertical list?? )
     *  Create a generalized fraction from an mlist that
     *  contains an \over (or \above, etc).
     */
	public void Over() {
		/*
		Object over = this.data.overI;
		Object from = this.data.overF;
		Object from_name = from; // from.name
		Object num = {type:mlist, mlist:this.Range(open+1, over-1)};
		Object den = {type:mlist, mlist:this.Range(over)};
		Object atom = CItem.Fraction(from_name, 
				num, den,
				from.thickness, from.left, from.right);
		this.mlist = [atom];
		*/
	}
	
	/**
	 * Take a raw mList (that has been produced by parsing some TeX
     *  expression), and perform the modifications outlined in
     *  Appendix G of the TeXbook.
	 * @param style
	 * @param size
	 */
	public void Atomize(String style, int size) {
		MItem mitem = null;
		MItem prev = null;
		// 这里直接将 style, size 设置到此实例中.
		this.style = style;
		this.size = size;
		
		// 遍历每个 mItem, 对其执行 Atomize().
		for (int i = 0; i < this.mlist.size(); ++i) {
			mitem = this.mlist.get(i);
			String item_type = mitem.type;
			mitem.delta = 0f; // 这里假定了所有 mItem 都有一个 delta 属性..., 其应该与倾斜校正(ic)有关.
			if ("choice".equals(item_type)) {
				throw new RuntimeException(); 
				// TODO: this.mlist = this.Atomize.choice(this.style, mitem, i, this.mlist); 
				// --i;
			}
			// 原来 js 使用 javascript 特有的map方式实现函数调用, 因为数量有限, 我们改造为
			//   函数名字一个一个对比的方式.
			else {
				if ("ord".equals(item_type)) {
					// "ord" 最为常见, 放上面方便调试.
					atomize_ord(style, size, mitem, prev, i);
				} else if ("style".equals(item_type)) {
					atomize_style(style, size, mitem, prev, i);
				} else if ("size".equals(item_type)) {
					atomize_size(style, size, mitem, prev, i);
				} else if ("phantom".equals(item_type)) {
					atomize_phantom(style, size, mitem, prev, i);
				} else if ("smash".equals(item_type)) {
					atomize_smash(style, size, mitem, prev, i);
				} else if ("raise".equals(item_type)) {
					atomize_raise(style, size, mitem, prev, i);
				} else if ("lap".equals(item_type)) {
					atomize_lap(style, size, mitem, prev, i);
				} else if ("bin".equals(item_type)) {
					atomize_bin(style, size, mitem, prev, i);
				} else if ("rel".equals(item_type)) {
					atomize_rel(style, size, mitem, prev, i);
				} else if ("close".equals(item_type)) {
					atomize_close(style, size, mitem, prev, i);
				} else if ("punct".equals(item_type)) {
					atomize_punct(style, size, mitem, prev, i);
				} else if ("open".equals(item_type)) {
					atomize_open(style, size, mitem, prev, i);
				} else if ("inner".equals(item_type)) {
					atomize_inner(style, size, mitem, prev, i);
				} else if ("vcenter".equals(item_type)) {
					atomize_vcenter(style, size, mitem, prev, i);
				} else if ("underline".equals(item_type)) {
					atomize_underline(style, size, mitem, prev, i);
				} else if ("radical".equals(item_type)) {
					atomize_radical(style, size, (RadicalItem)mitem, prev, i);
				} else if ("accent".equals(item_type)) {
					atomize_accent(style, size, (AtomItem)mitem, prev, i);
				} else if ("op".equals(item_type)) {
					atomize_op(style, size, mitem, prev, i);
				} else if ("fraction".equals(item_type)) {
					atomize_fraction(style, size, (FractionItem)mitem, prev, i);
				} else if ("SupSub".equals(item_type)) {
					atomize_SupSub(style, size, mitem);
				}	
			}
			prev = mitem;
		}
		
		// mitem 现在应该是最后一个 item
		if (mitem != null && "bin".equals(mitem.type))
			mitem.type = "ord";
		
		// 如果这个 mlist 的左右是定界符, 如测试用例 \left( {xxx} \right)...
		if (this.mlist.size() >= 2 && "boundary".equals(mitem.type)  
				&& "boundary".equals(this.mlist.get(0).type)) {
			this.AddDelimiters(style, size);
		}
	}
	
	/**
	 * 对于由左右定界符包着的 MList 项, 我们替换定界符原子为 open/close 原子, 使其
	 *   尺寸大小适合于列表的内容.
	 * 测试用例: "\left( x \right)"
	 *  For a list that has boundary delimiters as its first and last
     *  entries, we replace the boundary atoms by open and close
     *  atoms whose nuclii are the specified delimiters properly sized
     *  for the contents of the list.  (Rule 19)
	 * @param style
	 * @param size
	 */
	private void AddDelimiters(String style, int size) {
		float unset = -10000; 
		float h = unset; float d = unset;
		
		// 计算 mlist 的 h(高度), d(深度).
		for (int i = 0; i < this.mlist.size(); ++i) {
			MItem mitem = this.mlist.get(i);
			if (mitem.atom || "box".equals(mitem.type)) { // "box" == mitem.type 会出现吗? 应该不出现才对.
				Box tmp_nuc = (Box)((AtomItem)mitem).nuc;
				// 只有 atom, 且转换为了 "box" 才有尺寸...
				h = Math.max(h, tmp_nuc.h + tmp_nuc.y);
				d = Math.max(d, tmp_nuc.d - tmp_nuc.y);
			}
		}
		
		// TeX 参数计算.
		//CTeX TeX = JsMath.TeX;
		float a = Typeset.TeX(style, size).axis_height;
		float delta = Math.max(h-a, d+a);
		double _tmp_1 = Math.floor(CTeX.integer*delta / 500f) * CTeX.delimiterfactor;
		double _tmp_2 = CTeX.integer * (2f*delta - CTeX.delimitershortfall);
		// H 的值应该是指适合的定界符的高度...但这里计算较复杂, 不理解.
		float H = (float)Math.max(_tmp_1, _tmp_2) / CTeX.integer;
		BoundaryItem left = (BoundaryItem)this.mlist.get(0);
		BoundaryItem right = (BoundaryItem)this.mlist.get(this.mlist.size()-1);
		
		// 估计如下的函数用于产生恰当大小的定界符. (delimiter) 测试用例: \left( x \right)
		left.nuc = Box.Delimiter(H, left.left, style, false/*null*/);
		right.nuc = Box.Delimiter(H, right.right, style, false/*null*/);

		// 处理之后, 设置 left,right 的新内容类型.
		left.type = "open"; left.atom = true; left.left = null;
		right.type = "close"; right.atom = true; right.right = null;
	}
	
	/**
	 * 排版一个数学列表以产生其最终的 HTML.
	 * Typeset a math list to produce final HTML for the list.
	 * @param style
	 * @param size
	 * @return
	 */
	public Box Typeset(String style, int size) {
		Typeset typeset = new Typeset(this.mlist);
		return typeset.DoTypeset(style, size);
	}
}
