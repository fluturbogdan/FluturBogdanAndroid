<?php
    $conn = mysqli_connect("localhost","root","","adroidapp");

    $username = "";
    $password = "";
    $name = "";
    $email = "";

    if(isset($_POST['username']))
    {
        $username = $_POST["username"];
    }

    if(isset($_POST['password']))
    {
        $password = $_POST["password"];
    } 
    if(isset($_POST['name']))
    {
        $name = $_POST["name"];
    }
    if(isset($_POST['email']))
    {
        $email = $_POST["email"];
    }
   
    
    $statement = mysqli_prepare($conn,"SELECT * FROM user WHERE username = ?");
    mysqli_stmt_bind_param($statement,"s",$username);
    mysqli_stmt_execute($statement);
    
    $response = array();
    
    if(mysqli_stmt_fetch($statement)){
        
        $response["success"] = false;
        print(json_encode($response));
        
    } else {
        $statement = mysqli_prepare($conn,"INSERT INTO user (name,username,email,password) VALUES (?,?,?,?)");
        mysqli_stmt_bind_param($statement,"ssss",$name,$username,$email,$password);
        mysqli_stmt_execute($statement);
        
        $response["success"] = true;
        $response["email"] = $email;
        print(json_encode($response));
    }
?>