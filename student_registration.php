<?php
// Include database config
include 'config.php';

// Set response content type
header('Content-Type: application/json');

// Initialize response array
$response = array();
$response['success'] = false;

// Check if all required fields are provided
if (isset($_POST['student_id']) && isset($_POST['name']) && isset($_POST['email']) && 
    isset($_POST['password']) && isset($_POST['department']) && isset($_POST['student_type'])) {
    
    // Get parameters
    $student_id = $_POST['student_id'];
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $department = $_POST['department'];
    $student_type = $_POST['student_type'];
    
    // Start transaction
    mysqli_begin_transaction($conn);
    
    try {
        // Check if student already exists
        $check_sql = "SELECT * FROM student WHERE student_id = ? OR email = ?";
        $check_stmt = mysqli_prepare($conn, $check_sql);
        mysqli_stmt_bind_param($check_stmt, "ss", $student_id, $email);
        mysqli_stmt_execute($check_stmt);
        $check_result = mysqli_stmt_get_result($check_stmt);
        
        if (mysqli_num_rows($check_result) > 0) {
            throw new Exception("Student ID or email already exists.");
        }
        
        // Check if department exists
        $dept_sql = "SELECT * FROM department WHERE dept_name = ?";
        $dept_stmt = mysqli_prepare($conn, $dept_sql);
        mysqli_stmt_bind_param($dept_stmt, "s", $department);
        mysqli_stmt_execute($dept_stmt);
        $dept_result = mysqli_stmt_get_result($dept_stmt);
        
        if (mysqli_num_rows($dept_result) == 0) {
            // Department doesn't exist, insert it
            $insert_dept_sql = "INSERT INTO department (dept_name, location) VALUES (?, 'TBD')";
            $insert_dept_stmt = mysqli_prepare($conn, $insert_dept_sql);
            mysqli_stmt_bind_param($insert_dept_stmt, "s", $department);
            
            if (!mysqli_stmt_execute($insert_dept_stmt)) {
                throw new Exception("Error creating department: " . mysqli_error($conn));
            }
        }
        
        // Insert into account table
        $account_sql = "INSERT INTO account (email, password, type) VALUES (?, ?, 'student')";
        $account_stmt = mysqli_prepare($conn, $account_sql);
        mysqli_stmt_bind_param($account_stmt, "ss", $email, $password);
        
        if (!mysqli_stmt_execute($account_stmt)) {
            throw new Exception("Error creating account: " . mysqli_error($conn));
        }
        
        // Insert into student table
        $student_sql = "INSERT INTO student (student_id, name, email, dept_name) VALUES (?, ?, ?, ?)";
        $student_stmt = mysqli_prepare($conn, $student_sql);
        mysqli_stmt_bind_param($student_stmt, "ssss", $student_id, $name, $email, $department);
        
        if (!mysqli_stmt_execute($student_stmt)) {
            throw new Exception("Error creating student record: " . mysqli_error($conn));
        }
        
        // Insert into specific student type table
        if ($student_type == "undergraduate") {
            // Default values for new undergraduate
            $total_credits = 0;
            $class_standing = "Freshman";
            
            $undergrad_sql = "INSERT INTO undergraduate (student_id, total_credits, class_standing) VALUES (?, ?, ?)";
            $undergrad_stmt = mysqli_prepare($conn, $undergrad_sql);
            mysqli_stmt_bind_param($undergrad_stmt, "sis", $student_id, $total_credits, $class_standing);
            
            if (!mysqli_stmt_execute($undergrad_stmt)) {
                throw new Exception("Error creating undergraduate record: " . mysqli_error($conn));
            }
        } else if ($student_type == "master") {
            // Default values for new master student
            $total_credits = 0;
            
            $master_sql = "INSERT INTO master (student_id, total_credits) VALUES (?, ?)";
            $master_stmt = mysqli_prepare($conn, $master_sql);
            mysqli_stmt_bind_param($master_stmt, "si", $student_id, $total_credits);
            
            if (!mysqli_stmt_execute($master_stmt)) {
                throw new Exception("Error creating master record: " . mysqli_error($conn));
            }
        } else if ($student_type == "phd") {
            $phd_sql = "INSERT INTO PhD (student_id, qualifier, proposal_defence_date, dissertation_defence_date) VALUES (?, NULL, NULL, NULL)";
            $phd_stmt = mysqli_prepare($conn, $phd_sql);
            mysqli_stmt_bind_param($phd_stmt, "s", $student_id);
            
            if (!mysqli_stmt_execute($phd_stmt)) {
                throw new Exception("Error creating PhD record: " . mysqli_error($conn));
            }
        } else {
            throw new Exception("Invalid student type: " . $student_type);
        }
        
        // Commit transaction
        mysqli_commit($conn);
        
        // Set success response
        $response['success'] = true;
        $response['message'] = "Student account created successfully!";
        
    } catch (Exception $e) {
        // Roll back transaction on error
        mysqli_rollback($conn);
        $response['message'] = $e->getMessage();
    }
} else {
    $response['message'] = "Missing required fields";
}

// Send response
echo json_encode($response);
?>