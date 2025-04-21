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

// Check if all required fields are provided
if (isset($data['student_id']) && isset($data['course_id']) && isset($data['section_id'])) {
    
    $student_id = $data['student_id'];
    $course_id = $data['course_id'];
    $section_id = $data['section_id'];
    $semester = "Spring"; // Current semester hardcoded
    $year = 2025; // Current year hardcoded
    
    // Start transaction
    mysqli_begin_transaction($conn);
    
    try {
        // Check if student is already registered for this course section
        $check_sql = "SELECT * FROM take WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
        $check_stmt = mysqli_prepare($conn, $check_sql);
        mysqli_stmt_bind_param($check_stmt, "ssssi", $student_id, $course_id, $section_id, $semester, $year);
        mysqli_stmt_execute($check_stmt);
        $check_result = mysqli_stmt_get_result($check_stmt);
        
        if (mysqli_num_rows($check_result) > 0) {
            throw new Exception("You are already registered for this course section.");
        }
        
        // Check prerequisites
        $prereq_sql = "SELECT prereq_id FROM prereq WHERE course_id = ?";
        $prereq_stmt = mysqli_prepare($conn, $prereq_sql);
        mysqli_stmt_bind_param($prereq_stmt, "s", $course_id);
        mysqli_stmt_execute($prereq_stmt);
        $prereq_result = mysqli_stmt_get_result($prereq_stmt);
        
        while ($prereq = mysqli_fetch_assoc($prereq_result)) {
            $prereq_id = $prereq['prereq_id'];
            
            // Check if student has taken the prerequisite
            $taken_sql = "SELECT * FROM take WHERE student_id = ? AND course_id = ? AND grade IS NOT NULL";
            $taken_stmt = mysqli_prepare($conn, $taken_sql);
            mysqli_stmt_bind_param($taken_stmt, "ss", $student_id, $prereq_id);
            mysqli_stmt_execute($taken_stmt);
            $taken_result = mysqli_stmt_get_result($taken_stmt);
            
            if (mysqli_num_rows($taken_result) == 0) {
                throw new Exception("Prerequisite course " . $prereq_id . " not completed.");
            }
        }
        
        // Check if section is full (max 15 students)
        $count_sql = "SELECT COUNT(*) as student_count FROM take WHERE course_id = ? AND section_id = ? AND semester = ? AND year = ?";
        $count_stmt = mysqli_prepare($conn, $count_sql);
        mysqli_stmt_bind_param($count_stmt, "sssi", $course_id, $section_id, $semester, $year);
        mysqli_stmt_execute($count_stmt);
        $count_result = mysqli_stmt_get_result($count_stmt);
        $count_row = mysqli_fetch_assoc($count_result);
        
        if ($count_row['student_count'] >= 15) {
            throw new Exception("Course section is full.");
        }
        
        // Register student for the course
        $register_sql = "INSERT INTO take (student_id, course_id, section_id, semester, year) VALUES (?, ?, ?, ?, ?)";
        $register_stmt = mysqli_prepare($conn, $register_sql);
        mysqli_stmt_bind_param($register_stmt, "ssssi", $student_id, $course_id, $section_id, $semester, $year);
        
        if (!mysqli_stmt_execute($register_stmt)) {
            throw new Exception("Error registering for course: " . mysqli_error($conn));
        }
        
        // Commit transaction
        mysqli_commit($conn);
        
        // Set success response
        $response['success'] = true;
        $response['message'] = "Successfully registered for the course!";
        
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