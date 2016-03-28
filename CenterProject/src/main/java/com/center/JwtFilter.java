package com.center;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import com.center.utils.CommonUtil;

public class JwtFilter extends GenericFilterBean {

	@Override
	public void doFilter(final ServletRequest req,
            final ServletResponse res,
            final FilterChain chain) throws IOException, ServletException {
		
		//System.out.println("doFilter");
		
		final HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse)res;

        final String token = request.getHeader("X-Auth");
        if (token == null || token.isEmpty()) {
            //throw new ServletException("Missing or invalid Authorization header.");
        	System.out.println("token null");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        	return;
        }

        //final String token = authHeader.substring(7); // The part after "Bearer "

        String new_token = null;
        try {
        	new_token = CommonUtil.parseJWT(token);
        }
        catch (SignatureException e) {
            //throw new ServletException("Invalid token.");
        	System.out.println("parse error");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        	return;
        } catch (IOException e) {
            //throw new ServletException("Invalid token.");
        	System.out.println("IOException error");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        	return;
        } catch (Exception e) {
            //throw new ServletException("Invalid token.");
        	System.out.println("expire error");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        	return;
        }

        response.addHeader("X-Auth", new_token);
        chain.doFilter(req, res);
	}

}
