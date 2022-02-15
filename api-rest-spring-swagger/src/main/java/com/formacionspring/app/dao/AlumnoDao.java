package com.formacionspring.app.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.formacionspring.app.entity.Alumno;

@Repository
public interface AlumnoDao extends CrudRepository<Alumno, Long> {

}
