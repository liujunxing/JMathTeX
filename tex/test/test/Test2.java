package test;

import java.util.ArrayList;

/* 测试用例:
 * (1)   ab           最普通的两个项目, 可用于研究产生的 HTML, 尺寸等信息.
 * (2)   a \, b       \, 产生 1/6 em个宽度的空白(SpaceItem), 用于研究 Typeset() 中 "space" 分支
 * (3)   a+b          +号的 atom type 是 'bin'=2, 用于研究产生的间距. 以及解析.
 * (4)   a \sum b       \sum 的 atom type 是 'op'=1.
 *     (或 a \surd b) 我们也可以将某个字符设置为其 class='op' 做测试.
 * (5)   a^2          测试上标: 解析, 原子化, 排版. (先跟踪 js, 对比 java 进行详细测试)
 * (6)   x^m_n        测试上标,下标都存在的时候的排版.
 * (7)   \frac a b    测试分数. (a/b 分数)
 *   (7.1)  {a \brack b} 或  {a \brace b} 或 {a \choose b}, 以及扩展的 {a \bangle b}  
 *   (7.2)  \left( 1 \over 1-x^2 \right)  测试 left, right 定界符的解析.
 * (8)   x-y          测试减号, 减号在 jsmath 中用 symbol #x2212 实现的.
 */
public class Test2 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// jsmath.Startup.main("\\left( x \\right)");	// 测试 left,right 定界符
		// jsmath.Startup.main("{a \\brack b}"); // 临时测试 \over 系列命令用.
		// jsmath.Startup.main("\\frac x M");  // x/M 下面宽一些.
		// jsmath.Startup.main("\\hat a"); // 给字符 a 添加重音符号.
		jsmath.Startup.main("\\matrix{1 \\cr 2}");
	}
}
