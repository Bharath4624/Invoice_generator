<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Details</title>
    <script>
        async function sendRequest(option) {
            try {
                const requestData = JSON.stringify({ option: option });
                const response = await fetch('customers', {
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
                    document.getElementById('response').textContent='Error:'+response.statusText;
                }
            }
            catch (error) {
                document.getElementById('response').textContent='Error:'+error.message;
            }
        }
    </script>
</head>
<body>
    <h3>Click to see the data</h3>
    <button onclick="sendRequest('customers')">Customer details</button>
    <pre id="response"></pre>
</body>
</html>
