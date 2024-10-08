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

@WebServlet("/purchase")
public class TopCustomer extends HttpServlet {
    public Gson gson = new Gson();

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(orders.totalamount),orders.cus_id,customers.name FROM orders INNER JOIN customers ON orders.cus_id=customers.cus_id GROUP BY orders.cus_id ORDER BY SUM(orders.totalamount) DESC");
            JsonObject response = getTopCustomer(rs);
            PrintWriter out = res.getWriter();
            out.println(gson.toJson(response));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
    }

    public JsonObject getTopCustomer(ResultSet rs) throws SQLException {
        JsonArray topcustomers = new JsonArray();
        while (rs.next()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Customer_id", rs.getInt(2));
            jsonObject.addProperty("Customer_name", rs.getString(3));
            jsonObject.addProperty("Total_purchase_amount", rs.getDouble(1));
            topcustomers.add(jsonObject);
        }
        JsonObject response = new JsonObject();
        response.add("Top_purchase", topcustomers);
        return response;
    }
}