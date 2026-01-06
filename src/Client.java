import java.net.*;
import java.security.*;

import ciphers.AESGCM_Cipher;
import ciphers.AES_KeyGen;
import utils.ConvertingUtils;
import dh.DiffieHellman;

import java.io.*;
import javax.crypto.SecretKey;

public class Client {

    public static final String USAGE = "Usage: java Client serverAddr serverPort";
    public static final int SOCKET_TIMEOUT = 60000;

    // TEMP (?)
    public static final String SECURERANDOM_ALGORITHM = "SHA1PRNG";

    public static void main(String[] args) {

        InetAddress addr = null;
        int port = -1;
        try{
            if(args.length == 2){
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
                if (port < 1024 || port > 65536){
                    System.err.println("port is outside the range of valid ports. [1024-65535]");
					System.out.println(USAGE);
                    System.exit(1);
                }                
            }else{
                System.out.println(USAGE);                
                System.exit(1);
            }
        }catch (NumberFormatException e) {
			System.err.println("Server port not valid: ");
			e.printStackTrace();
			System.out.println(USAGE);
			System.exit(2);
		} catch (UnknownHostException e) {
			System.err.println("Server's IP address not valid: ");
			e.printStackTrace();
			System.out.println(USAGE);
			System.exit(2);
		} catch (SecurityException e) {
			System.err.println("Server's IP address not valid: ");
			e.printStackTrace();
			System.out.println(USAGE);
			System.exit(2);
		} 

        System.out.println("Client application started...");

        Socket socket = null;
        DataInputStream inputSocket = null;
        DataOutputStream outputSocket = null;

        try{
            socket = new Socket(addr, port);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            System.out.println("Connection created! Socket: " + socket);
        }catch (SocketException e) {
			System.err.println("protocol problems in socket creation: ");
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.err.println("problems during socket creation: ");
			e.printStackTrace();
			System.exit(3);
		} catch (Exception e) {
			System.err.println("problems during socket creation: ");
			e.printStackTrace();
			System.exit(3);
		}

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String message = null;

        try{
            inputSocket = new DataInputStream(socket.getInputStream());
            outputSocket = new DataOutputStream(socket.getOutputStream());
        }catch (IOException e) {
            System.err.println("problems during streams creation: ");
            e.printStackTrace();
            System.exit(2);
		}

        System.out.println("Chat started: ");
        try{

            KeyPair dhKeyPair = DiffieHellman.generateDHKeyPair();
            PublicKey pubKey = dhKeyPair.getPublic();
            PrivateKey privKey = dhKeyPair.getPrivate();
            //System.out.println("Generated Diffie-Hellman Key Pair: \n PubKey ->" + pubKey + "\n PrivKey ->" + privKey);

            byte[] encodedPubKey = DiffieHellman.getEncodedPubKey(pubKey);
            outputSocket.writeInt(encodedPubKey.length);
            outputSocket.write(encodedPubKey);
            
            int receivedKeyLen = inputSocket.readInt();
            byte [] receivedPubKey_Bytes = new byte[receivedKeyLen];
            inputSocket.readFully(receivedPubKey_Bytes);
            PublicKey peerPubKey = DiffieHellman.decodePublicKey(receivedPubKey_Bytes);

            byte[] sharedSecret = DiffieHellman.computeSharedSecret(privKey, peerPubKey);

            /*
             * AES-GCM Cipher initialization
             */
            SecretKey aesKey = AES_KeyGen.deriveAESKey(sharedSecret);
            //System.out.println("Derived AES Key: " + aesKey);
            AESGCM_Cipher cipher = new AESGCM_Cipher(aesKey);

            /*
             * Strarting Clients communcations 
             */
            Receiver messageReceiver = new Receiver(inputSocket, cipher);
            messageReceiver.start();

            while((message = stdIn.readLine()) != null){

                byte [] ecryptedMex = cipher.encrypt(ConvertingUtils.toByteArray(message));
                outputSocket.writeInt(ecryptedMex.length);
                outputSocket.write(ecryptedMex);
            }
        }catch (Exception e) {
			System.err.println("Errors: ");
			e.printStackTrace();
			System.err.println("Shutting down the connection!");
			System.exit(1);
		}

        try{
            System.out.println("\n\n Closing the Client.");
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        }catch (Exception e) {
			System.err.println("Errors: ");
			e.printStackTrace();
			System.err.println("Shutting down the connection!");
			System.exit(1);
		}
    }
}
