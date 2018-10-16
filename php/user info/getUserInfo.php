<?php
include("conn.php");

$phone = $_POST["phone"];
$pass = $_POST["password"];


$qry = "select * from user where phone like '$phone' and pass like '$pass';";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));

$rows=array();

while($row = mysqli_fetch_array($result)) {
	$rows[] = $row;
}

echo json_encode(array('profileData' =>$rows));


mysqli_close($conn);
?>