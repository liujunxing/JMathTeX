package test;

/**
 * 测试 Random() 的随机性, 以及进行某些统计信息.
 *
 */
public class Trnd {
	/** 来自命令行的参数 */
	private final String[] args;
	private Randomer rnd = new Randomer();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Trnd obj = new Trnd(args);
		obj.do_main();
	}
	
	private Trnd(String[] args) {
		this.args = args;
	}
	
	/**
	 * 执行主要程序.
	 */
	private void do_main() throws Exception {
		// 小结论: 不存在某种固定模式, 使得概率高一些. 必须研究更复杂的策略.
		//SeqFinder sf = new SeqFinder(1000000, new int[] {51, 51, 0, 51, 0, 51});
		//sf.find();
		
		AutoStar astr = new AutoStar(100000, 18);
		astr.chong();
		
		// stat_xbs2(10000000);
		// stat_lxb3(50, 10000000, 18);
		// PercentStat ps = stat_perc(75, 10000000); ps.print();
		
		// 从命令行输入一个字符串.
		java.io.InputStreamReader isr = new java.io.InputStreamReader(System.in);
		java.io.BufferedReader reader = new java.io.BufferedReader(isr);
		
		while (true) {
			System.out.print(">>>"); // 提示符.
			String cmd = reader.readLine().trim();
			if ("exit".equals(cmd) || "quit".equals(cmd)) break;
			if ("t".equals(cmd)) t();
		}
		
	}
	
	public static final void Print(String s) {
		System.out.print(s);
	}
	public static final void Println(String s) {
		System.out.println(s);
	}
	
	private int pian_cha = 0;
	private void t() {
		int r = this.rnd.rand_100();
		if (r < 50) pian_cha += 50;
		else pian_cha -= 50;
		System.out.println("r = " + r + ", pian_cha = " + pian_cha);
	}
	
	/** 冲星的成功率, 在当前星数向上冲的成功比率 */
	public static final int[] STAR_RATIO = new int[] {
	//                               3星                  5星                7星                9星
	//  0    1    2    3    4    5    6   7   8   9   10  11  12  13  14  15  16  17  18
		100, 100, 100, 100, 100, 100, 71, 66, 61, 56, 51, 51, 51, 51, 51, 51, 51, 51, 51
	};
	
	/** 冲星失败之后掉落到的星数 */
	public static final int[] STAR_DOWN = new int[] {
	//                                3星                  5星                7星                9星
	//  0    1    2    3    4    5    6    7   8   9   10  11  12  13  14  15  16  17  18
		0,   1,   2,   3,   4,   5,   6,   6,  6,  6,  9,  10, 10, 10, 13, 14, 14, 14, 17
	};
	
	// 特定序列的次数/概率查找
	private class SeqFinder {
		/** 要查找的成功率 pattern, 如 [73, 71, 51, 51, -1] 中 73 表示查找 73% 成功率的一个节点. -1 表示忽略该成功率(垫手) */
		public int[] patt;
		
		public int[] buf = new int [10240]; // 一个大的 buffer, 在此 buffer 中扫描
		public int buf_begin = 0; // buf 扫描开始位置.
		public int buf_end = 0;   // buf 扫描结束位置.
		public int filled_n = 0;  // buf 中已经填充了的数目.
		public int n; // 总计要在多长的序列中找
		public int count; // 找到的序列总数.
		
		public SeqFinder(int n, int[] patt) {
			this.n = n;
			this.patt = patt;
		}
		// 进行查找.
		public void find() {
			while (true) {
				// 1. 填充 buf, 如果全部完成则退出.
				if (filled_n >= n) break;
				fill_buf();
				
				// 2. 在已经填充的序列中找.
				if (buf_end - buf_begin < patt.length) break; // 如果 buf 数据不足, 则退出.
				int_find();
			}
			
			String pat_str = java.util.Arrays.toString(patt);
			Println("在长度为 " + n + " 的随机数序列中查找模式 " + pat_str + " 共找到 " + count + " 次.");
		}
		
		private void int_find() {
			for (; buf_begin < buf_end - patt.length; ++buf_begin) {
				// 尝试匹配 buf[i] 开始的 buf, 长度为 patt.length, 看是否能匹配.
				if (match(buf, buf_begin)) {
					++count;
				}
			}
		}
		
		private boolean match(int[] buf, int start) {
			for (int i = 0; i < patt.length; ++i) {
				if (patt[i] == 0) continue;
				
				if (patt[i] > 0) {
					// 产生的 buf[i] 的随机数如果大于 patt[i], 则表示冲星失败, 也即匹配失败. 
					if (buf[start+i] > patt[i]) return false;
				} else { // patt[i] < 0
					// 如 patt[i] = -66 表示 66% 失败
					if (buf[start+i] < -patt[i]) return false;
				}
			}
			return true; // 是完全匹配的.
		}
		
		private void fill_buf() {
			// 如果不在开始, 则移动直到开始.
			if (buf_begin > 0) {
				System.arraycopy(buf, buf_begin, buf, 0, buf_end - buf_begin);
				buf_end -= buf_begin; buf_begin = 0;
			}
			// 计算可填充的数量.
			int can_fill = buf.length - buf_end;
			if (can_fill + filled_n > n)
				can_fill = n - filled_n;
			
			// 填充 buf.
			for (int i = buf_end; i < can_fill+buf_end; ++i)
				buf[i] = rnd.rand_100();
			
			buf_end += can_fill; // 移动末尾指针.
			filled_n += can_fill; // 更新已经填充的总量.
		}
	}
	
	
	// 用于: 计算自动冲星 1.需要消耗的石头数量, 2.平均次数, 等数据.
	private class AutoStar {
		private int n; // 冲星次数.
		private int stars; // 冲到多少个(内部)星, 3星表示=6, 5星=10, 6.5星=13
		
		private int cur_stars; // 当前星数.
		
		public AutoStar(int n, int stars) {
			this(n, stars, 6);  // 缺省从 3 星开始.
		}

		public AutoStar(int n, int stars, int init_stars) {
			this.n = n;
			this.stars = stars;
			this.cur_stars = init_stars;
		}

		public void chong() {
			for (int i = 0; i < n; ++i) {
				chong2();
				stat_init_chong2();
			}
			
			Println("自动冲星到 " + stars/2 + " 星 " + n + " 次 :");
			Println("  总计尝试次数: " + total_try_num);
			Println("  成功次数: " + total_suc_num);
			Println("  失败次数: " + total_fail_num);
		}
		
		// 总的冲星次数, 成功次数, 失败次数.
		private int total_try_num = 0;
		private int total_suc_num = 0;
		private int total_fail_num = 0;
		
		private int try_num = 0;
		private int suc_num = 0;
		private int fail_num = 0;
		
		private void stat_init_chong2() {
			total_try_num += try_num;
			total_suc_num += suc_num;
			total_fail_num += fail_num;
			
			try_num = 0;
			suc_num = 0;
			fail_num = 0;
			cur_stars = 6;
		}
		
		private void chong2() {
			while (true) {
				++this.try_num;
				
				// 产生随机数.
				int r = rnd.rand_100();
				// 计算当前星数下成功比率
				int ss = STAR_RATIO[this.cur_stars];
				
				if (r < ss) {
					// 成功.
					++this.cur_stars;
					++this.suc_num;
				}
				else {
					// 失败.
					this.cur_stars = STAR_DOWN[this.cur_stars]; // 星掉落
					++this.fail_num;
					// TODO: 更精细的统计在不同位置掉落次数.
				}
				
				if (cur_stars >= stars) break; // 冲好了.
			}
		}
	}
	
	
	private static class PercentStat {
		public int percent;
		public int n;
		public int plus;
		public int minus;
		public PercentStat(int percent, int n, int plus, int minus) {
			this.percent = percent;
			this.n = n;
			this.plus = plus;
			this.minus = minus;
		}
		public void print() {
			System.out.println("统计 " + n + " 次, " + percent + "% 成功 " + plus + " 次, 失败 " + minus + " 次");
		}
	}
	
	/**
	 * 统计
	 * @param percent -- 在区间 [0, percent) 的算正, 在区间 [percent, 100) 算负.
	 * @param n -- 循环次数
	 */
	private PercentStat stat_perc(int percent, int n) {
		int plus = 0;  // 正(成功)
		int minus = 0; // 负(失败)
		for (int i = 0; i < n; ++i) {
			int r = this.rnd.rand_100();
			if (r < percent) 
				++plus;
			else
				++minus;
		}
		return new PercentStat(percent, n, plus, minus);
	}

	/** 统计在百分比 percent 下连续爆 2 次的数量 */
	private void stat_lxb(int percent, int n) {
		boolean prev_bao = false;
		int bao_num = 0;
		for (int i = 0; i < n; ++i) {
			int r = this.rnd.rand_100();
			if (r < percent) 
				prev_bao = false; // 成功了.
			else {
				if (prev_bao)
					++bao_num;
				prev_bao = true;
			}
		}
		
		System.out.println("统计 " + n + " 次, 在百分比 " + percent + " 下连续爆 2 次的次数为 " + bao_num + " 次.");
	}
	
	/** 统计在百分比 percent 下连续爆 x 次(>=2)的数量 */
	private void stat_lxb3(int percent, int n, int x) {
		int prev_bao = 0;
		int bao_num = 0;
		for (int i = 0; i < n; ++i) {
			int r = this.rnd.rand_100();
			if (r < percent) 
				prev_bao = 0; // 成功了, 则爆的次数清 0.
			else {
				++prev_bao;
				if (prev_bao >= x)
					++bao_num;
			}
		}
		
		System.out.println("统计 " + n + " 次, 在百分比 " + percent + " 下连续爆 " + x + " 次的次数为 " + bao_num + " 次.");
	}

	/** 统计连续两次 66% 失败之后, 51% 的成功数量. */
	private void stat_xbs2(int n) {
		int p66_num = 0;
		int p66_false = 0;
		int p51_true = 0;
		int p51_false = 0;
		for (int i = 0; i < n; ++i) {
			int r = this.rnd.rand_100();
			if (p66_false >= 2) {
				++p66_num;
				if (r >= 51)
					++p51_false;
				else
					++p51_true;
				if (r >= 66)
					++p66_false;
			}
			
			if (r >= 66) {
				++p66_false;
			}
			else
				p66_false = 0;
		}
		
		System.out.println("统计 " + n + " 次, 连续两次 66% 失败次数 " + p66_num + ", 51% 成功次数 " +
				p51_true + ", 51% 失败次数 " + p51_false);
	}
}
