package Servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    private final List<Libro> libros;

    public Servidor() {
        libros = new ArrayList<>();
        libros.add(new Libro("001", "El Quijote", "Miguel de Cervantes", 12.50));
        libros.add(new Libro("002", "Cien Años de Soledad", "Gabriel García Márquez", 18.75));
        libros.add(new Libro("003", "1984", "George Orwell", 14.90));
        libros.add(new Libro("004", "Orgullo y Prejuicio", "Jane Austen", 10.60));
        libros.add(new Libro("005", "Crónica de una Muerte Anunciada", "Gabriel García Márquez", 16.40));

    }
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.iniciarServidor();
    }

    public void iniciarServidor() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado en el puerto 12345. Esperando clientes...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                new Thread(new ManejadorCliente(socket, this)).start(); // Pasar la instancia de Servidor
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public class ManejadorCliente implements Runnable {
        private final Socket socket;

        public ManejadorCliente(Socket socket, Servidor servidor) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream())) {

                String opcion = (String) entrada.readObject();
                String parametro = (String) entrada.readObject();
                String resultado;

                switch (opcion) {
                    case "ISBN":
                        resultado = buscarPorISBN(parametro);
                        break;
                    case "Titulo":
                        resultado = buscarPorTitulo(parametro);
                        break;
                    case "Autor":
                        resultado = buscarPorAutor(parametro);
                        break;
                    case "Añadir":
                        String[] datosLibro = parametro.split(",");
                        resultado = agregarLibro(datosLibro);
                        break;
                    default:
                        resultado = "Opción no válida";
                }
                salida.writeObject(resultado);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String buscarPorISBN(String isbn) {
        synchronized (libros) {
            return libros.stream()
                    .filter(libro -> libro.getIsbn().equals(isbn))
                    .map(Libro::toString)
                    .findFirst()
                    .orElse("Libro no encontrado");
        }
    }

    public String buscarPorTitulo(String titulo) {
        synchronized (libros) {
            return libros.stream()
                    .filter(libro -> libro.getTitulo().equalsIgnoreCase(titulo))
                    .map(Libro::toString)
                    .findFirst()
                    .orElse("Libro no encontrado");
        }
    }

    public String buscarPorAutor(String autor) {
        synchronized (libros) {
            List<String> librosAutor = new ArrayList<>();
            for (Libro libro : libros) {
                if (libro.getAutor().equalsIgnoreCase(autor)) {
                    librosAutor.add(libro.toString());
                }
            }
            return librosAutor.isEmpty() ? "No se encontraron libros de este autor" : String.join("\n", librosAutor);
        }
    }

    public String agregarLibro(String[] datos) {
        if (datos.length != 4) {
            return "Datos inválidos para agregar libro";
        }

        synchronized (libros) {
            libros.add(new Libro(datos[0], datos[1], datos[2], Double.parseDouble(datos[3])));
            return "Libro agregado exitosamente";
        }
    }
}

