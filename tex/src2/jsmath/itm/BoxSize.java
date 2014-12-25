package jsmath.itm;

/**
 * 表示 EmBoxFor 返回的值.
 */
public class BoxSize {
	public float w;
	public float h;
	
	public BoxSize() {
		w = h = 0;
	}
	public BoxSize(float w, float h) {
		this.w = w; this.h = h;
	}
}
