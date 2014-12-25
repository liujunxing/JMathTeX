package jsmath;

/**
 * 表示一个距离单位. (见 texbook 第10章)
 *
 */
public class Dimen {
	/** 宽度值 (这里应该以 em 为单位) */
	private float w;
	
	public static Dimen ZERO_D = new Dimen(0f);
	
	public Dimen() {
		this.w  = 0f;
	}
	
	public Dimen(float w) {
		this.w = w;
	}
	
	/**
	 * 对值取反.
	 * @return
	 */
	public Dimen toNeg() {
		return new Dimen(-w);
	}

	/**
	 * 得到实际宽度
	 * @return
	 */
	public float fvalue() { 
		return w;
	}
}
