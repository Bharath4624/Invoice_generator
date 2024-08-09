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

@WebServlet("/orders")
public class OrderDetails extends HttpServlet {
    public Gson gson = new Gson();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM orders");
            JsonObject responseJson = getOrderDetails(rs, con);
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

    public JsonObject getOrderDetails(ResultSet rs, Connection con) throws SQLException {
        JsonArray orders = new JsonArray();
        PreparedStatement stmt = con.prepareStatement("SELECT date from customers where cus_id=?");
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            int cus_id = rs.getInt("cus_id");
            stmt.setInt(1, cus_id);
            ResultSet result = stmt.executeQuery();
            obj.addProperty("Customer_id", cus_id);
            obj.addProperty("Customer_name", rs.getString("cus_name"));
            obj.addProperty("Invoice_id", rs.getInt("inv_id"));
            if (result.next()) {
                obj.addProperty("Date", String.valueOf(result.getDate(1)));
            } else {
                obj.addProperty("Date", "Date not available");
            }
            obj.addProperty("Total_tax", rs.getDouble("totaltax"));
            obj.addProperty("Total_amount", rs.getDouble("totalamount"));
            orders.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Order_details", orders);
        return response;
    }
}
