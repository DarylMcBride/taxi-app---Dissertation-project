<?php
include ("conn.php");

$companyId = $_POST['companyId'];

$qry = "select bookinginfo.fromLocality, bookinginfo.toLocality, bookinginfo.timestamp, bookinginfo.price, driver.fName, user.fName, user.lName,
user.phone from bookinginfo
inner join driver on driver.id = bookinginfo.driverid
inner join user on user.id = bookinginfo.userId
where bookinginfo.companyId = $companyId"


?>