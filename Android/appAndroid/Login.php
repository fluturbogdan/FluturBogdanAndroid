<?php
    $user = 'root';
    $pass = '';
    $db = 'adroidapp';
    
    $con = mysqli_connect("localhost", $user, $pass, $db);
    
	$username = "";
	$password = "";

   if(isset($_POST['username']))
    {
        $username = $_POST["username"];
    }
    if(isset($_POST['password']))
    {
        $password = $_POST["password"];
    } 
    
    /*$statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ? AND password = ?");
    mysqli_stmt_bind_param($statement, "ss", $username, $password);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);

    mysqli_stmt_bind_result($statement, $user_id, $name, $username, $password);
    
    $response = array();
    */
    
    /*while(mysqli_stmt_fetch($statement)){
    	echo "am intrat aici";
        $response["success"] = true;  
        $response["name"] = $name;
        $response["username"] = $username;
        $response["password"] = $password;
    }*/

    
	    $sql = "Select * from user where username ='$username' and password = '$password'";
	    $db_id = "";
		$query = mysqli_query($con,$sql);
		$row = mysqli_fetch_array($query);
		//$id = (string)$row['id'];
		$db_id = $row['user_id'];
		$db_name = $row['name'];
		$db_email = $row['email'];
		$db_password = $row['password'];
		$db_username = $row['username'];
		
		$response["success"] = false;

		if($password == $db_password and $db_username == $username)
		{
			$response["success"] = true; 
			$response["id"] = $db_id; 
			$response["name"] = $db_name;
	        $response["username"] = $db_username;
	       // $response["password"] = $db_password;
			//header("Location: index1.php");

		}
		/*else{
			echo "You didn't enter the correct details!";

		}*/
	
	print(json_encode($response));
   
?>    

<!--<?php
	/*$conn = mysqli_connect("localhost","root","","adroidapp");
	if(isset($_POST['username']))
    {
        $username = $_POST["username"];
    }
    if(isset($_POST['password']))
    {
        $password = $_POST["password"];
    }
	
	$statement = mysqli_prepare($conn,"SELECT * FROM user WHERE username = ? AND password = ?");
	mysqli_stmt_bind_param($statement,"ss",$username,$password);
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement,$id,$name,$username,$password);
	
	$response = array();
	$response["success"] = false;
	
	while(mysqli_stmt_fetch($statement)){
		echo "aici";
		$response["success"] = true;
		$response["id"] = $id;
		echo gettype($id);
		$response["name"] = $name;
		$response["username"] = $username;
	}
	
	print(json_encode($response));*/
?>-->