<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Gallery</title>
 <script type = "text/javascript" 
         src = "https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js">
      </script>
<script> 
$(document).ready(function(){ 
	
	var filesize;
	alert("-+/////-+-");
	$('#GFG_UP').text("Choose file from system to get the fileSize"); 
    $('#File').on('change', function() {
    	
    	filesize = this.files[0].size;
        if (filesize > 2097152) { 
            alert("Try to upload file less than 2MB!"); 
        } else { 
            $('#GFG_DOWN').text(filesize + "bytes"); 
        } 
    }); 
	
    function val(){
    	alert("-*-*-"+filesize);
    	return false;
    }
	
});
</script>
<style type="text/css">

.fileUp {
	margin: 100px;
	border: 1px solid red;
	padding: 20px;
	width: 500px;
	background-color: #eea861;
	padding-top: 100px;
	padding-bottom: 100px;
}

</style>
</head>
<body>
    
      <center>
       <div class="fileUp">
    <strong id="setsize"></strong>
    <form method="post" action="./saveGallery?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
    	<input type="file" name="file" value="" id="File" />
    	<input type="submit" value="submit" onClick="return val();" />
    </form>
    <p id="GFG_DOWN" style= 
        "color:green; font-size: 20px; font-weight: bold;"> 
    </p> 
    </div>
    </center>
   
</body>
</html>