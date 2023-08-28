package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.SubGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubGroupRepository extends JpaRepository<SubGroup,Integer> {
    List<SubGroup> findByGroupId(Integer groupId);

    Optional<SubGroup> findByIdAndGroupId(Integer subGroupId, Integer groupId);

    void deleteByIdAndGroupId(Integer subGroupId, Integer groupId);

    @Query("SELECT s FROM SubGroup s WHERE s.group.studyYear.department.id = :departmentId")
    List<SubGroup> findByDepartmentId(Integer departmentId);
}
