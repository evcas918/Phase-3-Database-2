<?php
// Include database config
include 'config.php';

// Set response content type
header('Content-Type: application/json');

// Initialize response array
$response = array();
$response['success'] = false;

// Check if POST data is sent
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Get JSON data
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);
    
    // Check if required fields are provided
    if (isset($data['email']) && isset($data['password']) && isset($data['type'])) {
        $email = $data['email'];
        $password = $data['password'];
        $type = $data['type'];
        
        // Check if type is instructor
        if ($type != 'instructor') {
            $response['message'] = "Invalid account type.";
            echo json_encode($response);
            exit;
        }
        
        // Check credentials in account table
        $login_sql = "SELECT * FROM account WHERE email = ? AND password = ? AND type = ?";
        $login_stmt = mysqli_prepare($conn, $login_sql);
        mysqli_stmt_bind_param($login_stmt, "sss", $email, $password, $type);
        mysqli_stmt_execute($login_stmt);
        $login_result = mysqli_stmt_get_result($login_stmt);
        
        if (mysqli_num_rows($login_result) > 0) {
            // Get instructor ID and name
            $instructor_sql = "SELECT instructor_id, instructor_name FROM instructor WHERE email = ?";
            $instructor_stmt = mysqli_prepare($conn, $instructor_sql);
            mysqli_stmt_bind_param($instructor_stmt, "s", $email);
            mysqli_stmt_execute($instructor_stmt);
            $instructor_result = mysqli_stmt_get_result($instructor_stmt);
            
            if (mysqli_num_rows($instructor_result) > 0) {
                $instructor = mysqli_fetch_assoc($instructor_result);
                $response['success'] = true;
                $response['instructor_id'] = $instructor['instructor_id'];
                $response['instructor_name'] = $instructor['instructor_name']; // Add this line
            } else {
                $response['message'] = "Instructor record not found.";
            }
        } else {
            $response['message'] = "Invalid email or password.";
        }
    } else {
        $response['message'] = "Missing required fields.";
    }
}

// Send response
echo json_encode($response);
?>