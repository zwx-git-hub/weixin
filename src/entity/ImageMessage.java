package entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("xml")
public class ImageMessage extends BaseMessage{
	@XStreamAlias("MediaId")
	private String mediaId;
	
	public ImageMessage(Map<String, String> requestMap,String mediaId) {
		super(requestMap);
		this.setMsgType("image");
		this.mediaId=mediaId;

	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	@Override
	public String toString() {
		return "ImageMessage [mediaId=" + mediaId + ", getToUserName()=" + getToUserName() + ", getFromUserName()="
				+ getFromUserName() + ", getCreatTime()=" + getCreatTime() + ", getMsgType()=" + getMsgType() + "]";
	}
	
	




}
