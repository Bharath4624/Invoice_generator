<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%
try {
    Class.forName("com.mysql.cj.jdbc.Driver");
} catch (ClassNotFoundException e) {
    e.printStackTrace();
}
Connection con = null;
Statement stmt = null;
ResultSet rs = null;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Invoice</title>
    <script>
        function sendInvoice(event) {
            event.preventDefault();
            const customerData = {
                name: document.querySelector('input[name="name"]').value,
                date: document.querySelector('input[name="date"]').value,
                address: document.querySelector('input[name="address"]').value,
                city: document.querySelector('input[name="city"]').value,
                zipcode: document.querySelector('input[name="zipcode"]').value,
                country: document.querySelector('input[name="country"]').value,
                mobno: document.querySelector('input[name="mobno"]').value,
                email: document.querySelector('input[name="email"]').value
            };
            const empty=Object.values(customerData).some(value=>value==='');
            if(empty){
            alert("Please fill all the customer details");
            return;
            }
            const products = [];
            const productRows = document.querySelectorAll('input[name="product"]:checked');
            productRows.forEach((checkbox) => {
                const productName = checkbox.value;
                const quantity = document.querySelector('input[name="quantity' + productName + '"]').value;
                products.push({
                    name: productName,
                    quantity: parseInt(quantity, 10)
                });
            });
            if(products.length==0){
            alert("Please add atleast 1 product");
            return;
            }
            const formData = {
                customer: customerData,
                products: products
            };
            fetch('invoice', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
            .then(response => response.json()).then(data => {
                console.log(data);
                displayResult();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Please try again');
            });
        }
        function displayResult() {
            window.location.href="invoice.jsp";
        }
    </script>
</head>
<body>
    <form onsubmit="sendInvoice(event)">
        Name: <br><input type="text" name="name"/><br>
        Date: <br><input type="date" name="date"/><br>
        Address: <br><input type="text" name="address"/><br>
        City: <br><input type="text" name="city"/><br>
        Zipcode: <br><input type="text" name="zipcode"/><br>
        Country: <br><input type="text" name="country"/><br>
        Mobile: <br><input type="text" name="mobno"/><br>
        Email: <br><input type="text" name="email"/><br>
        <br>
        <table border="1px">
            <tr>
                <td>Product</td>
                <td>Quantity</td>
                <td>Price</td>
            </tr>
            <%
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/invoice", "root", "bharath123@#");
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT * FROM products");
                while (rs.next()) {
            %>
            <tr>.
                <td>
                    <input type="checkbox" name="product" value="<%= rs.getString(1) %>"/><%= rs.getString(1) %>
                </td>
                <td>
                    <input type="number" min="1" name="quantity<%=rs.getString(1)%>" value="1"/>
                </td>
                <td><%= rs.getDouble(2) %></td>
            </tr>
            <%
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            %>
            <tr>
                <td>
                    <input type="submit" name="Submit" value="Place order"/>
                </td>
            </tr>
        </table>
    </form>
    <pre id="result"></pre>
</body>
</html>