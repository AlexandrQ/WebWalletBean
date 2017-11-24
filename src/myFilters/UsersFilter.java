package myFilters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myBeans.Bean;


@WebFilter("/UsersFilter")
public class UsersFilter implements Filter {

   
    public UsersFilter() {
       
    }

	
	public void destroy() {
		
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		Bean session = (Bean) req.getSession().getAttribute("bean1");
		String url = req.getRequestURI();
		
		if (session == null || !session.isLogged) {
			if(url.indexOf("/costs.xhtml") >= 0 || url.indexOf("/logout.xhtml") >= 0) {
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		} else {
			if (url.indexOf("/register.xhtml") >= 0 || url.indexOf("/login.xhtml") >= 0) {
				resp.sendRedirect(req.getServletContext().getContextPath() + "/costs.xhtml");
			} else if(url.indexOf("/logout.xhtml") >= 0) {
				req.getSession().removeAttribute("bean1");
				resp.sendRedirect(req.getServletContext().getContextPath() + "/login.xhtml");
			} else {
				chain.doFilter(request, response);
			}
		}
		
		
		
		
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
