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

@WebServlet("/sales")
public class SalesDetails extends HttpServlet {
    public Gson gson = new Gson();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        BufferedReader reader = req.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        String option = jsonObject.get("option").getAsString();
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, SUM(subtotal), SUM(quantity) FROM invoiceproducts GROUP BY name ORDER BY SUM(subtotal) DESC");
            JsonObject response = getProductSales(rs);
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

    public JsonObject getProductSales(ResultSet rs) throws SQLException {
        JsonArray productSales = new JsonArray();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Product_Name", rs.getString(1));
            obj.addProperty("Quantity_sold", rs.getInt(3));
            obj.addProperty("Total_amount", rs.getDouble(2));
            productSales.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Product_sales", productSales);
        return response;
    }
}
