
package com.ibm.broker.supportpac.pgp;

import java.util.ArrayList;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

/**
 * PGP Public keyring wrapper class.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * PGP Public keyring wrapper class.
 */
public class PGPPublicKeyRingWrapper  {
	
    PGPPublicKeyRing base;

    /**
     * 
     * @param iBase
     */
    public PGPPublicKeyRingWrapper(PGPPublicKeyRing iBase){
        base = iBase;
    }

    /**
     * 
     * @return
     */
    public PGPPublicKeyRing getPublicKeyRing(){
        return base;
    }

    /**
     * 
     * @return
     */
    public PGPPublicKey getMasterKey(){
        return base.getPublicKey();
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
	public PGPPublicKey getEncryptionKey(){
        Iterator iter = base.getPublicKeys();
        PGPPublicKey encKey = null;
        while(iter.hasNext()){
            PGPPublicKey k = (PGPPublicKey) iter.next();
            if(k.isEncryptionKey())
                encKey = k;
        }

        return encKey ;
    }

    /**
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PGPPublicKeyRingWrapper)){
            return false;
        } else {
            return ((PGPPublicKeyRingWrapper)obj).base.equals(base);
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    @SuppressWarnings("rawtypes")
	public boolean matchUserId(String userId) {

		Iterator iter = base.getPublicKeys();
		while (iter.hasNext()) {
			PGPPublicKey k = (PGPPublicKey) iter.next();
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
	public boolean containsUserId(String userId){
    	ArrayList arraylist = getUserIds();

    	for (int i = 0; i < arraylist.size(); i++) {
			if(userId.equals((String)arraylist.get(i))){
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
	public boolean containsKeyId(String hexKeyId){
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
	public ArrayList getUserIds(){
    	ArrayList arraylist = new ArrayList();
    	Iterator iter = base.getPublicKeys();

        while(iter.hasNext()){
            PGPPublicKey k = (PGPPublicKey) iter.next();
            Iterator itr = k.getUserIDs();

            while(itr.hasNext()){
            	String userId = (String)itr.next();
            	arraylist.add(userId);
            }
        }

        arraylist.trimToSize();
        return arraylist;
    }
    
    /**
     * 
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getKeyIds(){
    	ArrayList arraylist = new ArrayList();
    	Iterator iter = base.getPublicKeys();

        while(iter.hasNext()){
            PGPPublicKey k = (PGPPublicKey) iter.next();
            arraylist.add(Integer.toHexString((int)k.getKeyID()));
        }

        arraylist.trimToSize();
        return arraylist;
    }
    
    /**
     * 
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getSubKeyIds(){
    	ArrayList arraylist = new ArrayList();
    	Iterator iter = base.getPublicKeys();

        while(iter.hasNext()){
            PGPPublicKey k = (PGPPublicKey) iter.next();
            arraylist.add(Long.toHexString(k.getKeyID()));
        }

        arraylist.trimToSize();
        return arraylist;
    }

    /**
     * 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.base != null ? this.base.hashCode() : 0);
        return hash;
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
