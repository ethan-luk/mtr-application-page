package com.mtr.servlet.apply;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/apply")
public class ApplicationServlet extends HttpServlet {
	
	//create query
	private static final String INSERT_QUERY = "INSERT INTO APPLICANTS(id, firstName, lastName, email) VALUES(?,?,?,?)";
			
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// get PrintWriter
		PrintWriter pw = res.getWriter();
		
		// set Content type
		res.setContentType("text/html");
		
		// read form values
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		String email = req.getParameter("email");
		
		System.out.println("Name: " + firstName + " " + lastName);
		System.out.println("email: " + email);
		
		// load jdbc driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// create connection to MySQL
		try(Connection con = DriverManager.getConnection("jdbc:mysql:///mtr", "root", "root"); // will need to change database connection to non-local database
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY);){
		    
			// get number of rows in the table 
		    Statement stmt = con.createStatement();
		    String query = "select count(*) from applicants";
		    ResultSet rs = stmt.executeQuery(query);
		    rs.next();
		    int rows = rs.getInt(1);
		    System.out.println("Num Rows: " + rows);
		     
			//set values
			ps.setInt(1, rows + 1); // primary key, write to database the number of current rows in the table + 1
			ps.setString(2, firstName);
			ps.setString(3, lastName);
			ps.setString(4, email);
			
			// write to database
			int count = ps.executeUpdate();
			if(count==0) {
				pw.println("Record not Stored into Database");
			}
			else {
				pw.println("Congratulations! Your application has been received!");
				pw.println("We look forward to reviewing your application and will get back to you as soon as we can!");
			}	
		} catch(SQLException se) {
			pw.println(se.getMessage());
			se.printStackTrace();
		} catch(Exception e) {
			pw.println(e.getMessage());
			e.printStackTrace();
		}
		
		// close stream
		pw.close();
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
}
