package users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import clientServer.Report;

/**
 * Stores all the user logged in and not logged in from the user file.
 * Updates the user file when necessary.
 * @author sophieguile
 *
 */
public class UserTable {
	private static ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
	private static String userFile = "src/users/UserFile.xml";
	
	private static Document doc;
	
	/**
	 * Reads in the user file from the XML file UserFile.xml and stores them in UserTable.
	 */
	public static synchronized void readInFile(){
		try{	
			File userXML = new File(userFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(userXML);

			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("user");
			
			for (int i = 0; i < nList.getLength(); i++){
				Node nNode = nList.item(i);
				
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eUser = (Element) nNode;
					createUser(eUser.getAttribute("name"), eUser.getAttribute("password"));
				}
			}
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
			Report.errorAndGiveUp("Couldn't configure user file");
		}
		catch(SAXException e){
			e.printStackTrace();
			Report.errorAndGiveUp("Couldn't parse user file");
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			Report.errorAndGiveUp("Couldn't find user file");
		}
		catch(Exception e){
			e.printStackTrace();
			Report.errorAndGiveUp("Unknown error in reading user file");
		}
		Report.notify("User file has been read in successfully");
	}
	
	
	/**
	 * Writes a new user out to the user file.
	 * @param username
	 * @param password
	 */
	public static void writeToFile(String username, String password){
		Element root = doc.getDocumentElement();
		
		Element eluser = doc.createElement("user");
		eluser.setAttribute("name", username);
		eluser.setAttribute("password", password);
		
		root.appendChild(eluser);
		
		
		DOMSource source = new DOMSource(doc);

        
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer;
			transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(userFile);
	        transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			Report.error("Unable to configure transformer for file writing");
			e.printStackTrace();
		} catch (TransformerException e) {
			Report.error("Error in writing to file");
			e.printStackTrace();
		}
	}
	
	
	public static void createUser(String username, String password){
		users.put(username, new User(username, password));
	}
	
	public static synchronized boolean updateUserFile(){
		//Yet to be implemented
		return false;
	}
	
	public static void joinGame(String userName, Integer gameID){
		users.get(userName).joinGame(gameID);
	}
	
	public static void leaveGame(String userName){
		users.get(userName).leaveGame();
	}
	
	public static synchronized boolean logInUser(String userName, int clientID){
		return users.get(userName).login(clientID);
	}
	
	public static synchronized boolean logoutUser(String userName){
		return users.get(userName).logout();
	}
	
	public static synchronized boolean authenticate(String userName, String passWord){
		return users.get(userName).getPassword().equals(passWord);
	}
	
	public static synchronized boolean isUser(String userName){
		return users.containsKey(userName);
	}
	
	
	public static synchronized Optional<Integer> getClientID(String userName){
		return users.get(userName).getClientID();
	}
	
}
