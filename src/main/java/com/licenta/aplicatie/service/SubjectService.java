package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.SubjectRequest;
import com.licenta.aplicatie.auth.SubjectResponse;
import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.RoomRepository;
import com.licenta.aplicatie.repository.StudyYearRepository;
import com.licenta.aplicatie.repository.SubjectRepository;
import com.licenta.aplicatie.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudyYearRepository studyYearRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository, StudyYearRepository studyYearRepository, TeacherRepository teacherRepository, RoomRepository roomRepository) {
        this.subjectRepository = subjectRepository;
        this.studyYearRepository = studyYearRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
    }

    public Subject addSubject(Integer studyYearId, SubjectRequest subjectRequest) {
        StudyYear studyYear = studyYearRepository.findById(studyYearId)
                .orElseThrow(() -> new RuntimeException("No StudyYear with matching id found"));

        Optional<Subject> existingSubject = subjectRepository.findByAcronymAndStudyYear(subjectRequest.getAcronim(), studyYear);
        if (existingSubject.isPresent()) {
            throw new RuntimeException("A subject with this acronym already exists in this study year");
        }

        Subject subject = new Subject();
        subject.setName(subjectRequest.getName());
        subject.setAcronym(subjectRequest.getAcronim());
        subject.setStudyYear(studyYear);

        List<Teacher> teachers = subjectRequest.getTeacherIds().stream()
                .map(teacherId -> {
                    Teacher teacher = teacherRepository.findById(teacherId)
                            .orElseThrow(() -> new RuntimeException("No Teacher with id " + teacherId + " found"));
                    teacher.getSubjects().add(subject);
                    teacherRepository.save(teacher);
                    return teacher;
                })
                .collect(Collectors.toList());
        subject.setTeachers(teachers);

        Set<TeachingStyle> teachingStyles = subjectRequest.getTeachingStyles().stream()
                .map(style -> {
                    try {
                        return TeachingStyle.valueOf(style.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid teaching style: " + style);
                    }
                })
                .collect(Collectors.toSet());
        subject.setTeachingStyles(teachingStyles);


        return subjectRepository.save(subject);
    }

    public List<SubjectResponse> getSubjectNamesByStudyYearId(Integer studyYearId) {
        return subjectRepository.findByStudyYearId(studyYearId)
                .stream()
                .map(subject -> new SubjectResponse(subject.getName()))
                .collect(Collectors.toList());
    }
}


