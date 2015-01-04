package com.beiluoshimen.securityguard.market;


import retrofit.http.GET;
import retrofit.http.Query;


public interface DlSvcApi {
	
	public static final String DL_SVC_PATH = "/download";
	
	
	public static final String DL_100_PATH = DL_SVC_PATH + "/100.zip";
	public static final String DL_101_PATH = DL_SVC_PATH + "/101.zip";
	public static final String DL_102_PATH = DL_SVC_PATH + "/102.zip";
	public static final String DL_103_PATH = DL_SVC_PATH + "/103.zip";
	
	public static final String DL_FREE_COIN = DL_SVC_PATH + "/freeCoin";

	
}
