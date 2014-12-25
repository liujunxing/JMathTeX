package jsmath.itm;

/**
 * 表示一个 change-style 的 mItem, 在 mList 列表中使用.
 *
 */
public class StyleItem extends MItem {
	private final String style;
	
	/**
	 * 构造一个 ChangeStyleItem 的新实例.
	 */
	public StyleItem(String style) {
		super.type = TYPE_style;
		this.style = style;
	}
	
	/**
	 * 得到改变成的 style 值.
	 * @return
	 */
	public String getStyle() {
		return this.style;
	}
}
