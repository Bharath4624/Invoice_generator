<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Details</title>
    <script>
        async function sendRequest(option) {
            try {
                const response = await fetch('orders', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (response.ok) {
                    const responseData = await response.json();
                    displayTable(responseData.Order_details);
                } else {
                    document.getElementById('response').textContent = 'Error: ' + response.statusText;
                }
            } catch (error) {
                document.getElementById('response').textContent = 'Error: ' + error.message;
            }
        }
        function displayTable(orderDetails) {
            const table = document.createElement('table');
            table.border = '1';
            table.width='100%';
            const thead = document.createElement('thead');
            const tbody = document.createElement('tbody');
            const headerRow = document.createElement('tr');
            const headers = ['Customer ID', 'Customer Name', 'Invoice ID', 'Date', 'Total Tax', 'Total Amount'];
            headers.forEach(headerText => {
                const th = document.createElement('th');
                th.textContent = headerText;
                headerRow.appendChild(th);
            });
            thead.appendChild(headerRow);
            orderDetails.forEach(order => {
                const row = document.createElement('tr');
                const cells = [
                    order.Customer_id,
                    order.Customer_name,
                    order.Invoice_id,
                    order.Date,
                    order.Total_tax,
                    order.Total_amount
                ];
                cells.forEach(cellText => {
                    const td = document.createElement('td');
                    td.textContent = cellText;
                    row.appendChild(td);
                });
                tbody.appendChild(row);
            });
            table.appendChild(thead);
            table.appendChild(tbody);
            const responseElement = document.getElementById('response');
            responseElement.appendChild(table);
        }
    </script>
</head>
<body>
    <h3>Click to see the data</h3>
    <button onclick="sendRequest('orders')">Order details</button>
    <div id="response"></div>
</body>
</html>
