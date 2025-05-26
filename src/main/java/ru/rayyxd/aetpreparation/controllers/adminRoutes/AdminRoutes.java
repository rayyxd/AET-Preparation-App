package ru.rayyxd.aetpreparation.controllers.adminRoutes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.rayyxd.aetpreparation.services.ModuleService;
import ru.rayyxd.aetpreparation.sqlEntities.Module;

@RestController
public class AdminRoutes {
	
	@Autowired
	private ModuleService moduleService;
	
	@PostMapping("/admin/module")
	public ResponseEntity<?> createSqlModule(@RequestBody Module module){
		
		moduleService.createModule(module);
		
		return new ResponseEntity<>("Created: " + module, HttpStatus.CREATED); 
	}
	
	
}
