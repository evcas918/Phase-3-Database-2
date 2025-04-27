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

// Check if student_id is provided
if (isset($data['student_id'])) {
    $student_id = $data['student_id'];
    
    // Fetch course todos (todos with event_id)
    $course_sql = "SELECT st.*, ce.course_id, c.course_name, ce.event_type
                  FROM student_todo st
                  JOIN course_event ce ON st.event_id = ce.event_id
                  JOIN course c ON ce.course_id = c.course_id
                  WHERE st.student_id = ?
                  ORDER BY st.due_date ASC";
    
    $course_stmt = mysqli_prepare($conn, $course_sql);
    mysqli_stmt_bind_param($course_stmt, "s", $student_id);
    mysqli_stmt_execute($course_stmt);
    $course_result = mysqli_stmt_get_result($course_stmt);
    
    $course_todos = array();
    if (mysqli_num_rows($course_result) > 0) {
        while ($row = mysqli_fetch_assoc($course_result)) {
            $course_todos[] = $row;
        }
    }
    
    // Fetch personal todos (todos without event_id)
    $personal_sql = "SELECT * FROM student_todo 
                    WHERE student_id = ? AND event_id IS NULL
                    ORDER BY due_date ASC";
    
    $personal_stmt = mysqli_prepare($conn, $personal_sql);
    mysqli_stmt_bind_param($personal_stmt, "s", $student_id);
    mysqli_stmt_execute($personal_stmt);
    $personal_result = mysqli_stmt_get_result($personal_stmt);
    
    $personal_todos = array();
    if (mysqli_num_rows($personal_result) > 0) {
        while ($row = mysqli_fetch_assoc($personal_result)) {
            $personal_todos[] = $row;
        }
    }
    
    // Set response
    $response['success'] = true;
    $response['course_todos'] = $course_todos;
    $response['personal_todos'] = $personal_todos;
    
} else {
    $response['message'] = "Student ID is required.";
}

// Send response
echo json_encode($response);
?>