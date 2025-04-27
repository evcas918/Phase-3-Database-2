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
if (isset($data['student_id']) && isset($data['todo_title']) && isset($data['due_date'])) {
    $student_id = $data['student_id'];
    $todo_title = $data['todo_title'];
    $todo_description = isset($data['todo_description']) ? $data['todo_description'] : '';
    $due_date = $data['due_date'];
    
    // Insert new todo item
    $sql = "INSERT INTO student_todo (student_id, todo_title, todo_description, due_date, is_completed)
            VALUES (?, ?, ?, ?, 0)";
    
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "ssss", $student_id, $todo_title, $todo_description, $due_date);
    
    if (mysqli_stmt_execute($stmt)) {
        $response['success'] = true;
        $response['todo_id'] = mysqli_insert_id($conn);
        $response['message'] = "Todo item added successfully.";
    } else {
        $response['message'] = "Error adding todo item: " . mysqli_error($conn);
    }
    
} else {
    $response['message'] = "Missing required fields.";
}

// Send response
echo json_encode($response);
?>