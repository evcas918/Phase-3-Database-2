<?php
// Include database config
include 'config.php';

// Set response content type
header('Content-Type: application/json');

// Initialize response array
$response = array();
$response['success'] = false;

// Get JSON data
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Check if instructor_id is provided
if (isset($data['instructor_id'])) {
    $instructor_id = $data['instructor_id'];
    
    // Get instructor information
    $query = "SELECT * FROM instructor WHERE instructor_id = ?";
    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "s", $instructor_id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    
    if (mysqli_num_rows($result) > 0) {
        $instructor = mysqli_fetch_assoc($result);
        
        $response['success'] = true;
        $response['instructor_name'] = $instructor['instructor_name'];
        $response['dept_name'] = $instructor['dept_name'];
        $response['title'] = $instructor['title'];
        $response['email'] = $instructor['email'];
    } else {
        $response['message'] = "Instructor not found.";
    }
} else {
    $response['message'] = "Instructor ID is required.";
}

// Send response
echo json_encode($response);
?>