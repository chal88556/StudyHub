package com.example.WebChat.Service;

import com.example.WebChat.Entity.Course;
import com.example.WebChat.Repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course findCourseById(String courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public Course findCourseByName(String courseName) {
        return courseRepository.findByCourseName(courseName);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }
}
