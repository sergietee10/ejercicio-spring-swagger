package com.formacionspring.app.service;

import java.util.List;
import com.formacionspring.app.entity.Alumno;

public interface AlumnoService {

	public List<Alumno> findAll();

	public Alumno findById(Long id);

	public Alumno save(Alumno usuario);

	public void delete(Long id);

}
