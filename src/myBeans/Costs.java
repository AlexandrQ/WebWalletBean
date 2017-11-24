package myBeans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "cost1")
@SessionScoped

public class Costs implements Serializable{	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String sum;
	private String date;
	private String category;
	private String description;
	private String userid;
	
	public Costs() {
		
	}
	
	public Costs(String Type, String Sum, String Date, String Category, String Description) {
		this.type = Type;
		this.sum = Sum;
		this.date = Date;
		this.category = Category;
		this.description = Description;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getSum() {
		return this.sum;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setType(String Type) {
		this.type = Type;
	}
	
	public void setSum(String Sum) {
		this.sum = Sum;
	}
	
	public void setDate(String Date) {
		this.date = Date;
	}
	
	public void setCategory(String Category) {
		this.category = Category;
	}
	
	public void setDescription(String Description) {
		this.description = Description;
	}
	
	
	public boolean addCost() {
					
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Bean bean = (Bean) req.getSession().getAttribute("bean1");
		
		this.userid = getUseridFromDB(bean.getName());
		
		if (this.userid != null && this.type != null && this.sum != null && this.date != null) {
			if (AddRowInDB(this.type, this.sum, this.date, this.category, this.description, this.userid)) {
				this.type = null;
				this.sum = null;
				this.date = null;
				this.category = null;
				this.description = null;
				return true;
			} else {
				return false;
			}
		}
		else return false;
	}
	
	private boolean AddRowInDB(String Type, String Sum, String Date, String Category, String Description, String Userid ) {
		if (Userid.length()>0 && Type.length()>0 && Sum.length()>0 && Date.length()>0) {
			String querryStr = "INSERT INTO public.\"Costs\"(type, sum, date, category, description, userid) VALUES ('" + Type + "','" + Sum + "','" + Date + "','" + Category + "','" + Description + "','" + Userid + "')";
			Connection dbConnection = null;
		    Statement statement = null;
		    
		    try {
			    dbConnection = getDBConnection();
			    statement = dbConnection.createStatement();		 
			    
			    statement.executeUpdate(querryStr);			    
			    
			    if (dbConnection != null) {
			    	dbConnection.close();	
			    }
		        		
			    return true;
			    
			} catch (SQLException e) {
			    System.out.println(e.getMessage());	
			    
			    if (dbConnection != null) {
		            try {
						dbConnection.close();
					} catch (SQLException ee) {				
						ee.printStackTrace();
					}		            
		        }	
			    
			    return false;
			} 
		    
		}
		else return false;
	}
	
	
	private String getUseridFromDB(String username) {
		String querryStr = "SELECT userid FROM public.\"Users\" WHERE username = '" + username +"'";		
		Connection dbConnection = null;
	    Statement statement = null;
	    String Userid = "";
	    
	    
	    try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();	 
		    
		    ResultSet rs = statement.executeQuery(querryStr);
		    
		    while (rs.next()) {	
		    	Userid = rs.getString("userid");		    	
		    }
		    
		    return Userid;		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return Userid;
		    
		} finally {
			if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException e) {				
					e.printStackTrace();
				}
	        }				
		}		    
	}
	
	
	private static Connection getDBConnection() {
	    Connection dbConnection = null;
	    try {
	        Class.forName("org.postgresql.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println(e.getMessage());
	        
	    }
	    try {
	        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/myTestPostgresDB","postgres", "admin");
	        return dbConnection;
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return dbConnection;
	}
	
}
