package entity;

import java.io.Serializable;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public class BaseMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	@XStreamAlias("ToUserName")
	private String toUserName;
	@XStreamAlias("FromUserName")
	private String fromUserName;
	@XStreamAlias("CreateTime")
	private String createTime;
	@XStreamAlias("MsgType")
	private String msgType;
	
	public BaseMessage(Map<String, String> requestMap) {
		this.toUserName = requestMap.get("FromUserName");
		this.fromUserName = requestMap.get("ToUserName");
		this.createTime = System.currentTimeMillis()/1000+"";
	}
	
	
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getCreatTime() {
		return createTime;
	}
	public void setCreatTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}


	@Override
	public String toString() {
		return "BaseMessage [toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime
				+ ", msgType=" + msgType + "]";
	}
	
	
}
