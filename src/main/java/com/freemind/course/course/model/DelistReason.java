package com.freemind.course.course.model;

public enum DelistReason {

	REGULATION_CHANGE("法規變更"), 
	LICENSE_EXPIRED("授權期滿"), 
	LECTURER_REQUEST("講師要求"), 
	SUDDEN_DISPUTE("突發爭議"),
	TECHNICAL_FAILURE("技術故障"), 
	TOO_MANY_QUALITY_COMPLAINTS("品質投訴過多");

	private final String label;

	DelistReason(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static DelistReason fromLabel(String label) {
		if (label == null) {
			return null;
		}

		for (DelistReason reason : DelistReason.values()) {
			if (reason.getLabel().equals(label)) {
				return reason;
			}
		}

		throw new IllegalArgumentException("未知的下架原因: " + label);
	}
}
