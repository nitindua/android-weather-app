package com.nitin.csci571;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Servlet implementation class WeatherForecastServlet
 */

public class WeatherForecastServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public WeatherForecastServlet() {
        
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String location = request.getParameter("locationValue");
		String tempUnit = request.getParameter("tempUnits");
		String type = request.getParameter("locationType");
		PrintWriter out = response.getWriter();

		if(location != null && tempUnit != null && type != null)
		{
			//call php
			String urlString = "http://default-environment-5qzg4275mi.elasticbeanstalk.com/";
			String query = "locationValue=" + location + "&locationType=" + type + "&tempUnit=" + tempUnit;
			
			try{
			URL url = new URL(urlString + "?" + query);
			URLConnection conn = url.openConnection();
			conn.setAllowUserInteraction(false);
	        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
	        BufferedReader in = new BufferedReader(isr);
	        StringBuffer buffer = new StringBuffer();
	        int ch;
	        while ((ch = in.read()) != -1) {
	             buffer.append((char)(ch));
	        }
	        in.close();

	        String xml = buffer.toString();
			
			JSONObject json = new JSONObject();
			json = XML.toJSONObject(xml);
			response.setContentType("application/json");
			out.write(json.toString());
				
				
			}
			catch(MalformedURLException malExp)						//catch all separate exceptions as per spec			
			{
				//out.println(malExp.getMessage());
				String jsonErrorURLMSG = "{\"weather\":{\"error\":\"malformedURL\"} }";
				try {
					JSONObject jsonError = new JSONObject(jsonErrorURLMSG);
					response.setContentType("application/json");
					out.write(jsonError.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				
			}
			catch(IOException ioExp)
			{
				//out.println(ioExp.getMessage());
				String jsonErrorIOMSG = "\'{\"weather\":{\"error\":\"io\"} }";
				try {
					JSONObject jsonError = new JSONObject(jsonErrorIOMSG);
					response.setContentType("application/json");
					out.write(jsonError.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			catch(Exception e)
			{
				//out.println(e.getMessage());
				String jsonErrorMSG = "\'{\"weather\":{\"error\":\"generalerror\"} }";
				try {
					JSONObject jsonError = new JSONObject(jsonErrorMSG);
					response.setContentType("application/json");
					out.write(jsonError.toString());
				} catch (JSONException je) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
			
		
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	

}
