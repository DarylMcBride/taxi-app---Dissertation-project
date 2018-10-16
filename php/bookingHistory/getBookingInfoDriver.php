<?php
include("conn.php");

$driverId = $_POST['id'];

$query = "SELECT bookinginfo.id, bookinginfo.fromLocality, bookinginfo.toLocality, bookinginfo.fromLat, bookinginfo.fromLng,bookinginfo.toLat, bookinginfo.toLng, bookinginfo.timestamp,
bookinginfo.price, bookinginfo.distance, user.fName, user.lName, user.phone 
FROM bookinginfo
INNER JOIN user
ON bookinginfo.userid = user.id
WHERE bookinginfo.driverId = '$driverId' AND bookinginfo.status = 'approved'";

$result = mysqli_query($conn, $query) or die (mysqli_error($conn));

$rows=array();

while($row = mysqli_fetch_array($result)) {
	$rows[] = $row;
}

echo json_encode(array('bookingRequests' =>$rows));

?>