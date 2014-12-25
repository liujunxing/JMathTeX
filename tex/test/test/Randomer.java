package test;

/**
 * 用于测试随机数.
 * 
 * 在 MSVC 中使用如下类似的算法生成伪随机数.
 * unsigned long test_holdrand = 0;
void test_srand(unsigned int seed)
{
  test_holdrand = (unsigned long)seed;
}

int test_rand()
{
  return ( ((test_holdrand = test_holdrand * 214013L + 2531011L) >> 16) & 0x7fff );
}
 */
public class Randomer {
	private final long init_seed;
	public long seed;
	
	/**
	 * 使用缺省的 seed 值构造一个 Randomer 的新实例.
	 */
	public Randomer() {
		this.init_seed = 0;
		this.seed = 0;
	}
	
	/**
	 * 使用指定的 seed 值构造一个 Randomer 的新实例.
	 * @param seed
	 */
	public Randomer(long seed) {
		this.init_seed = seed;
		this.seed = seed;
	}
	
	/**
	 * 产生一个新的随机数.
	 * @return
	 */
	public int rand() {
		// 分解的计算步骤, 用于测试.
		long seed2 = seed * 214013L + 2531011L;
		long seed3 = seed2 >> 16;
		long seed4 = seed3 & 0x7fff;
		int seed5 = (int)seed4;
		
		int r = (int)( ((seed = seed * 214013L + 2531011L) >> 16) & 0x7fff );
		return r;
	}
	
	/**
	 * 产生 [0-100) 之间的一个随机数. 我们当前先使用取模算法. 也许以后有更精确的算法.
	 * @return
	 */
	public int rand_100() {
		int r = rand();
		// return r % 100;
		// 现在已知 r 的范围在 [0, 0x7fff), 为了更精确的产生 [0, 100) 以内的随机数
		// 使用乘法/除法: 
		return (r * 100 / 0x7fff);
	}
	
	/**
	 * 恢复到初始的 seed 值.
	 */
	public void resetInitSeed() {
		this.seed = this.init_seed;
	}

}
