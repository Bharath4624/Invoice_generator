package com.bharath;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/customers")
public class CustomerDetails extends HttpServlet {
    public Gson gson = new Gson();
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers");
            JsonObject responseJson = getCustomerDetails(rs);
            PrintWriter out = res.getWriter();
            out.println(gson.toJson(responseJson));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
    }
    public JsonObject getCustomerDetails(ResultSet rs) throws SQLException {
        JsonArray customers = new JsonArray();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Id", rs.getInt("cus_id"));
            obj.addProperty("Name", rs.getString("name"));
            obj.addProperty("Address", rs.getString("address") + "-" + rs.getString("city") + "-" + rs.getString("zipcode") + "-" + rs.getString("country"));
            obj.addProperty("Mobile", rs.getString("mobile"));
            obj.addProperty("Email_Id", rs.getString("email"));
            customers.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Customer_details", customers);
        return response;
    }
}