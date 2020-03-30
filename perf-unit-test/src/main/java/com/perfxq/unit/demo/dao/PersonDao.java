package com.perfxq.unit.demo.dao;

import com.perfxq.unit.demo.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonDao extends JpaRepository<Person, Integer> {
    @Query(nativeQuery = true,value = "SELECT * FROM Person where first_name LIKE %:name% or last_name like %:name%")
    List<Person> find(@Param("name") String name);

    @Query(nativeQuery = true,value = "SELECT * FROM Person")
    List<Person> selectAll();

    @Query(nativeQuery = true,value = "SELECT * FROM Person where title LIKE %:title%")
    List<Person> queryByTitle(@Param("title") String title);

}
