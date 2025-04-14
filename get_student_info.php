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
    
    // Check if student_id is provided
    if (isset($data['student_id'])) {
        $student_id = $data['student_id'];
        
        // Get student information
        $query = "SELECT s.*, 
                    CASE
                        WHEN u.student_id IS NOT NULL THEN 'undergraduate'
                        WHEN m.student_id IS NOT NULL THEN 'master'
                        WHEN p.student_id IS NOT NULL THEN 'phd'
                        ELSE NULL
                    END as student_type,
                    u.total_credits as undergrad_credits, 
                    u.class_standing,
                    m.total_credits as master_credits
                FROM student s
                LEFT JOIN undergraduate u ON s.student_id = u.student_id
                LEFT JOIN master m ON s.student_id = m.student_id
                LEFT JOIN PhD p ON s.student_id = p.student_id
                WHERE s.student_id = ?";
        
        $stmt = mysqli_prepare($conn, $query);
        mysqli_stmt_bind_param($stmt, "s", $student_id);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        
        if (mysqli_num_rows($result) > 0) {
            $student = mysqli_fetch_assoc($result);
            
            // Set response values
            $response['success'] = true;
            $response['name'] = $student['name'];
            $response['dept_name'] = $student['dept_name'];
            $response['student_type'] = $student['student_type'];
            
            // Add type-specific information
            if ($student['student_type'] === 'undergraduate') {
                $response['credits'] = $student['undergrad_credits'];
                $response['class_standing'] = $student['class_standing'];
            } else if ($student['student_type'] === 'master') {
                $response['credits'] = $student['master_credits'];
            } else if ($student['student_type'] === 'phd') {
                // Get PhD-specific info if needed
                $phd_query = "SELECT qualifier, proposal_defence_date, dissertation_defence_date 
                            FROM PhD WHERE student_id = ?";
                $phd_stmt = mysqli_prepare($conn, $phd_query);
                mysqli_stmt_bind_param($phd_stmt, "s", $student_id);
                mysqli_stmt_execute($phd_stmt);
                $phd_result = mysqli_stmt_get_result($phd_stmt);
                
                if (mysqli_num_rows($phd_result) > 0) {
                    $phd_info = mysqli_fetch_assoc($phd_result);
                    $response['qualifier'] = $phd_info['qualifier'];
                    $response['proposal_date'] = $phd_info['proposal_defence_date'];
                    $response['dissertation_date'] = $phd_info['dissertation_defence_date'];
                }
            }
        } else {
            $response['message'] = "Student not found.";
        }
    } else {
        $response['message'] = "Student ID is required.";
    }
} else {
    $response['message'] = "Invalid request method.";
}

// Send response
echo json_encode($response);
?>