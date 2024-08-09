package com.bharath;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/customers")
public class CustomerDetails extends HttpServlet {
    public Gson gson = new Gson();
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuffer = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        JsonObject jsonObject = gson.fromJson(jsonBuffer.toString(), JsonObject.class);
        String option = jsonObject.get("option").getAsString();
        if (option == null || (!"customers".equalsIgnoreCase(option) && !"orders".equalsIgnoreCase(option))) {
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "Please select a valid option");
            try (PrintWriter out = res.getWriter()) {
                out.println(gson.toJson(errorResponse));
            }
            return;
        }
        try (Connection con = getConnection();
             PreparedStatement pstmt = createPreparedStatement(con, option);
             ResultSet rs = pstmt.executeQuery()) {
            JsonObject responseJson;
            if ("customers".equalsIgnoreCase(option)) {
                responseJson = getCustomerDetails(rs);
            } else {
                responseJson = getOrderDetails(rs);
            }
            try (PrintWriter out = res.getWriter()) {
                out.println(gson.toJson(responseJson));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "An error occurred");
            try (PrintWriter out = res.getWriter()) {
                out.println(gson.toJson(errorResponse));
            }
        }
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
    }

    public PreparedStatement createPreparedStatement(Connection con, String option) throws SQLException {
        String query;
        if ("customers".equalsIgnoreCase(option)) {
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
            obj.addProperty("Invoice_id", rs.getInt("inv_id"));
            orders.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Orders", orders);
        return response;
    }
}
