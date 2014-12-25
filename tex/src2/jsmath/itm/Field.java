package jsmath.itm;

/**
 * 作为原子的 nuc, sup, sub (分别是原子核,上标,下标) 字段值.
 * 
 * 当前子类有:
 *   TeXCharField -- type="TeX" 
 *   MlistField -- type="mlist" MathList 作为原子的核.
 *   HtmlTextField -- type="text" 表示 HTML 文本.
 */
public abstract class Field extends TypeObject {
	/** 空的原子核(出现在给出上下标, 但没有原子核的情况下) */
	public final static String FIELD_TYPE_null = "null";
	
	/** 字段的类型 "TeX", 表示一个 tex 的字符(或一个单元), 其具有指定字体. */
	public final static String FIELD_TYPE_TeX = "TeX"; 
	
	/** MlistField 使用 MathList 作为原子的字段. */
	public final static String FIELD_TYPE_mlist = "mlist";
	
	/** HtmlTextField 使用的类型 (由于实现的问题, 我们应避免使用此类型) */
	public final static String FIELD_TYPE_text = "text";
	
	
	/** (当前已知)在排版 Typeset.DoTypeset() 函数中设置/使用. */
	public float x;
	public float y;
	
	/**
	 * 构造.
	 */
	protected Field() {
		
	}

	
	/**
	 * 判断对象 o 的类型是否是指定类型 type.
	 * @param o
	 * @param type
	 * @return
	 */
	/*
	public static boolean _isTypeEqual(TypeObject o, String type) {
		String otype = o.type;
		if (type == null && otype == null) // 两者都为 null 则认为相等. 
			return true;
		if (type == null) {
			// 此时 otype 必不为 null
			return otype.equals(type);
		}
		else {
			// 此时 type 必不为 null
			return type.equals(otype);
		}
	} */
}
