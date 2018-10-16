<?php
include("conn.php");

$bookingId = $_POST["bookingId"];
$driverName = $_POST["driverName"];
$driverLat = $_POST["driverLat"];
$driverLng = $_POST["driverLng"];

$qry = "";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));


if (mysqli_num_rows($result) > 0) {


	}
} else { 
echo "failed";
}


?>