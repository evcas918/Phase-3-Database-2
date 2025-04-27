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

// Check if required fields are provided
if (isset($data['student_id']) && isset($data['event_id'])) {
    $student_id = $data['student_id'];
    $event_id = $data['event_id'];
    
    // Check if already in todo list
    $check_sql = "SELECT * FROM student_todo 
                 WHERE student_id = ? AND event_id = ?";
    
    $check_stmt = mysqli_prepare($conn, $check_sql);
    mysqli_stmt_bind_param($check_stmt, "si", $student_id, $event_id);
    mysqli_stmt_execute($check_stmt);
    $check_result = mysqli_stmt_get_result($check_stmt);
    
    if (mysqli_num_rows($check_result) > 0) {
        $response['message'] = "This event is already in your to-do list.";
    } else {
        // Get event details
        $event_sql = "SELECT * FROM course_event WHERE event_id = ?";
        $event_stmt = mysqli_prepare($conn, $event_sql);
        mysqli_stmt_bind_param($event_stmt, "i", $event_id);
        mysqli_stmt_execute($event_stmt);
        $event_result = mysqli_stmt_get_result($event_stmt);
        
        if (mysqli_num_rows($event_result) > 0) {
            $event = mysqli_fetch_assoc($event_result);
            
            // Add to student_todo
            $todo_sql = "INSERT INTO student_todo 
                        (student_id, event_id, todo_title, todo_description, due_date, is_completed)
                        VALUES (?, ?, ?, ?, ?, 0)";
            
            $todo_stmt = mysqli_prepare($conn, $todo_sql);
            mysqli_stmt_bind_param($todo_stmt, "sisss", 
                                  $student_id, $event_id, 
                                  $event['event_title'], $event['event_description'], 
                                  $event['event_date']);
            
            if (mysqli_stmt_execute($todo_stmt)) {
                $response['success'] = true;
                $response['todo_id'] = mysqli_insert_id($conn);
                $response['message'] = "Event added to your to-do list.";
            } else {
                $response['message'] = "Error adding event to to-do list: " . mysqli_error($conn);
            }
        } else {
            $response['message'] = "Event not found.";
        }
    }
} else {
    $response['message'] = "Missing required fields.";
}

// Send response
echo json_encode($response);
?>