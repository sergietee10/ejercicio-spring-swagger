package com.formacionspring.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionspring.app.dao.AlumnoDao;
import com.formacionspring.app.entity.Alumno;


@Service
public class AlumnoServiceImpl implements AlumnoService {

	@Autowired
	private AlumnoDao usuarioDao;

	@Override
	@Transactional(readOnly = true)
	public List<Alumno> findAll() {

		return (List<Alumno>) usuarioDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Alumno findById(Long id) {

		return usuarioDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Alumno save(Alumno usuario) {

		return usuarioDao.save(usuario);
	}
	
	@Override
	public void delete(Long id) {

		usuarioDao.deleteById(id);

	}


}