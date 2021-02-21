package edu.bit.ex.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.bit.ex.dao.BDao;

public class BReplyCommand implements BCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		String bId = request.getParameter("bId");
	      	String bName = request.getParameter("bName");
	     	String bTitle = request.getParameter("bTitle");
	      	String bContent = request.getParameter("bContent");
	      	// 위에는 원글에 대한 정보 (write할때는 저것만 있어도됨)
	     	String bGroup = request.getParameter("bGroup");
	      	String bStep = request.getParameter("bStep");
	     	String bIndent = request.getParameter("bIndent");
	      	//댓글에 필요한 요소(hidden으로 숨겨진 요소들을 받아야하기때문에 선언)
	      
	     	BDao dao = new BDao();
	      	dao.reply(bId, bName, bTitle, bContent, bGroup, bStep, bIndent);
	}
}