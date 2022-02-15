package com.formacionspring.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.formacionspring.app.entity.Alumno;
import com.formacionspring.app.service.AlumnoService;


@RestController
@RequestMapping("/api")
public class AlumnoController {

	@Autowired
	private AlumnoService servicio;

	@GetMapping("/alumnos")
	public List<Alumno> alumno() {
		return servicio.findAll();
	}

	@GetMapping("/alumnos/{id}")
	public ResponseEntity<?> alumnoShow(@PathVariable Long id) {
		Alumno alumno = null;
		Map<String, Object> response = new HashMap<>();

		try {
			alumno = servicio.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta a la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		if (alumno == null) {
			response.put("mensaje", "El usuario ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}


	@PostMapping("/alumnos")
	public ResponseEntity<?> saveAlumno(@RequestBody Alumno alumno) {
		Alumno alumnoNew = null;
		Map<String, Object> response = new HashMap<>();

		try {

			alumnoNew = servicio.save(alumno);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar insert a la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		response.put("mensaje", "El usuario ha sido creado con éxito!");
		response.put("usuario", alumnoNew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}

	@PutMapping("/alumnos/{id}")
	public ResponseEntity<?> updateAlumno(@RequestBody Alumno alumno, @PathVariable Long id) {

		Alumno alumnoActual = servicio.findById(id);

		Map<String, Object> response = new HashMap<>();

		try {

			if (alumnoActual == null) {
				response.put("mensaje",
						"El usuario ID: ".concat(id.toString().concat(" no existe en la base de datos")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

			}

			alumnoActual.setNombre(alumno.getNombre());
			alumnoActual.setApellido(alumno.getApellido());
			alumnoActual.setCp(alumno.getCp());
			alumnoActual.setDireccion(alumno.getDireccion());
			alumnoActual.setDni(alumno.getDni());
			alumnoActual.setEmail(alumno.getEmail());
			alumnoActual.setTelefono(alumno.getTelefono());
//			usuarioActual.setCreateAt(usuario.getCreateAt());

			servicio.save(alumnoActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar la base de datos");
			response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		response.put("mensaje", "El alumno ha sido actualizado con éxito!");
		response.put("alumno", alumnoActual);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}


	//Delete method
	@DeleteMapping("/alumnos/{id}")
	public ResponseEntity<?> deleteAlumno(@PathVariable Long id) {
		Alumno alumnoBorrado = servicio.findById(id);
		Map<String, Object> response = new HashMap<>();

		if (alumnoBorrado == null) {
			response.put("mensaje", "El usuario ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		} else {

			try {

				servicio.delete(id);

				String nombreFotoAnterior = alumnoBorrado.getImagen();

				if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {

					Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
					File archivoFotoAnterior = rutaFotoAnterior.toFile();

					if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
						archivoFotoAnterior.delete();
					}

				}

			} catch (DataAccessException e) {
				response.put("mensaje", "Error al borrar un usuario la base de datos");
				response.put("error", e.getMessage().concat("_ ").concat(e.getMostSpecificCause().getMessage()));

				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

			}
			response.put("mensaje", "El usuario ha sido actualizado con éxito!");
			response.put("usuario", alumnoBorrado);

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

		}
	}

	@PostMapping("/alumno/upload")
	public ResponseEntity<?> uploadImagen(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();
		Alumno alumno = servicio.findById(id);

		if (!archivo.isEmpty()) {
//			String nombreArchivo = archivo.getOriginalFilename();
			String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();

			try {

				Files.copy(archivo.getInputStream(), rutaArchivo);

			} catch (IOException e) {

				response.put("mensaje", "Error al subir la imagen del cliente.");
				response.put("error", e.getMessage().concat("_ ").concat(e.getCause().getMessage()));

				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

			}

			String nombreFotoAnterior = alumno.getImagen();

			if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {

				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();

				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}

			}

			alumno.setImagen(nombreArchivo);

			servicio.save(alumno);
			response.put("usuario", alumno);
			response.put("mensaje", "subida correcta de imagen " + nombreArchivo);

		} else {
			response.put("mensaje", "Archivo vacio");

		}
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

	}
}

