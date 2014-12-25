package jsmath;

/**
 * 表示一对字符串 key, value.
 *
 */
public class StringPair {
	public String key;
	public String value;
	
	public StringPair() {
		this.key = null;
		this.value = null;
	}
	
	public StringPair(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
