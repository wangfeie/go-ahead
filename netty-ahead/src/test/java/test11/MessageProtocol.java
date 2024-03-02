package test11;

/**
 * 协议包对象类
 * @author user
 *
 */
public class MessageProtocol {
	//数据发送的长度
	private int len;
	
	//数据本体，一般是放在byte数组中
	private byte[] content;

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
}