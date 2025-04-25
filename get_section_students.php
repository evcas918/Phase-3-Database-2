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

// Check if section_identifier is provided
if (isset($data['section_identifier'])) {
    // Parse section identifier
    $section_parts = explode('|', $data['section_identifier']);
    
    if (count($section_parts) === 4) {
        $course_id = $section_parts[0];
        $section_id = $section_parts[1];
        $semester = $section_parts[2];
        $year = $section_parts[3];
        
        // Fetch students for this section
        $query = "SELECT t.*, s.name, s.email,
                        CASE
                            WHEN u.student_id IS NOT NULL THEN 'undergraduate'
                            WHEN m.student_id IS NOT NULL THEN 'master'
                            WHEN p.student_id IS NOT NULL THEN 'phd'
                            ELSE NULL
                        END as student_type
                FROM take t
                JOIN student s ON t.student_id = s.student_id
                LEFT JOIN undergraduate u ON t.student_id = u.student_id
                LEFT JOIN master m ON t.student_id = m.student_id
                LEFT JOIN PhD p ON t.student_id = p.student_id
                WHERE t.course_id = ? AND t.section_id = ?
                AND t.semester = ? AND t.year = ?
                ORDER BY s.name";
        
        $stmt = mysqli_prepare($conn, $query);
        mysqli_stmt_bind_param($stmt, "sssi", $course_id, $section_id, $semester, $year);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        
        if (mysqli_num_rows($result) > 0) {
            $students = array();
            
            while ($row = mysqli_fetch_assoc($result)) {
                $students[] = $row;
            }
            
            $response['success'] = true;
            $response['students'] = $students;
        } else {
            $response['success'] = true;
            $response['students'] = array();
            $response['message'] = "No students enrolled in this section.";
        }
    } else {
        $response['message'] = "Invalid section identifier format.";
    }
} else {
    $response['message'] = "Section identifier is required.";
}

// Send response
echo json_encode($response);
?>