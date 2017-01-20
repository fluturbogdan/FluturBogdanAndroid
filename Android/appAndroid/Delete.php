<?php
	 $user = 'root';
	 $pass = '';
	 $db = 'adroidapp';  
	 
	
	 $conn = new mysqli('localhost', $user, $pass, $db) or die("Unable to connect to DB");
		 
	 $user_id = isset($_POST['user_id']) ? (string)$_POST['user_id'] : '';
	 $link_site = isset($_POST['link_site']) ? $_POST['link_site'] : '';
	 $comment =isset($_POST['comment']) ? $_POST['comment'] : '';
	 $sql = "DELETE FROM sites WHERE user_id ='".$user_id."' and link_site ='".$link_site."' and comment ='".$comment."' ";
	 $response["success"] = false;
	 if(mysqli_query($conn, $sql) === TRUE)  
	 {  
		$response["success"] = true;
	 } 
		
	print(json_encode($response));	
 ?>  
