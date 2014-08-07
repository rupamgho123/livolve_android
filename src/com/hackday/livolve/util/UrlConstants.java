package com.hackday.livolve.util;

import android.content.Context;

public class UrlConstants {
	private static final String BASE_DOMAIN = "http://172.17.91.244:3000";
	
	public static String getLoginUrl(){
		return BASE_DOMAIN + "/user/login";
	}
	
	public static String getIssuesUrl(IssueType type, Context context) {
		String id = Util.getUserId(context);
		switch(type)
		{
		case MINE:
			return BASE_DOMAIN + "/issue/"+id;
		case OTHERS:
			return BASE_DOMAIN + "/invite/"+id+"/accepted/issues";
		default:
			return null;
		}
	}
	
	public static String getConversationUrl(Context applicationContext,String issueId) {
		return BASE_DOMAIN + "/conversation/"+issueId;
	}
	
	public static String getMarkAnswerUrl(String conversationId) {
		return BASE_DOMAIN + "/conversation/"+conversationId+"/solution";
	}
	
	public static String getFriendsUrl(String teamId) {
		return BASE_DOMAIN + "/team/"+teamId+"/users";
	}
	
	public static String addIssueUrl(){
		return BASE_DOMAIN + "/issue/new";
	}
	
	public static String getInviteFriendsUrl(){
		return BASE_DOMAIN + "/invite/bulk";
	}
}
