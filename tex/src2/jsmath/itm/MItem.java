package jsmath.itm;

import jsmath.Box;
import jsmath.Dimen;

/**
 * 表示所有数学 mItem 的基类. (大致)对应 jsMath 中的 jsMath.mItem.
 * 
 * mItem 是数学列表(math list) 的基本构成块.
 * 
 * 当前子类有:
 *   GeneralAtomItem -- 通用的, 含有原子的项目. -- 里面有 atom. 类型 itemType=["ord","op","bin" 等]
 *     RaiseItem -- (派生的) 升高降低的项目.
 *   
 *   BoundaryItem -- 作为 OpenItem, CloseItem 的基类. itemType="boundary"
 *     OpenItem -- 对应 \left 或 {
 *     CloseItem -- 对应 \right 或 }
 *   ChangeStyleItem -- 对应 \displaystyle 等命令的实现. 类型 itemType="style"
 *   FractionItem -- 对应 \frac, \over 等命令的实现. 类型 itemType="fraction"
 *   RadicalItem -- 根式, 对应 \sqrt 等命令. 类型 itemType="radical"
 *   TODO: 更多类型
 *   
 * 例子反向查找:
 * mItem.Atom() 函数的使用:
 *     1. mList.Close() 中使用, 构造 item_type="inner", atom_type="mlist" (分数 \over)
 *                          构造 item_type="ord", atom_type="mlist" (定界符包围的 \left ... \right)
 *     2. Atomize.accent() 构造 item_type="ord" 
 *     3. Parser.Prime() -- 构造 item_type="ord", atom_type=null
 *     4. Parser.MoveLeftRight() -- 构造 item_type="ord", atom=...
 *     5. Parser.Sqrt() -- 构造 item_type="radical", atom=ProcessArg()
 *     6. Parser.Root() -- 构造 item_type="radical", atom=ProcessArg()
 *     7. Parser.BuildRel() -- 构造 item_type="op", atom=ProcessArg()
 *     8. Parser.MakeBig() -- 构造 item_type="ord", "open", "close" 等数种之一.
 *     9. Parser.Matrix() -- 构造 item_type="ord" | "inner" (可暂略)
 *    10. Parser.Array() -- 构造 item_type="ord" | "inner"
 *    11. Parser.MathAccent() -- 构造 item_type="accent"
 *    12. Parser.HandleSuperscript() -- 构造 item_type="ord"
 *    13. Parser.HandleSubscript() -- 构造 item_type="ord"
 *    
 * 根据 jsMath.mList.Atomize 的子函数, item_type(IType) 有如下可能值:
 *    style -- 函数 HandleStyle(), \displaystyle 等命令...
 *    size -- 函数 HandleSize(), \tiny 等改变尺寸的命令...
 *    phantom -- 函数 Phantom(), \phantom 等命令.
 *    smash -- 函数 Smash(), \smash 等命令.
 *    raise -- 函数 RaiseLower(), \raise, \lower 命令.
 *    lap -- 函数 HandleLap(), \llap 等命令.
 *    ord, op, bin, rel, close, punct, open, inner  -- 基本内容类型. 部分有命令特定建立该类型.
 *    vcenter -- 函数 HandleAtom(), \vcenter 命令
 *    overline -- 函数 HandleAtom(), \overline 命令
 *    underline -- 函数 HandleAtom(), \\underline 命令
 *    radical -- 函数 , \over, \sqrt, \root 等命令
 *    accent -- 函数 MathAccent(), 数学重音命令.
 *    fraction -- 函数 Frac() 等, \frac 分数.
 *    SupSub -- 函数 , 上标下标.
 *   
 */
public abstract class MItem extends TypeObject {
	/** 普通原子, 如 'x'. */
	public static final String TYPE_ord = "ord";
	
	/** 巨算符. 如 BuildRel() 实现 \buildrel ... 的命令 */
	public static final String TYPE_op = "op";
	
