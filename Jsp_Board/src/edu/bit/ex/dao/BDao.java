package edu.bit.ex.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context; // naming으로 import!!!!!
import javax.naming.InitialContext;
import javax.sql.DataSource; // 항상 import할때 조심!! sql임

import edu.bit.ex.dto.BDto;

public class BDao {
	private DataSource dataSource; //Connection Pool , 커네션 풀은 DataSource다
	
	public BDao() {
		
		try {
			Context context = new InitialContext(); // Context는 해당 서버 안에 있는 context.xml을 읽어들이는 객체
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/oracle"); //lookup=찾는다, jdbc/oracle은 context.xml에 name과 반드시 맞춰줘야함
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<BDto> list() { // DB에 있는것을 객체화시켜서 가져온 것(Data Transfer Object), 레코드를 하나씩 리스트로 저장한다
	    
	    ArrayList<BDto> dtos = new ArrayList<BDto>(); //BDto도 패키지달라서 임포트해줘야됨
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	       connection = dataSource.getConnection();//DriverManager가 아닌 dataSource - 커넥션풀에서 객체를 가져올거기 때문에
	       
	       String query = "select bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent from mvc_board order by bGroup desc, bStep asc";
	       // order by bGroup desc, bStep asc 안해주면 댓글이 정렬이 안됨
	       preparedStatement = connection.prepareStatement(query);
	       resultSet = preparedStatement.executeQuery();
	       
	       while (resultSet.next()) {
	          int bId = resultSet.getInt("bId");
	          String bName = resultSet.getString("bName");
	          String bTitle = resultSet.getString("bTitle");
	          String bContent = resultSet.getString("bContent");
	          Timestamp bDate = resultSet.getTimestamp("bDate");
	          int bHit = resultSet.getInt("bHit");
	          int bGroup = resultSet.getInt("bGroup");
	          int bStep = resultSet.getInt("bStep");
	          int bIndent = resultSet.getInt("bIndent");
	          
	          BDto dto = new BDto(bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
	          //DB에서 가져온 레코드들을 BDto에 값을 저장하기 위해 객체선언하고 값을 저장한다
	          dtos.add(dto);
	          // 값들을 AraayList에 하나씩 더해서 dto 출력하면 테이블 화면 구현이된다
	       }
	       
	    } catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	          if(resultSet != null) resultSet.close();
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
	    return dtos;

	 }

	public BDto contentView(String bId) {
		upHit(bId);
		
		BDto dtos = null;
		
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "select * from mvc_board where bId = ?"; // ?는 PreparedStatement의 변수처리를 위한 문법 (PreparedStatement는 자바문법)
	       
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setInt(1, Integer.parseInt(bId)); // 1번째 물음표에는 bId 들어오는 값 넣어라 
	       
	       resultSet = preparedStatement.executeQuery();
	       
	       while (resultSet.next()) {
	          int id = resultSet.getInt("bId");
	          String bName = resultSet.getString("bName");
	          String bTitle = resultSet.getString("bTitle");
	          String bContent = resultSet.getString("bContent");
	          Timestamp bDate = resultSet.getTimestamp("bDate");
	          int bHit = resultSet.getInt("bHit");
	          int bGroup = resultSet.getInt("bGroup");
	          int bStep = resultSet.getInt("bStep");
	          int bIndent = resultSet.getInt("bIndent");
	          
	          dtos = new BDto(id, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
	          
	       }
	       
	    } catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	          if(resultSet != null) resultSet.close();
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
	    return dtos;
	
	}

	public void write(String bName, String bTitle, String bContent) {
		
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	  
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "insert into mvc_board(bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent) values (mvc_board_seq.nextval, ?, ?, ?, sysdate, 0, mvc_board_seq.currval, 0, 0 )"; 
	       
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setString(1,bName);
	       preparedStatement.setString(2,bTitle);
	       preparedStatement.setString(3,bContent);
	      
	       int rn = preparedStatement.executeUpdate(); // 업데이트하는게 성공하면 처리된 레코드 수를 반환
	       System.out.println("insert 결과: " + rn); // 디버깅용(업데이트 잘됐는지 확인)
	       
	    } catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }

	}

