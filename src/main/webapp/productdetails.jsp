<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Product Details</title>
    <script>
        async function sendRequest() {
            try {
                const response = await fetch('products', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (response.ok) {
                    const responseData = await response.json();
                    displayTable(responseData);
                } else {
                    document.getElementById('response').textContent = 'Error: ' + response.statusText;
                }
            } catch (error) {
                document.getElementById('response').textContent = 'Error: ' + error.message;
            }
        }
        function displayTable(data) {
            const products = data.Products;
            let tableHtml = '<table border="1" width="100%"><thead><tr><th>Name</th><th>Price</th></tr></thead><tbody>';
            products.forEach(product => {
                tableHtml += `<tr><td>${product.Name}</td><td>${product.Price}</td></tr>`;
            });
            tableHtml += '</tbody></table>';
            document.getElementById('response').innerHTML = tableHtml;
        }
    </script>
</head>
<body onload="sendRequest()">
    <h3>Product List</h3>
    <div id="response">Loading...</div>
</body>
</html>
