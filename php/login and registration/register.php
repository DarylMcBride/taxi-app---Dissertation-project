<?php
include ("conn.php");

$phone = $_POST["phone"];
$pass = $_POST["password"];
$fname = $_POST["fName"];
$lname = $_POST["lName"];
$joined = $_POST["joined"];


$qry = "insert into user (fName, lName, phone, pass, joined) values ('$fname','$lname','$phone','$pass', $joined);";

$result = mysqli_query($conn, $qry) or die (mysqli_error($conn));

mysqli_close("conn.php");

?>