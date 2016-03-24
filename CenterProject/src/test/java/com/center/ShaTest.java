package com.center;

import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;

import com.center.utils.CryptographyPasswordHash;

import junit.framework.Assert;

public class ShaTest {

	@Test
	public void ShaTest() {
		String hashedPassword = null;
		try {
			hashedPassword = CryptographyPasswordHash.computePasswordHash("1234", null);
			System.out.println("hashedPassword:" + hashedPassword + "," + hashedPassword.length());
			boolean result = CryptographyPasswordHash.verifyPassword("1234", hashedPassword);
			System.out.println("result:" + result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		/*String hashedPassword = CryptographyPasswordHash.calculateHash("1234", "aaa");
		System.out.println("hashedPassword:" + hashedPassword);
		String hashedPassword2 = CryptographyPasswordHash.calculateHash("1234", "aaa");
		System.out.println("hashedPassword:" + hashedPassword2);
		
		System.out.println("result:" + hashedPassword.equals(hashedPassword2) + "," + hashedPassword.length());*/
	}
}
