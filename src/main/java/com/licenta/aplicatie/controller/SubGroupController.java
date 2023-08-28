package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.models.SubGroup;
import com.licenta.aplicatie.service.SubGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/subgroups")
public class SubGroupController {

    private final SubGroupService subGroupService;

    @Autowired
    public SubGroupController(SubGroupService subGroupService) {
        this.subGroupService = subGroupService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubGroup> addSubGroup(@PathVariable Integer groupId, @RequestBody SubGroup subgroup) {
        SubGroup addedSubGroup = subGroupService.addSubGroup(groupId, subgroup);
        return new ResponseEntity<>(addedSubGroup, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<SubGroup>> getAllSubGroups(@PathVariable Integer groupId) {
        List<SubGroup> subGroups = subGroupService.getAllSubGroups(groupId);
        return new ResponseEntity<>(subGroups, HttpStatus.OK);
    }

    @GetMapping("/{subGroupId}")
    public ResponseEntity<SubGroup> getSubGroup(@PathVariable Integer groupId, @PathVariable Integer subGroupId) {
        SubGroup subGroup = subGroupService.getSubGroup(groupId, subGroupId)
                .orElseThrow(() -> new RuntimeException("No subgroup with matching id found"));
        return new ResponseEntity<>(subGroup, HttpStatus.OK);
    }

    @DeleteMapping("/{subGroupId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteSubGroup(@PathVariable Integer groupId, @PathVariable Integer subGroupId) {
        subGroupService.deleteSubGroup(groupId, subGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<SubGroup>> getAllSubGroupsByDepartmentId(@PathVariable Integer departmentId) {
        List<SubGroup> subGroups = subGroupService.getAllSubGroupsByDepartmentId(departmentId);
        return new ResponseEntity<>(subGroups, HttpStatus.OK);
    }
}
