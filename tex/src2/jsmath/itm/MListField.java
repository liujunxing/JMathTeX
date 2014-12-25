package jsmath.itm;

import jsmath.MList;

/**
 * 内容是 mlist 的字段.
 *
 */
public class MListField extends Field {
	/** 内部是一个 MList 对象. 基本可认为是 MItem[] . */
	public MList mlist;
	
	/**
	 * 构造新实例.
	 */
	public MListField(MList mlist) {
		super.type = "mlist";
		this.mlist = mlist;
	}
}
