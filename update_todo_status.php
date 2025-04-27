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
if (isset($data['todo_id']) && isset($data['is_completed'])) {
    $todo_id = $data['todo_id'];
    $is_completed = $data['is_completed'];
    
    // Update todo status
    $sql = "UPDATE student_todo SET is_completed = ? WHERE todo_id = ?";
    
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "ii", $is_completed, $todo_id);
    
    if (mysqli_stmt_execute($stmt)) {
        $response['success'] = true;
        $response['message'] = "Todo status updated successfully.";
    } else {
        $response['message'] = "Error updating todo status: " . mysqli_error($conn);
    }
    
} else {
    $response['message'] = "Missing required fields.";
}

// Send response
echo json_encode($response);
?>