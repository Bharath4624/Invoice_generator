<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%
try{
Class.forName("com.mysql.cj.jdbc.Driver");
}catch(ClassNotFoundException e){
e.printStackTrace();
}
Connection con=null;
%>
<!DOCTYPE html>
<html>
<head>
    <style>
        h1 {
        position: absolute;
        top: 5px;
        right: 50px;
        font-size: 58px;
        color: red;
        }
        h2 {
        position: absolute;
        top: 90px;
        right:100px;
        font-size: 20px;
        }
        .details {
        position: absolute;
        line-height: 3px;
        top: 10px;
        left: 50px;
        font-size: 15px;
        }
        .storename {
        font-size: 20px;
        }
        .Customer {
        position: absolute;
        line-height: 3px;
        top: 170px;
        left: 50px;
        font-size: 15px;
        }
        .customername{
        font-size: 20px;
        }
        .Shipping {
        position: absolute;
        line-height: 3px;
        top: 350px;
        left: 50px;
        font-size: 15px;
        }
        .invoicedetails {
        position: absolute;
        line-height: 3px;
        bottom: 270px;
        right: 50px;
        }
        .productdetails {
        position: absolute;
        bottom:100px;
        left: 400px;
        word-spacing: 250px;
        }
    </style>
</head>
<body>
<%
try{
con=DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
Statement stmt2=con.createStatement();
ResultSet orders=stmt2.executeQuery("SELECT * FROM orders ORDER BY inv_id DESC LIMIT 1");
if(orders.next()){
%>
    <h1>Invoice</h1>
    <h2>#INV-<%=orders.getInt("inv_id")%></h2>
    <div class="details">
        <p class="storename"><b>Store</b></p>
        <p>No.127, Abc street</p>
        <p>Chennai-6000098, TamilNadu</p>
        <p>India</p>
        <p>+91 1234567890</p>
        <p>store.chennai@gmail.com</p>
        <p>https://storechennai.in/</p>
    </div>
    <%
    Statement stmt1=con.createStatement();
    ResultSet customers=stmt1.executeQuery("SELECT * FROM customers ORDER BY cus_id DESC LIMIT 1");
    if(customers.next()){
    %>
    <div class="Customer">
        <p><b>BILL TO</b></p>
        <p class="customername"><b><%=customers.getString("name")%></b></p>
        <p>Id:<%=customers.getInt("cus_id")%></p>
        <p><%=customers.getString("address")%></p>
        <p><%=customers.getString("city")%></p>
        <p><%=customers.getString("zipcode")%></p>
        <p><%=customers.getString("country")%></p>
        <p><%=customers.getString("mobile")%></p>
        <p><%=customers.getString("email")%></p>
    </div>
    <div class="Shipping">
        <p><b>SHIP TO</b></p>
        <p class="customername"><b><%=customers.getString("name")%></b></p>
        <p>Id:<%=customers.getInt("cus_id")%></p>
        <p><%=customers.getString("address")%></p>
        <p><%=customers.getString("city")%></p>
        <p><%=customers.getString("zipcode")%></p>
        <p><%=customers.getString("country")%></p>
        <p><%=customers.getString("mobile")%></p>
        <p><%=customers.getString("email")%></p>
    </div>
    <div class="invoicedetails">
        <p>Invoice date:<%=customers.getDate("date")%></p>
        <p>Place of supply:<%=customers.getString("city")%></p>
    </div>
     <%
        }
     %>
    <div class="productdetails">
        <table border="1px">
            <tr>
                <th>Item</th>
                <th>Quantity</th>
                <th>Tax</th>
                <th>Amount</th>
            </tr>
            <%
            int invid=orders.getInt("inv_id");
            Statement stmt3=con.createStatement();
            ResultSet products=stmt3.executeQuery("SELECT * FROM invoiceproducts WHERE inv_id="+invid);
            while(products.next()){
            %>
            <tr>
                <td><%=products.getString("name")%></td>
                <td><%=products.getInt("quantity")%></td>
                <td><%=products.getDouble("tax")%></td>
                <td><%=products.getDouble("subtotal")%></td>
            </tr>
            <%
            }
            %>
            <tr>
                <td><b>Total:</b></td>
                <td></td>
                <td><b><%=orders.getDouble("totaltax")%></b></td>
                <td><b><%=orders.getDouble("totalamount")%></b></td>
            </tr>
        </table>
    </div>
    <%
    }
    }
    catch(Exception e){
    e.printStackTrace();
    }
    finally{
    con.close();
    }
    %>
</body>
</html>