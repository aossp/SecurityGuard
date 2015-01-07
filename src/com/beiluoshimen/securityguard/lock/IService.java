package com.beiluoshimen.securityguard.lock;


/**
 * used with LockService.
 * we exposed LockService method to others.
 * 
 * >>callTempStopLock  << this is callback.
 * 
 * @author Hsieh Yu-Hua
 */
public interface IService {
	public void callTempStopLock(String packName);
}
