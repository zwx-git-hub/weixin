package test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

import entity.AbstractButton;
import entity.Button;
import entity.ClickButton;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.PhotoOrAlbumButton;
import entity.SubButton;
import entity.TextMessage;
import entity.VideoMessage;
import entity.ViewButton;
import entity.VoiceMessage;
import net.sf.json.JSONObject;
import service.WxService;
import util.GetConfig;

public class TestWX {
	//设置APPID/AK/SK
	public static final String APP_ID = "11519092";
	public static final String API_KEY = "q3TlGWWqEBG9uGvlFIBtpvY5";
	public static final String SECRET_KEY = "A14W5VRNG8my1GXYYAyNND0RjzBwxI8A";
	
	
	@Test
	public void testGetUserInfo() {
		//不同的公众号关注的同一个用户的openid是不同的  oZbGXjol4nzxEa2UyCX-sX2e3Zo4
		String user="oOAs80-MWjcchdCH5mi4YvEBkcpc";//oOAs80-MWjcchdCH5mi4YvEBkcpc
		String info = WxService.getUserInfo(user);
		System.out.println(info);
	}
	
	@Test
	public void testQrCode() {
		String ticket = WxService.getQrCodeTicket();
		System.out.println(ticket);
		//gQFf7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZGpsdDBfVWJjWWoxMXRZNzF2Y0IAAgTtJAdfAwRwFwAA
	}
	
	@Test
	public void test() {
		System.out.println(WxService.getAccessToken());
		//35_zsms4X6P3A4UQikNcscso5zS9Lz0-UTjQ2PAnNaib9lk6Gk0girPrjgE_BVFZIHT4dtsVDVlHhm_IRxescxA_oRwObEIm9Bt9cTV7L0H5T95v1tuDyvAGgOuQPmE-t0DoxhV1afTMBEZyygPVFYiAHAYIB
		//https://api.weixin.qq.com/cgi-bin/media/get?access_token=35_zsms4X6P3A4UQikNcscso5zS9Lz0-UTjQ2PAnNaib9lk6Gk0girPrjgE_BVFZIHT4dtsVDVlHhm_IRxescxA_oRwObEIm9Bt9cTV7L0H5T95v1tuDyvAGgOuQPmE-t0DoxhV1afTMBEZyygPVFYiAHAYIB&media_id=Q6kTWguXzDCAVlQx86ZcLaX8PiOg51EjkQ_oQ32K5_xeDTbErQjzEElXfA1mzY9f
		/*
		 * GetConfig config = new GetConfig(); String appid =
		 * config.getProperties("appid"); System.out.println(appid);
		 */
	}
	@Test
	public void testUpload() {
		String file = "C:\\Users\\糖糖\\Desktop\\01.png";
		String result = WxService.upload(file, "image");
		System.out.println(result);
		//Q6kTWguXzDCAVlQx86ZcLaX8PiOg51EjkQ_oQ32K5_xeDTbErQjzEElXfA1mzY9f
	}
	
	@Test
	public void testPic() {
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 可选：设置代理服务器地址, http和socket二选一，或者均不设置
		//client.setHttpProxy("proxy_host", proxy_port); // 设置http代理
		//client.setSocketProxy("proxy_host", proxy_port); // 设置socket代理

		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		//System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

		// 调用接口
		String path = "C:\\Users\\糖糖\\Desktop\\01.JPG";
		org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
		System.out.println(res.toString(2));
	}
	
	@Test
	public void testButton() {
		// 菜单对象
		Button btn = new Button();
		// 第一个一级菜单
		btn.getButton().add(new ClickButton("一级点击", "1"));
		// 第二个一级菜单
		btn.getButton().add(new ViewButton("一级跳转", "http://www.baidu.com"));
		// 创建第三个一级菜单
		SubButton sb = new SubButton("有子菜单");
		// 为第三个一级菜单增加子菜单
		sb.getSub_button().add(new PhotoOrAlbumButton("传图", "31"));
		sb.getSub_button().add(new ClickButton("点击", "32"));
		sb.getSub_button().add(new ViewButton("网易新闻", "http://news.163.com"));
		// 加入第三个一级菜单
		btn.getButton().add(sb);
		// 转为json
		JSONObject jsonObject = JSONObject.fromObject(btn);
		System.out.println(jsonObject.toString());
	}
	
	@Test
	public void testToken() {
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
	}
	
	
	
	@Test
	public void textMsg() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ToUserName", "to");
		map.put("FromUserName", "from");
		map.put("MsgType", "type");
		TextMessage tm = new TextMessage(map, "还好");
		XStream stream =new XStream();
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(MusicMessage.class);
		String xml = stream.toXML(tm);
		System.out.println(xml);
	}
}
