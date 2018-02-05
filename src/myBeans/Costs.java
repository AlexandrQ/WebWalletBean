package myBeans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.RowEditEvent;

import DBConn.SingletonDBConnection;

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
	private String costsid;
	
	public Costs() {
		
	}
	
	public Costs(String Type, String Sum, String Date, String Category, String Description, String Costsid) {
		this.type = Type;
		this.sum = Sum;
		this.date = Date;
		this.category = Category;
		this.description = Description;
		this.costsid = Costsid;
	}
	
	public String getCostsid() {
		return this.costsid;
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
	
	public void setCostsid(String Costsid) {
		this.costsid = Costsid;
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
	
	public void onRowEdit(RowEditEvent event) {
		//Costs editCost = (Costs)event.getObject();
		Costs editCost = (Costs)event.getSource();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! +++ " + editCost.getDescription()); 
		FacesMessage msg;
		if(EditRowInDB(editCost.getType(), editCost.getSum(), editCost.getDate(), editCost.getCategory(), editCost.getDescription(), editCost.getCostsid())) {
			msg = new FacesMessage("Costs edited", "CostsID : " + editCost.getCostsid() + "\n Description : " +  editCost.getDescription());
		}
		else {
			msg = new FacesMessage("Costs was not edited", "CostsID : " + editCost.getCostsid() );
		}
				
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Costs canceled", ((Costs)event.getObject()).getDescription());
		FacesContext.getCurrentInstance().addMessage(null, msg);
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
	
	
	private boolean EditRowInDB(String Type, String Sum, String Date, String Category, String Description, String Costid) {
		
	//	                    UPDATE public. "Costs " SET type='outcomes    ', sum='10000      ', date='2017-11-09  ', category='salary          ', description='testdescr2         ' WHERE costsid=61;
		String querryStr = "UPDATE public.\"Costs\" SET type='" + Type + "', sum='" + Sum + "', date='" + Date + "', category='" + Category + "', description='" + Description + "' WHERE costsid='" + Costid + "'";
		//String querryStr = "INSERT INTO public.\"Costs\"(type, sum, date, category, description, userid) VALUES ('" + Type + "','" + Sum + "','" + Date + "','" + Category + "','" + Description + "','" + Userid + "')";
		Connection dbConnection = null;
		Statement statement = null;
		    
		    try {
			    dbConnection = SingletonDBConnection.getInstance().getConnInst();
			    statement = dbConnection.createStatement();		 
			    
			    
			    
			    if (!((statement.executeUpdate(querryStr) == 1))) {
			    	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + statement.executeUpdate(querryStr)); 
			    	return false;	
			    }
			    
			    
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
	
	
	private boolean AddRowInDB(String Type, String Sum, String Date, String Category, String Description, String Userid ) {
		if (Userid.length()>0 && Type.length()>0 && Sum.length()>0 && Date.length()>0) {
			String querryStr = "INSERT INTO public.\"Costs\"(type, sum, date, category, description, userid) VALUES ('" + Type + "','" + Sum + "','" + Date + "','" + Category + "','" + Description + "','" + Userid + "')";
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
	
	
	private String getUseridFromDB(String username) {
		String querryStr = "SELECT userid FROM public.\"Users\" WHERE username = '" + username +"'";		
		Connection dbConnection = null;
	    Statement statement = null;
	    String Userid = "";
	    
	    
	    try {
		    dbConnection = SingletonDBConnection.getInstance().getConnInst();
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
	
}
