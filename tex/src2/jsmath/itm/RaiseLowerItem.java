package jsmath.itm;

import jsmath.Dimen;

/**
 * 为支持 \raise, \lower 命令.
 *
 */
public class RaiseLowerItem extends AtomItem {
	/** 要提升的尺寸. 其值可能有正有负, 正值表示向上提升, 负值表示向下降低. */
	private Dimen h;
	
	/**
	 * 构造一个新的 RaiseLowerItem 的实例.
	 * @param atom
	 * @param raise_h
	 */
	public RaiseLowerItem(Field atom, Dimen h) {
		super("raise", atom);
		this.h = h;
	}
	
	/***/
	public Dimen getH() {
		return this.h;
	}
}
