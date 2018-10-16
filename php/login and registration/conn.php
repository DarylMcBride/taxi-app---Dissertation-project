<?php

$dbname = "Csc7057";
$myuser = "daryl";
$passw = "csc7057";
$server = "localhost";

$conn = mysqli_connect($server,$myuser,$passw,$dbname);

if (!$conn) {

	die ("connection failed: " .mysqli_connect_error() );

} 

?>


