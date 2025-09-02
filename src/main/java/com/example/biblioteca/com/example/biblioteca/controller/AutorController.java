package com.example.biblioteca.controller;
import com.example.biblioteca.domain.Autor;
import com.example.biblioteca.repository.AutorRepository;
import com.example.biblioteca.exception.NotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutorController {
  private final AutorRepository repo;
  public AutorController(AutorRepository repo){ this.repo = repo; }

  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<Autor>>> listar(){
    List<EntityModel<Autor>> autores = repo.findAll().stream().map(a -> toModel(a)).toList();
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutorController.class).listar()).withSelfRel();
    return ResponseEntity.ok(CollectionModel.of(autores, self));
  }

  @GetMapping("/{id}")
  public EntityModel<Autor> obter(@PathVariable Long id){
    var autor = repo.findById(id).orElseThrow(() -> new NotFoundException("Autor não encontrado"));
    return toModel(autor);
  }

  @PostMapping
  public ResponseEntity<EntityModel<Autor>> criar(@Valid @RequestBody Autor autor){
    var salvo = repo.save(autor);
    var model = toModel(salvo);
    return ResponseEntity.status(HttpStatus.CREATED).body(model);
  }

  @PutMapping("/{id}")
  public EntityModel<Autor> atualizar(@PathVariable Long id, @Valid @RequestBody Autor dto){
    var a = repo.findById(id).orElseThrow(() -> new NotFoundException("Autor não encontrado"));
    a.setNome(dto.getNome()); a.setNacionalidade(dto.getNacionalidade());
    return toModel(repo.save(a));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remover(@PathVariable Long id){
    if(!repo.existsById(id)) throw new NotFoundException("Autor não encontrado");
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private EntityModel<Autor> toModel(Autor a){
    var self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutorController.class).obter(a.getId())).withSelfRel();
    var update = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutorController.class).atualizar(a.getId(), a)).withRel("update");
    var delete = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutorController.class).remover(a.getId())).withRel("delete");
    return EntityModel.of(a, self, update, delete);
  }
}
