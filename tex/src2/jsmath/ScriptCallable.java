package jsmath;

/**
 * 定义可以在 CScript 中被队列化来执行的调用的接口.
 *
 */
public interface ScriptCallable {
	/**
	 * 从 CScript 中执行, method 和 data 是注册时给出的, 由注册者自己负责解释.
	 * @param method
	 * @param data
	 */
	public void scriptCall(String method, Object data);
}
