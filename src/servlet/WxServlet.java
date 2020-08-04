package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.DuplicateRemovalMessage;
import service.WxService;

/**
 * Servlet implementation class WxServlet
 */
@WebServlet("/wx")
public class WxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WxServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		//校验证请求
		if(WxService.check(signature,timestamp,nonce)) {
			System.out.println("接入成功");
			PrintWriter out = response.getWriter();
			//原样返回echostr
			out.print(echostr);
			out.flush();
			out.close();
		}else {
			System.out.println("接入失败");
		}
		
	}

	/**
	 * 接收消息和事件推送
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		Map<String,String> requestMap= WxService.parseRequst(request.getInputStream());
		
		//准备回复的数据包
		String respxml =WxService.getRespose(requestMap);
		System.out.println("准备回复的数据包respxml："+respxml);
		PrintWriter out = response.getWriter();
		out.print(respxml);
		out.flush();
		out.close();
		
	}
	

	
	
}
