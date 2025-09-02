package com.example.biblioteca.repository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.biblioteca.domain.Livro;
public interface LivroRepository extends JpaRepository<Livro, Long> {
  Page<Livro> findByAutor_NomeContainingIgnoreCaseAndDisponivel(String autor, boolean disponivel, Pageable pageable);
  Page<Livro> findByAutor_NomeContainingIgnoreCase(String autor, Pageable pageable);
  Page<Livro> findByDisponivel(boolean disponivel, Pageable pageable);
}
