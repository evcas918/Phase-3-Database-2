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
    
    // Fetch course events for courses the student is enrolled in
    $sql = "SELECT ce.*, c.course_name,
                 CASE WHEN st.todo_id IS NOT NULL THEN 1 ELSE 0 END as in_todo_list
           FROM course_event ce
           JOIN course c ON ce.course_id = c.course_id
           JOIN take t ON ce.course_id = t.course_id 
                AND ce.section_id = t.section_id
                AND ce.semester = t.semester
                AND ce.year = t.year
           LEFT JOIN student_todo st ON ce.event_id = st.event_id AND st.student_id = ?
           WHERE t.student_id = ?
           ORDER BY ce.event_date";
    
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "ss", $student_id, $student_id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    
    $events = array();
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_assoc($result)) {
            $events[] = $row;
        }
    }
    
    // Set response
    $response['success'] = true;
    $response['events'] = $events;
    
} else {
    $response['message'] = "Student ID is required.";
}

// Send response
echo json_encode($response);
?>