package Servidor;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private final Socket socket;
    private final Servidor servidor;

    // Constructor que recibe el socket y el servidor
    public ManejadorCliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Manejando cliente: " + socket.getInetAddress());

            // Leer la opción y el parámetro enviados por el cliente
            String opcionServidor = (String) entrada.readObject();
            String parametro = (String) entrada.readObject();

            // Variable para la respuesta
            String respuesta;

            // Procesar la opción recibida
            switch (opcionServidor) {
                case "ISBN":
                    respuesta = servidor.buscarPorISBN(parametro); // Usar el método de Servidor
                    break;
                case "Titulo":
                    respuesta = servidor.buscarPorTitulo(parametro); // Usar el método de Servidor
                    break;
                case "Autor":
                    respuesta = servidor.buscarPorAutor(parametro); // Usar el método de Servidor
                    break;
                case "Añadir":
                    respuesta = servidor.agregarLibro(parametro.split(",")); // Usar el método de Servidor
                    break;
                default:
                    respuesta = "Opción no válida.";
            }

            // Enviar la respuesta al cliente
            salida.writeObject(respuesta);
            salida.flush(); // Asegura que todos los datos se envíen

        } catch (IOException | ClassNotFoundException e) {
            // Manejo de excepciones para entrada/salida y lectura de objetos
            System.err.println("Error al manejar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar el socket y limpiar los recursos al finalizar
            try {
                socket.close();  // Cerrar el socket después de que se haya enviado la respuesta
                System.out.println("Cliente desconectado: " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }
}
