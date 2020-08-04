package service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

import entity.AccessToken;
import entity.Article;
import entity.BaseMessage;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.TextMessage;
import entity.VideoMessage;
import entity.VoiceMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.font.StrikeCache;
import util.Util;



public class WxService {
	
	
	private static final String TOKEN ="zwxbaba";
	//机器人key
	private static final String APPKEY="1fec136dbd19f44743803f89bd55ca62";
	private static final String GET_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//微信公众号
	private static final String APPID="wx8a84b060941a42b0";
	private static final String APPSECRET="8afd23dd46f86f20f1b7033e907b62d2";
//	private static final String APPID="wx67d857bc3508d133";
//	private static final String APPSECRET="ace5fa4e0fed1b18ab5124e80fe0ee33";
	
	//百度AI
	public static final String APP_ID = "11519092";
	public static final String API_KEY = "q3TlGWWqEBG9uGvlFIBtpvY5";
	public static final String SECRET_KEY = "A14W5VRNG8my1GXYYAyNND0RjzBwxI8A";

	//用于存储token
	private static AccessToken at;

	
	/**
	 * 获取token
	 * 
	 */
	private static void getToken() {
		String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		String tokenStr = Util.get(url);
		JSONObject jsonObject = JSONObject.fromObject(tokenStr);
		String token = jsonObject.getString("access_token");
		String expireIn = jsonObject.getString("expires_in");
		//创建token对象,并存起来。
		at = new AccessToken(token, expireIn);
	}
	
	/**
	 * 向处暴露的获取token的方法
	 * @return
	 * 
	 */
	public static String getAccessToken() {
		if(at==null||at.isExpired()) {
			getToken();
		}
		return at.getAccessToken();
	}
	
