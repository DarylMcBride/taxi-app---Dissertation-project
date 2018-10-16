<?php
include ("conn.php");

$fromLocation = $_POST["fromLocation"];
$toLocation = $_POST["toLocation"];
$uid = $_POST["userId"];
$status = "pending";


$qry = "Select * from bookinginfo where fromLocality = '$fromLocation' and toLocality = '$toLocation'
 and userId = '$uid'
 and status = '$status';";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));


if (mysqli_num_rows($result) > 0) {
	
	echo "Success";
	
	
} else {
	$accepted = "accepted";
	
	$acceptedqry = "Select * from bookinginfo where fromLocality = '$fromLocation' and toLocality = '$toLocation'
	and userId = '$uid'
	and status = '$accepted';";
	
	$acceptresult = mysqli_query($conn, $acceptedqry) or die (mysqli_error($conn));
	
	if (mysqli_num_rows($acceptresult) > 0) {
		
		echo "accepted";
	
	} else {
		
		echo "gone";
		
	}
	
}

mysqli_close($conn);
?>