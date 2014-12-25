package jsmath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implement image-based fonts for fallback method
 *
 */
public class CImg {
	/** 容器对象 */
	//private final JsMath jsMath;
	
	/** image fonts are loaded */
	private boolean loaded = false;
	
	/** font sizes available */
	public static int fonts[] = new int[] { 50, 60, 70, 85, 100, 120, 144, 173, 207, 249, 298, 358, 430 };
	
	private float em = 1.0f;
	
	/** (已知)在 Box.TeXIMG() 函数中使用. */
	public float scale = 1.0f;

	// 问题, 这个 em 是怎么算出来的, 相对于谁?
	/** 不同字体大小下 em 的宽度. em widths for the various font size directories */
	private static HashMap<Integer, Float> w = init_w(); 	
	private static HashMap<Integer, Float> init_w() { 
		HashMap<Integer, Float> m = new HashMap<Integer, Float>();
		m.put(50, 6.9f);
		m.put(60, 8.3f);
		m.put(70, 9.7f);
		m.put(85, 11.8f);
		m.put(100, 13.9f);
		m.put(120, 16.7f);
		m.put(144, 20.0f);
		m.put(173, 24.0f);
		m.put(207, 28.8f);
		m.put(249, 34.6f);
		m.put(298, 41.4f);
		m.put(358, 49.8f);
		m.put(430, 59.8f);
		return m;
	}
	
	/** index of best font size in the fonts list */
	public int best = 4;
	
	// 在 jsMath 中 jsMath.Font.Check() 中调用 SetFont() 使用的参数形式为 {cmr10: ['all'], cmmi10: ['all'] ...}
	// 所以 update 结构可能是 Map<String, List<String>> 的形式...?
	/** 要更新的字体. fonts to update (see UpdateFonts below) */
	private Map<String, List<String>> update = new HashMap<String, List<String>>();
	
	/** 图像字的缩放因子(以求得更好的打印效果).  factor by which to shrink images (for better printing) */
	private float factor = 1.0f;
	
	public CImg() {
		
	}

	/**
	 * 调用示例: (在 jsMath.Font.Check() 函数中)
	 * jsMath.Img.SetFont({
          cmr10:  ['all'], cmmi10: ['all'], cmsy10: ['all'],
          cmex10: ['all'], cmbx10: ['all'], cmti10: ['all']
        });
     * 
     * 我们简化输入参数为 String, List<String>, 原来的调用就分解为多次调用. 
	 * add characters to be drawn using images
	 */
	public void SetFont(String font_name, List<String> change) {
		if (this.update.containsKey(font_name) == false) {
			this.update.put(font_name, new ArrayList<String>());
		}
		List<String> old_change = this.update.get(font_name);
		old_change.addAll(change);
	}
	
	/**
	 * 实际现在未见到调用的地方, 因此不实现它了.
	 * Called by the extra-font definition files to add an image font into the mix
	 * @param size
	 * @param def
	 */
	public Object AddFont(Object size, Object deqf) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 计算图像字体的比例因子(scale factor). (暂时不知道哪里使用该因子)
	 * Get the scaling factor for the image fonts
	 */
	public void Scale() {
		if (this.loaded == false) return;
		// 计算最适合的图片字体的索引. (用该图片效果最好, 显示及打印)
		this.best = this.BestSize(); 
		
		this.em = CImg.w.get(CImg.fonts[this.best]); // 图像字的 em 大小.
		this.scale = JsMath.em / this.em; // 缩放比例?
		if (Math.abs(this.scale - 1) < 0.12f) // 距离 1.0 比较近, 则使用 1.0
			this.scale = 1.0f;
	}
	
	/**
	 * 找到最适合于当前字体的 图像字体大小(其是图像文件的目录名, 用于 fallback(下落)模式)
	 * Find the font size that best fits our current font
     *  (this is the directory name for the img files used
     *  in some fallback modes).
	 * @return 返回到数组 fonts[] 的索引.
	 */
	public int BestSize() {
		float w = JsMath.em * this.factor; // 如每 em 大约 24.8;
		// min=this.fonts[0] -- 最小的字体比例. this.w[min] -- 该比例下 em 值.
		int _min = CImg.fonts[0]; // 从最小的字体比例开始.
		float m = CImg.w.get(_min); // 得到其对应宽度. (?单位是 em), 如 min=50 对应 m=6.9f

		// 遍历所有可能的图像大小, 
		int i;
		for (i = 1; i < CImg.fonts.length; ++i) {
			float next_m = CImg.w.get(CImg.fonts[i]);
			if (w < (next_m + 2*m) / 3) // 在本等级->下等级之间. 加权平均离 next_m 远一点, 离 m 近一点. 
				return i-1;
			m = next_m;
		}
		return i-1; // 最大的也不够用, 那也只能选择最大的了.
	}

	/**
	 * 更新字体 (fonts) 以使用图像文件, 而不是使用原始(原生)字体.
	 *   它在 jsMath.Img.update 数组中查找需要更新的字体的名字, 以及...
	 *   
	 * Update font(s) to use image data rather than native fonts
     *  It looks in the jsMath.Img.update array to find the names
     *  of the fonts to udpate, and the arrays of character codes
     *  to set (or 'all' to change every character);
	 * @return
	 */
	public void UpdateFonts() {
		Map<String, List<String>> change = this.update;
		if (this.loaded == false) return;
		
		// 遍历要更新的所有的字体.
		for (String font_name : change.keySet()) {
			List<String> font_change_list = change.get(font_name);
			for (int i = 0; i < font_change_list.size(); ++i) {
				String c = font_change_list.get(i);
				if ("all".equals(c)) {
					// 更新指定字体中的所有字符.
					FontInfo fi = JsMath.TeX.getFontByName(font_name);
					for (FontCharInfo fci : fi.getAll()) {
						// 原: jsMath.TeX[font][c].img = {}, 我们暂时用 Object 替代.
						fci.img = CharImg.Empty;
					}
				}
				else {
					// 其它情况我们暂时不支持... (为更新指定字符的 img 信息.)
					throw new UnsupportedOperationException(); 
				}
			}
		}
		
		this.update = new HashMap<String, List<String>>();
	}
}

