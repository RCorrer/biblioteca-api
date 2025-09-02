package com.example.biblioteca.domain;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Autor {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable=false)
  private String nome;
  private String nacionalidade;
  @OneToMany(mappedBy="autor", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<Livro> livros = new ArrayList<>();
}
