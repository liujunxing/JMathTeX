package jsmath.itm;

/**
 * 表示带有 type 属性的基类, 提供给 mItem, Box, Atom 做基类.
 *
 */
public class TypeObject {
	/** 这个对象的类型, 在原 js 中使用 type 属性来区分很多东西, 我们暂时先继承其实现. */
	public String type = null;
	
	/** 简单的判断函数, 判断 type == otype */
	public boolean type_is(String otype) {
		if (this.type == null) {
			return (otype == null);
		}
		else {
			return this.type.equals(otype);
		}
	}

	/** 简单的判断函数, 判断 type != otype */
	public boolean type_not_is(String otype) {
		return !type_is(otype);
	}
}
