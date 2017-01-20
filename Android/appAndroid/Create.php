<?php 
		$servername = "localhost";
		$username = 'root';
		$password = '';
		$dbname = "adroidapp";

		// Create connection
		$conn = mysqli_connect($servername, $username, $password, $dbname);
		// Check connection
		if (!$conn) {
		    die("Connection failed: " . mysqli_connect_error());
		}
		
		$user_id = "";
		$link_site = "";
		$comment = "";


		
		$link_site = isset($_POST['link_site']) ? $_POST['link_site'] : '';
		$comment =isset($_POST['comment']) ? $_POST['comment'] : '';
		$user_id = isset($_POST['user_id']) ? (string)$_POST['user_id'] : '';


		$response["success"] = false;
		$sql = "INSERT INTO sites (link_site, user_id,comment)
				VALUES ('$link_site','$user_id','$comment')";

				if (mysqli_query($conn, $sql)) {
				   // echo "New record created successfully";
					$response["success"] = true;
				} else {
				    //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
				}

		print(json_encode($response));		
?>