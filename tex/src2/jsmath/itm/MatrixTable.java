package jsmath.itm;

import jsmath.SafeList;

/**
 * 表示一个 matrix/array 的表格.
 * @author liujunxing
 *
 */
public class MatrixTable extends SafeList<MatrixRow> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8394885570932336572L;

	/**
	 * 构造新实例.
	 */
	public MatrixTable() {
		
	}

	/**
	 * 计算最大列数. 每行都有列数, 这里返回具有最多个列的那一行的列的数量.
	 * @return 返回最大列数. 没有则返回 0.
	 */
	public int get_max_cols() {
		int max_cols = 0;
		for (int i = 0; i < size(); ++i) {
			int cols = get(i).size();
			if (cols > max_cols) max_cols = cols;
		}
		return max_cols;
	}
}
