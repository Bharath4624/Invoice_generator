<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Product Details</title>
    <script>
        async function sendRequest(option) {
            try {
                const requestData = JSON.stringify({ option: option });
                const response = await fetch('products', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    },
                    body: requestData
                });
                if (response.ok) {
                    const responseData = await response.json();
                    document.getElementById('response').textContent = JSON.stringify(responseData, null, 2);
                }
                else {
                    document.getElementById('response').textContent = 'Error:'+response.statusText;
                }
            }
            catch (error) {
                document.getElementById('response').textContent = 'Error:'+error.message;
            }
        }
    </script>
</head>
<body>
    <h2>Choose the type of data you want</h2>
    <button onclick="sendRequest('products')">All products</button>
    <button onclick="sendRequest('sales')">Sales details of products</button>
    <pre id="response"></pre>
</body>
</html>
