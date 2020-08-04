package manager;

import org.junit.Test;

import service.WxService;
import util.Util;

public class TemplateMessageManager {
	/**
	 * 设置行业

	 */
	@Test
	public void set() {
		String at = WxService.getAccessToken();
		String url="https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token="+at;
		String data="{\n" + 
				"          \"industry_id1\":\"1\",\n" + 
				"          \"industry_id2\":\"4\"\n" + 
				"       }";
		String result = Util.post(url, data);
		System.out.println(result);
	}
	
	
	/**
	 * 获取行业
	 * 
	 */
	@Test
	public void get() {
		String at = WxService.getAccessToken();
		String url="https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token="+at;
		String result = Util.get(url);
		System.out.println(result);
	}
	
	/**
	 * 发送模板消息
	 * 封装成对象
	 */
	@Test
	public void sendTemplateMessage() {
		String at = WxService.getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+at;
		String data="{\n" + 
				"           \"touser\":\"oOAs80-MWjcchdCH5mi4YvEBkcpc\",\n" + 
				"           \"template_id\":\"gy3kOh_E8TOCPjuKj3rERgX1wxbru4K2IT1TuNwZ0KA\",         \n" + 
				"           \"data\":{\n" + 
				"                   \"first\": {\n" + 
				"                       \"value\":\"您有新的反馈信息啦！\",\n" + 
				"                       \"color\":\"#abcdef\"\n" + 
				"                   },\n" + 
				"                   \"company\":{\n" + 
				"                       \"value\":\"zwx公司\",\n" + 
				"                       \"color\":\"#fff000\"\n" + 
				"                   },\n" + 
				"                   \"time\": {\n" + 
				"                       \"value\":\"2000年11月11日\",\n" + 
				"                       \"color\":\"#1f1f1f\"\n" + 
				"                   },\n" + 
				"                   \"result\": {\n" + 
				"                       \"value\":\"面试通过\",\n" + 
				"                       \"color\":\"#173177\"\n" + 
				"                   },\n" + 
				"                   \"remark\":{\n" + 
				"                       \"value\":\"请和本公司人事专员联系！\",\n" + 
				"                       \"color\":\"#173177\"\n" + 
				"                   }\n" + 
				"           }\n" + 
				"       }";
		String result = Util.post(url, data);
		System.out.println(result);
	}
	
	
}
