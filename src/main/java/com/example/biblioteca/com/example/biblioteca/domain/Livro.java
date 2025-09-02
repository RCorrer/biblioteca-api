package com.example.biblioteca.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Livro {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable=false)
  private String titulo;
  @Column(unique=true)
  private String isbn;
  private Integer anoPublicacao;
  @ManyToOne(optional=false)
  private Autor autor;
  private boolean disponivel = true;
}
