<?php

include("conn.php");

$query = "select * from userlifthistory where userid = 1";

$result=mysqli_query($conn, $query) or die (mysqli_error($conn));

$rows=array();

while($row = mysqli_fetch_array($result)) {
	$rows[] = $row;
}

echo json_encode(array('bookingHistory' =>$rows));


mysqli_close($conn);

?>