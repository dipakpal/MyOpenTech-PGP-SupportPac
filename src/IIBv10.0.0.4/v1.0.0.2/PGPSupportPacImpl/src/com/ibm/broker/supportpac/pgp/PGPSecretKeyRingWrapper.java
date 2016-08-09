package com.ibm.broker.supportpac.pgp;

import java.util.ArrayList;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

/**
 * PGP Secret keyring wrapper class.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * PGP Secret keyring wrapper class.
 */
public class PGPSecretKeyRingWrapper {
	
	PGPSecretKeyRing base;

	/**
	 * 
	 * @param iBase
	 */
	public PGPSecretKeyRingWrapper(PGPSecretKeyRing iBase) {
		base = iBase;
	}

	/**
	 * 
	 * @return
	 */
	public PGPSecretKeyRing getSecretKeyRing() {
		return base;
	}

	/**
	 * 
	 * @return
	 */
	public PGPSecretKey getMasterKey() {
		return base.getSecretKey();
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPSecretKey getDecryptionKey() {
		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			if (k.isMasterKey())
				return k;
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPSecretKey getSigningKey() {
		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			if (k.isSigningKey())
				return k;
		}
		return null;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean matchUserId(String userId) {

		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			Iterator it = k.getUserIDs();

			while (it.hasNext()) {
				if (userId.equals((String)it.next())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean containsUserId(String userId) {
		ArrayList arraylist = getUserIds();

		for (int i = 0; i < arraylist.size(); i++) {
			if (userId.equals((String) arraylist.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param hexKeyId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean containsKeyId(String hexKeyId) {
		ArrayList arraylist = getKeyIds();

		for (int i = 0; i < arraylist.size(); i++) {
			String keyId = (String)arraylist.get(i);
			if(keyId.equalsIgnoreCase(hexKeyId)){
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getUserIds() {
		ArrayList arraylist = new ArrayList();
		
		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			
			Iterator itr = k.getUserIDs();			
			while (itr.hasNext()) {
				arraylist.add(itr.next().toString());
			}
		}

		arraylist.trimToSize();
		return arraylist;
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList getKeyIds() {
		ArrayList arraylist = new ArrayList();
		
		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			arraylist.add(Integer.toHexString((int)k.getKeyID()));
		}

		arraylist.trimToSize();
		return arraylist;
	}
	
	/**
	 * Get list of subkeys
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getSubKeyIds() {
		ArrayList arraylist = new ArrayList();
		
		Iterator iter = base.getSecretKeys();
		while (iter.hasNext()) {
			PGPSecretKey k = (PGPSecretKey) iter.next();
			arraylist.add(Long.toHexString(k.getKeyID()));
		}

		arraylist.trimToSize();
		return arraylist;
	}

	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		StringBuilder outStr = new StringBuilder();
		Iterator iter = getMasterKey().getUserIDs();
		
		outStr.append("KeyId (Hex): [0x");
		outStr.append(Integer.toHexString((int)getMasterKey().getKeyID()).toUpperCase());
        outStr.append("] Key User Id: [");

        boolean single = true;
        while(iter.hasNext()){
        	if(single){
        		outStr.append(iter.next().toString());
        		single = false;
        	} else {
        		outStr.append("; " + iter.next().toString());
        	}            
        }
        
        outStr.append("]");

		return outStr.toString();
	}

}
