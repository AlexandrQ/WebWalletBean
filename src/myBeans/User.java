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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import DBConn.SingletonDBConnection;


@ManagedBean(name = "user1")
@SessionScoped

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String email;
	
	public User() {
		
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String register() {
		if (this.username != null && this.password != null && this.email != null) {
			if(CheckUserDuplicate(this.username)) {
				if(CreateUser(this.username, this.password, this.email)) {					
					return "login.xhtml?faces-redirect=true";
				} else {					
					this.username = null;
					this.email = null;
					return "register.xhtml?faces-redirect=true";
				}			
			} else {				
				this.username = null;			
				
			}
		}				
		
		FacesMessage message = null;
		message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Register Error", "Invalid data");
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "register.xhtml?faces-redirect=true";
		

	}
	
	
	private boolean CheckUserDuplicate(String Username) {
		String querryStr = "SELECT COUNT(username) AS Count FROM public.\"Users\" WHERE username = '" + Username + "'";
		Connection dbConnection = null;
	    Statement statement = null;
		
		try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
		    statement = dbConnection.createStatement();		 
		    
		    ResultSet rs = statement.executeQuery(querryStr);		    
		    
		    if (rs.next()) {		    	
		    	if(rs.getString("Count").equals("1") ) {
			    	return false;
			    }
		    	else return true;   
		    }
		    else return true; 		    
			
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
	
	
	private boolean CreateUser(String Username, String Password, String Email ) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "SHA-256" );
		} catch (NoSuchAlgorithmException e1) {			
			e1.printStackTrace();
		}
	    md.update( Password.getBytes( StandardCharsets.UTF_8 ) );
	    byte[] digest = md.digest();
	    String password = String.format( "%064x", new BigInteger( 1, digest ) );
	    
		if (Username.length()>0 && Password.length()>0 && Email.length()>0) {
			String querryStr = "INSERT INTO public.\"Users\"(username, password, email) VALUES ('" + Username + "','" + password + "','" + Email + "')";
			Connection dbConnection = null;
		    Statement statement = null;
		    
		    try {
			    dbConnection = SingletonDBConnection.getInstance().getConnInst();
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
}
