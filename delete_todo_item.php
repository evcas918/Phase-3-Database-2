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

// Check if todo_id is provided
if (isset($data['todo_id'])) {
    $todo_id = $data['todo_id'];
    
    // Delete todo item
    $sql = "DELETE FROM student_todo WHERE todo_id = ?";
    
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "i", $todo_id);
    
    if (mysqli_stmt_execute($stmt)) {
        $response['success'] = true;
        $response['message'] = "Todo item deleted successfully.";
    } else {
        $response['message'] = "Error deleting todo item: " . mysqli_error($conn);
    }
    
} else {
    $response['message'] = "Todo ID is required.";
}

// Send response
echo json_encode($response);
?>