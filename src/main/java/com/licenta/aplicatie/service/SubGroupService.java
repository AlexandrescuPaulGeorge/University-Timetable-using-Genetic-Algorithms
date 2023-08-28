package com.licenta.aplicatie.service;

import com.licenta.aplicatie.models.SubGroup;
import com.licenta.aplicatie.repository.GroupRepository;
import com.licenta.aplicatie.repository.SubGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubGroupService {

    private final SubGroupRepository subGroupRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public SubGroupService(SubGroupRepository subGroupRepository, GroupRepository groupRepository) {
        this.subGroupRepository = subGroupRepository;
        this.groupRepository = groupRepository;
    }

    public SubGroup addSubGroup(Integer groupId, SubGroup subgroup) {
        return groupRepository.findById(groupId).map(group -> {
            subgroup.setName(group.getName() + subgroup.getName()); // append group number to subgroup name
            subgroup.setGroup(group);
            return subGroupRepository.save(subgroup);
        }).orElseThrow(()-> new RuntimeException("No group with matching id found"));
    }

    public List<SubGroup> getAllSubGroups(Integer groupId) {
        return subGroupRepository.findByGroupId(groupId);
    }

    public Optional<SubGroup> getSubGroup(Integer groupId, Integer subGroupId) {
        return subGroupRepository.findByIdAndGroupId(subGroupId, groupId);
    }
    public void deleteSubGroup(Integer groupId, Integer subGroupId) {
        subGroupRepository.deleteByIdAndGroupId(subGroupId, groupId);
    }
    public List<SubGroup> getAllSubGroupsByDepartmentId(Integer departmentId) {
        return subGroupRepository.findByDepartmentId(departmentId);
    }
}
