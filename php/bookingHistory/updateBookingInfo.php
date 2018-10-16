<?php
include("conn.php");

$bookingId = $_POST["bookingId"];

$qry = "select * from bookinginfo where id='$bookingId';";


$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));

if (mysqli_num_rows($result) > 0) {
while($row = mysqli_fetch_array($result)) {
	$fromLocality = $row['fromLocality'];
	$toLocality = $row['toLocality'];
	$timestamp = $row['timestamp'];
	$price = $row['price'];
	$distance = $row['distance'];
	$userId = $row['userId'];
	$companyId = $row['companyId'];
	
	$insert = "insert into declinedbooking (fromLocality, toLocality, timestamp, price, distance, userId, companyId)
	values ('$fromLocality','$toLocality','$timestamp','$price','$distance','$userId','$companyId')";
	
	$insertresult = mysqli_query($conn, $insert) or die(mysqli_error);
	$num = mysqli_affected_rows($insertresult);
	
	if (!$num) {
		$delete = "delete from bookinginfo where id='$bookingId';";
		
		$deleteresult = mysqli_query($conn, $delete) or die (mysqli_error($conn));
		
	} else { 
	echo "error";
	}
} 

}else {
	echo mysqli_error($conn);


}	
?>