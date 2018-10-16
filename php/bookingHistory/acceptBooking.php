<?php
include("conn.php");

$bookingId = $_POST["bookingId"];
$driverName = $_POST["driverName"];

$qry = "SELECT * from driver where fName = '$driverName'";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));


if (mysqli_num_rows($result) > 0) {
	while($row = mysqli_fetch_assoc ($result)) {
		$driverId = $row['id'];
		
		$driverqry = "UPDATE bookinginfo SET status = 'approved', driverId = '$driverId' where id = '$bookingId';";
		
		$driverresult = mysqli_query($conn, $driverqry) or die (mysqli_error($conn)); 
		
	

	}
} else { 
echo "failed";
}


?>