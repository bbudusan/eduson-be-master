package com.servustech.eduson.features.account.lectorTitle;

import com.servustech.eduson.features.account.lectorTitle.dto.LectorTitleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lector/title")
@RequiredArgsConstructor
public class LectorTitleController {
	
	private final LectorTitleService lectorTitleService;
	
	@PostMapping
	public ResponseEntity<?> createLectorTitle(@RequestBody LectorTitleDto lectorTitleDto) {
		
		return ResponseEntity.ok(lectorTitleService.saveTitle(lectorTitleDto));
	}
	
	@DeleteMapping("/{titleId}")
	public void deleteCourse(@PathVariable Long titleId) {
		lectorTitleService.deleteTitle(titleId);
	}
	
	@GetMapping
	public ResponseEntity<?> getAllTitles(){
		return ResponseEntity.ok(lectorTitleService.getAllTitles());
	}
}