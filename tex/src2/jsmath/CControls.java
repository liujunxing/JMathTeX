package jsmath;

/**
 * Implements the jsMath control panel.
 *  Much of the code is in jsMath-controls.html, which is
 *  loaded into a hidden IFRAME on demand
 *
 */
public class CControls {
	/** Data stored in the jsMath cookie */
	public CCookie cookie = new CCookie();
	
	/**
	 * Get the cookie data from the browser
     *  (for file: references, use url '?' syntax)
	 */
	public void GetCookie() {
		// 由于我们现在没有浏览器, 那么 cookie 也就不需要了吧?
		// 因为 cookie 就是我们给浏览器的~
	}
}