	public static final String TYPE_bin = "bin";
	public static final String TYPE_rel = "rel";
	public static final String TYPE_open = "open";
	public static final String TYPE_close = "close";
	public static final String TYPE_punct = "punct";
	/** 为 \left...\right 构造的 inner item. "inner" 内部原子. */
	public static final String TYPE_inner = "inner";
	
	/** 以定界符为内容的 item. */
	public static final String TYPE_boundary = "boundary";
	
	/** ChangeStyle 的 item_type */
	public static final String TYPE_style = "style";
	
	/** SizeItem 的 item_type */
	public static final String TYPE_size = "size";
	
	/** 根式项目. */
	public static final String TYPE_radical = "radical";
	
	/** 分数项目 */
	public static final String TYPE_fraction = "fraction";
	
	/** 处理 \raise, \lower 命令产生的项目类型. */
	public static final String TYPE_raise = "raise";
	
	/** 处理 \phantom 命令, 在 Parser.Phantom() */
	public static final String TYPE_phantom = "phantom";
	
	/** \smash */
	public static final String TYPE_smash = "smash"; 
	
	/** 数学重音符号 */
	public static final String TYPE_accent = "accent";
	
	/** 在处理 \moveleft, \moveright 时候会产生, 也有别处生成. 
	 * TODO: 问题, 在 Atomize() 的各个子函数中却没有见到, 这是为什么? */
	public static final String TYPE_space = "space";
	
	// vcenter, overline, underline 在 HandleAtom() 函数中, 怎么弄?
	
	/** 是否有原子核(nuc 是一个 MathField)  */
	public boolean atom = false;
	
	/** ?似乎是倾斜校正用的, 在 MList.SupSub() 函数中有设置 */
	public float delta = 0f;
	
	/**
	 * 构造函数.
	 * 作为基类, 这个类不应被直接实例化.
	 */
	protected MItem() {
		
	}
	
	/**
	 * 得到此 MItem 的类型. 派生类根据自己的类型, 返回正确的类型.
	 * @return
	 */
	//public abstract String getItemType();
	
	/**
	 * 通用的原子(atom), 给出该原子的核.
     *  A general atom (given a nucleus for the atom)
	 */
	public static AtomItem Atom(String type, Field nuc) {
		AtomItem item = new AtomItem(type, nuc);
		return item;
	}

	public static AtomItem Atom(String type, Box box) {
		AtomItem item = new AtomItem(type, box);
		return item;
	}
	
	/**
	 * 一个核为 tex 字符(c,f,a 的集合)的原子. 
     * (现在已知)在 HandleTeXchar() 中构造此类原子.
     *  An atom whose nucleus is a TeX character in a specific font
	 * @param item_type
	 * @param code
	 * @param font
	 * @return
	 */
	public static AtomItem TeXAtom(String item_type, char code, String font) {
		// 先构造内部的原子的核. 
		TeXCharField nuc = new TeXCharField(font, code);
		AtomItem item = new AtomItem(item_type, nuc);
		return item;
	}
	
	/**
	 * 创建一个插入若干间距的 atom. 实际创建返回为 SpaceItem.
	 * An atom that inserts some glue
	 * @param w
	 * @return 返回 MItem (其子类 SpaceItem)
	 */
	public static SpaceItem Space(float w) {
		// return new jsMath.mItem("space", {w: w});
		Dimen dimen = new Dimen(w);
		SpaceItem item = new SpaceItem(dimen);
		return item;
	}

	/**
	 * 创建一个原子, 其原子核为一个 排版的 Box ()
	 * An atom that contains a typeset box (like an hbox or vbox)
	 * @param box
	 * @return 返回 mItem 对象.
	 */
	public static AtomItem Typeset(Box box) {
		// 使用另一个 AtomItem 的构造方式.
		AtomItem item = new AtomItem(TYPE_ord, box);
		return item;
	}
}