	public static boolean check(String signature,String timestamp,String nonce) {
		//1.将signature,timestamp,nonce三个参数进行排序
		String[] strs=new String[] {TOKEN,timestamp,nonce};
		Arrays.sort(strs);
		//2.三个参数字符串拼接成一个字符串进行sha1加密
		String str = strs[0]+strs[1]+strs[2];
		String mySign =sha1(str);
		System.out.println(mySign);
		System.out.println(signature);
		return mySign.equalsIgnoreCase(signature);
		
	}
	
	
	/**
	 * sha1加密
	 * @param str
	 * @return
	 */
	private static String sha1(String str) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("sha1");
			byte[] digest = md.digest(str.getBytes());
			char[] chars= {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			StringBuilder sb =new StringBuilder();
			//处理加密结果
			for (byte b : digest) {
				sb.append(chars[(b>>4)&15]);
				sb.append(chars[b&15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		

		
		
		return null;
	}


	public static Map<String, String> parseRequst(InputStream is) {
		HashMap<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			//读取输入流，获取xml文档对象
			Document document = reader.read(is);
			//根据文档对象获取根节点
			Element root = document.getRootElement();
			//获取根节点的所有子节点
			List<Element> elements = root.elements();
			for (Element e : elements) {
				map.put(e.getName(), e.getStringValue());
			}
		} catch (DocumentException e) {			
			e.printStackTrace();
		}
		
		return map;
	}

	/**
	 * 用于处理所有的事件和消息的回复
	 * @param requestMap
	 * @return
	 */
	public static String getRespose(Map<String, String> requestMap) {
		BaseMessage msg =null;
		String msgType = requestMap.get("MsgType");
		switch (msgType) {
		//处理文本消息
		case "text":
			msg=dealTextMessage(requestMap);
			break;
		case "image":
			//图片文字翻译
			//msg=dealImage(requestMap);
			msg=dealImage_pic(requestMap);
			break;
		case "voice":
	
			break;
		case "video":
	
			break;
		case "shortvideo":
			
			break;
		case "location":
			
			break;
		case "link":
			
			break;
		case "event":
			msg = dealEvent(requestMap);
			break;
		default:
			break;
		}
		//把消息对象处理为xml数据包
		if(msg!=null) {
			return beanToXml(msg);	
		}
		return null;
	}

	private static BaseMessage dealImage(Map<String, String> requestMap) {
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 调用接口
		String path = requestMap.get("PicUrl");
		//本地图片
	//	org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
		//微信上传的图片url
		org.json.JSONObject res = client.generalUrl(path, new HashMap<String, String>());
		String json = res.toString();
		JSONObject jsonObject = JSONObject.fromObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("words_result");
		Iterator<JSONObject> it = jsonArray.iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			JSONObject next = it.next();
			sb.append(next.get("words"));
			
		}
		return new TextMessage(requestMap, sb.toString());
	}
	
	/**
	 * 上传图片
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealImage_pic(Map<String, String> requestMap) {
		String path = requestMap.get("PicUrl");
		String filename = requestMap.get("MediaId");

		try {
			String rootUrl = httpImgUrlSave(path, filename);			
			String result = upload_img("D:\\wenxinPic\\"+rootUrl);
			JSONObject jsonObject = JSONObject.fromObject(result);			
			requestMap.put("url",jsonObject.getString("url"));
			System.out.println("url*****"+jsonObject.getString("url"));
			String  url= "http://pic1.free.idcfengye.com/pic/addPic";
		//	String sendGet = Util.sendGet("http://pic1.free.idcfengye.com/addPic", "openId="+openId);
		//	String sendGet = Util.get("http://pic1.free.idcfengye.com/pic/addPic?openId="+openId+"&picUrl="+jsonObject.get("url").toString());
			String data = JSON.toJSONString(requestMap);
			System.out.println("data****"+data);
			String post = Util.post(url, data);
			System.out.println("post****"+post);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return new TextMessage(requestMap,"已保存到自己的相册" );
	}

	/**
	 * 处理事件推送
	 * @param requestMap
	 * @return
	 * 
	 */
	private static BaseMessage dealEvent(Map<String, String> requestMap) {
		String event = requestMap.get("Event");
		switch (event) {
			case "CLICK":
				return dealClick(requestMap);
			case "VIEW":
				return dealView(requestMap);
			default:
				break;
		}
		return null;
	}
	
	
	/**
	 * 处理view类型的按钮的菜单
	 * @param requestMap
	 * @return
	 * 
	 */
	private static BaseMessage dealView(Map<String, String> requestMap) {
		
		return null;
	}

	/**
	 * 处理click菜单
	 * @param requestMap
	 * @return
	 * 
	 */
	private static BaseMessage dealClick(Map<String, String> requestMap) {
		String key = requestMap.get("EventKey");
		switch (key) {
			//点击一菜单点
			case "1":
				//处理点击了第一个一级菜单
				return new TextMessage(requestMap, "你点了一点第一个一级菜单");
			case "32":
				//处理点击了第三个一级菜单的第二个子菜单
				break;
			default:
				break;
		}
		return null;
	}

	/**
	 * 把消息对象处理为xml数据包
 	 * @param msg
 	 * @return
 	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream =new XStream();
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(MusicMessage.class);
		String xml = stream.toXML(msg);
		return xml;
	}


	private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
		//用户发来的内容
		String msg = requestMap.get("Content");
		String openId = requestMap.get("FromUserName");
		if(msg.equals("女孩")) {
			List<Article> articles = new ArrayList<>();
			articles.add(new Article("这是图文消息的标题", "这是图文消息的详细介绍", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1594280124929&di=ed1d0f27a822e3c8866847b98cb6dea7&imgtype=0&src=http%3A%2F%2Fpic24.nipic.com%2F20120923%2F6782367_002314154142_2.jpg", "http://pic1.free.idcfengye.com/pic/picHtml?openId="+openId));
			NewsMessage nm = new NewsMessage(requestMap,"1", articles);
			return nm;
		}
		
		if(msg.equals("登录")) {
			String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx67d857bc3508d133&redirect_uri=http://zsy.free.idcfengye.com/wenxin/GetUserInfo&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect"; 
		//	String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx67d857bc3508d133&redirect_uri=http://zsy.free.idcfengye.com/wenxin/GetUserInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
			TextMessage tm = new TextMessage(requestMap, "点击<a href=\""+url+"\">这里</a>登录");
			return tm;
		}
		//调用方法返回聊天的内容（图灵机器人聊天）
		String resp = chat(msg);
		TextMessage tm= new TextMessage(requestMap,resp);
		return tm;
	}

	/**
	 * 调用图灵机器人聊天
	 * @param msg
	 * @return
	 */
	private static String chat(String msg) {
        String result =null;
        String url ="http://op.juhe.cn/robot/index";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请到的本接口专用的APPKEY
        params.put("info",msg);//要发送给机器人的内容，不要超过30个字符
        params.put("dtype","");//返回的数据的格式，json或xml，默认为json
        params.put("loc","");//地点，如北京中关村
        params.put("lon","");//经度，东经116.234632（小数点后保留6位），需要写为116234632
        params.put("lat","");//纬度，北纬40.234632（小数点后保留6位），需要写为40234632
        params.put("userid","");//1~32位，此userid针对您自己的每一个用户，用于上下文的关联
        try {
            result =Util.net(url, params, "GET");
            System.out.println(result);
            //解析json
            JSONObject jsonObject = JSONObject.fromObject(result);
            //取出error_code
            int code = jsonObject.getInt("error_code");
            if(code!=0) {
            		return null;
            }
            //取出返回的消息的内容
            String resp = jsonObject.getJSONObject("result").getString("text");
            
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * 上传临时素材
	 * @param path	上传的文件的路径
	 * @param type	上传的文件类型
	 * @return
	 */
	public static String upload(String path,String type) {
		File file = new File(path);
		//地址
		String url="https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
		try {
			URL urlObj = new URL(url);
			//强转为案例连接
			HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
			//设置连接的信息
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//设置请求头信息
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "utf8");
			//数据的边界
			String boundary = "-----"+System.currentTimeMillis();
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			//获取输出流
			OutputStream out = conn.getOutputStream();
			//创建文件的输入流
			InputStream is = new FileInputStream(file);
			//第一部分：头部信息
			//准备头部信息
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition:form-data;name=\"media\";filename=\""+file.getName()+"\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			out.write(sb.toString().getBytes());
			System.out.println(sb.toString());
			//第二部分：文件内容
			byte[] b = new byte[1024];
			int len;
			while((len=is.read(b))!=-1) {
				out.write(b, 0, len);
			}
			is.close();
			//第三部分：尾部信息
			String foot = "\r\n--"+boundary+"--\r\n";
			out.write(foot.getBytes());
			out.flush();
			out.close();
			//读取数据
			InputStream is2 = conn.getInputStream();
			StringBuilder resp = new StringBuilder();
			while((len=is2.read(b))!=-1) {
				resp.append(new String(b,0,len));
			}
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 上传永久图片素材
	 * @param path
	 * @return
	 */
	public static String upload_img(String path) {
		File file = new File(path);
		//地址
		String url="https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
		url = url.replace("ACCESS_TOKEN", getAccessToken());
		try {
			URL urlObj = new URL(url);
			//强转为案例连接
			HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
			//设置连接的信息
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			//设置请求头信息
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "utf8");
			//数据的边界
			String boundary = "-----"+System.currentTimeMillis();
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			//获取输出流
			OutputStream out = conn.getOutputStream();
			//创建文件的输入流
			InputStream is = new FileInputStream(file);
			//第一部分：头部信息
			//准备头部信息
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition:form-data;name=\"media\";filename=\""+file.getName()+"\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			out.write(sb.toString().getBytes());
			System.out.println(sb.toString());
			//第二部分：文件内容
			byte[] b = new byte[1024];
			int len;
			while((len=is.read(b))!=-1) {
				out.write(b, 0, len);
			}
			is.close();
			//第三部分：尾部信息
			String foot = "\r\n--"+boundary+"--\r\n";
			out.write(foot.getBytes());
			out.flush();
			out.close();
			//读取数据
			InputStream is2 = conn.getInputStream();
			StringBuilder resp = new StringBuilder();
			while((len=is2.read(b))!=-1) {
				resp.append(new String(b,0,len));
			}
			return resp.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取带参数二维码的ticket
	 * @return
	 */
	public static String getQrCodeTicket() {
		String at = getAccessToken();
		String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+at;
		//生成临时字符二维码
		String data="{\"expire_seconds\": 6000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"llzs\"}}}";
		String result = Util.post(url, data);
		String ticket = JSONObject.fromObject(result).getString("ticket");
		return ticket;
	}

	/**
	 * 获取用户的基本信息
	 * @return
	 */
	public static String getUserInfo(String openid) {
		String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		url = url.replace("ACCESS_TOKEN", getAccessToken()).replace("OPENID", openid);
		String result = Util.get(url);
		return result;
	}
	
	
	/**
	 * http网络上的图片保存至本地
	 * @return 
	 * @throws Exception 
	 */
	public static String httpImgUrlSave(String urlPath,String filename) throws Exception {
		//new一个URL对象  
        URL url = new URL(urlPath);  
        //打开链接  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        //设置请求方式为"GET"  
        conn.setRequestMethod("GET");  
        //超时响应时间为5秒  
        conn.setConnectTimeout(5 * 1000);  
        //通过输入流获取图片数据  
        InputStream inStream = conn.getInputStream();  
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        byte[] data = readInputStream(inStream);  
        //new一个文件对象用来保存图片，默认保存当前工程根目录  
        File imageFile = new File("D:\\wenxinPic\\"+filename+".jpg");  
        //创建输出流  
        FileOutputStream outStream = new FileOutputStream(imageFile);  
        //写入数据  
        outStream.write(data);  
        //关闭输出流  
        outStream.close();  
		
		return filename+".jpg";
		
	}
	
	public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }
	
}
