package jsmath;

/**
 * 用于 FontCharInfo 中 delim 字段, 描述一个组合字符的定义.
 * @author liujunxing
 *
 */
public class DelimChar {
	public int top;
	public int mid;
	public int bot;
	public int rep;
	
	public DelimChar(int top, int mid, int bot, int rep) {
		this.top = top;
		this.mid = mid;
		this.bot = bot;
		this.rep = rep;
	}
}
