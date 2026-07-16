package com.freemind.util;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

public final class ImageUploadValidator {
	
	private ImageUploadValidator() {}
	
	public static void validateImageSize(MultipartFile file, DataSize maxSize) {
		if (file != null && !file.isEmpty() && file.getSize() > maxSize.toBytes()) {
			throw new IllegalArgumentException(
					"圖片過大，請上傳 " + maxSize.toMegabytes() + "MB 以內的檔案");
		}
	}

}
