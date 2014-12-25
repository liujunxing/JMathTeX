package jsmath.itm;

import jsmath.Box;
import jsmath.DelimInfo;

/**
 * 表示分数 math item.
 *
 */
public class FractionItem extends AtomItem /*MItem*/ {
	/** 构造此分数相的命令的名字. */
	public String from;
	
	/** 分子部分 */
	public Field num;
	
	/** 分母部分 */
	public Field den;
	
	/** 分数线的粗细. 其类型我们稍后确定下来. */
	public Object thickness = null;
	
	/** 这是什么类型的呢? 在 Parser.Close() 函数中, 为 \over 命令调用的 Fraction() 函数
	 *  给出了 left, right 参数, 其来自于 from(data.overF) 参数.
	 *  现在暂时认为是 String 类型的. */
	public DelimInfo left = null;
	
	public DelimInfo right = null;
	
	/**
	 * 构造一个分数项.
	 * @param cmd_name -- 构造此分数项的命令名, 当前知道有 'over', 'above' 等几种.
	 * @param num
	 * @param den
	 */
	public FractionItem(String from, Field num, Field den) {
		super(TYPE_fraction, (Box)null);
		// super.type = TYPE_fraction;
		this.from = from;
		this.num = num;
		this.den = den;
	}

	public FractionItem(String from, Field num, Field den, Object thickness, 
			DelimInfo left_delim, DelimInfo right_delim) {
		super(TYPE_fraction, (Box)null);
		// super.type = TYPE_fraction;
		this.from = from;
		this.num = num;
		this.den = den;
		this.thickness = thickness;
		this.left = left_delim;
		this.right = right_delim;
	}
}
