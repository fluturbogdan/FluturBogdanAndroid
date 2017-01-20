<?php 
$conn = mysqli_connect('localhost', 'root', '', 'adroidapp');  

 
 $user_id = isset($_POST['user_id']) ? (string)$_POST['user_id'] : '';
 $link_site = isset($_POST['link_site']) ? $_POST['link_site'] : '';
 $comment = isset($_POST['comment']) ? $_POST['comment'] : '';
 $old_link_site = isset($_POST['old_link_site']) ? $_POST['old_link_site'] : '';
 $old_comment = isset($_POST['old_comment']) ? $_POST['old_comment'] : '';	

 #$sql = "UPDATE sites SET link_site ='$link_site' , comment ='$comment' WHERE user_id ='".$user_id."' and link_site ='".$link_site."' and comment ='".$comment."'";
 #$sql = "UPDATE sites ". "SET link_site = $link_site , comment = $comment"."WHERE user_id = $user_id and link_site = $link_site and  comment = $comment" ;
  #`user_id` = '$user_id' and `link_site`='$link_site' and `comment`='$comment'"
 $statement = mysqli_prepare($conn,"UPDATE sites SET link_site = ? , comment = ? WHERE user_id = ? and comment = ? and link_site = ?");
 mysqli_stmt_bind_param($statement,"sssss",$link_site,$comment,$user_id,$old_comment,$old_link_site);
 
 $response = array();
 $response["success"] = false;
 if(mysqli_stmt_execute($statement) === TRUE)#(mysqli_query($conn, $sql) === TRUE)  
 {  
	$response["success"] = true; 
 }
 print(json_encode($response));	
?>