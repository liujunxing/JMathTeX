package jsmath;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供能够安全调用 get(), set() 方法的 List
 * @author liujunxing
 *
 */
public class SafeList<T> extends ArrayList<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 如果 get() 不存在时返回的缺省值 */
	public T defval = null;
	
	/**
	 * 构造新实例.
	 */
	public SafeList() {
		
	}
	
	public SafeList(T defval) {
		this.defval = defval;
	}
	
	public SafeList(List<T> arr) {
		if (arr != null) {
			this.addAll(arr);
		}
	}
	
	/**
	 * 安全的获取指定索引的值. 如果不存在则返回 defval.
	 */
	@Override
	public T get(int index) {
		if (index < 0 || index >= this.size())
			return this.defval;
		return super.get(index);
	}
	
	/**
	 * get(index) 的重载版本, 如果指定 index 位置不存在, 则返回 defval.
	 * @param index
	 * @param defval
	 * @return
	 */
	public T get(int index, T defval) {
		if (index < 0 || index >= this.size())
			return this.defval;
		return super.get(index);
	}

	/**
	 * 设置指定位置上的元素.
	 */
	public T set(int index, T element) {
		// 确保能容纳下 index 的元素.
		super.ensureCapacity(index+1);
		
		return super.set(index, element);
	}
	
	/**
	 * 确保有 size 个元素. 如果不足则用 fill_element 来填充.
	 * @param size
	 */
	public void ensureSize(int size, T fill_element) {
		if (this.size() < size) {
			for (int i = this.size(); i < size; ++i)
				this.add(fill_element);
		}
	}

	/**
	 * 用于 assert 的辅助函数, 判断每个元素都非空.
	 * @return 返回 true 表示所有元素都非空; 返回 false 表示至少有一个元素为 null.
	 */
	public boolean notNull() {
		for (int i = 0; i < size(); ++i) {
			if (super.get(i) == null) 
				return false;
		}
		return true;
	}
}
