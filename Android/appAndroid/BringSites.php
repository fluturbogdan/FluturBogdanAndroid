<?php
    $user = 'root';
    $pass = '';
    $db = 'adroidapp';
    
    $con = mysqli_connect("localhost", $user, $pass, $db);
    
    $user_id = '';
    if(isset($_POST['id']))
    {
        $user_id = $_POST['id'];
    }
    //echo "id" + $user_id;
    intval($user_id);
    
    /*$statement = mysqli_prepare($con, "SELECT * FROM sites WHERE user_id = ?");
    mysqli_stmt_bind_param($statement, "i", $user_id);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $link_site, $user_id, $comment);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
    	echo "am intrat aici";
        $response["success"] = true;  
        $response["user_id"] = $user_id;
        $response["link_site"] = $link_site;
        $response["comment"] = $comment;
    }*/
    $sql = "SELECT * FROM sites WHERE user_id = '$user_id'";
    $result = mysqli_query($con, $sql); 
    $emparray = array();
    //$response["success"] = false; 
    if(mysqli_num_rows($result) > 0)  
    { 

      while($row = mysqli_fetch_array($result))  
      { 
       // $response["success"] = true; 
        $response[] = $row;

      }
       //print json_encode(array('Words' => $emparray)); //array('kitten' => $result)
        print(json_encode(array('sites'=>$response)));
    }else{print("we have a problem here!");}
?>