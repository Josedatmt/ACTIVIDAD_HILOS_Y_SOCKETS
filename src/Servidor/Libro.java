package Servidor;

import java.io.Serializable;

public class Libro implements Serializable {
    private final String isbn;
    private final String titulo;
    private final String autor;
    private final double precio;

    public Libro(String isbn, String titulo, String autor, double precio) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public Double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return String.format("ISBN: %s, Título: %s, Autor: %s, Precio: %.2f", isbn, titulo, autor, precio);
    }
}
