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
    
    // Query to get all courses (past and current)
    $query = "SELECT t.course_id, t.section_id, t.semester, t.year, t.grade,
                    c.course_name, c.credits, i.instructor_name,
                    CASE
                        WHEN t.grade IS NULL AND t.semester = 'Spring' AND t.year = 2025 THEN 'current'
                        WHEN t.grade IS NOT NULL THEN 'completed'
                        ELSE 'other'
                    END as course_status
              FROM take t
              JOIN course c ON t.course_id = c.course_id
              JOIN section s ON t.course_id = s.course_id AND t.section_id = s.section_id
                    AND t.semester = s.semester AND t.year = s.year
              LEFT JOIN instructor i ON s.instructor_id = i.instructor_id
              WHERE t.student_id = ?
              ORDER BY t.year DESC, FIELD(t.semester, 'Spring', 'Summer', 'Fall', 'Winter'), t.course_id";
    
    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, "s", $student_id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    
    if (mysqli_num_rows($result) > 0) {
        $courses = array();
        $current_courses = array();
        $past_courses = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            if ($row['course_status'] == 'current') {
                $current_courses[] = $row;
            } else {
                $past_courses[] = $row;
            }
        }
        
        $response['success'] = true;
        $response['current_courses'] = $current_courses;
        $response['past_courses'] = $past_courses;
    } else {
        $response['message'] = "No courses found for this student.";
    }
} else {
    $response['message'] = "Student ID is required.";
}

// Send response
echo json_encode($response);
?>