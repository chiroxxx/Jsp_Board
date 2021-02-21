package edu.bit.ex.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context; // naming���� import!!!!!
import javax.naming.InitialContext;
import javax.sql.DataSource; // �׻� import�Ҷ� ����!! sql��

import edu.bit.ex.dto.BDto;

public class BDao {
	private DataSource dataSource; //Connection Pool , Ŀ�׼� Ǯ�� DataSource��
	
	public BDao() {
		
		try {
			Context context = new InitialContext(); // Context�� �ش� ���� �ȿ� �ִ� context.xml�� �о���̴� ��ü
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/oracle"); //lookup=ã�´�, jdbc/oracle�� context.xml�� name�� �ݵ�� ���������
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<BDto> list() { // DB�� �ִ°��� ��üȭ���Ѽ� ������ ��(Data Transfer Object), ���ڵ带 �ϳ��� ����Ʈ�� �����Ѵ�
	    
	    ArrayList<BDto> dtos = new ArrayList<BDto>(); //BDto�� ��Ű���޶� ����Ʈ����ߵ�
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    
	    try {
	       connection = dataSource.getConnection();//DriverManager�� �ƴ� dataSource - Ŀ�ؼ�Ǯ���� ��ü�� �����ðű� ������
	       
	       String query = "select bId, bName, bTitle, bContent, bDate, bHit, bGroup, bStep, bIndent from mvc_board order by bGroup desc, bStep asc";
	       // order by bGroup desc, bStep asc �����ָ� ����� ������ �ȵ�
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
	          //DB���� ������ ���ڵ���� BDto�� ���� �����ϱ� ���� ��ü�����ϰ� ���� �����Ѵ�
	          dtos.add(dto);
	          // ������ AraayList�� �ϳ��� ���ؼ� dto ����ϸ� ���̺� ȭ�� �����̵ȴ�
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
	       
	       String query = "select * from mvc_board where bId = ?"; // ?�� PreparedStatement�� ����ó���� ���� ���� (PreparedStatement�� �ڹٹ���)
	       
	       preparedStatement = connection.prepareStatement(query);
	       preparedStatement.setInt(1, Integer.parseInt(bId)); // 1��° ����ǥ���� bId ������ �� �־�� 
	       
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
	      
	       int rn = preparedStatement.executeUpdate(); // ������Ʈ�ϴ°� �����ϸ� ó���� ���ڵ� ���� ��ȯ
	       System.out.println("insert ���: " + rn); // ������(������Ʈ �ߵƴ��� Ȯ��)
	       
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
	       // ���� String���� �޾Ƶ��� ��, ���⼭ Integer�� �ٲ������
	       
	       int rn = preparedStatement.executeUpdate();
	       System.out.println("delete ����! " + rn);
	       
	      
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
		// write�� �Ȱ�����! ������ �ϴ°ű⶧����
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
	//reply�� insert�� ó���� �� ������ �ʿ� �����ϱ� insert�� ó�����ָ��!	
	    replyShape(bGroup, bStep); // �ٸ� ����� �ֽ����� �޸��� �޷��ִ� ��� �о�� �Լ�
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
           preparedStatement.setInt(5, Integer.parseInt(bStep) + 1); // �о �� ���� ��ȣ �ű�°�
           preparedStatement.setInt(6, Integer.parseInt(bIndent) + 1);
           
           int rn = preparedStatement.executeUpdate();
           /* execute : �����Ѵ��� ���̴ϱ� �̰��ؾ� DB�� ����Ǵ°�!!!
           int rn���� ���� �����Ѱ� ������ rn ���� ���� �ߵ������� Ȯ���ϴ� �� */
           System.out.println(rn);
	       
	
	    } catch (Exception e) {
	       e.printStackTrace();
	    } 
	}
	
	private void replyShape(String strGroup, String strStep) {
		// �亯 ������ ���� �Լ� 
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		      
		try {
			connection = dataSource.getConnection();
			String query = "update mvc_board set bStep = bStep + 1 where bGroup = ? and bStep > ?";
			/* �������� 1�� ���¿��� step���� �� 1�̸� ������ �ȵǱ� ������(:�ð������� �����������)
			+1�� �������ν� ������ ��Ų�� (������ �� 1�� ����) ���� step�� ���η� ��ܼ������ �������شٶ�� �����ص� ��*/
			
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