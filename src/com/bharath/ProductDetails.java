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
@WebServlet("/products")
public class ProductDetails extends HttpServlet {
    public Gson gson = new Gson();
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        BufferedReader reader = req.getReader();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        String option = jsonObject.get("option").getAsString();
        try {
            Connection con = getConnection();
            String responseJson;
            if ("products".equalsIgnoreCase(option)) {
                responseJson = getAllProducts(con);
            } else {
                responseJson = getProductSales(con);
            }
            PrintWriter out = res.getWriter();
            out.println(responseJson);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
    }
    public String getAllProducts(Connection con) throws SQLException {
        String query = "SELECT * FROM products ORDER BY name ASC";
        PreparedStatement pdst = con.prepareStatement(query);
        ResultSet rs = pdst.executeQuery();
        JsonArray products = new JsonArray();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Name", rs.getString("name"));
            obj.addProperty("Price", rs.getDouble("price"));
            products.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Products", products);
        return gson.toJson(response);
    }
    public String getProductSales(Connection con) throws SQLException {
        String query = "SELECT name, SUM(subtotal), SUM(quantity) FROM invoiceproducts GROUP BY name ORDER BY SUM(subtotal) DESC";
        PreparedStatement pdst = con.prepareStatement(query);
        ResultSet rs = pdst.executeQuery();
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
        return gson.toJson(response);
    }
}
