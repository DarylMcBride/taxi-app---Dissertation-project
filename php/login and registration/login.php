<?php
session_start();
include ("conn.php");

$phone = $_POST["phone"];
$pass = $_POST["password"];

$qry = "select * from user where phone like '$phone' and pass = '$pass';";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));


$rows=array();


if (mysqli_num_rows($result) > 0) {
	
	
	echo "loginCom";

} else {


	$driverqry = "select * from driver where phone like '$phone' and pass like '$pass';";


	$driverresult = mysqli_query($conn, $driverqry) or die (mysqli_error($conn));

	if (mysqli_num_rows($driverresult) > 0) {
		
			
			echo "loginDriverCom";
		
	} else {
		$companyqry = "select * from company where phone like '$phone' and pass like '$pass';";

		$companyresult = mysqli_query($conn, $companyqry) or die (mysqli_error($conn));
		
		if (mysqli_num_rows($companyresult) > 0) {
			
				
				
				echo "loginCompanyCom";
		
			
		} else {
	
			echo "login details are incorrect";
		}

	} 

}
?>