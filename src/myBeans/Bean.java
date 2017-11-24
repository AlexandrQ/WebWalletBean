package myBeans;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name = "bean1")
@SessionScoped

public class Bean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public boolean isLogged = false;
	private String name;
	private String pass;
	private ArrayList<Costs> myCosts = new ArrayList<Costs>();
	private Date fromDate;
	private Date toDate;


	public Bean() {
		
	}
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public ArrayList<Costs> getMyCosts() {
		return myCosts;
	}

	public void setMyCosts(ArrayList<Costs> myCosts) {
		this.myCosts = myCosts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String action() {		
		if(UserAutenticate(name, pass)) {
			isLogged = true;			
			this.myCosts = getAllCostsFromDB(name);
			return "costs.xhtml?faces-redirect=true";
			
		}
		else {
			isLogged = false;
			return "login.xhtml";
		}
		
	}
	
	
	public String filterByDate() {		
		if (this.fromDate != null && this.toDate != null) {
			this.myCosts = filterCostsByDate(this.name, this.fromDate.toString(), this.toDate.toString());	
			return "costs.xhtml?faces-redirect=true";
		}
		else return "costs.xhtml?faces-redirect=true";
		
	}
	
	
	private ArrayList<Costs> filterCostsByDate(String username, String fromDate, String toDate) {
		String querryStr = "SELECT type, sum, date, category, description FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND date >= '" + fromDate + "' AND date <= '" + toDate + "'";		
		Connection dbConnection = null;
	    Statement statement = null;
	    ArrayList<Costs> myCosts = new ArrayList<Costs>();
	    
	    try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();	 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    
		    while (rs.next()) {	
		    	Costs myCost = new Costs(rs.getString("type"), 
		    			rs.getString("sum").substring(0, rs.getString("sum").length()-1), 
		    			rs.getString("date"), 
		    			rs.getString("category"), 
		    			rs.getString("description") );	 
		    	myCosts.add(myCost);
		    }
		    
		    return myCosts;
		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return myCosts;
		    
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
	
	
	private ArrayList<Costs> getAllCostsFromDB(String username) {
		String querryStr = "SELECT type, sum, date, category, description FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid";		
		Connection dbConnection = null;
	    Statement statement = null;
	    ArrayList<Costs> myCosts = new ArrayList<Costs>();
	    
	    try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();	 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    while (rs.next()) {	
		    	Costs myCost = new Costs(rs.getString("type"), 
		    			rs.getString("sum").substring(0, rs.getString("sum").length()-1), 
		    			rs.getString("date"), 
		    			rs.getString("category"), 
		    			rs.getString("description") );	 
		    	myCosts.add(myCost);
		    }
		    
		    return myCosts;		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return myCosts;
		    
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
	
	
	private boolean UserAutenticate(String username, String password) {
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "SHA-256" );
		} catch (NoSuchAlgorithmException e1) {			
			e1.printStackTrace();
		}
	    md.update( password.getBytes( StandardCharsets.UTF_8 ) );
	    byte[] digest = md.digest();
	    String Password = String.format( "%064x", new BigInteger( 1, digest ) );	    
		
		String querryStr = "SELECT COUNT(username) AS Count FROM public.\"Users\" WHERE username = '" + username + "' AND password = '" + Password + "'";
		Connection dbConnection = null;
	    Statement statement = null;
		
		try {
		    dbConnection = getDBConnection();
		    statement = dbConnection.createStatement();		 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    if (rs.next()) {		    	
		    	if(rs.getString("Count").equals("1") ) {
			    	return true;
			    }
		    	else return false;   
		    }
		    else return false; 		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    return false;
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






