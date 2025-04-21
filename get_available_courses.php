<?php
// Include database config
include 'config.php';

// Set response content type
header('Content-Type: application/json');

// Initialize response array
$response = array();
$response['success'] = false;

// Current semester and year (hardcoded for now)
$current_semester = "Spring";
$current_year = 2025;

// Get all courses offered in the current semester
$query = "SELECT c.course_id, c.course_name, c.credits, 
                 s.section_id, s.instructor_id, i.instructor_name,
                 t.day, t.start_time, t.end_time,
                 cl.building, cl.room_number,
                 (SELECT COUNT(*) FROM take 
                  WHERE course_id = c.course_id 
                  AND section_id = s.section_id 
                  AND semester = ? 
                  AND year = ?) as enrolled_students
          FROM course c
          JOIN section s ON c.course_id = s.course_id
          JOIN instructor i ON s.instructor_id = i.instructor_id
          JOIN time_slot t ON s.time_slot_id = t.time_slot_id
          JOIN classroom cl ON s.classroom_id = cl.classroom_id
          WHERE s.semester = ? AND s.year = ?
          ORDER BY c.course_id, s.section_id";

$stmt = mysqli_prepare($conn, $query);
mysqli_stmt_bind_param($stmt, "siss", $current_semester, $current_year, $current_semester, $current_year);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

if (mysqli_num_rows($result) > 0) {
    $courses = array();
    
    while ($row = mysqli_fetch_assoc($result)) {
        // Add is_full flag based on enrollment count
        $row['is_full'] = ($row['enrolled_students'] >= 15) ? true : false;
        $row['available_seats'] = 15 - $row['enrolled_students'];
        $courses[] = $row;
    }
    
    $response['success'] = true;
    $response['courses'] = $courses;
} else {
    $response['message'] = "No courses found for current semester.";
}

// Send response
echo json_encode($response);
?>