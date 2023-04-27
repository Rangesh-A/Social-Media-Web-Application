package Services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.*;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class LocalServices {
	public static String capitalizeName(String name) {
		return (name.charAt(0) + "").toUpperCase() + name.substring(1);
	}

	public static String getCurrentDateTime() {
		return getByFormat("yyyy MM dd HH mm ss");
	}

	public static String getByFormat(String format) {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern(format);
		LocalDateTime myDateObj = LocalDateTime.now();
		String formattedDate = myDateObj.format(myFormatObj);
		return formattedDate;
	}

	public static String getTime(String time) {
		String hour = time.substring(11, 13);
		String min = time.substring(14, 16);
		String type = "AM";
		hour = hour.equals("00") ? "12" : hour;
		if (Integer.parseInt(hour) > 12) {
			hour = (Integer.parseInt(hour) - 12) + "";
			type = "PM";
		}
		return hour + ":" + min + " " + type;
	}

	public static String getMonthYear(String dateTime) {
		String months[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		String format[] = dateTime.split("\\s");
		return months[Integer.parseInt(format[1]) - 1] + " " + format[0];
	}

	public static ArrayList<String> getHashTags(String txt) {
		ArrayList<String> hashtags = new ArrayList<>();
		String spaces[] = txt.split("\\s");
		for (String word : spaces) {
			if (word.startsWith("#")) {
				hashtags.add(word);
			}
		}
		return hashtags;
	}
	public static String createSignature(String data) throws Exception {
		String signature=null;
		String PATH_PIRVATE_KEY = "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\social-private.key";
		ObjectInputStream inputStream = null;

		inputStream = new ObjectInputStream(new FileInputStream(PATH_PIRVATE_KEY));
		final PrivateKey social_privateKey = (PrivateKey) inputStream.readObject();

		Signature dsa = Signature.getInstance("SHA1withDSA");
		dsa.initSign(social_privateKey);
		byte[] dataBytes = encryptData(data,"AES/ECB/PKCS5Padding").getBytes("UTF-8");
		dsa.update(dataBytes);

		// Generate the signature
		byte[] signatureBytes = dsa.sign();

		signature=Base64.getEncoder().encodeToString(signatureBytes);
		return signature;
	}
	public static byte[] getSHA(String input,String algo) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algo);
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}
	public static String toHexString(byte[] hash){
		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));
		while (hexString.length() < 64)
		{
			hexString.insert(0, '0');
		}
		return hexString.toString();
	}
	public static byte[] encrypt(String plainText,PublicKey publicKey ) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes()) ;
		return cipherText;
	}
	public static String decrypt(byte[] cipherTextArray, PrivateKey privateKey) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);
		return new String(decryptedTextArray);
	}
	public static boolean validateSignature(String signature,String data,String hashing_algorithm) throws Exception {
		String private_key="";
		String PATH_PUBLIC_KEY = "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\chat-public.key";
		ObjectInputStream inputStream = null;
		inputStream = new ObjectInputStream(new FileInputStream(PATH_PUBLIC_KEY));
		final PublicKey chat_publicKey = (PublicKey) inputStream.readObject();
		Signature dsa = Signature.getInstance("SHA1withDSA");

		dsa.initVerify(chat_publicKey);
		dsa.update(data.getBytes("UTF-8"));
		boolean verified = dsa.verify(Base64.getDecoder().decode(signature));
		return verified;
	}
	public static String encryptData(String jsondata,String dataalgo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		String SECRET_KEY_PATH="C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\secret.key";
		byte[] data = jsondata.getBytes("UTF-8");
		ObjectInputStream inputStream = null;
		inputStream = new ObjectInputStream(new FileInputStream(SECRET_KEY_PATH));
		final SecretKey secretKey = (SecretKey) inputStream.readObject();
		Cipher cipher = Cipher.getInstance(dataalgo);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] ciphertext = cipher.doFinal(data);
		String encrypted_text=Base64.getEncoder().encodeToString(ciphertext);
		return encrypted_text;
	}
	public static String decryptData(String encrypted_data,String dataalgo) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String SECRET_KEY_PATH="C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\resources\\secret.key";
		ObjectInputStream inputStream = null;
		inputStream = new ObjectInputStream(new FileInputStream(SECRET_KEY_PATH));
		final SecretKey secretKey = (SecretKey) inputStream.readObject();
		Cipher cipher = Cipher.getInstance(dataalgo);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted_data));
		String decrypted_data= new String(decrypted, "UTF-8");
		System.out.println(decrypted_data);
		return decrypted_data;
	}
	public static HashMap<Integer,Integer> jsonToHashmap(String jsondata) throws ParseException {
		Map<Integer, Integer> hashmap = new HashMap<>();
		JSONParser jsonParser=new JSONParser();
		JSONObject jsonObject= (JSONObject) jsonParser.parse(jsondata);
		for (Object key : jsonObject.keySet()) {
			hashmap.put(Integer.parseInt((key).toString()), Integer.parseInt(jsonObject.get(key).toString()));
		}
		return (HashMap<Integer, Integer>) hashmap;
	}
	public static String getHashValueofMedia(Part part) throws Exception{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = part.getInputStream()) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
        	}
			byte[] byteArray = baos.toByteArray();
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(byteArray);
			return bytesToHex(hash);
		} catch (IOException e) {
			return null;
		}
	}
	private static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

	public static String generateSHA1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	public static int getBreachedPasswords(String fullHash){
        try{
            String prefix = fullHash.substring(0, 5);
            URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String line[]=inputLine.split(":");
					if((prefix+line[0].toLowerCase()).equals(fullHash)){
						return Integer.parseInt(line[1]);
					}
                }
                in.close();
            } else {
                System.out.println("Failed to retrieve data from HIBP API");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
	public static int isPasswordBreached(String password){
		String sha1hash=generateSHA1(password);
        System.out.println("password-sha1 applied = "+sha1hash);
        return getBreachedPasswords(sha1hash);
	} 
}