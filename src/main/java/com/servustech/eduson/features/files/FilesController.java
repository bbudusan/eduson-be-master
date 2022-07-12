package com.servustech.eduson.features.files;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.InvalidConfirmTokenException;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.utils.filestorage.FileStorage;
import com.servustech.eduson.utils.filestorage.FileStorageException;
import com.servustech.eduson.utils.filestorage.FileStorageUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@RestController
@RequestMapping("/files")
public class FilesController {
	
	private final FileStorage fileStorage;
	private final FileService fileService;
	
	@GetMapping("/download")
	public void downloadFile(@RequestParam("key") String key,
							 @RequestParam("filename") String filename, HttpServletResponse response) throws
																									  IOException {
		final HttpHeaders responseHeaders = new HttpHeaders();
		
		try (InputStream inputStream = fileStorage.retrieve(key)) {
			FileStorageUtils.setResponseContentType(response, responseHeaders, filename);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			throw new NotFoundException("file-not-found");
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping
	public void delete(@RequestParam("key") String key) {
		this.fileStorage.delete(key);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping(path="/upload", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) throws IOException {
		try {
			var image = fileService.saveWithFile(file);
			var response = new FileUploadResponseDto();
			response.setImageUrl(fileService.getUrl(image.getId()));
			response.setStatus(true);
			response.setOriginalName(image.getOriginalFilename());
			response.setGeneratedName(image.getPath());
			response.setMsg("Image "+image.getOriginalFilename()+" uploaded successfully"); // TODO
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			throw new FileStorageException("file-upload-error " + e.getLocalizedMessage());
		}
	}
	
	@GetMapping("/download/url/{fileId}")
	public ResponseEntity<?> getDownloadLink(@PathVariable  Long fileId){
		return ResponseEntity.ok(fileService.getUrl(fileId));
	}
	
}
