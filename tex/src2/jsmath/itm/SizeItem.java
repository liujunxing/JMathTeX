package jsmath.itm;

/**
 * 改变尺寸的项目.
 *
 */
public class SizeItem extends MItem {
	/** 字的尺寸 */
	private int size;
	
	/**
	 * 构造新的.
	 */
	public SizeItem(int size) {
		super.type = TYPE_size;
		this.size = size;
	}
	
	/**
	 * 得到尺寸.
	 * @return
	 */
	public int getSize() {
		return size;
	}
}
