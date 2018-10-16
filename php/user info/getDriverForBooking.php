<?php
include("conn.php");

$qry = "select * from driver where companyId = 1";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));

$rows=array();

while($row = mysqli_fetch_array($result)) {
	$rows[] = $row;
}

echo json_encode(array('driverInfo' =>$rows));


mysqli_close($conn);
?>