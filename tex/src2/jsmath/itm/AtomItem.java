package jsmath.itm;

import jsmath.Box;

/**
 * 含有原子核,上标,下标字段的 mitem (被给出该原子的核).
 *  A general atom (given a nucleus for the atom)
 *  
 *  假设有 AccentAtom extends AtomItem, 则 AccentAtom 额外有字段 accent.
 *  
 */
public class AtomItem extends MItem {
	/** 此原子的核(nucleus), 其可能为空. 其可能为 Field 类型, 也可能是 Box 类型. */
	public TypeObject nuc;  // 这里最好用 Field 类型, 但是某些地方设置了 box 值, 所以我们只能暂时用 TypeObject 做类型.
		// 或者我们把 Box 从 Field 派生?? 这也许是一个很好的主意...
	
	/** 上标(subscript), 可能没有. */
	public Field sup = null;
	
	/** 下标(superscript), 可能没有. */
	public Field sub = null;
	
	/** 是否有 \limits, \nolimits 命令, 或 null 没有限定修饰(取缺省) */
	public Boolean limits = null;
	
	/** 在 MList.atomize_ord() 函数中设置. 语义是什么? */
	public boolean textsymbol = false;
	
	/** 重音信息; 当前暂时用 int[3] 的类型. 也可能用 MathCharDef 类型很合适. */
	public int[] accent;
	
	/** 提供给派生类使用. */
	protected AtomItem() {
		
	}
	
	/**
	 * 使用指定参数构造
	 * @param type
	 * @param nucleus
	 */
	public AtomItem(String type, Field nuc) {
		super.type = type;
		super.atom = true;
		this.nuc = nuc;
	}
	
	/**
	 * 在 MItem.Typeset() 中调用此构造.
	 * @param type
	 * @param box
	 */
	public AtomItem(String type, Box box) {
		super.type = type;
		super.atom = true;
		this.nuc = box;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("AtomItem{type=").append(this.type).append(",")
			.append("atom=").append(this.atom)
			.append("nuc=").append(this.nuc);
		if (this.sup != null) {
			str.append(",sup=").append(this.sup);
		}
		if (this.sub != null) {
			str.append(",sub=").append(this.sub);
		}
		str.append("}");
		return str.toString();
	}
}
