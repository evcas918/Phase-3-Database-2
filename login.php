<?php
// Include database config
include 'config.php';

// Set response content type
header('Content-Type: application/json');

// Initialize response array
$response = array();
$response['success'] = false;

// Check if all required fields are provided
if (isset($_POST['email']) && isset($_POST['password'])) {
    
    // Get parameters
    $email = $_POST['email'];
    $password = $_POST['password'];
    
    // Check credentials in account table
    $login_sql = "SELECT * FROM account WHERE email = ? AND password = ?";
    $login_stmt = mysqli_prepare($conn, $login_sql);
    mysqli_stmt_bind_param($login_stmt, "ss", $email, $password);
    mysqli_stmt_execute($login_stmt);
    $login_result = mysqli_stmt_get_result($login_stmt);
    
    if (mysqli_num_rows($login_result) > 0) {
        // Get account and set response
        $account = mysqli_fetch_assoc($login_result);
        $account_type = $account['type'];
        
        $response['success'] = true;
        $response['account_type'] = $account_type;
        
        // Get additional info based on account type
        if ($account_type === 'student') {
            // Get student ID
            $student_sql = "SELECT student_id FROM student WHERE email = ?";
            $student_stmt = mysqli_prepare($conn, $student_sql);
            mysqli_stmt_bind_param($student_stmt, "s", $email);
            mysqli_stmt_execute($student_stmt);
            $student_result = mysqli_stmt_get_result($student_stmt);
            
            if (mysqli_num_rows($student_result) > 0) {
                $student = mysqli_fetch_assoc($student_result);
                $response['student_id'] = $student['student_id'];
            } else {
                $response['success'] = false;
                $response['message'] = "Student record not found.";
            }
        } else if ($account_type === 'instructor') {
            // Get instructor ID
            $instructor_sql = "SELECT instructor_id FROM instructor WHERE email = ?";
            $instructor_stmt = mysqli_prepare($conn, $instructor_sql);
            mysqli_stmt_bind_param($instructor_stmt, "s", $email);
            mysqli_stmt_execute($instructor_stmt);
            $instructor_result = mysqli_stmt_get_result($instructor_stmt);
            
            if (mysqli_num_rows($instructor_result) > 0) {
                $instructor = mysqli_fetch_assoc($instructor_result);
                $response['instructor_id'] = $instructor['instructor_id'];
            } else {
                $response['success'] = false;
                $response['message'] = "Instructor record not found.";
            }
        }
        // For admin, no additional info needed
    } else {
        $response['message'] = "Invalid email or password.";
    }
} else {
    $response['message'] = "Missing email or password.";
}

// Send response
echo json_encode($response);
?>