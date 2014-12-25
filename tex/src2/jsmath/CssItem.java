package jsmath;

import java.util.LinkedList;
import java.util.List;

/**
 * 表示 CSS 的一个条目.
 *
 */
public class CssItem {
	/** 此 CSS 项目的名字, 如 '.math', '.typeset .normal' */
	private String name;
	
	/** 所有的 css 值 */
	private List<StringPair> items = new LinkedList<StringPair>();
	
	public CssItem() {
		
	}
	
	/**
	 * 使用指定名字和 css 值构造 CssItem 的新实例.
	 * @param name
	 * @param sparr
	 */
	public CssItem(String name, StringPair ... sparr) {
		this.name = name;
		for (StringPair e : sparr)
			items.add(e);
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<StringPair> getItems() {
		return this.items;
	}
}
