package jsmath.itm;

import jsmath.DelimInfo;
import jsmath.MList;

/**
 * 定界符项目类.
 *
 */
public class BoundaryItem extends AtomItem {
	/** 此实例保存的 MList 的当前数据. */
	public MList.Data data;
	
	/** 左定界符, 如果有的话. 没有则表示是一个 '{' 组开始. */
	public DelimInfo left;
	
	/** 右定界符, 如果有的话. */
	public DelimInfo right;
	
	/**
	 * 构造 BoundaryItem 的新实例.
	 * @param data
	 */
	public BoundaryItem(MList.Data data, DelimInfo left, DelimInfo right) {
		super.type = TYPE_boundary;		// "boundary"
		super.atom = false;
		this.data = data;
		this.left = left;
		this.right = right;
	}

}
