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
@WebServlet("/products")
public class ProductDetails extends HttpServlet {
    public Gson gson = new Gson();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        String option1 = req.getParameter("allproducts");
        String option2 = req.getParameter("productsales");
        if (checkOptions(option1, option2)) {
            out.println("Please select one option");
            return;
        }
        Connection con = null;
        try {
            con = getConnection();
            if ("allproducts".equalsIgnoreCase(option1)) {
                String responseJson = getAllProducts(con);
                out.println(responseJson);
            } else {
                String responseJson = getProductSales(con);
                out.println(responseJson);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean checkOptions(String option1, String option2) {
        return (option1 == null && option2 == null) || (option1 != null && option2 != null);
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
