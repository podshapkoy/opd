package com.example.Web.repo;

import com.example.Web.models.ExpertsOpinion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpertsOpinionRepo extends CrudRepository<ExpertsOpinion, Integer> {
//    List<ExpertsOpinion> findByOccupationIdOrderByAdjectiveCountDesc(int occupationId);
}
