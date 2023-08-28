package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.GroupResponse;
import com.licenta.aplicatie.models.Group;
import com.licenta.aplicatie.repository.GroupRepository;
import com.licenta.aplicatie.repository.StudyYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudyYearRepository studyYearRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, StudyYearRepository studyYearRepository) {
        this.groupRepository = groupRepository;
        this.studyYearRepository = studyYearRepository;
    }

    public Group addGroup(Integer studyYearId, Group group) {
        return studyYearRepository.findById(studyYearId)
                .map(studyYear -> {
                    group.setStudyYear(studyYear);
                    return groupRepository.save(group);
                }).orElseThrow(() -> new RuntimeException("No StudyYear with matching id found"));
    }

    public List<GroupResponse> getGroupNamesByStudyYearId(Integer studyYearId) {
        return groupRepository.findByStudyYearId(studyYearId)
                .stream()
                .map(group -> new GroupResponse(group.getName()))
                .collect(Collectors.toList());
    }
}

