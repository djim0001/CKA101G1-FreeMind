package com.freemind.article.exception;

import java.util.Map;

public class ArticleValidationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    private final Map<String, String> fieldErrors;

    public ArticleValidationException(Map<String, String> fieldErrors) {
        super("表單驗證失敗");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
    	return fieldErrors;
    }
    
}
