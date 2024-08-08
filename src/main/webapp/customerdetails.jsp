<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
<form action="customers">
<table border="1px">
<tr>
<td>Choose the type of data u want (Select only one option at a time)</td>
</tr>
<tr>
<td><input type="checkbox" name="allcustomers" value="allcustomerdetails"/>All customer data</td>
</tr>
<tr>
<td><input type="checkbox" name="onlyorderdetails" value=-"orderdetails"/>Order details</td>
</tr>
<tr>
<td><input type="submit" value="Submit"></td>
</tr>
</table>
</form>
</body>
</html>