package jsmath;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement font check and messages
 *
 */
public class Font {
	/**
	 * 在原jsMath 中此函数做很多事情, 我们为了调用 Img.SetFont() 先加上一个空壳函数吧.
	 */
	public static void Check() {
		do_setFont();
		// TODO: 更多的处理...
	}
	
	private static void do_setFont() {
		List<String> change_list = new ArrayList<String>();
		change_list.add("all"); // "all" 表示更新所有的字符的, 否则为该指定字符值...; 但现在都是 all
		
		JsMath.Img.SetFont("cmr10", change_list);
		JsMath.Img.SetFont("cmmi10", change_list);
		JsMath.Img.SetFont("cmsy10", change_list);
		JsMath.Img.SetFont("cmex10", change_list);
		JsMath.Img.SetFont("cmbx10", change_list);
		JsMath.Img.SetFont("cmti10", change_list);
	}
}
