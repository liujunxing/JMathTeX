package jsmath;

/**
 * Implement browser-specific checks
 *
 */
public class Browser {

	/**
	 * (已知) 在 HTML.Blank() 中使用, 判定某条件.
	 */
	public boolean blankWidthBug = false;
	
	/**
	 * (已知) 在 HTML.Blank() 中使用, 判定某条件.
	 */
	public boolean quirks = false;
	
	/**
	 * (已知) 在 HTML.Blank() 中使用.
	 */
	public boolean mozInlineBlockBug = false;
	
	/**
	 * (已知) 在 HTML.Blank() 中使用.
	 */
	public boolean msieBlockDepthBug = false;

	/**
	 * (已知) 在 HTML.Absolute() 中使用.
	 */
	public boolean msieAbsoluteBug = false;
	
	/**
	 * (已知) 在 HTML.PlaceAbsolute() 中使用.
	 */
	public boolean msieRelativeClipBug = false;
	
	/**
	 * (已知) 在 HTML.Blank() 中使用.
	 */
	public boolean widthAddsBorder = false;

	/**
	 * (已知) 在 HTML.PlaceAbsolute() 中使用.
	 */
	public boolean operaAbsoluteWidthBug = false;
	
	/**
	 * (已知) 在 HTML.Absolute() 中使用.
	 */
	public boolean lineBreakBug = false;
	
	/** (已知) 在 Parser.Typeset() 函数中使用. */
	public boolean allowAbsolute = true;
	
	/** (已知) 在 Box.LayoutAbsolute() 函数中使用. */
	public boolean spanHeightVaries = true;
}
