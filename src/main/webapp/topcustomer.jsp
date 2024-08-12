<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Top Customers</title>
    <script>
        async function sendRequest(option) {
            try {
                const response = await fetch('purchase', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (response.ok) {
                    const responseData = await response.json();
                    displayTable(responseData.Top_purchase);
                } else {
                    document.getElementById('response').textContent = 'Error: ' + response.statusText;
                }
            } catch (error) {
                document.getElementById('response').textContent = 'Error: ' + error.message;
            }
        }
        function displayTable(topCustomers) {
            const table = document.createElement('table');
            table.border = '1';
            const thead = document.createElement('thead');
            const tbody = document.createElement('tbody');
            const headerRow = document.createElement('tr');
            const headers = ['Customer ID', 'Customer Name', 'Total Purchase Amount'];
            headers.forEach(headerText => {
                const th = document.createElement('th');
                th.textContent = headerText;
                headerRow.appendChild(th);
            });
            thead.appendChild(headerRow);
            topCustomers.forEach(customer => {
                const row = document.createElement('tr');
                const cells = [
                    customer.Customer_id,
                    customer.Customer_name,
                    customer.Total_purchase_amount
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
            responseElement.innerHTML = '';
            responseElement.appendChild(table);
        }
    </script>
</head>
<body>
    <h3>Click to see the data</h3>
    <button onclick="sendRequest('purchase')">Top customers</button>
    <div id="response"></div>
</body>
</html>