	public void delete(int bId) {

	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    
	  
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "delete from mvc_board where bId=?"; 
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setInt(1, bId);
	       // 위에 String으로 받아도됨 단, 여기서 Integer로 바꿔줘야함
	       
	       int rn = preparedStatement.executeUpdate();
	       System.out.println("delete 성공! " + rn);
	       
	      
	    } catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	     
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
		
	}

	public void modify(String bId, String bName, String bTitle, String bContent) {
		// write랑 똑같은것! 수정만 하는거기때문에
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "update mvc_board set bName=?, bTitle=?, bContent=? where bId =?"; 
	       
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setString(1, bName);
	       preparedStatement.setString(2, bTitle);
	       preparedStatement.setString(3, bContent);
	       preparedStatement.setString(4, bId);
	       
	       int rn = preparedStatement.executeUpdate();
	       
	    }catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
	}

	public BDto reply_view(String bId) {
		
		BDto dtos = null;
		
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "select * from mvc_board where bId = ?"; 
	       
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setInt(1, Integer.parseInt(bId)); 
	       
	       resultSet = preparedStatement.executeQuery();
	       
	       while (resultSet.next()) {
	          int id = resultSet.getInt("bId");
	          String bName = resultSet.getString("bName");
	          String bTitle = resultSet.getString("bTitle");
	          String bContent = resultSet.getString("bContent");
	          Timestamp bDate = resultSet.getTimestamp("bDate");
	          int bHit = resultSet.getInt("bHit");
	          int bGroup = resultSet.getInt("bGroup");
	          int bStep = resultSet.getInt("bStep");
	          int bIndent = resultSet.getInt("bIndent");
	          
	          dtos = new BDto(id, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent);
	          
	       }
	       
	    } catch (Exception e) {
	       e.printStackTrace();
	    } finally {
	       try {
	          if(resultSet != null) resultSet.close();
	          if(preparedStatement != null) preparedStatement.close();
	          if(connection != null) connection.close();
	       } catch (Exception e2) {
	          e2.printStackTrace();
	       }
	    }
	    return dtos;
	}

	public void reply(String bId, String bName, String bTitle, String bContent, String bGroup, String bStep,
			String bIndent) {
	//reply는 insert로 처리한 후 보여줄 필요 없으니까 insert만 처리해주면됨!	
	    replyShape(bGroup, bStep); // 다른 댓글이 최신으로 달리면 달려있던 댓글 밀어내는 함수
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    
	    try {
	       connection = dataSource.getConnection();
	       
	       String query = "insert into mvc_board (bId, bName, bTitle, bContent, bGroup, bStep, bIndent) values (mvc_board_seq.nextval, ?, ?, ?, ?, ?, ?)";
	       preparedStatement = connection.prepareStatement(query);
	       
           preparedStatement.setString(1, bName);
           preparedStatement.setString(2, bTitle);
           preparedStatement.setString(3, bContent);
           preparedStatement.setInt(4, Integer.parseInt(bGroup));
           preparedStatement.setInt(5, Integer.parseInt(bStep) + 1); // 밀어낸 후 본인 번호 매기는것
           preparedStatement.setInt(6, Integer.parseInt(bIndent) + 1);
           
           int rn = preparedStatement.executeUpdate();
           /* execute : 실행한다의 뜻이니까 이걸해야 DB가 실행되는것!!!
           int rn으로 변수 선언한게 디버깅용 rn 값에 따라서 잘들어갔는지를 확인하는 용 */
           System.out.println(rn);
	       
	
	    } catch (Exception e) {
	       e.printStackTrace();
	    } 
	}
	
	private void replyShape(String strGroup, String strStep) {
		// 답변 정렬을 위한 함수 
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		      
		try {
			connection = dataSource.getConnection();
			String query = "update mvc_board set bStep = bStep + 1 where bGroup = ? and bStep > ?";
			/* 원본글이 1인 상태에서 step들이 다 1이면 정렬이 안되기 때문에(:시간순으로 정렬해줘야함)
			+1을 해줌으로써 정렬을 시킨다 (개념은 다 1이 맞음) 따라서 step은 세로로 계단순서대로 정렬해준다라고 생각해도 됨*/
			
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, Integer.parseInt(strGroup));
			preparedStatement.setInt(2, Integer.parseInt(strStep));
		         
			int rn = preparedStatement.executeUpdate();
		} catch (Exception e) {
		         e.printStackTrace();
		} finally {
			try {
				if(preparedStatement != null) preparedStatement.close();
				if(connection != null) connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void upHit(String bId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = dataSource.getConnection();
			String query = "update mvc_board set bHit = bHit+1 where bId=?";
			
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, bId);
			
			int rs = preparedStatement.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(connection != null) {
					connection.close();
				}if(preparedStatement != null) {
					preparedStatement.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}