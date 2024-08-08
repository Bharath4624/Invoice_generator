package com.bharath;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
@WebServlet("/invoice")
public class UserDetails extends HttpServlet {
    public static int invoiceid = 99;
    public Gson gson = new Gson();
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonObject customerjson = jsonObject.getAsJsonObject("customer");
            String name = customerjson.get("name").getAsString();
            String address = customerjson.get("address").getAsString();
            String city = customerjson.get("city").getAsString();
            String zipcode = customerjson.get("zipcode").getAsString();
            String country = customerjson.get("country").getAsString();
            String mobile = customerjson.get("mobno").getAsString();
            String email = customerjson.get("email").getAsString();
            String date = customerjson.get("date").getAsString();
            JsonArray productsArray = jsonObject.getAsJsonArray("products");
            if (checkEmpty(name, address, city, zipcode, country, mobile, email, date)) {
                sendErrorResponse(res, "Please fill customer details first");
                return;
            }
            Connection con = getConnection();
            int customerId = insertCustomer(con, name, date, address, city, zipcode, country, mobile, email);
            JsonArray productDetailsArray = new JsonArray();
            double[] totals = processProducts(con, productsArray, customerId, productDetailsArray);
            insertOrder(con, customerId, name, totals[1], totals[0], invoiceid);
            JsonObject responseJson = createResponseJson(customerId, name, date, address, city, zipcode, country, mobile, email, totals, productDetailsArray);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
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
    public boolean checkEmpty(String name, String address, String city, String zipcode, String country, String mobile, String email, String date) {
        return name.isEmpty() || address.isEmpty() || city.isEmpty() || zipcode.isEmpty() || country.isEmpty() || mobile.isEmpty() || email.isEmpty() || date.isEmpty();
    }
    public void sendErrorResponse(HttpServletResponse res, String message) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("error", message);
        PrintWriter out = res.getWriter();
        out.println(gson.toJson(errorResponse));
        out.flush();
    }
    public int insertCustomer(Connection con, String name, String date, String address, String city, String zipcode, String country, String mobile, String email) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT cus_id FROM customers WHERE name=? AND address=? AND city=? AND zipcode=? AND country=? AND mobile=? AND email=?");
        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.setString(3, city);
        stmt.setString(4, zipcode);
        stmt.setString(5, country);
        stmt.setString(6, mobile);
        stmt.setString(7, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("cus_id");
        } else {
            PreparedStatement insertcus = con.prepareStatement("INSERT INTO customers(name, date, address, city, zipcode, country, mobile, email) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            insertcus.setString(1, name);
            insertcus.setString(2, date);
            insertcus.setString(3, address);
            insertcus.setString(4, city);
            insertcus.setString(5, zipcode);
            insertcus.setString(6, country);
            insertcus.setString(7, mobile);
            insertcus.setString(8, email);
            insertcus.executeUpdate();
            ResultSet generatedKeys = insertcus.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating customer failed, no ID obtained.");
            }
        }
    }
    public double[] processProducts(Connection con, JsonArray productsArray, int customerId, JsonArray productDetailsArray) throws SQLException {
        double total = 0;
        double totaltax = 0;
        Statement stmt = con.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM invoiceproducts ORDER BY inv_id DESC LIMIT 1");
        if (resultSet.next()) {
            invoiceid = resultSet.getInt("inv_id");
        }
        invoiceid++;
        for (JsonElement productElement : productsArray) {
            JsonObject productObj = productElement.getAsJsonObject();
            String productName = productObj.get("name").getAsString();
            int quantity = productObj.get("quantity").getAsInt();
            double price = getPrice(con, productName);
            double tax = 0;
            double subtotal = (price * quantity);
            tax = (12 * subtotal) / 100;
            subtotal += tax;
            addProductToInvoice(con, productName, quantity, tax, subtotal, invoiceid);
            total += subtotal;
            totaltax += tax;
            JsonObject productDetails = new JsonObject();
            productDetails.addProperty("name", productName);
            productDetails.addProperty("quantity", quantity);
            productDetails.addProperty("subtotal", subtotal);
            productDetails.addProperty("tax", tax);
            productDetailsArray.add(productDetails);
        }
        return new double[]{total, totaltax};
    }
    public double getPrice(Connection con, String product) throws SQLException {
        PreparedStatement pdst = con.prepareStatement("SELECT price FROM products WHERE name = ?");
        pdst.setString(1, product);
        ResultSet rs = pdst.executeQuery();
        if (rs.next()) {
            return rs.getDouble("price");
        }
        return 0;
    }
    public void addProductToInvoice(Connection con, String product, int quantity, double tax, double subtotal, int invoiceid) throws SQLException {
        PreparedStatement insertinvprod = con.prepareStatement("INSERT INTO invoiceproducts(name, quantity, tax, subtotal, inv_id) VALUES (?, ?, ?, ?, ?)");
        insertinvprod.setString(1, product);
        insertinvprod.setInt(2, quantity);
        insertinvprod.setDouble(3, tax);
        insertinvprod.setDouble(4, subtotal);
        insertinvprod.setInt(5, invoiceid);
        insertinvprod.executeUpdate();
    }
    public void insertOrder(Connection con, int customerId, String name, double totaltax, double total, int invoiceid) throws SQLException {
        PreparedStatement insertorders = con.prepareStatement("INSERT INTO orders(cus_id, cus_name, totaltax, totalamount, inv_id) VALUES (?, ?, ?, ?, ?)");
        insertorders.setInt(1, customerId);
        insertorders.setString(2, name);
        insertorders.setDouble(3, totaltax);
        insertorders.setDouble(4, total);
        insertorders.setInt(5, invoiceid);
        insertorders.executeUpdate();
    }
    public JsonObject createResponseJson(int customerId, String name, String date, String address, String city, String zipcode, String country, String mobile, String email, double[] totals, JsonArray productDetailsArray) {
        JsonObject responseJson = new JsonObject();
        JsonObject customerJson = new JsonObject();
        customerJson.addProperty("customer_id", customerId);
        customerJson.addProperty("customer_name", name);
        customerJson.addProperty("date", date);
        customerJson.addProperty("address", address);
        customerJson.addProperty("city", city);
        customerJson.addProperty("zipcode", zipcode);
        customerJson.addProperty("country", country);
        customerJson.addProperty("mobile", mobile);
        customerJson.addProperty("email", email);
        JsonObject orderSummaryJson = new JsonObject();
        orderSummaryJson.addProperty("invoice_id", invoiceid);
        orderSummaryJson.addProperty("total_amount", totals[0]);
        orderSummaryJson.addProperty("total_tax", totals[1]);
        orderSummaryJson.addProperty("customer_id", customerId);
        responseJson.add("customer", customerJson);
        responseJson.add("order_summary", orderSummaryJson);
        responseJson.add("products", productDetailsArray);
        return responseJson;
    }
}