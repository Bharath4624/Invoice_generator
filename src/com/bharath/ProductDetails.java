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
@WebServlet("/products")
public class ProductDetails extends HttpServlet {
    public Gson gson = new Gson();
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY name ASC");
            JsonObject responseJson = getAllProducts(rs);
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

    public JsonObject getAllProducts(ResultSet rs) throws SQLException {
        JsonArray products = new JsonArray();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Name", rs.getString("name"));
            obj.addProperty("Price", rs.getDouble("price"));
            products.add(obj);
        }
        JsonObject response = new JsonObject();
        response.add("Products", products);
        return response;
    }
}