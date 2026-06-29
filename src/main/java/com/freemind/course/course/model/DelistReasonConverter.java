package com.freemind.course.course.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DelistReasonConverter implements AttributeConverter<DelistReason, String> {

	@Override
	public String convertToDatabaseColumn(DelistReason attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getLabel();
	}

	@Override
	public DelistReason convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return DelistReason.fromLabel(dbData);
	}
}
