package com.example.Web.repo;

import com.example.Web.models.Tests;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TestsRepo extends CrudRepository<Tests, Integer> {
    Optional<Tests> findById(Integer id);

}
