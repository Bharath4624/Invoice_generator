<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Details</title>
    <script>
        async function sendRequest() {
            try {
                const response = await fetch('customers', {
                    method: 'GET'
                });
                if (response.ok) {
                    const responseData = await response.json();
                    displayData(responseData.Customer_details);
                } else {
                    document.getElementById('response').textContent = 'Error: ' + response.statusText;
                }
            } catch (error) {
                document.getElementById('response').textContent = 'Error: ' + error.message;
            }
        }
        function displayData(customers) {
            const table = document.createElement('table');
            table.border = '1';
            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');
            ['Id', 'Name', 'Address', 'Mobile', 'Email_Id'].forEach(headerText => {
                const th = document.createElement('th');
                th.textContent = headerText;
                headerRow.appendChild(th);
            });
            thead.appendChild(headerRow);
            table.appendChild(thead);
            const tbody = document.createElement('tbody');
            customers.forEach(customer => {
                const row = document.createElement('tr');
                Object.keys(customer).forEach(key => {
                    const cell = document.createElement('td');
                    cell.textContent = customer[key];
                    row.appendChild(cell);
                });
                tbody.appendChild(row);
            });
            table.appendChild(tbody);
            const responseDiv = document.getElementById('response');
            responseDiv.innerHTML = '';
            responseDiv.appendChild(table);
        }
    </script>
</head>
<body>
    <h3>Click to see the data</h3>
    <button onclick="sendRequest()">Customer details</button>
    <div id="response"></div>
</body>
</html>
