package Cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345); // Establece la conexión con el servidor
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            // Asegura que los datos se envíen de manera ordenada
            salida.flush(); // Realizamos un primer flush para asegurar la correcta inicialización del flujo

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nMenú:");
                System.out.println("1. Consultar libro por ISBN");
                System.out.println("2. Consultar libro por título");
                System.out.println("3. Consultar libros por autor");
                System.out.println("4. Añadir libro");
                System.out.println("5. Salir");
                System.out.print("Seleccione una opción: ");

                // Verifica si la opción ingresada es válida
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer para leer el siguiente input

                String parametro = null;
                String opcionServidor = null;

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese el ISBN: ");
                        parametro = scanner.nextLine();
                        opcionServidor = "ISBN";
                        break;
                    case 2:
                        System.out.print("Ingrese el título: ");
                        parametro = scanner.nextLine();
                        opcionServidor = "Titulo";
                        break;
                    case 3:
                        System.out.print("Ingrese el autor: ");
                        parametro = scanner.nextLine();
                        opcionServidor = "Autor";
                        break;
                    case 4:
                        System.out.print("Ingrese los datos del libro (ISBN,Título,Autor,Precio): ");
                        parametro = scanner.nextLine();
                        opcionServidor = "Añadir";
                        break;
                    case 5:
                        System.out.println("Cerrando cliente...");
                        return; // Finaliza la ejecución del cliente
                    default:
                        System.out.println("Opción no válida");
                        continue; // Vuelve a mostrar el menú si la opción es inválida
                }

                // Envia la solicitud al servidor
                salida.writeObject(opcionServidor);
                salida.writeObject(parametro);
                salida.flush();  // Asegura que la información se envíe al servidor

                // Espera la respuesta del servidor
                try {
                    String respuesta = (String) entrada.readObject();
                    System.out.println("Respuesta del servidor:\n" + respuesta);
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("Error al recibir la respuesta del servidor: " + e.getMessage());
                    break; // Rompe el bucle en caso de error al recibir la respuesta
                }
            }
        } catch (IOException e) {
            System.err.println("Error de comunicación con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

