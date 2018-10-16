<?php
session_start();
include ("conn.php");

$userid = $_POST["userid"];
$flocal = $_POST["fromLocality"];
$tlocal = $_POST["toLocality"];
$flat = $_POST["fromLat"];
$flng = $_POST["fromLng"];
$tlat = $_POST["toLat"];
$tlng = $_POST["toLng"];
$timestamp = $_POST["dateTime"];
$price = $_POST["ridePrice"];
$distance = $_POST["rideDistance"];
$companyId = "1";

$validquery = "Select * from user where id like '$userid'";

$validresult = mysqli_query($conn,$validquery) or die (mysqli_error($conn));

if (mysqli_num_rows($validresult) > 0 ) {
	

$query = "INSERT INTO bookinginfo (fromLocality,toLocality,fromLat,fromlng,toLat,toLng, timestamp, price, distance 
,userId, companyId) values ('$flocal','$tlocal','$flat','$flng','$tlat','$tlng','$timestamp','$price','$distance','$userid','$companyId');";

$result = mysqli_query($conn, $query) or die (mysql_error($conn)); 

	
echo "successfully booked";

} else {
echo "failed" .mysqli_error();
}

?>