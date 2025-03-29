package com.example.WebChat.Service;

import com.example.WebChat.Entity.Student;
import com.example.WebChat.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    public Student saveStudent(Student student) {
        return studentRepo.save(student);
    }

    public Student findStudentByEmail(String email) {
        return studentRepo.findByEmail(email);
    }

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
}
