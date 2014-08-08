package com.hackday.livolve.util;

public class UrlConstants {
	private static final String BASE_DOMAIN = "http://172.17.91.244:3000";
	
	public static String getLoginUrl(){
		return BASE_DOMAIN + "/user/login";
	}
	
	public static String getIssuesUrl(IssueType type, String id) {
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
	
	public static String getConversationUrl(String issueId) {
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
	
	public static String getInvitesForMe(String userId) {
		return BASE_DOMAIN + "/invite/"+userId+"/pending";
	}
	
	public static String updateInviteUrl(String inviteId, String status) {
		return BASE_DOMAIN + "/invite/"+inviteId+"/status/"+status;
	}

	public static String createConversationUrl() {
		return BASE_DOMAIN + "/conversation/new";
	}
	
	public static String updateIssueUrl(String id,String status) {
		return BASE_DOMAIN + "/issue/"+id+"/status/"+status;
	}
}
