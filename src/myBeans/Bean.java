package myBeans;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import DBConn.SingletonDBConnection;


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
	private String outcomes = "0";
	private String incomes = "0";


	public Bean() {
		
	}
	
	
	
	public String getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(String outcomes) {
		this.outcomes = outcomes;
	}

	public String getIncomes() {
		return incomes;
	}

	public void setIncomes(String incomes) {
		this.incomes = incomes;
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
			 //RequestContext context = RequestContext.getCurrentInstance();
		     FacesMessage message = null;
		     message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
		     FacesContext.getCurrentInstance().addMessage(null, message);
		     			
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
		String querryStrOut = "SELECT SUM(sum) FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND date >= '" + fromDate + "' AND date <= '" + toDate + "' AND type = 'outcomes'";
		String querryStrIn = "SELECT SUM(sum) FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND date >= '" + fromDate + "' AND date <= '" + toDate + "' AND type = 'incomes'";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rs;
	    ArrayList<Costs> myCosts = new ArrayList<Costs>();
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rs = statement.executeQuery(querryStr);		    
		    
		    while (rs.next()) {	
		    	Costs myCost = new Costs(rs.getString("type"), 
		    			rs.getString("sum").substring(0, rs.getString("sum").length()-1), 
		    			rs.getString("date"), 
		    			rs.getString("category"), 
		    			rs.getString("description") );	 
		    	myCosts.add(myCost);
		    }
		    		    
		    rs = statement.executeQuery(querryStrOut);	   
		    		    
		    while (rs.next()) {		    	
		    	
		    	if(rs.getString(1) != null) {		    		
		    		this.outcomes = rs.getString(1).substring(0, rs.getString(1).length()-1);	
		    	} else this.outcomes = "0";	    	
		    }
		    
		    rs = statement.executeQuery(querryStrIn);
		    while (rs.next()) {			    	
		    	if(rs.getString(1) != null) {		    		
		    		this.incomes = rs.getString(1).substring(0, rs.getString(1).length()-1);
		    	} else this.incomes = "0";
		    }
		    
		    
		    //return myCosts;
		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());
		    //return myCosts;
		    
		} finally {
			if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException e) {				
					e.printStackTrace();
				}
	        }				
		}
	    
	    return myCosts;
	}
	
	
	private ArrayList<Costs> getAllCostsFromDB(String username) {
		String querryStr = "SELECT type, sum, date, category, description FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid";		
		String querryStrOut = "SELECT SUM(sum) FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND type = 'outcomes'";
		String querryStrIn = "SELECT SUM(sum) FROM public.\"Users\", public.\"Costs\" WHERE username = '" + username +"' AND public.\"Costs\".userid = public.\"Users\".userid AND type = 'incomes'";
		
		Connection dbConnection = null;
	    Statement statement = null;
	    ResultSet rs;
	    ArrayList<Costs> myCosts = new ArrayList<Costs>();
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();	 
		    
		    rs = statement.executeQuery(querryStr);		    
		    
		    while (rs.next()) {	
		    	Costs myCost = new Costs(rs.getString("type"), 
		    			rs.getString("sum").substring(0, rs.getString("sum").length()-1), 
		    			rs.getString("date"), 
		    			rs.getString("category"), 
		    			rs.getString("description") );	 
		    	myCosts.add(myCost);
		    }
		    
		    rs = statement.executeQuery(querryStrOut);
		    while (rs.next()) {	
		    	if(rs.getString(1) != null) {
		    		this.outcomes = rs.getString(1).substring(0, rs.getString(1).length()-1);	
		    	} else this.outcomes = "0";		    		    	
		    }
		    
		    rs = statement.executeQuery(querryStrIn);
		    while (rs.next()) {	
		    	if(rs.getString(1) != null) {
		    		this.incomes = rs.getString(1).substring(0, rs.getString(1).length()-1);	
		    	} else this.incomes = "0";
		    }		    		    		    
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());			    
		} finally {
			if (dbConnection != null) {
	            try {
					dbConnection.close();
				} catch (SQLException e) {				
					e.printStackTrace();
				}
	        }				
		}
	    return myCosts;
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
	    ResultSet rs;
		
		try {				
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();		    	
		    statement = dbConnection.createStatement();		 
		    
		    rs = statement.executeQuery(querryStr);		    
		    
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

}






