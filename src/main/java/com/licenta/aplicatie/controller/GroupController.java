package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.GroupResponse;
import com.licenta.aplicatie.models.Group;
import com.licenta.aplicatie.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyyears/{studyYearId}/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Group> addGroup(@PathVariable Integer studyYearId, @RequestBody Group group) {
        Group addedGroup = groupService.addGroup(studyYearId, group);
        return new ResponseEntity<>(addedGroup, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GroupResponse>> getGroupNamesByStudyYearId(@PathVariable Integer studyYearId) {
        List<GroupResponse> groupNames = groupService.getGroupNamesByStudyYearId(studyYearId);
        return new ResponseEntity<>(groupNames, HttpStatus.OK);
    }
}
