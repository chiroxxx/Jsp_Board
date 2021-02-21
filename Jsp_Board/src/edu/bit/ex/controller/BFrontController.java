package edu.bit.ex.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.bit.ex.command.BCommand;
import edu.bit.ex.command.BContentCommand;
import edu.bit.ex.command.BDeleteCommand;
import edu.bit.ex.command.BListCommand;
import edu.bit.ex.command.BModifyCommand;
import edu.bit.ex.command.BReplyCommand;
import edu.bit.ex.command.BReplyViewCommand;
import edu.bit.ex.command.BWriteCommand;

/**
 * Servlet implementation class FrontController
 */
@WebServlet("*.do") //앞에 뭘 붙이던 끝에 .do로 오는 모든걸 받겠다
public class BFrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BFrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet");
		actionDo(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
		actionDo(request, response);
	}
	
	private void actionDo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("actionDo");
		
		request.setCharacterEncoding("EUC-KR"); // 한글처리할때 에러 → throws ServletException, IOException로 처리
		
		String viewPage = null;
		BCommand command = null; 
		
		String uri = request.getRequestURI();
	    	String conPath = request.getContextPath();
	    	String com = uri.substring(conPath.length()); // subString은 길이를 지정해준 지점부터 문자를 읽어들여서 출력 
	      
	    	System.out.println(uri); // /jsp_mvcBoard/list.do
	    	System.out.println(conPath);// /jsp_mvcBoard
	    	System.out.println(com);// /list.do
	    	// System.out.println()은 콘솔 출력
	    
	    	if(com.equals("/list.do")){
	    		command = new BListCommand(); // 다형성적용, 부모는 자식(BCommand인터페이스로 부모, BListCommand 자식-구현)
			command.execute(request, response); //BListCommand에 있는 함수 호출(구현한부분)
	        	viewPage = "list.jsp"; // list.do가 실행되면 보여줄 viewPage
	   	}else if(com.equals("/content_view.do")){ // 걸려있는 링크 처리
	    		command = new BContentCommand(); 
	      	  	command.execute(request, response); 
	        	viewPage = "content_view.jsp"; 
	    	}else if(com.equals("/write_view.do")){ 
	       	 	viewPage = "write_view.jsp"; 
	    	}else if(com.equals("/write.do")){ 
	    		command = new BWriteCommand(); 
	       		 command.execute(request, response); 
	       		 viewPage = "list.do"; 
	    	}else if(com.equals("/delete.do")){ 
	    		command = new BDeleteCommand(); 
	      		command.execute(request, response); 
	       		viewPage = "list.do"; 
	    	}else if(com.equals("/modify.do")){ 
	    		command = new BModifyCommand(); 
	        	command.execute(request, response); 
	        	viewPage = "list.do"; 
	    	}else if(com.equals("/reply_view.do")){ 
	    		command = new BReplyViewCommand(); 
	        	command.execute(request, response); 
	        	viewPage = "reply_view.jsp"; 
	    	}else if(com.equals("/reply.do")){ 
	    	// 댓글작성하는건 write와 똑같은데 답변에는 group, step, indent가 있으니 그걸 계산해서 하는게 차이(계산하는 sql문 암기)
	    		command = new BReplyCommand(); 
	        	command.execute(request, response); 
	        	viewPage = "list.do"; 
	    	}
	    
	    	RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
	   	dispatcher.forward(request, response);
	   	/* 위 두문장이 servlet에서 forword해주는 문장! jsp에서는 액션태그를 이용해서 forward
	    	 왜 forward를 하는가? request를 살려서 view단으로 넘겨서 보여줘야 하기 때문임 (따라서 문장은 그대로 암기!!)*/
	}
	
}
