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

// Check if instructor_id is provided
if (isset($data['instructor_id'])) {
    $instructor_id = $data['instructor_id'];
    $current_only = isset($data['current_only']) ? $data['current_only'] : false;
    
    // Current semester and year
    $current_semester = "Spring";
    $current_year = 2025;
    
    // Base query for sections
    $query = "SELECT s.course_id, s.section_id, s.semester, s.year, 
                    c.course_name, c.credits,
                    cl.building, cl.room_number,
                    ts.day, ts.start_time, ts.end_time,
                    (SELECT COUNT(*) FROM take t
                    WHERE t.course_id = s.course_id AND t.section_id = s.section_id
                    AND t.semester = s.semester AND t.year = s.year) as enrolled_students,
                    CASE
                        WHEN s.semester = ? AND s.year = ? THEN 1
                        ELSE 0
                    END as is_current
              FROM section s
              JOIN course c ON s.course_id = c.course_id
              LEFT JOIN classroom cl ON s.classroom_id = cl.classroom_id
              LEFT JOIN time_slot ts ON s.time_slot_id = ts.time_slot_id
              WHERE s.instructor_id = ?";
    
    // Add condition for current semester only if requested
    if ($current_only) {
        $query .= " AND s.semester = ? AND s.year = ?";
    }
    
    // Add ordering
    $query .= " ORDER BY s.year DESC, FIELD(s.semester, 'Spring', 'Summer', 'Fall', 'Winter'), s.course_id";
    
    // Prepare and execute query
    $stmt = mysqli_prepare($conn, $query);
    
    if ($current_only) {
        mysqli_stmt_bind_param($stmt, "siiss", $current_semester, $current_year, $instructor_id, $current_semester, $current_year);
    } else {
        mysqli_stmt_bind_param($stmt, "sis", $current_semester, $current_year, $instructor_id);
    }
    
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    
    if (mysqli_num_rows($result) > 0) {
        $sections = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            // Format times
            if ($row['start_time'] && $row['end_time']) {
                $row['start_time'] = date("g:i A", strtotime($row['start_time']));
                $row['end_time'] = date("g:i A", strtotime($row['end_time']));
            }
            
            // Convert is_current to boolean
            $row['is_current'] = $row['is_current'] == 1;
            
            $sections[] = $row;
        }
        
        $response['success'] = true;
        $response['sections'] = $sections;
    } else {
        $response['success'] = true;
        $response['sections'] = array();
        $response['message'] = "No sections found for this instructor.";
    }
} else {
    $response['message'] = "Instructor ID is required.";
}

// Send response
echo json_encode($response);
?>