package jsmath.itm;

import jsmath.Dimen;

/**
 * 表示一个水平间距项目. 
 *
 */
public class SpaceItem extends MItem {
	/** 间距的宽度 */
	private Dimen w;
	
	/**
	 * 构造.
	 * @param w
	 */
	public SpaceItem(Dimen w) {
		super.type = TYPE_space;
		this.w = w;
	}
	
	/**
	 * 得到水平间距.
	 * @return
	 */
	public Dimen getW() {
		return this.w;
	}

	public float getWfloat() {
		return w.fvalue();
	}
}
