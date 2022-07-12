package com.servustech.eduson.features.general;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.features.permissions.ChangeBeneficiaryDto; // TODO generalize this
import com.servustech.eduson.utils.filestorage.FileStorageUtils;
import com.servustech.eduson.features.files.FileUploadResponseDto;
import com.servustech.eduson.utils.filestorage.FileStorageException;
import com.servustech.eduson.exceptions.CustomException;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;


@RestController
@AllArgsConstructor
@RequestMapping("/assets")
public class AssetController {

//	private final AssetService assetService;

	// private final FileService fileService;
	// private final AuthService authService;
  // private final JwtService jwtService;

	private String getI18nResource(String scope, String lang) {
		if (lang == null) {
			lang = "ro";
		}
		String[] langs = {"ro", "en"};
		if (!Arrays.asList(langs).contains(lang)) {
			throw new NotFoundException("lang-not-found");
		}
		if (scope == null) {
			scope = "";
		} else {
			String[] scopes = {"account", "admin", "auth", "cart", "categories", "footer", "home"};
			if (!Arrays.asList(scopes).contains(scope)) {
				throw new NotFoundException("scope-not-found");
			}
			scope = scope + "/";
		}
		String text = null;
		try {
			InputStream inputStream = new ClassPathResource("classpath:i18n/" + scope + lang + ".json").getInputStream();
			text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			// development case:
			try {
				File file = ResourceUtils.getFile("classpath:i18n/" + scope + lang + ".json");
				text = new String(new FileInputStream(file).readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception ee) {
				return "{}";
			}
		}
		return text;
	}


	@GetMapping("/i18n")
	public void getI18n(
	// public ResponseEntity<?> getI18n(
		@RequestParam(required = false) String lang,
		@RequestParam(required = false) String scope,
		HttpServletResponse response
	)	 {
		final HttpHeaders responseHeaders = new HttpHeaders();

		if (lang == null) {
			lang = "ro";
		}
		String[] langs = {"ro", "en"};
		if (!Arrays.asList(langs).contains(lang)) {
			throw new NotFoundException("lang-not-found");
		}

		if (scope == null) {
			scope = "";
		} else {
			String[] scopes = {"account", "admin", "auth", "cart", "categories", "footer", "home"};
			if (!Arrays.asList(scopes).contains(scope)) {
				throw new NotFoundException("scope-not-found");
			}
			scope = scope + "/";
		}

		try (InputStream inputStream = (new AssetFileStorage()).retrieve(lang + ".json", scope, "i18n/")) {
			FileStorageUtils.setResponseContentType(response, responseHeaders, lang + ".json");
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", lang + ".json"));
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			throw new NotFoundException("translation-file-not-found");
		}

//		return ResponseEntity.ok(ChangeBeneficiaryDto.builder().response(getI18nResource(scope, lang)).build());
	}

	public static boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping(path="/i18n", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> setI18n(
		@RequestPart MultipartFile file,
		@RequestPart I18nDto i18nDto
	) {

		String filename = file.getOriginalFilename();

		if (i18nDto.getLang() == null) {
			i18nDto.setLang("ro");
		}
		String[] langs = {"ro", "en"};
		if (!Arrays.asList(langs).contains(i18nDto.getLang())) {
			throw new NotFoundException("lang-not-found");
		}

		if (!filename.equals(i18nDto.getLang() + ".json")) {
			throw new NotFoundException("filename-error");
		}

		if (i18nDto.getScope() == null || i18nDto.getScope().equals("")) {
			i18nDto.setScope("");
		} else {
			String[] scopes = {"account", "admin", "auth", "cart", "categories", "footer", "home"};
			if (!Arrays.asList(scopes).contains(i18nDto.getScope())) {
				throw new NotFoundException("scope-not-found");
			}
			i18nDto.setScope(i18nDto.getScope() + "/");
		}

		String content = "not json";
		try {
			content = new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new CustomException("file-format-not-json");
		}
		if (!isJSONValid(content)) {
			throw new CustomException("file-format-not-json");
		}

		try {
			String uri = (new AssetFileStorage()).store(file, i18nDto.getScope(), "i18n/");
			var response = new FileUploadResponseDto();
			response.setImageUrl(uri);
			response.setStatus(true);
			response.setOriginalName(i18nDto.getScope() + file.getOriginalFilename());
			// response.setGeneratedName(image.getPath());
			response.setMsg("i18n image "+i18nDto.getScope()+file.getOriginalFilename()+" uploaded successfully"); // TODO
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			throw new FileStorageException(e.getLocalizedMessage()); // TODO
		}
	}
}
