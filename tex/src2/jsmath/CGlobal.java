package jsmath;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements items associated with the global cache.
 *
 * This object will be replaced by a global version when 
 *  (and if) jsMath-global.html is loaded.
 */
public class CGlobal {
	/** a local copy if jsMath-global.html hasn't been loaded */
	private boolean isLocal = true;
	
	// 含 T,D,R,B 等 ......
	private Map<String, Object> cache = new HashMap<String, Object>();
	
	private static CGlobal g_instance = new CGlobal();
	
	/**
	 * 获得此类的唯一实例. 
	 * TODO: 这个类全局是唯一的吗? 还是每 JsMath 都需要不同的??
	 * @return
	 */
	public static CGlobal instance() {
		return g_instance;
	}
	
	/**
	 * Clear the global (or local) cache.
	 */
	public void ClearCache() {
		throw new java.lang.UnsupportedOperationException();
	}
	
    /**
     *  Initiate global mode
     */
	public void GoGlobal() {
		throw new java.lang.UnsupportedOperationException();
	}
	
	/**
     *  Check if we need to go to global mode
     */
    public void Init() {
    	// TODO: 以后根据需要进行初始化~
    	// this.GoGlobal(...)
	}
	
    /**
     *  Try to register with a global.html window that contains us
     */
    public void Register() {
    	// 在 jsMath 启动的时候调用. 暂时不实现该功能(应不太影响)
    }
    
    /**
     *  If we're not the parent window, try to set the domain to
     *  match the parent's domain (so we can use the Global data
     *  if the surrounding frame is a Global frame).
     */
    public void Domain() {
    	throw new java.lang.UnsupportedOperationException();
    }
}
