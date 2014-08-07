package com.hackday.livolve;

public enum DialogType {
	ERROR(0),PROGRESS(1),CONFIRMATION(2);
	private int id;
	DialogType(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}

	public static DialogType getTypeByID(int int1) {
		switch(int1){
		case 0:
			return ERROR;
		case 1:
			return PROGRESS;
		case 2:
			return CONFIRMATION;
		default:
			return null;
		}
	}
}

