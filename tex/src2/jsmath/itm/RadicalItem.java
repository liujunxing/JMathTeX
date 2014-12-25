package jsmath.itm;

/**
 * 根式项目.
 *
 */
public class RadicalItem extends AtomItem {
	/** 根式下的表达式, 也即原子的核 nuc. */
	
	/** 根次, 可能为 null */
	public Field root;
	
	
	/**
	 * 构造新的根式项目.
	 */
	public RadicalItem(Field nuc) {
		super(TYPE_radical, nuc);
	}
}
