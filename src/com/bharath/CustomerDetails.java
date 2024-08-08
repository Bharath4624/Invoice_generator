package com.bharath;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/customers")
public class CustomerDetails extends HttpServlet {
    public Gson gson = new Gson();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        String option1 = req.getParameter("allcustomers");
        String option2 = req.getParameter("onlyorderdetails");
        if ((option1 == null && option2 == null) || (!(option1 == null) && !(option2 == null))) {
            out.println("Please select one option");
            return;
        }
        try (Connection con = getConnection();
             PreparedStatement pstmt = createPreparedStatement(con, option1, option2);
             ResultSet rs = pstmt.executeQuery()) {
            if ("allcustomers".equalsIgnoreCase(option1) || "allcustomerdetails".equalsIgnoreCase(option1)) {
                out.println(gson.toJson(getCustomerDetails(rs)));
            } else {
                out.println(gson.toJson(getOrderDetails(rs)));
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
    }
    public PreparedStatement createPreparedStatement(Connection con, String option1, String option2) throws SQLException {
        String query;
        if ("allcustomers".equalsIgnoreCase(option1) || "allcustomerdetails".equalsIgnoreCase(option1)) {
            query = "SELECT * FROM customers";
        } else {
            query = "SELECT * FROM orders";
        }
        return con.prepareStatement(query);
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
    public JsonObject getOrderDetails(ResultSet rs) throws SQLException {
        JsonArray orders = new JsonArray();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Customer_id", rs.getInt("cus_id"));
            obj.addProperty("Customer_name", rs.getString("cus_name"));
            obj.addProperty("invoice_id", rs.getInt("inv_id"));
            obj.addProperty("Total_tax", rs.getDouble("totaltax"));
            obj.addProperty("Total_amount", rs.getDouble("totalamount"));
            orders.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Order_details", orders);
        return response;
    }
}